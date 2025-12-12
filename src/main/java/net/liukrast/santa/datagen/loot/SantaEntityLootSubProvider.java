package net.liukrast.santa.datagen.loot;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.registry.SantaEntityTypes;
import net.liukrast.santa.registry.SantaItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.lwjgl.system.NonnullDefault;

import java.util.stream.Stream;

public class SantaEntityLootSubProvider extends EntityLootSubProvider {
    public SantaEntityLootSubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @NonnullDefault
    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return super.getKnownEntityTypes().filter(et -> BuiltInRegistries.ENTITY_TYPE.getKey(et).getNamespace().equals(SantaConstants.MOD_ID));
    }

    @Override
    public void generate() {
        this.add(
                SantaEntityTypes.ROBO_ELF.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(SantaItems.CRYOLITE_SHARD)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
        );
    }
}
