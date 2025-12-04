package net.liukrast.santa.world.entity;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.liukrast.santa.DeployerGoggleInformation;
import net.liukrast.santa.SantaLang;
import net.liukrast.santa.registry.SantaAttributes;
import net.liukrast.santa.registry.SantaBlocks;
import net.liukrast.santa.world.entity.ai.goal.RoboElfFindStationGoal;
import net.liukrast.santa.world.level.block.ElfChargeStationBlock;
import net.liukrast.santa.world.level.block.entity.ElfChargeStationBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@NonnullDefault
public class RoboElf extends PathfinderMob implements DeployerGoggleInformation {
    private static final EntityDataAccessor<Float> CHARGE_ID = SynchedEntityData.defineId(RoboElf.class, EntityDataSerializers.FLOAT);
    @Nullable
    private ElfChargeStationBlockEntity chargeStation = null;
    public RoboElf(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setCharge(this.getMaxCharge());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RoboElfFindStationGoal(this, 1.5));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGE_ID, 1f);
    }

    public float getCharge() {
        return this.entityData.get(CHARGE_ID);
    }

    public void setCharge(float charge) {
        this.entityData.set(CHARGE_ID, Mth.clamp(charge, 0, this.getMaxCharge()));
    }

    public final float getMaxCharge() {
        return (float) this.getAttributeValue(SantaAttributes.MAX_CHARGE);
    }

    public float extractCharge(float amount) {
        if(amount < 0) return insertCharge(-amount);
        float charge = this.getCharge();
        if(amount > charge) {
            setCharge(0);
            return amount - charge;
        }
        setCharge(charge - amount);
        return amount;
    }

    public float insertCharge(float amount) {
        if(amount < 0) return extractCharge(-amount);
        float charge = this.getCharge();
        if(charge + amount > getMaxCharge()) {
            setCharge(getMaxCharge());
            return charge + amount - getMaxCharge();
        }
        setCharge(charge + amount);
        return amount;
    }

    public static AttributeSupplier.Builder createRoboElfAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(SantaAttributes.MAX_CHARGE, 1024);
    }

    @Override
    public void tick() {
        super.tick();
        if(chargeStation != null && chargeStation.isRemoved()) {
            setCharging(null);
            reloadFlags();
            return;
        }
        if(chargeStation == null) {
            reloadFlags();
            return;
        }
        var a = blockPosition();
        if(!chargeStation.getBlockPos().relative(chargeStation.getBlockState().getValue(ElfChargeStationBlock.FACING)).equals(a)) {
            setCharging(null);
            reloadFlags();
            return;
        }
        chargeStation.update(this);
        if(this.getCharge() >= this.getMaxCharge()) setCharging(null);
        reloadFlags();
    }

    public void reloadFlags() {
        boolean fl = chargeStation == null && getCharge() > 0;
        for(var flag : Goal.Flag.values()) {
            goalSelector.setControlFlag(flag, fl);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        super.travel(travelVector);
        double motion = travelVector.length();
        extractCharge((float) motion);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        SantaLang.translate("gui.robo_elf.info_header").forGoggles(tooltip);
        SantaLang.translate("gui.robo_elf.title")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        IRotate.StressImpact.getFormattedStressText(0)
                .forGoggles(tooltip);
        SantaLang.translate("gui.robo_elf.capacity")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        double remainingCharge = getCharge();
        LangBuilder su = CreateLang.translate("generic.unit.stress");
        LangBuilder stressTip = CreateLang.number(remainingCharge)
                .add(su)
                .style(IRotate.StressImpact.of(1-(remainingCharge/getMaxCharge())).getRelativeColor());
        stressTip.forGoggles(tooltip, 1);
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Charge", this.getCharge());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Charge", Tag.TAG_ANY_NUMERIC))
            this.setCharge(compound.getFloat("Charge"));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPushable() {
        if(this.chargeStation != null) return false;
        return super.isPushable();
    }

    public void setCharging(@Nullable BlockPos pos) {
        if(pos == null) {
            if(chargeStation != null) {
                if(chargeStation.getBlockState().is(SantaBlocks.ELF_CHARGE_STATION.get()))
                    level().setBlock(chargeStation.getBlockPos(), chargeStation.getBlockState().setValue(ElfChargeStationBlock.OCCUPIED, false), 3);
            }
            chargeStation = null;

            reloadFlags();
            return;
        }
        if(!(level().getBlockEntity(pos) instanceof ElfChargeStationBlockEntity be)) return;
        level().setBlock(pos, be.getBlockState().setValue(ElfChargeStationBlock.OCCUPIED, true), 3);
        chargeStation = be;
        reloadFlags();
    }

    @Nullable
    public static BlockPos findTarget(LevelReader level, BlockPos pos) {
        if(!level.isEmptyBlock(pos)) return null;
        for(Direction direction : Direction.values()) {
            if(direction.getAxis().isVertical()) continue;
            BlockState state = level.getBlockState(pos.relative(direction));
            if(!state.is(SantaBlocks.ELF_CHARGE_STATION.get())) continue;
            if(state.getValue(ElfChargeStationBlock.OCCUPIED)) continue;
            if(!state.getValue(ElfChargeStationBlock.FACING).getOpposite().equals(direction)) continue;
            return pos.relative(direction);
        }
        return null;
    }
}
