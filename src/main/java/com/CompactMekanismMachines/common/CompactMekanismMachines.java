package com.CompactMekanismMachines.common;


import com.CompactMekanismMachines.common.config.CompactMekanismMachinesConfig;
import com.CompactMekanismMachines.common.registries.CompactBlocks;
import com.CompactMekanismMachines.common.registries.CompactContainerTypes;
import com.CompactMekanismMachines.common.registries.CompactTileEntityTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
@Mod(CompactMekanismMachines.MODID)
public class CompactMekanismMachines {

    public static final String MODID = "compactmekanismmachines";
    // Define mod id in a common place for everything to reference
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace


    public CompactMekanismMachines()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        CompactMekanismMachinesConfig.registerConfigs(ModLoadingContext.get());

        CompactBlocks.BLOCKS.register(modEventBus);
        CompactContainerTypes.CONTAINER_TYPES.register(modEventBus);
        CompactTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
    }
    public static ResourceLocation rl(String path) {
        return new ResourceLocation(CompactMekanismMachines.MODID, path);
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

    }

}
