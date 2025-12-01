package net.liukrast.santa.registry;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.level.block.SantaDockBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SantaBlocks {
    private SantaBlocks() {}

    private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(SantaConstants.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SantaConstants.MOD_ID);

    public static final DeferredBlock<SantaDockBlock> SANTA_DOCK = REGISTER.register("santa_dock", () -> new SantaDockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    static {
        ITEMS.register("santa_dock", () -> new BlockItem(SANTA_DOCK.get(), new Item.Properties()));
    }

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
        ITEMS.register(eventBus);
    }
}
