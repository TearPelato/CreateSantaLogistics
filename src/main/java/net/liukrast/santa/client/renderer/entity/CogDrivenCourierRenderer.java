package net.liukrast.santa.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.liukrast.santa.SantaLogisticsConstants;
import net.liukrast.santa.client.model.CogDrivenCourierModel;
import net.liukrast.santa.world.entity.CogDrivenCourier;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class CogDrivenCourierRenderer extends EntityRenderer<CogDrivenCourier> {
    private static final ResourceLocation TEXTURE = SantaLogisticsConstants.id("textures/entity/cog_driven_courier.png");
    protected CogDrivenCourierModel model;

    public CogDrivenCourierRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CogDrivenCourierModel(context.bakeLayer(CogDrivenCourierModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(CogDrivenCourier entity) {
        return TEXTURE;
    }

    @Override
    public void render(CogDrivenCourier entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0, -1.625, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F + entityYaw));
        this.model.setupAnim(entity, 0, 0, partialTick /*TODO*/, entityYaw, entity.getXRot());
        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
