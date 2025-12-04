package net.liukrast.santa.registry;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.client.renderer.block.ElfChargeStationRenderer;
import net.liukrast.santa.client.renderer.block.SantaDoorBlockEntityRenderer;
import net.liukrast.santa.client.visual.block.ElfChargeStationVisual;
import net.liukrast.santa.world.level.block.entity.ElfChargeStationBlockEntity;
import net.liukrast.santa.world.level.block.entity.SantaDockBlockEntity;
import net.liukrast.santa.world.level.block.entity.SantaDoorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("DataFlowIssue")
public class SantaBlockEntityTypes {
    private SantaBlockEntityTypes() {}

    private static final DeferredRegister<BlockEntityType<?>> REGISTER = SantaConstants.createDeferred(BuiltInRegistries.BLOCK_ENTITY_TYPE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SantaDockBlockEntity>> SANTA_DOCK = REGISTER.register("santa_dock", () -> BlockEntityType.Builder.of(SantaDockBlockEntity::new, SantaBlocks.SANTA_DOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SantaDoorBlockEntity>> SANTA_DOOR = REGISTER.register("santa_door", () -> BlockEntityType.Builder.of(SantaDoorBlockEntity::new, SantaBlocks.SANTA_DOOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElfChargeStationBlockEntity>> ELF_CHARGE_STATION = REGISTER.register("elf_charge_station", () -> BlockEntityType.Builder.of(ElfChargeStationBlockEntity::new, SantaBlocks.ELF_CHARGE_STATION.get()).build(null));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SantaBlockEntityTypes.SANTA_DOOR.get(), SantaDoorBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(SantaBlockEntityTypes.ELF_CHARGE_STATION.get(), ElfChargeStationRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public static void fmlClientSetup(FMLClientSetupEvent ignored) {
        SimpleBlockEntityVisualizer.builder(SantaBlockEntityTypes.ELF_CHARGE_STATION.get())
                .factory(ElfChargeStationVisual::new)
                .skipVanillaRender(be -> false)
                .apply();
    }
}
