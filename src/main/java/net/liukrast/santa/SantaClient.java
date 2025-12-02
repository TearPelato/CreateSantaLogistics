package net.liukrast.santa;

import com.mojang.blaze3d.vertex.PoseStack;
import net.liukrast.santa.client.gui.screens.SantaDockScreen;
import net.liukrast.santa.client.gui.screens.SantaScreen;
import net.liukrast.santa.client.renderer.block.SantaDoorBlockEntityRenderer;
import net.liukrast.santa.client.renderer.entity.SleighRenderer;
import net.liukrast.santa.registry.SantaBlockEntityTypes;
import net.liukrast.santa.registry.SantaEntityTypes;
import net.liukrast.santa.registry.SantaMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
@Mod(value = SantaConstants.MOD_ID, dist = Dist.CLIENT)
public class SantaClient {

    public SantaClient(IEventBus eventBus, ModContainer container) {
        eventBus.register(this);
        eventBus.addListener(SantaEntityTypes::registerLayerDefinitions);
        eventBus.addListener(SantaEntityTypes::registerRenderers);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(this::renderLevelStage);
        NeoForge.EVENT_BUS.addListener(this::clientTick);
    }

    @SubscribeEvent
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SantaBlockEntityTypes.SANTA_DOOR.get(), SantaDoorBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SantaMenuTypes.SANTA_DOCK.get(), SantaDockScreen::new);
        event.register(SantaMenuTypes.SANTA_MENU.get(), SantaScreen::new);
    }

    public void clientTick(ClientTickEvent.Pre event) {
        if((SleighRenderer.DOCKS != null || SleighRenderer.ORIGIN != null) && Minecraft.getInstance().level == null) {
            SleighRenderer.DOCKS = null;
            SleighRenderer.ORIGIN = null;
        }
    }

    public void renderLevelStage(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        Vec3 cameraPos = event.getCamera().getPosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0, -1.625, 0);
        MultiBufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
        var level = Minecraft.getInstance().level;
        assert level != null;
        SleighRenderer.render(level.dayTime() % 24000 + event.getPartialTick().getGameTimeDeltaPartialTick(true), poseStack, source);
        poseStack.popPose();
    }
}
