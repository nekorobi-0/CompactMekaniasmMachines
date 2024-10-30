package com.CompactMekanismMachines.client.gui;

import com.CompactMekanismMachines.common.tile.TileEntityCompactFissionReactor;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.generators.common.GeneratorsLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class GuiCompactFissionReactor extends GuiConfigurableTile<TileEntityCompactFissionReactor, MekanismTileContainer<TileEntityCompactFissionReactor>> {

    public GuiCompactFissionReactor(MekanismTileContainer<TileEntityCompactFissionReactor> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
        titleLabelY = 5;
        inventoryLabelY += 3;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiGasGauge(  () -> tile.heatedCoolantTank,()->tile.getGasTanks(null),  GaugeType.STANDARD,this,122,13));
        addRenderableWidget(new GuiGasGauge(  () -> tile.wasteTank        ,()->tile.getGasTanks(null),  GaugeType.STANDARD,this, 100,13));
        addRenderableWidget(new GuiGasGauge(  () -> tile.fuelTank         ,()->tile.getGasTanks(null),  GaugeType.STANDARD,this, 45,13));
        addRenderableWidget(new GuiFluidGauge(() -> tile.coolantFluidTank ,()->tile.getFluidTanks(null),GaugeType.STANDARD,this, 23,13));
        addRenderableWidget(new GuiGasGauge(  () -> tile.coolantGasTank   ,()->tile.getGasTanks(null),  GaugeType.STANDARD,this, 5,13));
        addRenderableWidget(new GuiHeatTab( this,  ()->{ Component enviroment = MekanismUtils.getTemperatureDisplay(tile.heatCapacitor.getTemperature(), UnitDisplayUtils.TemperatureUnit.KELVIN,false);
            return Collections.singletonList(MekanismLang.TEMPERATURE.translate(enviroment));
        }));
    }

    @Override
    protected void drawForegroundText(@NotNull PoseStack guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        drawString(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor());
        Component component = GeneratorsLang.GAS_BURN_RATE.translate(tile.getUsed());
        int left = inventoryLabelX + getStringWidth(playerInventoryTitle) + 4;
        int end = imageWidth - 8;
        left = Math.max(left, end - getStringWidth(component));
        drawTextScaledBound(guiGraphics, component, left, inventoryLabelY, titleTextColor(), end - left);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
