package net.liukrast.santa.world.entity.ai.goal;

import net.liukrast.santa.SantaConfig;
import net.liukrast.santa.registry.SantaBlocks;
import net.liukrast.santa.world.entity.RoboElf;
import net.liukrast.santa.world.level.block.ElfChargeStationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class RoboElfFindStationGoal extends MoveToBlockGoal {
    private final RoboElf elf;

    public RoboElfFindStationGoal(RoboElf elf, double speedModifier) {
        super(elf, speedModifier, 8, 2);
        this.elf = elf;
    }

    @Override
    protected BlockPos getMoveToTarget() {
        return this.blockPos;
    }

    @Override
    public boolean canUse() {
        return elf.getCharge()/elf.getMaxCharge() < (SantaConfig.ELF_RECHARGE_PERCENTAGE.getAsInt()/100f) && this.findNearestBlock();
    }

    @Override
    protected boolean findNearestBlock() {
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k = this.verticalSearchStart; k <= this.verticalSearchRange; k = k > 0 ? -k : 1 - k) {
            for (int l = 0; l < this.searchRange; l++) {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, i1, k - 1, j1);
                        if(!this.mob.isWithinRestriction(blockpos$mutableblockpos)) continue;
                        Level level = this.mob.level();
                        BlockState state = level.getBlockState(blockpos$mutableblockpos);
                        if(!state.is(SantaBlocks.ELF_CHARGE_STATION.get())) continue;
                        if(state.getValue(ElfChargeStationBlock.OCCUPIED)) continue;
                        var dir = state.getValue(ElfChargeStationBlock.FACING);
                        var targetPos = blockpos$mutableblockpos.relative(dir);
                        if(!level.isEmptyBlock(targetPos)) continue;
                        this.blockPos = targetPos;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void moveMobToBlock() {
        this.mob.getNavigation()
                .moveTo(
                        (double)this.blockPos.getX() + 0.5,
                        this.blockPos.getY() + 1,
                        (double)this.blockPos.getZ() + 0.5,
                        0,
                        this.speedModifier);
    }

    @Override
    public void tick() {
        BlockPos blockpos = this.getMoveToTarget();
        if (!blockpos.closerToCenterThan(this.mob.position().add(0, 0.5, 0), this.acceptedDistance())) {
            this.reachedTarget = false;
            this.tryTicks++;
            if (this.shouldRecalculatePath()) moveMobToBlock();
        } else {
            this.reachedTarget = true;
            this.tryTicks--;
        }
        if (this.isReachedTarget() && isValidTarget(elf.level(), blockPos)) {
            this.elf.setCharging(RoboElf.findTarget(elf.level(), blockPos));
        }
    }

    @Override
    public double acceptedDistance() {
        return 0.3;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        var pos1 = RoboElf.findTarget(level, pos);
        return pos1 != null;
    }

    @Override
    public boolean shouldRecalculatePath() {
        return tryTicks % 20 == 0;
    }
}
