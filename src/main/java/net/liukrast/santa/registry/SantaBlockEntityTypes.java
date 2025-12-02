package net.liukrast.santa.registry;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.level.block.entity.SantaDockBlockEntity;
import net.liukrast.santa.world.level.block.entity.SantaDoorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("DataFlowIssue")
public class SantaBlockEntityTypes {
    private SantaBlockEntityTypes() {}

    private static final DeferredRegister<BlockEntityType<?>> REGISTER = SantaConstants.createDeferred(BuiltInRegistries.BLOCK_ENTITY_TYPE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SantaDockBlockEntity>> SANTA_DOCK = REGISTER.register("santa_dock", () -> BlockEntityType.Builder.of(SantaDockBlockEntity::new, SantaBlocks.SANTA_DOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SantaDoorBlockEntity>> SANTA_DOOR = REGISTER.register("santa_door", () -> BlockEntityType.Builder.of(SantaDoorBlockEntity::new, SantaBlocks.SANTA_DOOR.get()).build(null));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
