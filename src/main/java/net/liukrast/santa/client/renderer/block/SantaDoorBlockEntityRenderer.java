package net.liukrast.santa.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.liukrast.santa.SantaConstants;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SantaDoorBlockEntityRenderer implements BlockEntityRenderer<SantaDoorBlockEntity> {
    private static final ResourceLocation TEXTURE = SantaConstants.id("textures/entity/santa_door.png");
    private final ModelPart body;
    private final ModelPart lock_1;
    private final ModelPart lock_2;
    private final ModelPart lock_3;
    private final ModelPart lock_4;
    private final ModelPart lock_5;
    private final ModelPart lock_6;
    private final ModelPart large_cog;
    private final ModelPart small_cog;

    public SantaDoorBlockEntityRenderer(BlockEntityRendererProvider.Context ignored) {
        ModelPart root = createBodyLayer().bakeRoot();
        this.body = root.getChild("body");
        this.lock_1 = this.body.getChild("lock_1");
        this.lock_2 = this.body.getChild("lock_2");
        this.lock_3 = this.body.getChild("lock_3");
        this.lock_4 = this.body.getChild("lock_4");
        this.lock_5 = this.body.getChild("lock_5");
        this.lock_6 = this.body.getChild("lock_6");
        this.large_cog = this.body.getChild("large_cog");
        this.small_cog = this.body.getChild("small_cog");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -64.0F, 0.0F, 64.0F, 64.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(140, 22).addBox(-1.0F, -10.0F, -1.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(140, 22).addBox(-1.0F, -26.0F, -1.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(140, 22).addBox(-1.0F, -42.0F, -1.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(140, 22).addBox(-1.0F, -58.0F, -1.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 24.0F, -3.0F));

        body.addOrReplaceChild("plant", CubeListBuilder.create().texOffs(10, 72).addBox(-16.0F, 0.0F, -6.0F, 30.0F, 30.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 108).addBox(-9.0F, 7.0F, -6.0F, 16.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(97, 72).addBox(-16.0F, 0.0F, -6.0F, 30.0F, 30.0F, 6.0F, new CubeDeformation(0.5F))
                .texOffs(10, 72).addBox(-16.0F, 0.0F, 6.0F, 30.0F, 30.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 108).addBox(-9.0F, 7.0F, 6.0F, 16.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(97, 72).addBox(-16.0F, 0.0F, 6.0F, 30.0F, 30.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(33.0F, -47.0F, 0.0F));

        body.addOrReplaceChild("lock_1", CubeListBuilder.create().texOffs(140, 11).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(140, 0).addBox(-2.0F, -3.5F, -2.0F, 15.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -47.5F, -2.0F));

        body.addOrReplaceChild("lock_2", CubeListBuilder.create().texOffs(140, 11).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(140, 0).addBox(-2.0F, -3.5F, -2.0F, 15.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -32.0F, -2.0F));

        body.addOrReplaceChild("lock_3", CubeListBuilder.create().texOffs(140, 11).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(140, 0).addBox(-2.0F, -3.5F, -2.0F, 15.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -16.5F, -2.0F));

        body.addOrReplaceChild("lock_4", CubeListBuilder.create().texOffs(140, 11).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(140, 0).addBox(-2.0F, -3.5F, -2.0F, 15.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -47.5F, 8.0F));

        body.addOrReplaceChild("lock_5", CubeListBuilder.create().texOffs(140, 11).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(140, 0).addBox(-2.0F, -3.5F, -2.0F, 15.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -32.0F, 8.0F));

        body.addOrReplaceChild("lock_6", CubeListBuilder.create().texOffs(140, 11).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(140, 0).addBox(-2.0F, -3.5F, -2.0F, 15.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -16.5F, 8.0F));

        body.addOrReplaceChild("large_cog", CubeListBuilder.create().texOffs(178, 22).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(31.0F, -31.0F, 0.0F));

        body.addOrReplaceChild("small_cog", CubeListBuilder.create().texOffs(182, 38).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(34.5F, -34.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void render(SantaDoorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var facing = blockEntity.getBlockState().getValue(SantaDoorBlock.FACING);
        int flag = blockEntity.getBlockState().getValue(SantaDoorBlock.OPEN) ? 1 : 0;
        poseStack.scale(-1, -1, 1);
        poseStack.translate(facing.getStepZ() == 0 ? -0.5 : facing.getStepZ() < 0 ? -1 : 0, -1.5, facing.getStepX() == 0 ? 0.5 : facing.getStepX() > 0 ? 1 : 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 + facing.toYRot()));
        poseStack.translate(0.5, 0, 0);

        int speed = 50;
        blockEntity.progress = blockEntity.progress < 0 ? flag : (blockEntity.progress*(speed-1)+flag)/speed;
        body.yRot = blockEntity.progress * (Mth.HALF_PI-0.3f);
        lock_1.yRot = blockEntity.progress;
        lock_2.yRot = blockEntity.progress*1.5f;
        lock_3.yRot = blockEntity.progress*2;
        lock_4.yRot = -blockEntity.progress;
        lock_5.yRot = -blockEntity.progress*1.5f;
        lock_6.yRot = -blockEntity.progress*2;
        assert blockEntity.getLevel() != null;
        float cogSpeed = (blockEntity.getLevel().getGameTime() + partialTick)/2;
        small_cog.zRot = cogSpeed;
        large_cog.zRot = -cogSpeed*0.75f;

        var vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    @Override
    public AABB getRenderBoundingBox(SantaDoorBlockEntity blockEntity) {
        var state = blockEntity.getBlockState();
        var dir = state.getValue(SantaDoorBlock.FACING).getCounterClockWise();
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).expandTowards(new Vec3(dir.getStepX()*3, 3, dir.getStepZ()*3));
    }
}
