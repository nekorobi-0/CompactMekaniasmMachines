package com.CompactMekanismMachines.common.registries;

import com.CompactMekanismMachines.common.CompactLang;
import com.CompactMekanismMachines.common.config.CompactMekanismMachinesConfig;
import com.CompactMekanismMachines.common.content.blocktype.BlockShapes;
import com.CompactMekanismMachines.common.tile.CompressedWindGenerator.*;
import com.CompactMekanismMachines.common.tile.TileEntityCompactFissionReactor;
import com.CompactMekanismMachines.common.tile.TileEntityCompactIndustrialTurbine;
import mekanism.api.Upgrade;
import mekanism.api.math.FloatingLong;
import mekanism.common.block.attribute.AttributeCustomSelectionBox;
import mekanism.common.block.attribute.Attributes;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.registries.GeneratorsContainerTypes;
import mekanism.generators.common.registries.GeneratorsSounds;

import java.util.EnumSet;

public class CompactBlockTypes {

    private CompactBlockTypes(){}

    public static final Generator<TileEntityCompactFissionReactor> COMPACT_FISSION_REACTOR = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.COMPACT_FISSION_REACTOR, GeneratorsLang.DESCRIPTION_GAS_BURNING_GENERATOR)
            .withGui(() -> CompactContainerTypes.COMPACT_FISSION_REACTOR)
            .withEnergyConfig(()-> FloatingLong.parseFloatingLong(String.valueOf(CompactMekanismMachinesConfig.machines.cfrEnergyCapacity.get())))
            .withCustomShape(BlockShapes.COMPACT_FISSION_REACTOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withSound(GeneratorsSounds.FISSION_REACTOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withComputerSupport("compactfissionreactor")
            .replace(Attributes.ACTIVE_LIGHT)
            .build();

    public static final Generator<TileEntityCompactIndustrialTurbine> COMPACT_INDUSTRIAL_TURBINE = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.COMPACT_INDUSTRIAL_TURBINE, GeneratorsLang.DESCRIPTION_GAS_BURNING_GENERATOR)
            .withGui(() -> CompactContainerTypes.COMPACT_INDUSTRIAL_TURBINE)
            .withEnergyConfig(()-> FloatingLong.parseFloatingLong(String.valueOf(CompactMekanismMachinesConfig.machines.cfrEnergyCapacity.get())))
            .withCustomShape(BlockShapes.COMPACT_FISSION_REACTOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withSound(GeneratorsSounds.FISSION_REACTOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withComputerSupport("compactindustrialturbine")
            .replace(Attributes.ACTIVE_LIGHT)
            .build();




    public static final Generator<TileEntityCompressedWindGenerator_x2> WIND_GENERATOR_X2 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X2, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X2)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(2))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x2")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x8> WIND_GENERATOR_X8 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X8, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X8)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(8))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x8")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x32> WIND_GENERATOR_X32 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X32, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X32)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(32))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x32")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x128> WIND_GENERATOR_X128 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X128, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X128)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(128))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x128")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x512> WIND_GENERATOR_X512 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X512, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X512)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(512))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x512")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x2048> WIND_GENERATOR_X2048 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X2048, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X2048)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(2048))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x2048")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x8192> WIND_GENERATOR_X8192 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X8192, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X8192)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(8192))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x8192")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x32768> WIND_GENERATOR_X32768 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X32768, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X32768)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(32768))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x32768")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x131072> WIND_GENERATOR_X131072 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X131072, CompactLang.DESCRIPTION_COMPRESSED_WIND_GENERATOR_x131072)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X131072)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(131072))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x131072")
            .build();
    public static final Generator<TileEntityCompressedWindGenerator_x532480> WIND_GENERATOR_X532480 = Generator.GeneratorBuilder
            .createGenerator(() -> CompactTileEntityTypes.WIND_GENERATOR_X532480, CompactLang.DESCRIPTION_COMPRESSED_WIND_GENERATOR_x532480)
            .withGui(() -> CompactContainerTypes.WIND_GENERATOR_X532480)
            .withEnergyConfig(()->MekanismGeneratorsConfig.storageConfig.windGenerator.get().multiply(532480))
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 4; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("compressed_wind_generator_x532480")
            .build();
}
