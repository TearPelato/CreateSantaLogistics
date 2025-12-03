package net.liukrast.santa.world.level.block.entity;

import net.liukrast.santa.registry.SantaBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SantaDoorBlockEntity extends BlockEntity {
    @OnlyIn(Dist.CLIENT)
    public boolean lastState = false;
    @OnlyIn(Dist.CLIENT)
    public long lastStateTime = -1;

    public SantaDoorBlockEntity(BlockPos pos, BlockState blockState) {
        super(SantaBlockEntityTypes.SANTA_DOOR.get(), pos, blockState);
    }
}
