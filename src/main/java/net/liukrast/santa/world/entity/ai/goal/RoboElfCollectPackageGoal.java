package net.liukrast.santa.world.entity.ai.goal;

import com.simibubi.create.content.logistics.box.PackageEntity;
import net.liukrast.santa.world.entity.RoboElf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class RoboElfCollectPackageGoal extends MeleeAttackGoal {
    public RoboElfCollectPackageGoal(RoboElf mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.mob.getMainHandItem().isEmpty() && this.mob.getTarget() instanceof PackageEntity pe && pe.isAlive();
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity target) {
        if(!(target instanceof PackageEntity pe)) return;
        if(this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            this.mob.setItemInHand(InteractionHand.MAIN_HAND, pe.box);
            target.discard();
            stop();
        }
    }
}
