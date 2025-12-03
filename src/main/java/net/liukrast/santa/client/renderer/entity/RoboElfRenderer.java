package net.liukrast.santa.client.renderer.entity;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.client.model.RoboElfModel;
import net.liukrast.santa.world.entity.RoboElf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class RoboElfRenderer extends MobRenderer<RoboElf, RoboElfModel> {
    private static final ResourceLocation TEXTURE = SantaConstants.id("textures/entity/robo_elf.png");

    public RoboElfRenderer(EntityRendererProvider.Context context) {
        super(context, new RoboElfModel(context.bakeLayer(RoboElfModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(RoboElf entity) {
        return TEXTURE;
    }
}
