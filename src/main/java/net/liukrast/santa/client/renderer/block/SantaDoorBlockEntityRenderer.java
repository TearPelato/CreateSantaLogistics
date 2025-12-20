package net.liukrast.santa.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.client.model.IModel;
import net.liukrast.santa.client.model.SantaDoorModel;
import net.liukrast.santa.registry.SantaBlocks;
import net.liukrast.santa.world.level.block.SantaDoorBlock;
import net.liukrast.santa.world.level.block.entity.SantaDoorBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.NonnullDefault;

import java.util.Map;

@NonnullDefault
public class SantaDoorBlockEntityRenderer implements BlockEntityRenderer<SantaDoorBlockEntity> {
    private static final Map<Block, IModel> MODEL_MAP = Map.of(
            SantaBlocks.SANTA_DOOR.get(), new SantaDoorModel()
    );


    public SantaDoorBlockEntityRenderer(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(SantaDoorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var model = MODEL_MAP.get(blockEntity.getBlockState().getBlock());
        if(model == null) {
            SantaConstants.LOGGER.error("Unable to locate renderer for {}", blockEntity.getBlockState());
            return;
        }
        var facing = blockEntity.getBlockState().getValue(SantaDoorBlock.FACING);
        poseStack.scale(-1, -1, 1);
        poseStack.translate(facing.getStepZ() == 0 ? -0.5 : facing.getStepZ() < 0 ? -1 : 0, -1.5, facing.getStepX() == 0 ? 0.5 : facing.getStepX() > 0 ? 1 : 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 + facing.toYRot()));
        poseStack.translate(0.5, 0, 0);
        boolean flag = blockEntity.getBlockState().getValue(SantaDoorBlock.OPEN);

        long gameTime = blockEntity.getLevel() == null ? 0 : blockEntity.getLevel().getGameTime();
        int animDuration = 100;
        if(blockEntity.lastStateTime < 0) {
            blockEntity.lastState = flag;
            blockEntity.lastStateTime = 0;
        }
        if(blockEntity.lastState != flag) {
            blockEntity.lastStateTime = gameTime- animDuration + (Mth.clamp(gameTime - blockEntity.lastStateTime, 0, animDuration));
            blockEntity.lastState = flag;
        }
        float xProgress = Mth.clamp(Mth.clamp(gameTime - blockEntity.lastStateTime, 0, animDuration) + partialTick, 0, animDuration)/animDuration;
        float progress = (flag ? 1-Mth.cos(xProgress*Mth.PI) : 1+Mth.cos(xProgress*Mth.PI)) /2f;
        model.setupAnim(blockEntity, partialTick, progress);
        model.render(poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public AABB getRenderBoundingBox(SantaDoorBlockEntity blockEntity) {
        var state = blockEntity.getBlockState();
        var dir = state.getValue(SantaDoorBlock.FACING);
        var cl = dir.getCounterClockWise();
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity)
                .expandTowards(new Vec3(dir.getStepX()*3, 3, dir.getStepZ()*3))
                .expandTowards(new Vec3(cl.getStepX()*3, 0, cl.getStepZ()*3));
    }
}
