package net.liukrast.santa.datagen;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.registry.SantaBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class SantaItemModelProvider extends ItemModelProvider {
    public SantaItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SantaConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(SantaBlocks.SANTA_DOOR.asItem());
    }
}
