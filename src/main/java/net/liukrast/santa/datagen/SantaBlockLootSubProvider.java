package net.liukrast.santa.datagen;

import net.liukrast.santa.SantaConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class SantaBlockLootSubProvider extends BlockLootSubProvider {
    public SantaBlockLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        List<Predicate<Block>> exceptions = List.of();
        //noinspection RedundantOperationOnEmptyContainer
        SantaConstants.getElements(BuiltInRegistries.BLOCK).filter(b -> exceptions.stream().allMatch(k -> k.test(b))).forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SantaConstants.MOD_ID)).toList();
    }
}
