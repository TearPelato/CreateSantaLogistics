package net.liukrast.santa.registry;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.client.model.RoboElfModel;
import net.liukrast.santa.client.renderer.entity.RoboElfRenderer;
import net.liukrast.santa.world.entity.RoboElf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SantaEntityTypes {
    private SantaEntityTypes() {}

    private static final DeferredRegister<EntityType<?>> REGISTER = SantaConstants.createDeferred(BuiltInRegistries.ENTITY_TYPE);

    public static final DeferredHolder<EntityType<?>, EntityType<RoboElf>> ROBO_ELF = REGISTER.register("robo_elf", () -> EntityType.Builder.of(RoboElf::new, MobCategory.CREATURE).sized(1, 1).build("robo_elf"));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ROBO_ELF.get(), RoboElfRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RoboElfModel.LAYER_LOCATION, RoboElfModel::createBodyLayer);
    }

    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ROBO_ELF.get(), RoboElf.createMobAttributes().build());
    }
}
