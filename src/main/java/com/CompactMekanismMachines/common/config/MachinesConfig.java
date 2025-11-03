package com.CompactMekanismMachines.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.*;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class MachinesConfig extends BaseMekanismConfig {
    private final ForgeConfigSpec configSpec;
    public final CachedLongValue cfrFuelTankCapacity;
    public final CachedLongValue cfrCoolantGasTankCapacity;
    public final CachedIntValue  cfrCoolantFluidTankCapacity;
    public final CachedLongValue cfrHeatedCoolantTankCapacity;
    public final CachedDoubleValue cfrHeatTankCpacity;
    public final CachedLongValue cfrWasteTankCapacity;
    public final CachedLongValue cfrBurnRate;
    public final CachedLongValue turbineenergy;
    public final CachedLongValue turbinegascapacity;
    public final CachedIntValue turbinefluidcapacity;
    public final CachedLongValue turbineenergycapacity;
    public final CachedIntValue turbinevertualcondensors;
    public final CachedIntValue turbinevertualdispersers;
    public final CachedIntValue turbinevertualvents;
    public final CachedIntValue turbinevertualblades;
    public final CachedIntValue turbinevertuallowervolume;
    public final CachedIntValue evaporationheight;

    MachinesConfig(){
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("CompactFissionReactor Settings").push("compactfissionreactor");
        this.cfrFuelTankCapacity = CachedLongValue.wrap(this,         builder.comment("The capacity in mB of the gas tank of fuel in the Compact Fission Reactor").defineInRange("tankCapacity", 18000000L, 1L, Long.MAX_VALUE));
        this.cfrCoolantGasTankCapacity = CachedLongValue.wrap(this,      builder.comment("The capacity in mB of the gas coolant tank of fuel in the Compact Fission Reactor").defineInRange("GascoolanttankCapacity", 18000000000L, 1L, Long.MAX_VALUE));
        this.cfrCoolantFluidTankCapacity = CachedIntValue.wrap(this,      builder.comment("The capacity in mB of the fluid coolant tank of fuel in the Compact Fission Reactor").defineInRange("FluidcoolanttankCapacity", Integer.MAX_VALUE, 1, Integer.MAX_VALUE));
        this.cfrHeatedCoolantTankCapacity = CachedLongValue.wrap(this,builder.comment("The capacity in mB of the heated coolant tank of fuel in the Compact Fission Reactor").defineInRange("HeatedcoolanttankCapacity", 18000000000L, 1L, Long.MAX_VALUE));
        this.cfrWasteTankCapacity = CachedLongValue.wrap(this,builder.comment("The capacity in mB of the waste tank of fuel in the Compact Fission Reactor").defineInRange("tankCapacity", 18000000L, 1L, Long.MAX_VALUE));
        this.cfrBurnRate = CachedLongValue.wrap(this,                 builder.comment("Max fuel cosume per tick of Compact Fission Reactor").defineInRange("burnrate",1920,1,Long.MAX_VALUE));
        this.cfrHeatTankCpacity = CachedDoubleValue.wrap(this, builder.comment("The heat capacity of Compact Fission Reactor")
                .defineInRange("HeatCapacity", 1_000_000D, 1, Double.MAX_VALUE));
        builder.pop();
        builder.comment("CompactFissionReactor Settings").push("compactturbine");
        //TODO: OutputEnergy Limiting.
        this.turbineenergy = CachedLongValue.wrap(this,builder.comment("UNUSED!: Max Output of Compact Industrial Turbine", "TODO: Output Energy Limiting.").defineInRange("maxoutput",2000000000000L,1L,Long.MAX_VALUE));
        this.turbinegascapacity = CachedLongValue.wrap(this,builder.comment("Gas Tank Capacity  of Compact Industrial Turbine").defineInRange("gastankcapacity",2000000000000L,1L,Long.MAX_VALUE));
        this.turbineenergycapacity = CachedLongValue.wrap(this,builder.comment("Energy Capacity of Compact Industrial Turbine").defineInRange("energycapacity",2000000000000L,0L,Long.MAX_VALUE));
        this.turbinefluidcapacity = CachedIntValue.wrap(this,builder.comment("Fluid Tank Capacity of Compact Industrial Turbine").defineInRange("fluidtankcapacity",Integer.MAX_VALUE,0,Integer.MAX_VALUE));
        this.turbinevertualcondensors = CachedIntValue.wrap(this, builder.comment("amount of virtual turbine condenser block").defineInRange("virtualcondenser",500,0,Integer.MAX_VALUE));
        this.turbinevertualdispersers = CachedIntValue.wrap(this, builder.comment("amount of virtual turbine disperser block").defineInRange("virtualvent",500,0,Integer.MAX_VALUE));
        this.turbinevertualvents = CachedIntValue.wrap(this,builder.comment("amount of virtual turbine vent block").defineInRange("upvolume",800,0,Integer.MAX_VALUE));
        this.turbinevertualblades = CachedIntValue.wrap(this,builder.comment("amount of virtual blade").defineInRange("blades",200,0,Integer.MAX_VALUE));
        this.turbinevertuallowervolume = CachedIntValue.wrap(this,builder.comment("vertual volume of lower section").defineInRange("lowervolume",500,0,Integer.MAX_VALUE));

        this.evaporationheight = CachedIntValue.wrap(this,builder.comment("virtual height of compact thermal evaporation block").defineInRange("virtual_height",36,1,Integer.MAX_VALUE));

        this.configSpec = builder.build();
    }
    public String getFileName() {
        return "compactmachines";
    }

    public ForgeConfigSpec getConfigSpec() {
        return this.configSpec;
    }

    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }
}
