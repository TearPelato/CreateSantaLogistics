package net.liukrast.santa.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.liukrast.santa.registry.SantaPartialModels;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FrostburnCoreRenderer extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), LightTexture.FULL_BRIGHT);
        float worldTime = AnimationTickHolder.getRenderTime()/20;
        ms.pushPose();
        ms.mulPose(Axis.YP.rotation(Mth.sin(worldTime)*2));
        ms.mulPose(Axis.XP.rotation(Mth.sin(worldTime)*2));
        ms.mulPose(Axis.ZP.rotation(Mth.sin(worldTime)*2));
        renderer.renderGlowing(SantaPartialModels.FROSTBURN_CORE.get(), LightTexture.FULL_BRIGHT);
        ms.popPose();
        ms.pushPose();
        ms.mulPose(Axis.XP.rotation(Mth.cos(worldTime*5)*0.1f));
        ms.mulPose(Axis.ZP.rotation(Mth.sin(worldTime*5)*0.1f));
        renderer.renderGlowing(SantaPartialModels.FROSTBURN_CORE_RING.get(), LightTexture.FULL_BRIGHT);
        ms.popPose();
    }
}
