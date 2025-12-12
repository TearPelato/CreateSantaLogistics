package net.liukrast.santa.registry;

import com.simibubi.create.api.stress.BlockStressValues;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.level.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("removal")
public class SantaBlocks {
    private SantaBlocks() {}

    private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(SantaConstants.MOD_ID);
    static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SantaConstants.MOD_ID);

    public static final DeferredBlock<ChristmasTreeBlock> CHRISTMAS_TREE = register("christmas_tree", () -> new ChristmasTreeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)));
    public static final DeferredBlock<SantaDockBlock> SANTA_DOCK = register("santa_dock", () -> new SantaDockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<SantaDoorBlock> SANTA_DOOR = register("santa_door", () -> new SantaDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<ElfChargeStationBlock> ELF_CHARGE_STATION = register("elf_charge_station", () -> new ElfChargeStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()), SantaStressImpacts.withStressImpact(2));
    public static final DeferredBlock<AmethystBlock> CRYOLITE_BLOCK = register("cryolite_block", () -> new AmethystBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)));
    public static final DeferredBlock<BuddingAmethystBlock> BUDDING_CRYOLITE = register("budding_cryolite", () -> new BuddingCryoliteBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BUDDING_AMETHYST)));
    public static final DeferredBlock<AmethystClusterBlock> CRYOLITE_CLUSTER = register("cryolite_cluster", () -> new AmethystClusterBlock(7,3,BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER)));
    public static final DeferredBlock<AmethystClusterBlock> LARGE_CRYOLITE_BUD = register("large_cryolite_bud", () -> new AmethystClusterBlock(5,3,BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_AMETHYST_BUD)));
    public static final DeferredBlock<AmethystClusterBlock> MEDIUM_CRYOLITE_BUD = register("medium_cryolite_bud", () -> new AmethystClusterBlock(4,3,BlockBehaviour.Properties.ofFullCopy(Blocks.MEDIUM_AMETHYST_BUD)));
    public static final DeferredBlock<AmethystClusterBlock> SMALL_CRYOLITE_BUD = register("small_cryolite_bud", () -> new AmethystClusterBlock(3,4,BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_AMETHYST_BUD)));
    public static final DeferredBlock<Block> FROSTBURN_ENGINE = register("frostburn_engine", () -> new FrostburnEngineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)), SantaStressImpacts.withStressCapacity(32), BlockStressValues.setGeneratorSpeed(32));

    @SafeVarargs
    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier, Consumer<T>... applications) {
        return register(name, supplier, true, applications);
    }

    @SafeVarargs
    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier, @SuppressWarnings("SameParameterValue") boolean withItem, Consumer<T>... applications) {
        var defBlock = REGISTER.register(name, () -> {
            var block = supplier.get();
            for (Consumer<T> application : applications) application.accept(block);
            return block;
        });
        if(withItem) ITEMS.register(name, () -> new BlockItem(defBlock.get(), new Item.Properties()));
        return defBlock;
    }

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
        ITEMS.register(eventBus);
    }
}
