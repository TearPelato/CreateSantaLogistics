package net.liukrast.santa.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.scores.Team;

import java.util.function.Predicate;

public class NearestNonCombatTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public NearestNonCombatTargetGoal(Mob mob, Class<T> targetType, boolean mustSee, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType, mustSee, targetPredicate);
        this.targetConditions = TargetingConditions.forNonCombat().range(this.getFollowDistance()).selector(targetPredicate);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) livingentity = this.targetMob;

        if (livingentity == null) return false;
        double d0 = this.getFollowDistance();
        if (this.mob.distanceToSqr(livingentity) > d0 * d0) return false;
        if (this.mustSee) {
            if (this.mob.getSensing().hasLineOfSight(livingentity)) {
                this.unseenTicks = 0;
            } else if (++this.unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) return false;
        }
        this.mob.setTarget(livingentity);
        return true;
    }
}
