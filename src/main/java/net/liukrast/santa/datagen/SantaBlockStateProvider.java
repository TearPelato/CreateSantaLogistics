package net.liukrast.santa.datagen;

import net.liukrast.multipart.block.AbstractMultipartBlock;
import net.liukrast.multipart.datagen.MultiPartAPIStateHelper;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.registry.SantaBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class SantaBlockStateProvider extends BlockStateProvider {
    public SantaBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SantaConstants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(SantaBlocks.ELF_CHARGE_STATION.get());
        MultiPartAPIStateHelper.multiPartBlock(this, SantaBlocks.CHRISTMAS_TREE.get());
        simpleCubeAll(SantaBlocks.CRYOLITE_BLOCK.get());
        simpleCubeAll(SantaBlocks.BUDDING_CRYOLITE.get());
        getVariantBuilder(SantaBlocks.FROSTBURN_ENGINE.get())
                .forAllStates(state -> {
                    var block = ((AbstractMultipartBlock)state.getBlock());
                    int index = state.getValue(block.getPartsProperty());
                    var pos = block.getPositions().get(index);
                    int type;
                    if(pos.getX() == 1 && pos.getZ() == 1) type = 0;
                    else if(pos.getX() == 1 || pos.getZ() == 1) type = 1;
                    else type = 2;
                    if(type == 0 && index <= 26)
                        return ConfiguredModel.builder()
                                .modelFile(existing("block/frostburn_engine/middle_%s", pos.getY()))
                                .build();
                    else if(index <= 26) {
                        int flat = index - pos.getY()*3;
                        int rot = switch (flat) {
                            case 0, 1 -> 0;
                            case 2, 11 -> 90;
                            case 20, 19 -> 180;
                            default -> 270;
                        };
                        return ConfiguredModel.builder()
                                .modelFile(existing("block/frostburn_engine/%s_%s", type == 1 ? "side" : "angle", pos.getY()))
                                .rotationY((180 + rot) % 360)
                                .build();
                    }

                    return ConfiguredModel.builder()
                            .modelFile(existing("block/frostburn_engine/tube"))
                            .build();
                });
    }

    private void simpleCubeAll(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }

    private void horizontalBlock(Block block) {
        horizontalBlock(block, new ModelFile.ExistingModelFile(SantaConstants.id("block/%s", BuiltInRegistries.BLOCK.getKey(block).getPath()), this.models().existingFileHelper));
    }

    private ModelFile existing(String key, Object... args) {
        return new ModelFile.ExistingModelFile(
                SantaConstants.id(key, args),
                this.models().existingFileHelper
        );
    }
}
