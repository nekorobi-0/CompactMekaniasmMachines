package com.CompactMekanismMachines.common.tile;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.math.FloatingLong;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.chemical.variable.VariableCapacityChemicalTankBuilder.VariableCapacityGasTank;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismGases;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.util.CableUtils;
import mekanism.common.util.MekanismUtils;

import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineValidator;
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

public class TileEntityCompactIndustrialTurbine extends TileEntityConfigurableMachine {

    /**
     * The tank this block is storing fuel in.
     */
    public GasTank gasTank;
    public FluidTank ventTank;
    public BasicEnergyContainer energyContainer;
    private FloatingLong maxoutput = FloatingLong.create(Long.MAX_VALUE);

    public Integer lowerVolume = CompactMekanismMachinesConfig.machines.turbinevertuallowervolume.get();

    @SyntheticComputerMethod(getter = "getDumpingMode")
    public TileEntityChemicalTank.GasMode dumpMode = TileEntityChemicalTank.GasMode.IDLE;

    @SyntheticComputerMethod(getter = "getLastSteamInputRate")
    public long lastSteamInput;
    public long newSteamInput;

    @ContainerSync
    @SyntheticComputerMethod(getter = "getFlowRate")
    public long clientFlow;

    public TileEntityCompactIndustrialTurbine(BlockPos pos, BlockState state) {
        super(CompactBlocks.COMPACT_INDUSTRIAL_TURBINE, pos, state);
        configComponent = new TileComponentConfig(this, TransmissionType.GAS,TransmissionType.FLUID,TransmissionType.ENERGY);
        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig !=null){
            gasConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.GasSlotInfo(true,false, gasTank));
            gasConfig.setDataType(DataType.INPUT,RelativeSide.FRONT);
        }
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig!=null){
            fluidConfig.addSlotInfo(DataType.OUTPUT,new FluidSlotInfo(false,true,ventTank));
            fluidConfig.setDataType(DataType.OUTPUT,RelativeSide.TOP);
        }
        ejectorComponent = new TileComponentEjector(this, ()->Long.MAX_VALUE,()->Integer.MAX_VALUE,()-> FloatingLong.create(Long.MAX_VALUE));
        ejectorComponent.setOutputData(configComponent, TransmissionType.GAS,TransmissionType.FLUID)
                .setCanEject(type -> MekanismUtils.canFunction(this));
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection,this::getConfig);
        builder.addTank(gasTank = new GasTank(listener));
        return builder.build();
    }

    @NotNull
    @Override
    public IFluidTankHolder getInitialFluidTanks(IContentsListener listener){
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this::getDirection,this::getConfig);
        builder.addTank(ventTank = new FluidTank(listener));
        return builder.build();
    }
    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = BasicEnergyContainer.output(MachineEnergyContainer.validateBlock(this).getStorage(), listener),getEnergySides());
        return builder.build();
    }

    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[]{RelativeSide.FRONT,RelativeSide.BACK,RelativeSide.BOTTOM,RelativeSide.TOP,RelativeSide.RIGHT,RelativeSide.LEFT};
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        lastSteamInput = newSteamInput;
        newSteamInput = 0;
        long stored = gasTank.getStored();
        double flowRate = 0;
        if (!gasTank.isEmpty() && MekanismUtils.canFunction(this)) {
            setActive(true);
            Set<Direction> emitDirections = EnumSet.noneOf(Direction.class);
            Direction direction = getDirection();
            for (RelativeSide energySide : getEnergySides()) {
                emitDirections.add(energySide.getDirection(direction));
            }
            CableUtils.emit(emitDirections, energyContainer, this, maxoutput);
            FloatingLong energyNeeded = energyContainer.getNeeded();
            if (stored > 0 && !energyNeeded.isZero()) {
                FloatingLong energyMultiplier = FloatingLong.create(CompactMekanismMachinesConfig.machines.turbineenergymultiply.getAsDouble());
                if (energyMultiplier.isZero()) {
                    clientFlow = 0;
                } else {
                    double rate = lowerVolume * (CompactMekanismMachinesConfig.machines.turbinevertualdispersers.get() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
                    rate = Math.min(rate, CompactMekanismMachinesConfig.machines.turbinevertualvents.get() * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
                    double proportion = stored / (double) getSteamCapacity();
                    rate = Math.min(Math.min(stored, rate), energyNeeded.divide(energyMultiplier).doubleValue()) * proportion*100000;
                    clientFlow = MathUtils.clampToLong(rate);
                    if (clientFlow > 0) {
                        energyContainer.insert(energyMultiplier.multiply(rate), Action.EXECUTE, AutomationType.INTERNAL);
                        gasTank.shrinkStack(clientFlow, Action.EXECUTE);
                        ventTank.setStack(new FluidStack(Fluids.WATER, Math.min(MathUtils.clampToInt(rate), CompactMekanismMachinesConfig.machines.turbinevertualcondensors.get()* MekanismGeneratorsConfig.generators.condenserRate.get())));
                    }
                }
            } else {
                clientFlow = 0;
            }

            if (dumpMode != TileEntityChemicalTank.GasMode.IDLE && !gasTank.isEmpty()) {
                long amount = gasTank.getStored();
                if (dumpMode == TileEntityChemicalTank.GasMode.DUMPING) {
                    gasTank.shrinkStack(getDumpingAmount(amount), Action.EXECUTE);
                } else {//DUMPING_EXCESS
                    //Don't allow dumping more than the configured amount
                    long targetLevel = MathUtils.clampToLong(gasTank.getCapacity() * MekanismConfig.general.dumpExcessKeepRatio.get());
                    if (targetLevel < amount) {
                        gasTank.shrinkStack(Math.min(amount - targetLevel, getDumpingAmount(amount)), Action.EXECUTE);
                    }
                }
            }

        }

    }

    //Methods relating to IComputerTile
    //End methods IComputerTile

    //Implementation of gas tank that on no longer being empty updates the output rate of this generator
    private class GasTank extends VariableCapacityGasTank {

        protected GasTank(@Nullable IContentsListener listener) {
            super(CompactMekanismMachinesConfig.machines.turbinegascapacity,ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi,
                    gas -> gas == MekanismGases.STEAM.getChemical(),null,listener);
        }

    }

    private  class FluidTank extends VariableCapacityFluidTank{
        protected FluidTank(@Nullable IContentsListener listener){
            super(CompactMekanismMachinesConfig.machines.turbinefluidcapacity,
                    (stack, automationType) -> true,
                    (stack, automationType) -> automationType != AutomationType.EXTERNAL,
                    fluid -> MekanismTags.Fluids.WATER_LOOKUP.contains(fluid.getFluid()),null);
        }
    }

    public long getSteamCapacity() {
        return lowerVolume * 64000L;
    }

    @ComputerMethod(nameOverride = "setDumpingMode")
    public void setDumpMode(TileEntityChemicalTank.GasMode mode) {
        if (dumpMode != mode) {
            dumpMode = mode;
        }
    }

    @ComputerMethod
    void incrementDumpingMode() {
        setDumpMode(dumpMode.getNext());
    }

    @ComputerMethod
    void decrementDumpingMode() {
        setDumpMode(dumpMode.getPrevious());
    }

    private long getDumpingAmount(long stored) {
        return Math.min(stored, Math.max(stored / 50, lastSteamInput * 2));
    }

    @ComputerMethod
    public long getMaxFlowRate() {
        double rate = lowerVolume * (CompactMekanismMachinesConfig.machines.turbinevertualdispersers.get() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, CompactMekanismMachinesConfig.machines.turbinevertualvents.get() * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return MathUtils.clampToLong(rate);
    }

    @ComputerMethod
    public FloatingLong getMaxProduction() {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(CompactMekanismMachinesConfig.machines.turbinevertualblades.get());
        double rate = lowerVolume * (CompactMekanismMachinesConfig.machines.turbinevertualdispersers.get() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, CompactMekanismMachinesConfig.machines.turbinevertualvents.get() * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return energyMultiplier.multiply(rate);
    }

    @ComputerMethod
    public FloatingLong getProductionRate() {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(CompactMekanismMachinesConfig.machines.turbinevertualblades.get());
        return energyMultiplier.multiply(clientFlow);
    }

    public BasicEnergyContainer getEnergyContainer() {
        return energyContainer;
    }
}
