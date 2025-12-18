package net.liukrast.santa.world.entity.ai.goal;

import net.liukrast.santa.world.entity.RoboElf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

public class RoboElfDeliverCraftGoal extends MeleeAttackGoal {
    private final RoboElf roboElf;
    public RoboElfDeliverCraftGoal(RoboElf mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        roboElf = mob;
    }

    @Override
    public boolean canUse() {
        if(!super.canUse()) return false;
        var list = roboElf.getCrafted();
        if(list.isEmpty()) return false;
        var target = mob.getTarget();
        if(target == null) return false;
        if(!target.isAlive()) return false;
        var info = list.peek();
        assert info != null;
        return target.getUUID().equals(info.getFirst());
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity target) {
        //super.checkAndPerformAttack(target);
        if(!this.canPerformAttack(target)) return;
        this.resetAttackCooldown();
        var info = roboElf.getCrafted().poll();
        assert info != null;
        this.mob.spawnAtLocation(info.getSecond());
        stop();
    }
}
