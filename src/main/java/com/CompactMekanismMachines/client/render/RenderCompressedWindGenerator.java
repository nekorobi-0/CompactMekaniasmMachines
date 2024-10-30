package com.CompactMekanismMachines.client.render;

import com.CompactMekanismMachines.common.tile.TileEntityCompressedWindGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.generators.client.model.ModelWindGenerator;
import mekanism.generators.common.GeneratorsProfilerConstants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class RenderCompressedWindGenerator<TILE extends TileEntityCompressedWindGenerator> extends MekanismTileEntityRenderer<TILE> implements IWireFrameRenderer{
    private final ModelWindGenerator model;
    public RenderCompressedWindGenerator(BlockEntityRendererProvider.Context context) {
        super(context);
        model = new ModelWindGenerator(context.getModelSet());
    }


    @Override
    protected void render(TILE tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        double angle = performTranslationsAndGetAngle(tile, partialTick, matrix);
        model.render(matrix, renderer, angle, light, overlayLight, false);
        matrix.popPose();
    }

    @Override
    protected String getProfilerSection() {
        return GeneratorsProfilerConstants.WIND_GENERATOR;
    }

    @Override
    public boolean shouldRenderOffScreen(TILE tile) {
        return true;
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, PoseStack matrix, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        if (tile instanceof TileEntityCompressedWindGenerator windGenerator) {
            double angle = performTranslationsAndGetAngle((TILE) windGenerator, partialTick, matrix);
            model.renderWireFrame(matrix, buffer, angle, red, green, blue, alpha);
            matrix.popPose();
        }
    }

    /**
     * Make sure to call {@link PoseStack#popPose()} afterwards
     */
    private double performTranslationsAndGetAngle(TILE tile, float partialTick, PoseStack matrix) {
        matrix.pushPose();
        matrix.translate(0.5, 1.5, 0.5);
        MekanismRenderer.rotate(matrix, tile.getDirection(), 0, 180, 90, 270);
        matrix.mulPose(Vector3f.ZP.rotationDegrees(180));
        double angle = tile.getAngle();
        if (tile.getActive()) {
            angle = (tile.getAngle() + ((tile.getBlockPos().getY() + 4F) / TILE.SPEED_SCALED) * partialTick) % 360;
        }
        return angle;
    }
}
