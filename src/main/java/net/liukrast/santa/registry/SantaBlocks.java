package net.liukrast.santa.registry;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.level.block.ElfChargeStationBlock;
import net.liukrast.santa.world.level.block.SantaDockBlock;
import net.liukrast.santa.world.level.block.SantaDoorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SantaBlocks {
    private SantaBlocks() {}

    private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(SantaConstants.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SantaConstants.MOD_ID);

    public static final DeferredBlock<SantaDockBlock> SANTA_DOCK = REGISTER.register("santa_dock", () -> new SantaDockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<SantaDoorBlock> SANTA_DOOR = REGISTER.register("santa_door", () -> new SantaDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<ElfChargeStationBlock> ELF_CHARGE_STATION = register("elf_charge_station", () -> new ElfChargeStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)), SantaStressImpacts.init(() -> 2));

    static {
        ITEMS.register("santa_dock", () -> new BlockItem(SANTA_DOCK.get(), new Item.Properties()));
        ITEMS.register("santa_door", () -> new BlockItem(SANTA_DOOR.get(), new Item.Properties()));
        ITEMS.register("elf_charge_station", () -> new BlockItem(ELF_CHARGE_STATION.get(), new Item.Properties()));
    }

    @SafeVarargs
    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier, Consumer<T>... applications) {
        return REGISTER.register(name, () -> {
            var block = supplier.get();
            for (Consumer<T> application : applications) application.accept(block);
            return block;
        });
    }

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
        ITEMS.register(eventBus);
    }
}
