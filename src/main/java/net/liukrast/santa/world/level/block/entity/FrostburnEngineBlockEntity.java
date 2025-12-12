package net.liukrast.santa.world.level.block.entity;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.liukrast.santa.registry.SantaBlockEntityTypes;
import net.liukrast.santa.registry.SantaBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FrostburnEngineBlockEntity extends GeneratingKineticBlockEntity {
    public FrostburnEngineBlockEntity(BlockPos pos, BlockState state) {
        super(SantaBlockEntityTypes.FROSTBURN_ENGINE.get(), pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        if(!getBlockState().is(SantaBlocks.FROSTBURN_ENGINE.get()))
            return 0;
        return 16;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return super.calculateAddedStressCapacity() * 16777216;
    }

    @Override
    public void initialize() {
        super.initialize();
        if(!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }
}
