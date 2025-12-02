package net.liukrast.santa.datagen.tags;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.registry.SantaBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.concurrent.CompletableFuture;

@NonnullDefault
public class SantaBlockTagsProvider extends BlockTagsProvider {
    public SantaBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SantaConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var axe = tag(BlockTags.MINEABLE_WITH_AXE);
        var pick = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        axe.add(SantaBlocks.SANTA_DOOR.get());
        pick.add(SantaBlocks.SANTA_DOCK.get());
    }
}
