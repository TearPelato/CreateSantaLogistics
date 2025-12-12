package net.liukrast.santa.world.level.block;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import net.liukrast.multipart.block.AbstractMultipartBlock;
import net.liukrast.santa.DeployerGoggleInformation;
import net.liukrast.santa.SantaLang;
import net.liukrast.santa.mixin.KineticBlockEntityAccessor;
import net.liukrast.santa.registry.SantaBlockEntityTypes;
import net.liukrast.santa.world.level.block.entity.FrostburnEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@SuppressWarnings("deprecation")
@NonnullDefault
public class FrostburnEngineBlock extends AbstractMultipartBlock implements IRotate, IBE<FrostburnEngineBlockEntity>, DeployerGoggleInformation {
    private static final VoxelShape BOTTOM = Shapes.or(
            box(0,0,0,16,3,16),
            box(0,12,0,16,16,16)
    );

    private static final VoxelShape TUBE = Shapes.or(
            box(3, 4, 3, 13, 16, 13)
    );

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    public FrostburnEngineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(getPartsProperty(), 10));
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        // onBlockAdded is useless for init, as sometimes the BE gets re-instantiated

        // however, if a block change occurs that does not change kinetic connections,
        // we can prevent a major re-propagation here

        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
            kineticBlockEntity.preventSpeedUpdate = 0;

            if (oldState.getBlock() != state.getBlock())
                return;
            if (state.hasBlockEntity() != oldState.hasBlockEntity())
                return;
            if (!areStatesKineticallyEquivalent(oldState, state))
                return;

            kineticBlockEntity.preventSpeedUpdate = 2;
        }
    }

    @Deprecated
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(newState.is(state.getBlock()) || movedByPiston) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }
        destroy(level, pos, state);
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int flags, int recursionLeft) {
        if (level.isClientSide())
            return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof KineticBlockEntity kbe))
            return;

        if (kbe.preventSpeedUpdate > 0)
            return;

        // Remove previous information when a block is added
        kbe.warnOfMovement();
        kbe.clearKineticInformation();
        kbe.updateSpeed = true;
    }

    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (oldState.getBlock() != newState.getBlock())
            return false;
        return getRotationAxis(newState) == getRotationAxis(oldState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        AdvancementBehaviour.setPlacedBy(level, pos, placer);
        if (level.isClientSide)
            return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof KineticBlockEntity kbe))
            return;

        ((KineticBlockEntityAccessor)kbe).getEffects().queueRotationIndicators();
    }

    @Override
    public void defineParts(Builder builder) {
        for(int x = 0; x < 3; x++) {
            for(int y = 0; y < 3; y++) {
                for(int z = 0; z < 3; z++) {
                    builder.define(x,y,z);
                }
            }
        }
        builder.define(-1, 2, 0);
        builder.define(-1, 2, 2);
        builder.define(3, 2, 0);
        builder.define(3, 2, 2);

        builder.define(0, 2, -1);
        builder.define(2, 2, -1);
        builder.define(0, 2, 3);
        builder.define(2, 2, 3);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(getPartsProperty()) == 10 && face == Direction.DOWN;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<FrostburnEngineBlockEntity> getBlockEntityClass() {
        return FrostburnEngineBlockEntity.class;
    }

    @Override
    @Nullable
    public BlockEntityType<? extends FrostburnEngineBlockEntity> getBlockEntityType() {
        return SantaBlockEntityTypes.FROSTBURN_ENGINE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(getPartsProperty()) == 10 ? IBE.super.newBlockEntity(pos, state) : null;
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    public boolean addToGoogleTooltip(Level level, BlockPos pos, BlockState state, List<Component> tooltip, boolean isPlayerSneaking) {
        if(state.getValue(getPartsProperty()) == 10) return false;
        var statePos = getPositions().get(state.getValue(getPartsProperty()));
        var direction = getDirection(state);
        BlockPos origin = getOrigin(pos, statePos, direction);
        BlockPos ten = getPositions().get(10);
        BlockEntity be = level.getBlockEntity(getRelative(origin, ten, direction));
        if(be instanceof FrostburnEngineBlockEntity febe)
            return febe.addToGoggleTooltip(tooltip, isPlayerSneaking);
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(getPartsProperty());
        if(index > 26) return TUBE;
        return getPositions().get(index).getY() == 0 ? BOTTOM : super.getShape(state, level, pos, context);
    }
}
