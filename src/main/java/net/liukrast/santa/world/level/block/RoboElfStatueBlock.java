package net.liukrast.santa.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class RoboElfStatueBlock extends HorizontalDirectionalBlock {
    private static final MapCodec<RoboElfStatueBlock> CODEC = simpleCodec(RoboElfStatueBlock::new);

    protected RoboElfStatueBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
