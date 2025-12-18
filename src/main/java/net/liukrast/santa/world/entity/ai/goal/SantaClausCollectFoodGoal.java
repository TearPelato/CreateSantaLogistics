package net.liukrast.santa.world.entity.ai.goal;

import com.simibubi.create.AllFluids;
import net.liukrast.santa.SantaConfig;
import net.liukrast.santa.registry.SantaAttachmentTypes;
import net.liukrast.santa.world.entity.SantaClaus;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class SantaClausCollectFoodGoal extends Goal {
    private final SantaClaus santa;
    private int cooldown;
    public static final int MAX_COOLDOWN = 100;
    private ItemEntity item = null;

    public SantaClausCollectFoodGoal(SantaClaus santa) {
        this.santa = santa;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if(!santa.getMainHandItem().isEmpty()) return false;
        if(item != null && !item.isRemoved()) return false;
        List<ItemEntity> list = santa.level().getEntitiesOfClass(ItemEntity.class, getRange(santa.getBoundingBox()), this::isValidItem);
        return !list.isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        if(!santa.getMainHandItem().isEmpty()) return false;
        if(item == null) return false;
        if(item.isRemoved()) return false;
        List<ItemEntity> list = santa.level().getEntitiesOfClass(ItemEntity.class, getRange(santa.getBoundingBox()), this::isValidItem);
        return !list.isEmpty();
    }

    public boolean isValidItem(ItemEntity itemEntity) {
        return santa.isTypeAFood(itemEntity.getItem()) || santa.isTypeBFood(itemEntity.getItem());
    }

    public int getTrust(ItemEntity itemEntity) {
        var owner = itemEntity.getOwner();
        if(owner == null) return 0;
        if(!owner.isAlive()) return 0;
        return owner.getData(SantaAttachmentTypes.TRUST);
    }

    public AABB getRange(AABB mobBoundingBox) {
        return mobBoundingBox;
    }

    @Override
    public void stop() {
        cooldown = 0;
    }

    @Override
    public void start() {
        List<ItemEntity> list = santa.level().getEntitiesOfClass(ItemEntity.class, getRange(santa.getBoundingBox()), this::isValidItem);
        if(list.isEmpty()) return;
        santa.setAnimationState(1);
        item = list.getFirst();
        santa.getLookControl().setLookAt(item);
    }

    @Override
    public void tick() {
        cooldown++;
        if(item != null && !item.isRemoved() && santa.getAnimationState() != 2) santa.getLookControl().setLookAt(item);
        if(cooldown == MAX_COOLDOWN) {
            if(item == null || item.isRemoved()) return;
            List<ItemEntity> list = santa.level().getEntitiesOfClass(ItemEntity.class, getRange(santa.getBoundingBox()), this::isValidItem);
            if(!list.contains(item)) return;
            boolean a = santa.isTypeAFood(item.getItem());
            int trust = getTrust(item);
            if((a && trust >= SantaConfig.TYPE_A_TRUST.getAsInt()) || (!a && trust >= SantaConfig.TYPE_B_TRUST.getAsInt())) {
                santa.setAnimationState(0);
                santa.setItemInHand(InteractionHand.MAIN_HAND, item.getItem());
                item.discard();
                return;
            }
            santa.setAnimationState(2);
        }
    }
}
