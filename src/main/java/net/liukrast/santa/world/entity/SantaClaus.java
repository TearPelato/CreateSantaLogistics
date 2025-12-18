package net.liukrast.santa.world.entity;

import com.simibubi.create.AllFluids;
import net.liukrast.santa.registry.SantaItems;
import net.liukrast.santa.registry.SantaTags;
import net.liukrast.santa.world.entity.ai.goal.SantaClausCollectFoodGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SantaClaus extends PathfinderMob {
    @OnlyIn(Dist.CLIENT)
    public float animationTime;
    @OnlyIn(Dist.CLIENT)
    public int lastFoundState;

    private int satisfactionA = 0;
    private int satisfactionB = 0;

    private static final EntityDataAccessor<Integer> STATE_ID = SynchedEntityData.defineId(SantaClaus.class, EntityDataSerializers.INT);

    public SantaClaus(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createSantaAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 2000)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1) {
            int cooldown = 0;

            @Override
            protected boolean shouldPanic() {
                return getAnimationState() == 2;
            }

            @Override
            public void start() {
                super.start();
                cooldown = 0;
            }

            @Override
            public void stop() {
                super.stop();
                setAnimationState(0);
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() || cooldown < 100;
            }

            @Override
            public void tick() {
                super.tick();
                cooldown++;
            }
        });
        this.goalSelector.addGoal(1, new SantaClausCollectFoodGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE_ID, 0);
    }

    /* GETTERS AND SETTERS */

    public int getAnimationState() {
        return this.entityData.get(STATE_ID);
    }

    public void setAnimationState(int value) {
        this.entityData.set(STATE_ID, value);
    }

    public boolean isTypeAFood(ItemStack stack) {
        return stack.is(SantaTags.Items.SANTA_FOOD_A);
    }

    public boolean isTypeBFood(ItemStack stack) {
        return stack.is(SantaTags.Items.SANTA_FOOD_B);
    }

    public void incrementSatisfaction(ItemStack stack, boolean typeA) {

    }

    @Override
    public void onDamageTaken(DamageContainer damageContainer) {
        super.onDamageTaken(damageContainer);
    }
}
