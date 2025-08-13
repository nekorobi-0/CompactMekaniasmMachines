package com.CompactMekanismMachines.common;


import com.CompactMekanismMachines.common.config.CompactMekanismMachinesConfig;
import com.CompactMekanismMachines.common.registries.CompactBlocks;
import com.CompactMekanismMachines.common.registries.CompactContainerTypes;
import com.CompactMekanismMachines.common.registries.CompactCreativeTabs;
import com.CompactMekanismMachines.common.registries.CompactTileEntityTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
@Mod(CompactMekanismMachines.MODID)
public class CompactMekanismMachines {

    public static final String MODID = "compactmekanismmachines";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CompactMekanismMachines()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        CompactMekanismMachinesConfig.registerConfigs(ModLoadingContext.get());

        CompactBlocks.BLOCKS.register(modEventBus);
        CompactCreativeTabs.CREATIVE_TABS.register(modEventBus);
        CompactContainerTypes.CONTAINER_TYPES.register(modEventBus);
        CompactTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    public static ResourceLocation rl(String path) {
        return new ResourceLocation(CompactMekanismMachines.MODID, path);
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

}
