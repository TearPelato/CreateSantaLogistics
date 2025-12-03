package net.liukrast.santa.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class CogDrivenCourierModel extends Model {
    private final ModelPart body;

    public CogDrivenCourierModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.body = root.getChild("body");
        this.body.getChild("tail");
        ModelPart back_left = this.body.getChild("back_left");
        ModelPart back_left_leg = back_left.getChild("back_left_leg");
        back_left_leg.getChild("back_left_foot");
        ModelPart back_right = this.body.getChild("back_right");
        ModelPart back_right_leg = back_right.getChild("back_right_leg");
        back_right_leg.getChild("back_right_foot");
        ModelPart neck = this.body.getChild("neck");
        neck.getChild("head");
        ModelPart front_left = this.body.getChild("front_left");
        ModelPart front_left_leg = front_left.getChild("front_left_leg");
        front_left_leg.getChild("front_left_foot");
        ModelPart front_right = this.body.getChild("front_right");
        ModelPart front_right_leg = front_right.getChild("front_right_leg");
        front_right_leg.getChild("front_right_foot");
        this.body.getChild("engine");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 7).addBox(-4.5F, -8.0F, -17.0F, 9.0F, 9.0F, 22.0F, new
                        CubeDeformation(0.05F))
                .texOffs(72, 0).addBox(-3.0F, -10.0F, -19.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 63).addBox(-4.0F, -7.0F, 1.0F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 6.0F));

        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(43, 48).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 66).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 4.0F, 0.4363F, 0.0F, 0.0F));

        PartDefinition back_left = body.addOrReplaceChild("back_left", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -2.0F, 3.0F));

        PartDefinition back_left_leg = back_left.addOrReplaceChild("back_left_leg", CubeListBuilder.create().texOffs(0, 36).addBox(-3.0F, 0.0F, -2.0F, 5.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        back_left_leg.addOrReplaceChild("back_left_foot", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(18, 37).addBox(-3.5F, -3.0F, -5.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(34, 38).addBox(-3.0F, 1.0F, -10.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(54, 38).addBox(-1.5F, -6.0F, -6.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition back_right = body.addOrReplaceChild("back_right", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, -2.0F, 3.0F));

        PartDefinition back_right_leg = back_right.addOrReplaceChild("back_right_leg", CubeListBuilder.create().texOffs(0, 36).mirror().addBox(-2.0F, 0.0F, -2.0F, 5.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        back_right_leg.addOrReplaceChild("back_right_foot", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(18, 37).mirror().addBox(-2.5F, -3.0F, -5.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(34, 38).mirror().addBox(-2.0F, 1.0F, -10.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(54, 38).mirror().addBox(-0.5F, -6.0F, -6.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(32, 12).addBox(-1.5F, -10.0F, -1.0F, 3.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(18, 43).addBox(0.0F, -10.0F, -3.0F, 0.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -17.0F, 0.5672F, 0.0F, 0.0F));

        neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 12).addBox(-3.5F, -2.0F, -6.0F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 8).addBox(3.5F, -14.0F, -5.0F, 0.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 8).mirror().addBox(-3.5F, -14.0F, -5.0F, 0.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(22, 0).addBox(-2.5F, -1.0F, -10.0F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(26, 72).addBox(2.0F, -4.0F, 1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 72).mirror().addBox(-4.0F, -4.0F, 1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -9.0F, 2.0F, -0.5672F, 0.0F, 0.0F));

        PartDefinition front_left = body.addOrReplaceChild("front_left", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -2.0F, -14.0F));

        PartDefinition front_left_leg = front_left.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(0, 36).addBox(-3.0F, 0.0F, -2.0F, 5.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        front_left_leg.addOrReplaceChild("front_left_foot", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(18, 37).addBox(-3.5F, -3.0F, -5.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(34, 38).addBox(-3.0F, 1.0F, -10.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(54, 38).addBox(-1.5F, -6.0F, -6.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition front_right = body.addOrReplaceChild("front_right", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, -2.0F, -14.0F));

        PartDefinition front_right_leg = front_right.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(0, 36).mirror().addBox(-2.0F, 0.0F, -2.0F, 5.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        front_right_leg.addOrReplaceChild("front_right_foot", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(18, 37).mirror().addBox(-2.5F, -3.0F, -5.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(34, 38).mirror().addBox(-2.0F, 1.0F, -10.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(54, 38).mirror().addBox(-0.5F, -6.0F, -6.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        body.addOrReplaceChild("engine", CubeListBuilder.create().texOffs(9, 46).addBox(-3.5F, -6.0F, -12.0F, 6.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public void setupAnim(float pitch, float yaw) {
        body.yRot=yaw;
        body.xRot = pitch;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
