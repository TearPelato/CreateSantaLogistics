package net.liukrast.santa.world.entity.ai.goal;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.createmod.catnip.math.VecHelper;
import net.liukrast.santa.world.entity.RoboElf;
import net.liukrast.santa.world.item.PresentItem;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RoboElfCreatePackageGoal extends Goal {
    protected final RoboElf mob;
    private final int maxCooldown;
    private int cooldown = 0;
    public RoboElfCreatePackageGoal(RoboElf mob, int cooldown) {
        this.mob = mob;
        this.maxCooldown = cooldown;
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if(mob.isCharging()) return false;
        ItemStack hand = mob.getItemInHand(InteractionHand.MAIN_HAND);
        if(hand.isEmpty()) return false;
        if(hand.getItem() instanceof PresentItem) return false;
        return PackageItem.isPackage(hand);
    }

    @Override
    public void start() {
        cooldown = maxCooldown/(mob.getStress()/10+1);
    }

    @Override
    public void tick() {
        if(cooldown > 0) {
            cooldown--;
            Vec3 m = VecHelper.offsetRandomly(new Vec3(0, 0.25f, 0), mob.getRandom(), .125f);
            ((ServerLevel)mob.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, mob.getMainHandItem()), mob.getX(), mob.getY(), mob.getZ(), 10, m.x, m.y, m.z, 0.1);
        } else {
            ItemStack stack = mob.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack packed = PresentItem.containing(PackageItem.getContents(stack));
            PackageItem.addAddress(packed, PackageItem.getAddress(stack));
            this.mob.spawnAtLocation(packed);
            mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            mob.extractCharge(20);
            this.mob.stress(5);
        }
    }
}
