package com.CompactMekanismMachines.common.registries;

import com.CompactMekanismMachines.common.CompactMekanismMachines;
import com.CompactMekanismMachines.common.tile.CompressedWindGenerator.*;
import com.CompactMekanismMachines.common.tile.TileEntityCompactFissionReactor;
import com.CompactMekanismMachines.common.tile.TileEntityCompactIndustrialTurbine;
import com.CompactMekanismMachines.common.tile.TileEntityCompactThermalEvaporation;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class CompactContainerTypes {
    private CompactContainerTypes(){

    }

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(CompactMekanismMachines.MODID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompactFissionReactor>> COMPACT_FISSION_REACTOR = CONTAINER_TYPES.register(CompactBlocks.COMPACT_FISSION_REACTOR, TileEntityCompactFissionReactor.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompactIndustrialTurbine>> COMPACT_INDUSTRIAL_TURBINE = CONTAINER_TYPES.register(CompactBlocks.COMPACT_INDUSTRIAL_TURBINE, TileEntityCompactIndustrialTurbine.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompactThermalEvaporation>> COMPACT_THERMAL_EVAPORATION = CONTAINER_TYPES.register(CompactBlocks.COMPACT_THERMAL_EVAPORATION,TileEntityCompactThermalEvaporation.class);


    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x2>> WIND_GENERATOR_X2 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X2, TileEntityCompressedWindGenerator_x2.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x8>> WIND_GENERATOR_X8 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X8, TileEntityCompressedWindGenerator_x8.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x32>> WIND_GENERATOR_X32 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X32, TileEntityCompressedWindGenerator_x32.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x128>> WIND_GENERATOR_X128 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X128, TileEntityCompressedWindGenerator_x128.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x512>> WIND_GENERATOR_X512 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X512, TileEntityCompressedWindGenerator_x512.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x2048>> WIND_GENERATOR_X2048 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X2048, TileEntityCompressedWindGenerator_x2048.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x8192>> WIND_GENERATOR_X8192 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X8192, TileEntityCompressedWindGenerator_x8192.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x32768>> WIND_GENERATOR_X32768 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X32768, TileEntityCompressedWindGenerator_x32768.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x131072>> WIND_GENERATOR_X131072 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X131072, TileEntityCompressedWindGenerator_x131072.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCompressedWindGenerator_x532480>> WIND_GENERATOR_X532480 = CONTAINER_TYPES.register(CompactBlocks.WIND_GENERATOR_X532480, TileEntityCompressedWindGenerator_x532480.class);
}
