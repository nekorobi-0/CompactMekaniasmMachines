package com.CompactMekanismMachines.common.tile;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.api.heat.HeatAPI;
import mekanism.api.math.FloatingLong;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes.CooledCoolant;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.chemical.variable.VariableCapacityChemicalTankBuilder.VariableCapacityGasTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismGases;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.util.HeatUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.tile.base.SubstanceType;

import mekanism.generators.common.config.MekanismGeneratorsConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.CompactMekanismMachines.common.registries.CompactBlocks;
import com.CompactMekanismMachines.common.config.CompactMekanismMachinesConfig;

import java.util.EnumSet;
import java.util.Set;

public class TileEntityCompactFissionReactor extends TileEntityConfigurableMachine {

    /**
     * The tank this block is storing fuel in.
     */
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class, methodNames = {"getFuel", "getFuelCapacity", "getFuelNeeded", "getFuelFilledPercentage"})
    public FuelTank fuelTank;
    public CoolantGasTank coolantGasTank;
    public CoolantFluidTank coolantFluidTank;
    public HeatedCoolantTank heatedCoolantTank;
    public WasteTank wasteTank;
    public HeatTank heatCapacitor;
    private long burnTicks;
    private int maxBurnTicks;
    private FloatingLong generationRate = FloatingLong.ZERO;
    private double biomeAmbientTemp;
    private double gasUsedLastTick;
    public  double partialWaste;
    public long lastBoilRate = 0;
    private static final double INVERSE_INSULATION_COEFFICIENT = 10_000;
    private static final double INVERSE_CONDUCTION_COEFFICIENT = 10;
    private static final double waterConductivity = 0.5;
    private ConfigInfo gasConfig;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem")
    GasInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem")
    EnergyInventorySlot energySlot;

    public TileEntityCompactFissionReactor(BlockPos pos, BlockState state) {
        super(CompactBlocks.COMPACT_FISSION_REACTOR, pos, state);
        biomeAmbientTemp = HeatAPI.getAmbientTemp(this.getLevel(), this.getTilePos());
        configComponent = new TileComponentConfig(this, TransmissionType.GAS,TransmissionType.FLUID);

        gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig !=null){
            gasConfig.addSlotInfo(DataType.INPUT_1, new ChemicalSlotInfo.GasSlotInfo(true,false,fuelTank));
            gasConfig.addSlotInfo(DataType.INPUT_2, new ChemicalSlotInfo.GasSlotInfo(true,false,coolantGasTank));
            gasConfig.addSlotInfo(DataType.OUTPUT_1,new ChemicalSlotInfo.GasSlotInfo(false,true,wasteTank));
            gasConfig.addSlotInfo(DataType.OUTPUT_2,new ChemicalSlotInfo.GasSlotInfo(false,true,heatedCoolantTank));
            gasConfig.setDataType(DataType.INPUT_1,RelativeSide.FRONT);
            gasConfig.setDataType(DataType.INPUT_2,RelativeSide.TOP);
            gasConfig.setDataType(DataType.OUTPUT_1,RelativeSide.BOTTOM);
            gasConfig.setDataType(DataType.OUTPUT_2,RelativeSide.BACK);
        }
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig!=null){
            fluidConfig.addSlotInfo(DataType.INPUT,new FluidSlotInfo(true,false,coolantFluidTank));
        }
        ejectorComponent = new TileComponentEjector(this, ()->Long.MAX_VALUE,()->Integer.MAX_VALUE,()-> FloatingLong.create(Long.MAX_VALUE));
        ejectorComponent.setOutputData(configComponent, TransmissionType.GAS,TransmissionType.FLUID)
                .setCanEject(type -> MekanismUtils.canFunction(this));
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection,this::getConfig);
        builder.addTank(fuelTank = new FuelTank(listener));
        builder.addTank(coolantGasTank = new CoolantGasTank(listener));
        builder.addTank(wasteTank = new WasteTank(listener));
        builder.addTank(heatedCoolantTank = new HeatedCoolantTank(listener));
        return builder.build();
    }

    @NotNull
    @Override
    public IFluidTankHolder getInitialFluidTanks(IContentsListener listener){
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this::getDirection,this::getConfig);
        builder.addTank(coolantFluidTank = new CoolantFluidTank(listener));
        return builder.build();
    }

    @NotNull
    @Override
    public IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature){
        HeatCapacitorHelper builder = HeatCapacitorHelper.forSide(this::getDirection);
        builder.addCapacitor(heatCapacitor = new HeatTank(listener),RelativeSide.TOP,RelativeSide.BOTTOM,RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK,RelativeSide.FRONT);
        return builder.build();
    }


    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!fuelTank.isEmpty() && MekanismUtils.canFunction(this)) {
            setActive(true);
            Set<Direction> emitDirections = EnumSet.noneOf(Direction.class);
            emitDirections.addAll(gasConfig.getSidesForOutput(DataType.OUTPUT_2));


            if (!fuelTank.isEmpty()) {
                maxBurnTicks = 1;
                generationRate = FloatingLong.create(50000);

            }

            long toUse = getToUse();
            FloatingLong toUseGeneration = generationRate.multiply(toUse);

            long total = burnTicks + fuelTank.getStored() * maxBurnTicks;
            total -= toUse;
            if (!fuelTank.isEmpty()) {
                //TODO: Improve this as it is sort of hacky
                fuelTank.setStack(new GasStack(fuelTank.getStack(), total / maxBurnTicks));
            }

            heatCapacitor.handleHeat(toUse * MekanismGeneratorsConfig.generators.energyPerFissionFuel.get().doubleValue());
            if (heatCapacitor.getTemperature()>1600){
                heatCapacitor.setHeat(heatCapacitor.getHeatCapacity()*1600);
            }
            double temp = heatCapacitor.getTemperature();
            double heat = toUse * (temp - HeatUtils.BASE_BOIL_TEMP) * heatCapacitor.getHeatCapacity();
            if (!coolantFluidTank.isEmpty()){
                double caseCoolantHeat = heat * waterConductivity;
                lastBoilRate = clampCoolantHeated(HeatUtils.getSteamEnergyEfficiency() * caseCoolantHeat / HeatUtils.getWaterThermalEnthalpy(),
                        coolantFluidTank.getFluidAmount());
                if (lastBoilRate > 0) {
                    MekanismUtils.logMismatchedStackSize(coolantFluidTank.shrinkStack((int) lastBoilRate, Action.EXECUTE), lastBoilRate);
                    // extra steam is dumped
                    heatedCoolantTank.insert(MekanismGases.STEAM.getStack(lastBoilRate), Action.EXECUTE, AutomationType.INTERNAL);
                    caseCoolantHeat = lastBoilRate * HeatUtils.getWaterThermalEnthalpy() / HeatUtils.getSteamEnergyEfficiency();
                    heatCapacitor.handleHeat(-caseCoolantHeat);
                }
            }   else if (!coolantGasTank.isEmpty()) {
                CooledCoolant coolantType = coolantGasTank.getStack().get(CooledCoolant.class);
                if (coolantType!=null){
                    double caseCoolantHeat = heat * coolantType.getConductivity();
                    lastBoilRate = clampCoolantHeated(caseCoolantHeat / coolantType.getThermalEnthalpy(), coolantGasTank.getStored());
                    if (lastBoilRate > 0) {
                        MekanismUtils.logMismatchedStackSize(coolantGasTank.shrinkStack(lastBoilRate, Action.EXECUTE), lastBoilRate);
                        heatedCoolantTank.insert(coolantType.getHeatedGas().getStack(lastBoilRate), Action.EXECUTE, AutomationType.INTERNAL);
                        caseCoolantHeat = lastBoilRate * coolantType.getThermalEnthalpy();
                        heatCapacitor.handleHeat(-caseCoolantHeat);
                    }
                };
            }
            partialWaste += toUse;
            long newWaste = (long) Math.floor(partialWaste);
            if (newWaste>0){
                partialWaste %= 1;
                GasStack wasteToAdd = MekanismGases.NUCLEAR_WASTE.getStack(newWaste);

                wasteTank.insert(wasteToAdd, Action.EXECUTE, AutomationType.INTERNAL);
            }
            burnTicks = total % maxBurnTicks;
            gasUsedLastTick = toUse / (double) maxBurnTicks;
        } else {
            if (fuelTank.isEmpty() && burnTicks == 0) {
                reset();
            }
            gasUsedLastTick = 0;
            setActive(false);
        }
    }

    private void reset() {
        burnTicks = 0;
        maxBurnTicks = 0;
        generationRate = FloatingLong.ZERO;
    }

    private long getToUse() {
        if (generationRate.isZero() || fuelTank.isEmpty()) {
            return 0;
        }
        long max = (long) Math.ceil(CompactMekanismMachinesConfig.machines.cfrBurnRate.get() * (fuelTank.getStored() / (double) fuelTank.getCapacity()));
        max = Math.min(maxBurnTicks * fuelTank.getStored() + burnTicks, max);
        return max;
    }

    private long clampCoolantHeated(double heated, long stored) {
        long heatedLong = MathUtils.clampToLong(heated);
        if (heatedLong < 0) {
            return 0;
        } else if (heatedLong > stored) {
            return stored;
        }
        return heatedLong;
    }
    public FloatingLong getGenerationRate() {
        return generationRate;
    }

    @ComputerMethod(nameOverride = "getBurnRate")
    public double getUsed() {
        return Math.round(gasUsedLastTick * 100) / 100D;
    }

    public int getMaxBurnTicks() {
        return maxBurnTicks;
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fuelTank.getStored(), fuelTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.GAS;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getGenerationRate, value -> generationRate = value));
        container.track(SyncableDouble.create(this::getUsed, value -> gasUsedLastTick = value));
        container.track(SyncableInt.create(this::getMaxBurnTicks, value -> maxBurnTicks = value));
    }

    //Methods relating to IComputerTile
    //End methods IComputerTile

    //Implementation of gas tank that on no longer being empty updates the output rate of this generator
    private class FuelTank extends VariableCapacityGasTank {

        protected FuelTank(@Nullable IContentsListener listener) {
            super(CompactMekanismMachinesConfig.machines.cfrFuelTankCapacity, ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi,
                    gas -> gas.equals(MekanismGases.FISSILE_FUEL.getChemical()), null, listener);
        }

        @Override
        public void setStack(@NotNull GasStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStack(stack);
            recheckOutput(stack, wasEmpty);
        }

        @Override
        public void setStackUnchecked(@NotNull GasStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStackUnchecked(stack);
            recheckOutput(stack, wasEmpty);
        }

        private void recheckOutput(@NotNull GasStack stack, boolean wasEmpty) {
        }
    }
    private class CoolantGasTank extends VariableCapacityGasTank{
        protected  CoolantGasTank(@Nullable IContentsListener listener){
            super(CompactMekanismMachinesConfig.machines.cfrCoolantGasTankCapacity,ChemicalTankBuilder.GAS.notExternal,ChemicalTankBuilder.GAS.alwaysTrueBi,
                    gas -> gas.has(CooledCoolant.class),null,listener);
        }
        @Override
        public void setStack(@NotNull GasStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStack(stack);
            recheckOutput(stack, wasEmpty);
        }

        @Override
        public void setStackUnchecked(@NotNull GasStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStackUnchecked(stack);
            recheckOutput(stack, wasEmpty);
        }

        private void recheckOutput(@NotNull GasStack stack, boolean wasEmpty) {
        }
    }

    private class CoolantFluidTank extends VariableCapacityFluidTank {
        protected  CoolantFluidTank(@Nullable IContentsListener listener){
            super(CompactMekanismMachinesConfig.machines.cfrCoolantFluidTankCapacity,
                    (stack, automationType) -> automationType != AutomationType.EXTERNAL,
                    (stack, automationType) -> true,
                    fluid -> fluid.isFluidEqual(new FluidStack(Fluids.WATER,1)),listener);
        }
    }

    private  class HeatedCoolantTank extends VariableCapacityGasTank {
        protected HeatedCoolantTank(@Nullable IContentsListener listener){
            super(CompactMekanismMachinesConfig.machines.cfrHeatedCoolantTankCapacity,
                    (stack, automationType) -> true,
                    (stack, automationType) -> automationType == AutomationType.INTERNAL,
                    gas -> (gas.has(GasAttributes.HeatedCoolant.class)||gas.equals(MekanismGases.STEAM.get())),ChemicalAttributeValidator.ALWAYS_ALLOW,listener);
        }
    }

    private  class  WasteTank extends VariableCapacityGasTank{
        protected WasteTank(@Nullable IContentsListener listener){
            super(CompactMekanismMachinesConfig.machines.cfrWasteTankCapacity,ChemicalTankBuilder.GAS.alwaysTrueBi,ChemicalTankBuilder.GAS.internalOnly,
                    gas -> gas.equals(MekanismGases.NUCLEAR_WASTE.getChemical()), ChemicalAttributeValidator.ALWAYS_ALLOW,listener);
        }
    }

    public class HeatTank extends VariableHeatCapacitor{
        protected HeatTank(@Nullable IContentsListener listener){
            super(CompactMekanismMachinesConfig.machines.cfrHeatTankCpacity.get(),() -> INVERSE_CONDUCTION_COEFFICIENT, () -> INVERSE_INSULATION_COEFFICIENT, () -> biomeAmbientTemp, null);
        }
    }
}
