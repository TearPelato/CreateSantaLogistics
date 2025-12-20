package net.liukrast.santa.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.liukrast.santa.registry.SantaPartialModels;
import net.liukrast.santa.world.level.block.entity.FrostburnEngineBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class FrostburnEngineRenderer extends KineticBlockEntityRenderer<FrostburnEngineBlockEntity> {
    public FrostburnEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FrostburnEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer vb = buffer.getBuffer(RenderTypes.itemGlowingTranslucent());
        ms.pushPose();
        ms.translate(0, 1.5, 0);
        float temp = (be.getTemperature())/5000f + 1;

        float worldTime = AnimationTickHolder.getRenderTime()/20;
        ms.pushPose();
        CachedBuffers.partial(SantaPartialModels.FROSTBURN_CORE, be.getBlockState())
                .rotateCentered(Axis.XP.rotation(Mth.sin(worldTime*temp)*2))
                .rotateCentered(Axis.YP.rotation(Mth.sin(worldTime*temp)*2))
                .rotateCentered(Axis.ZP.rotation(Mth.sin(worldTime*temp)*2))
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, vb);
        ms.popPose();
        ms.pushPose();
        CachedBuffers.partial(SantaPartialModels.FROSTBURN_CORE_RING, be.getBlockState())
                .rotateCentered(Axis.XP.rotation(Mth.cos(worldTime*5*temp)*0.1f))
                .rotateCentered(Axis.ZP.rotation(Mth.sin(worldTime*5*temp)*0.1f))
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, vb);
        ms.popPose();
        ms.popPose();


        if (VisualizationManager.supportsVisualization(be.getLevel())) return;

        Direction direction = Direction.UP;


        assert be.getLevel() != null;
        int lightBehind = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction.getOpposite()));

        SuperByteBuffer shaftHalf =
                CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), direction.getOpposite());

        standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull FrostburnEngineBlockEntity blockEntity) {
        return super.getRenderBoundingBox(blockEntity).inflate(2, 0, 2).expandTowards(0, 2, 0);
    }
}
