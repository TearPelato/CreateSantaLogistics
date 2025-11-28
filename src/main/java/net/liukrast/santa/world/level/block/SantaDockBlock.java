package net.liukrast.santa.world.level.block;

import com.mojang.serialization.MapCodec;
import net.liukrast.santa.world.level.block.entity.SantaDockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SantaDockBlock extends BaseEntityBlock {
    private static final MapCodec<? extends BaseEntityBlock> CODEC = simpleCodec(SantaDockBlock::new);
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

    public SantaDockBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity blockentity = level.getBlockEntity(pos);
        String dock = SantaDocks.getDock((ServerLevel) level, pos);
        if (blockentity instanceof SantaDockBlockEntity be) player.openMenu(be, buf -> {
            buf.writeBlockPos(pos);
            buf.writeUtf(dock == null ? "" : dock);
        });
        return InteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        if(level.isClientSide()) return;
        if(state.is(newState.getBlock())) return;
        SantaDocks.removeDock((ServerLevel) level, pos);
    }



    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof SantaDockBlockEntity santaDock) santaDock.recheckOpen();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SantaDockBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STATE);
    }

    public enum State implements StringRepresentable {
        IDLE("idle"),
        CONNECTED("connected"),
        ADDRESS_TAKEN("address_taken");

        private final String value;
        State(String value) {
            this.value = value;
        }


        @Override
        public String getSerializedName() {
            return value;
        }
    }
}
