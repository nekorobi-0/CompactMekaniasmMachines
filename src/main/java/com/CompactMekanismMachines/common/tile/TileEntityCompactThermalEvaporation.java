package com.CompactMekanismMachines.common.tile;

import com.CompactMekanismMachines.common.config.CompactMekanismMachinesConfig;
import com.CompactMekanismMachines.common.config.MachinesConfig;
import com.CompactMekanismMachines.common.registries.CompactBlocks;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.heat.HeatAPI;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.FluidToFluidRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public class TileEntityCompactThermalEvaporation extends TileEntityConfigurableMachine implements ISingleRecipeLookupHandler.FluidRecipeLookupHandler<FluidToFluidRecipe> {
    public float prevScale;
    private final IOutputHandler<@NotNull FluidStack> outputHandler;
    private final IInputHandler<@NotNull FluidStack> inputHandler;
    private final RecipeCacheLookupMonitor<FluidToFluidRecipe> recipeCacheLookupMonitor;
    public static final double MAX_MULTIPLIER_TEMP = 3_000;
    private double tempMultiplier;
    private final BooleanSupplier recheckAllRecipeErrors;


    public TileEntityCompactThermalEvaporation(BlockPos pos, BlockState state){
        super(CompactBlocks.COMPACT_THERMAL_EVAPORATION, pos, state);
        recipeCacheLookupMonitor = new RecipeCacheLookupMonitor<>(this);
        inputHandler = InputHelper.getInputHandler(inputTank, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        recheckAllRecipeErrors = TileEntityRecipeMachine.shouldRecheckAllErrors(this);
        inputInputSlot = FluidInventorySlot.fill(inputTank, this, 28, 20);
        outputInputSlot = OutputInventorySlot.at(this, 28, 51);
        inputOutputSlot = FluidInventorySlot.drain(outputTank, this, 132, 20);
        outputOutputSlot = OutputInventorySlot.at(this, 132, 51);
        inputInputSlot.setSlotType(ContainerSlotType.INPUT);
        inputOutputSlot.setSlotType(ContainerSlotType.INPUT);

        configComponent = new TileComponentConfig(this, TransmissionType.FLUID);
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig !=null){
            fluidConfig.addSlotInfo(DataType.INPUT, new FluidSlotInfo(true,false,inputTank));
            fluidConfig.addSlotInfo(DataType.OUTPUT, new FluidSlotInfo(false,true,outputTank));
            fluidConfig.setDataType(DataType.INPUT,RelativeSide.BOTTOM);
            fluidConfig.setDataType(DataType.OUTPUT,RelativeSide.TOP);
        }
        ejectorComponent = new TileComponentEjector(this,()->Long.MAX_VALUE,()->Integer.MAX_VALUE,()-> FloatingLong.create(Long.MAX_VALUE));
        ejectorComponent.setOutputData(configComponent,TransmissionType.FLUID).setCanEject(type -> MekanismUtils.canFunction(this));
    }

    //これが原因に近い気がする...
    @NotNull
    @Override
    public  IFluidTankHolder getInitialFluidTanks(IContentsListener listener){
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this::getDirection,this::getConfig);

        //出力してみよう
        //System.out.println(this.getConfig().toString());
        System.out.println(builder);


        builder.addTank(inputTank = new FluidTank(() -> 1800000, ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrueBi(), this::containsRecipe, createSaveAndComparator()));
        builder.addTank(outputTank = new FluidTank(() -> 1800000, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), (fluidStack -> true), this));
        return builder.build();
    }

    @NotNull
    @Override
    public IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature){
        HeatCapacitorHelper builder = HeatCapacitorHelper.forSide(this::getDirection);
        builder.addCapacitor(heatCapacitor = VariableHeatCapacitor.create(MekanismConfig.general.evaporationHeatCapacity.get() * 3, () -> 300D, this), RelativeSide.TOP,RelativeSide.BOTTOM,RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK,RelativeSide.FRONT);
        return builder.build();
    }


    private static class FluidTank extends VariableCapacityFluidTank{
        protected FluidTank(@NotNull IntSupplier capacity,
                            BiPredicate<FluidStack, AutomationType> canExtract,
                            BiPredicate<FluidStack, AutomationType> canInsert,
                            Predicate<FluidStack> validator,
                            @Nullable mekanism. api. IContentsListener listener){
            super(capacity,canExtract,canInsert,validator,listener);
        }
    }

    private static final List<CachedRecipe.OperationTracker.RecipeError> TRACKED_ERROR_TYPES = List.of(
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT,
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    protected IContentsListener createSaveAndComparator() {
        return () -> {
            recipeCacheLookupMonitor.onContentsChanged();
            if (!isRemote()) {
                markDirtyComparator();
            }
        };
    }
    @ContainerSync
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerFluidTankWrapper.class, methodNames = {"getInput", "getInputCapacity", "getInputNeeded", "getInputFilledPercentage"}, docPlaceholder = "input tank")
    public BasicFluidTank inputTank;
    @ContainerSync
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerFluidTankWrapper.class, methodNames = {"getOutput", "getOutputCapacity", "getOutputNeeded", "getOutputFilledPercentage"}, docPlaceholder = "output tank")
    public BasicFluidTank outputTank;
    @ContainerSync
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getInputItemInput", docPlaceholder = "input side's input slot")
    final FluidInventorySlot inputInputSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getInputItemOutput", docPlaceholder = "input side's output slot")
    final OutputInventorySlot outputInputSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getOutputItemInput", docPlaceholder = "output side's input slot")
    final FluidInventorySlot inputOutputSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getOutputItemOutput", docPlaceholder = "output side's output slot")
    final OutputInventorySlot outputOutputSlot;
    @SyntheticComputerMethod(getter = "getEnvironmentalLoss")
    public double lastEnvironmentLoss;
    @ContainerSync
    private final boolean[] trackedErrors = new boolean[TRACKED_ERROR_TYPES.size()];
    @ContainerSync
    @SyntheticComputerMethod(getter = "getProductionAmount")
    public double lastGain;
    @ContainerSync
    public VariableHeatCapacitor heatCapacitor;
    @Override
    public void onUpdateServer(){
        super.onUpdateServer();
        lastEnvironmentLoss = simulateEnvironment();
        // update temperature
        updateHeatCapacitors(null);
        //After we update the heat capacitors, update our temperature multiplier
        // Note: We use the ambient temperature without taking our biome into account as we want to have a consistent multiplier
        tempMultiplier = (Math.min(MAX_MULTIPLIER_TEMP, getTemperature()) - HeatAPI.AMBIENT_TEMP) * MekanismConfig.general.evaporationTempMultiplier.get()*((double) CompactMekanismMachinesConfig.machines.evaporationheight.get()/18);
        inputOutputSlot.drainTank(outputOutputSlot);
        inputInputSlot.fillTank(outputInputSlot);
        setActive(recipeCacheLookupMonitor.updateAndProcess());
        float scale = MekanismUtils.getScale(prevScale, inputTank);
        if (scale != prevScale) {
            prevScale = scale;
        }
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<FluidToFluidRecipe, InputRecipeCache.SingleFluid<FluidToFluidRecipe>> getRecipeType() {
        return MekanismRecipeType.EVAPORATING;
    }

    @Override
    public @Nullable FluidToFluidRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler);
    }
    @ComputerMethod
    public double getTemperature() {
        return heatCapacitor.getTemperature();
    }

    @Override
    public @NotNull CachedRecipe<FluidToFluidRecipe> createNewCachedRecipe(@NotNull FluidToFluidRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.fluidToFluid(recipe, recheckAllRecipeErrors, inputHandler, outputHandler)
                .setErrorsChanged(errors -> {
                    for (int i = 0; i < trackedErrors.length; i++) {
                        trackedErrors[i] = errors.contains(TRACKED_ERROR_TYPES.get(i));
                    }
                })
                .setActive(active -> {
                    //TODO: Make the numbers for lastGain be based on how much the recipe provides as an output rather than "assuming" it is 1 mB
                    // Also fix that the numbers don't quite accurately reflect the values as we modify number of operations, and not have a fractional
                    // amount
                    if (active) {
                        if (tempMultiplier > 0 && tempMultiplier < 1) {
                            lastGain = 1F / (int) Math.ceil(1 / tempMultiplier);
                        } else {
                            lastGain = tempMultiplier;
                        }
                    } else {
                        lastGain = 0;
                    }
                })
                .setRequiredTicks(() -> tempMultiplier > 0 && tempMultiplier < 1 ? (int) Math.ceil(1 / tempMultiplier) : 1)
                .setBaselineMaxOperations(() -> tempMultiplier > 0 && tempMultiplier < 1 ? 1 : (int) tempMultiplier);
    }

    public boolean hasWarning(CachedRecipe.OperationTracker.RecipeError error) {
        int errorIndex = TRACKED_ERROR_TYPES.indexOf(error);
        if (errorIndex == -1) {
            //Something went wrong
            return false;
        }
        return trackedErrors[errorIndex];
    }
}
