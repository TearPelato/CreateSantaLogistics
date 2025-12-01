package net.liukrast.santa.registry;

import net.liukrast.santa.SantaConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SantaEntityTypes {
    private SantaEntityTypes() {}

    private static final DeferredRegister<EntityType<?>> REGISTER = SantaConstants.createDeferred(BuiltInRegistries.ENTITY_TYPE);

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {

    }
}
