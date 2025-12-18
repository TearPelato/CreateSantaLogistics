package net.liukrast.santa.world.entity.ai.goal;

import net.createmod.catnip.math.VecHelper;
import net.liukrast.santa.world.entity.RoboElf;
import net.liukrast.santa.world.entity.TradeInfo;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class RoboElfCraftGoal extends Goal {
    protected final RoboElf mob;
    private int cooldown = 0;
    private TradeInfo trade;
    private UUID owner;

    public RoboElfCraftGoal(RoboElf mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if(mob.isCharging()) return false;
        return !mob.getQueue().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && cooldown >= 0;
    }

    @Override
    public void start() {
        var pair = mob.getQueue().peek();
        assert pair != null;
        owner = pair.getFirst();
        trade = pair.getSecond();
        cooldown = trade.getProcessTime()*10;
    }

    @Override
    public void tick() {
        if(cooldown > 0) {
            cooldown--;
            Vec3 m = VecHelper.offsetRandomly(new Vec3(0, 0.25f, 0), mob.level().random, .125f);
            ((ServerLevel)mob.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, trade.getResult()), mob.getX(), mob.getY(), mob.getZ(), 10, m.x, m.y, m.z, 0.1);
        } else if(cooldown == 0) {
            ItemStack stack = trade.getResult().copy();
            mob.setCrafted(owner, stack);
            mob.extractCharge(trade.getEnergy());
            mob.getQueue().poll();
            this.mob.stress(2);
            cooldown--;
        }
    }
}
