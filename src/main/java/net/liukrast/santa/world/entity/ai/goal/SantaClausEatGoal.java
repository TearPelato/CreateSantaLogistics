package net.liukrast.santa.world.entity.ai.goal;

import net.createmod.catnip.math.VecHelper;
import net.liukrast.santa.world.entity.SantaClaus;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SantaClausEatGoal extends Goal {
    private int cooldown;
    private final SantaClaus santa;

    public SantaClausEatGoal(SantaClaus santa) {
        this.santa = santa;
    }

    @Override
    public boolean canUse() {
        var stack = santa.getMainHandItem();
        return santa.isTypeAFood(stack) || santa.isTypeBFood(stack);
    }

    @Override
    public void start() {
        santa.setAnimationState(3);
        cooldown = 200;
    }

    @Override
    public void tick() {
        if(cooldown > 0) {
            cooldown--;
            Vec3 m = VecHelper.offsetRandomly(new Vec3(0, 0.25f, 0), santa.level().random, .125f);
            ((ServerLevel)santa.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, santa.getMainHandItem()), santa.getX(), santa.getY(), santa.getZ(), 10, m.x, m.y, m.z, 0.1);
        } else {
            var stack = santa.getMainHandItem();
            boolean a = santa.isTypeAFood(stack);
            if(!a && !santa.isTypeBFood(stack)) return;
            santa.incrementSatisfaction(stack, a);
            santa.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }
}
