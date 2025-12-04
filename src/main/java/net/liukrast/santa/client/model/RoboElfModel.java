package net.liukrast.santa.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.entity.RoboElf;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class RoboElfModel extends EntityModel<RoboElf> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SantaConstants.id("robo_elf"), "main");
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart body;
    private final ModelPart hat;
    private final ModelPart hat_1;
    private final ModelPart hat_2;
    private final ModelPart hat_3;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart wind;

    public RoboElfModel(ModelPart root) {
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
        this.body = root.getChild("body");
        this.hat = this.body.getChild("hat");
        this.hat_1 = this.hat.getChild("hat_1");
        this.hat_2 = this.hat_1.getChild("hat_2");
        this.hat_3 = this.hat_2.getChild("hat_3");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.wind = this.body.getChild("wind");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 63).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 63).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F))
                .texOffs(0, 75).addBox(-2.0F, 4.0F, -6.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 17.0F, 0.5F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 63).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(16, 63).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false)
                .texOffs(0, 75).mirror().addBox(-2.0F, 4.0F, -6.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 17.0F, 0.5F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(48, 8).addBox(-2.0F, -13.0F, -8.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition hat = body.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, 0.0F));

        PartDefinition hat_1 = hat.addOrReplaceChild("hat_1", CubeListBuilder.create().texOffs(0, 34).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition hat_2 = hat_1.addOrReplaceChild("hat_2", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, -2.0F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -6.0F, 0.0F));

        hat_2.addOrReplaceChild("hat_3", CubeListBuilder.create().texOffs(40, 40).addBox(0.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -2.0F, 0.0F));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 52).addBox(-2.0F, -2.0F, 0.0F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 49).addBox(-3.0F, 9.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 55).addBox(-3.0F, -2.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -13.0F, -1.0F));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(48, 52).mirror().addBox(1.0F, -2.0F, 0.0F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 49).mirror().addBox(0.0F, 9.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 55).mirror().addBox(0.0F, -2.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -13.0F, -1.0F));

        body.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(40, 16).addBox(0.0F, -6.0F, 0.0F, 0.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 5.0F));

        body.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(0, 49).addBox(-4.0F, -5.0F, 0.0F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -12.0F, -3.0F, 0.1828F, 0.346F, 0.0475F));

        body.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 49).mirror().addBox(-1.0F, -5.0F, 0.0F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, -12.0F, -3.0F, 0.1828F, -0.346F, -0.0475F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(RoboElf entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float animTime = limbSwing*1.75f;
        right_arm.zRot = 0.3f;
        right_arm.xRot = -Mth.sin(animTime)*limbSwingAmount;
        right_leg.xRot = -Mth.clamp(-Mth.cos(animTime),0,1)*limbSwingAmount;
        right_leg.z = Mth.cos(animTime)*limbSwingAmount*4f+0.5f;
        right_leg.y = 17-Mth.sin(animTime)*limbSwingAmount*2f;

        left_arm.zRot = -0.3f;
        left_arm.xRot = Mth.sin(animTime)*limbSwingAmount;
        left_leg.xRot = -Mth.clamp(Mth.cos(animTime)*limbSwingAmount,0,1);
        left_leg.z = -Mth.cos(animTime)*limbSwingAmount*4f+0.5f;
        left_leg.y = 17+Mth.sin(animTime)*limbSwingAmount*2f;

        body.y = 25-Mth.sin(animTime*2)*limbSwingAmount;

        body.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        hat.xRot = -0.2173615597F;
        hat.yRot = 0.0188652639f;
        hat.zRot = 0.0852087194f;

        hat_1.xRot = -0.0193190495f;
        hat_1.yRot = -0.2173231625f;
        hat_1.zRot = 0.0852087194f + Mth.sin(animTime*2)*0.05f;

        //xRot is just 0
        hat_2.yRot = -0.261799f;
        hat_2.zRot = 0.08520871941f + Mth.cos(animTime*2)*0.05f;

        //xRot is just 0
        hat_3.yRot = -0.3926991f;
        hat_3.zRot = 0.523599f;

        wind.zRot = limbSwing;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
