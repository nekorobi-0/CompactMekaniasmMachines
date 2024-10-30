package com.CompactMekanismMachines.client;

import com.CompactMekanismMachines.client.gui.CompressedWindGenerator.*;
import com.CompactMekanismMachines.client.gui.GuiCompactFissionReactor;
import com.CompactMekanismMachines.client.gui.GuiCompactIndustrialTurbine;
import com.CompactMekanismMachines.client.render.CompressedWindGenerator.*;
import com.CompactMekanismMachines.common.CompactMekanismMachines;
import com.CompactMekanismMachines.common.registries.CompactContainerTypes;
import com.CompactMekanismMachines.common.registries.CompactTileEntityTypes;
import mekanism.client.ClientRegistrationUtil;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompactMekanismMachines.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompactClientRegistration {
    private CompactClientRegistration(){}
    ;
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X2.get(), RenderCompressedWindGenerator_x2::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X8.get(), RenderCompressedWindGenerator_x8::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X32.get(), RenderCompressedWindGenerator_x32::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X128.get(), RenderCompressedWindGenerator_x128::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X512.get(), RenderCompressedWindGenerator_x512::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X2048.get(), RenderCompressedWindGenerator_x2048::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X8192.get(), RenderCompressedWindGenerator_x8192::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X32768.get(), RenderCompressedWindGenerator_x32768::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X131072.get(), RenderCompressedWindGenerator_x131072::new);
        event.registerBlockEntityRenderer(CompactTileEntityTypes.WIND_GENERATOR_X532480.get(), RenderCompressedWindGenerator_x532480::new);

    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.COMPACT_FISSION_REACTOR, GuiCompactFissionReactor::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.COMPACT_INDUSTRIAL_TURBINE, GuiCompactIndustrialTurbine::new);



            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X2, GuiCompressedWindGenerator_x2::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X8, GuiCompressedWindGenerator_x8::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X32, GuiCompressedWindGenerator_x32::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X128, GuiCompressedWindGenerator_x128::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X512, GuiCompressedWindGenerator_x512::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X2048, GuiCompressedWindGenerator_x2048::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X8192, GuiCompressedWindGenerator_x8192::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X32768,GuiCompressedWindGenerator_x32768::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X131072, GuiCompressedWindGenerator_x131072::new);
            ClientRegistrationUtil.registerScreen(CompactContainerTypes.WIND_GENERATOR_X532480, GuiCompressedWindGenerator_x532480::new);
    }
}
