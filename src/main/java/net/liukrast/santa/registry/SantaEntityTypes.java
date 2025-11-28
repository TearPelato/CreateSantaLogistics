package net.liukrast.santa.registry;

import net.liukrast.santa.SantaLogisticsConstants;
import net.liukrast.santa.client.model.CogDrivenCourierModel;
import net.liukrast.santa.client.renderer.entity.CogDrivenCourierRenderer;
import net.liukrast.santa.world.entity.CogDrivenCourier;
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

    private static final DeferredRegister<EntityType<?>> REGISTER = SantaLogisticsConstants.createDeferred(BuiltInRegistries.ENTITY_TYPE);

    public static final DeferredHolder<EntityType<?>, EntityType<CogDrivenCourier>> COG_DRIVEN_COURIER = REGISTER.register("cog_driven_courier", () -> EntityType.Builder.of(CogDrivenCourier::new, MobCategory.MISC).sized(10, 5).build("cog_driven_courier"));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(COG_DRIVEN_COURIER.get(), CogDrivenCourierRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CogDrivenCourierModel.LAYER_LOCATION, CogDrivenCourierModel::createBodyLayer);
    }

    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {

    }
}
