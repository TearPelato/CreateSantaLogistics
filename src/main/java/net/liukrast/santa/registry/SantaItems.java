package net.liukrast.santa.registry;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.item.FrostburnCoreItem;
import net.liukrast.santa.world.item.PresentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@SuppressWarnings("unused")
public class SantaItems {
    private SantaItems() {}
    static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(SantaConstants.MOD_ID);

    public static final DeferredItem<Item> CRYOLITE_SHARD = REGISTER.register("cryolite_shard", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> CRYOLITE_POWDER = REGISTER.register("cryolite_powder", () -> new Item(new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> ROBO_ELF_SPAWN_EGG = REGISTER.register("robo_elf_spawn_egg", () -> new DeferredSpawnEggItem(SantaEntityTypes.ROBO_ELF, 0xe4b763, 0x61a53f, new Item.Properties()));
    public static final DeferredItem<DeferredSpawnEggItem> SANTA_CLAUS_SPAWN_EGG = REGISTER.register("santa_claus_spawn_egg", () -> new DeferredSpawnEggItem(SantaEntityTypes.SANTA_CLAUS, 0xFF0000, 0xFFFFFF, new Item.Properties()));
    public static final DeferredItem<Item> CANDY_CANE = REGISTER.register("candy_cane", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FROSTBURN_CORE = REGISTER.register("frostburn_core", () -> wrapWithShiftSummary(new FrostburnCoreItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1))));

    public static final DeferredItem<Item> SANTA_KEY = REGISTER.register("santa_key", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final List<DeferredItem<PresentItem>> PRESENTS = SantaPackages.PRESENTS.stream()
            .map(k -> REGISTER.register(k.type() + "_present", () -> new PresentItem(new Item.Properties().stacksTo(1), k))).toList();

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    /**
     * Will be replaced with Deployer API
     * */
    @Deprecated(forRemoval = true)
    private static Item wrapWithShiftSummary(Item item) {
        var modifier = new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        TooltipModifier.REGISTRY.register(item, modifier);
        return item;
    }
}
