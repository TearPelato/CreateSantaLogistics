package net.liukrast.santa.world.entity;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.liukrast.santa.DeployerGoggleInformation;
import net.liukrast.santa.SantaConfig;
import net.liukrast.santa.SantaLang;
import net.liukrast.santa.registry.SantaAttachmentTypes;
import net.liukrast.santa.registry.SantaAttributes;
import net.liukrast.santa.registry.SantaBlocks;
import net.liukrast.santa.registry.SantaItems;
import net.liukrast.santa.world.entity.ai.goal.*;
import net.liukrast.santa.world.inventory.RoboElfMenu;
import net.liukrast.santa.world.item.PresentItem;
import net.liukrast.santa.world.level.block.ElfChargeStationBlock;
import net.liukrast.santa.world.level.block.entity.ElfChargeStationBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.*;

@NonnullDefault
public class RoboElf extends PathfinderMob implements DeployerGoggleInformation, MenuProvider {
    private static final EntityDataAccessor<Float> CHARGE_ID = SynchedEntityData.defineId(RoboElf.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> STRESS_ID = SynchedEntityData.defineId(RoboElf.class, EntityDataSerializers.INT);
    private static final int MAX_QUEUE = 18;

    private int unstressCooldown = 0;
    private final Queue<Pair<UUID, TradeInfo>> queue = new ArrayDeque<>();
    private final Queue<Pair<UUID, ItemStack>> crafted = new ArrayDeque<>();

    private static final List<TradeInfo> TRADES = List.of(
            new TradeInfo(new ItemStack(Items.SNOWBALL, 4), Items.SNOW_BLOCK.getDefaultInstance(), 10, 10, 10),
            new TradeInfo(new ItemStack(Items.SPRUCE_LOG, 4), new ItemStack(Items.SPRUCE_LEAVES, 8), Items.GOLD_INGOT.getDefaultInstance(), SantaBlocks.CHRISTMAS_TREE.toStack(), 100, 40, 40),
            new TradeInfo(Items.SUGAR.getDefaultInstance(), Items.RED_DYE.getDefaultInstance(), SantaItems.CANDY_CANE.toStack(), 20, 20, 10),
            new TradeInfo(new ItemStack(Items.WHEAT, 2), Items.COCOA_BEANS.getDefaultInstance(), new ItemStack(Items.COOKIE, 8), 10, 10, 10),
            new TradeInfo(new ItemStack(Items.SUGAR, 4), new ItemStack(Items.COCOA_BEANS, 4), Items.MILK_BUCKET.getDefaultInstance(), AllFluids.CHOCOLATE.getBucket().orElseThrow().getDefaultInstance(), 40, 80, 60)
    );

    @Nullable
    private ElfChargeStationBlockEntity chargeStation = null;

    /* INIT */
    public RoboElf(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setCharge(this.getMaxCharge());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RoboElfCraftGoal(this));
        this.goalSelector.addGoal(1, new RoboElfCreatePackageGoal(this, 100));
        this.goalSelector.addGoal(1, new RoboElfFindStationGoal(this, 1.5));
        this.goalSelector.addGoal(1, new NearestNonCombatTargetGoal<>(this, Player.class, true, player -> !crafted.isEmpty() && player.getUUID().equals(Objects.requireNonNull(crafted.peek()).getFirst())));
        this.goalSelector.addGoal(1, new NearestNonCombatTargetGoal<>(this, PackageEntity.class, true, pack -> pack instanceof PackageEntity pe && !(pe.box.getItem() instanceof PresentItem)));
        this.goalSelector.addGoal(1, new RoboElfCollectPackageGoal(this, 1.25, false));
        this.goalSelector.addGoal(1, new RoboElfDeliverCraftGoal(this, 1.25, false));
        // Secondary tasks
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGE_ID, 1f);
        builder.define(STRESS_ID, 0);
    }

    public static AttributeSupplier.Builder createRoboElfAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(SantaAttributes.MAX_CHARGE, 1024);
    }

    /* GETTERS AND SETTERS */
    public float getCharge() {
        return this.entityData.get(CHARGE_ID);
    }

    @Override
    public void onDamageTaken(DamageContainer damageContainer) {
        Entity source = damageContainer.getSource().getEntity();
        if(!(source instanceof Player player)) return;
        SantaAttachmentTypes.trust(player, -10);
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
        return 0;
    }

    public int getStress() {
        return this.entityData.get(STRESS_ID);
    }

    public void setStress(int stress) {
        this.entityData.set(STRESS_ID, Mth.clamp(stress, 0, 100));
    }

    public void stress(int amount) {
        setStress(getStress()+amount);
    }

    public Queue<Pair<UUID, TradeInfo>> getQueue() {
        return queue;
    }

    public Queue<Pair<UUID, ItemStack>> getCrafted() {
        return crafted;
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if(PackageItem.isPackage(itemEntity.getItem())) {
            this.take(itemEntity, 1);
        }
        super.pickUpItem(itemEntity);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide) return;
        if(unstressCooldown > 0) unstressCooldown-=(getCharge() == 0 || isCharging() ? 10 : 1);
        else {
            stress(-1);
            unstressCooldown = SantaConfig.ELF_UNSTRESS_COOLDOWN.getAsInt();
        }
        if(getStress() > 70 && getCharge() > 0) {
            extractCharge(0.1f);
            ((ServerLevel)level()).sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, getX(), getY(), getZ(), 1, 0,  1, 0, 0.05);
        }
        if(getCharge() == 0) {
            navigation.stop();
        }
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
        if(!chargeStation.getBlockPos().relative(chargeStation.getBlockState().getValue(ElfChargeStationBlock.HORIZONTAL_FACING)).equals(a)) {
            setCharging(null);
            reloadFlags();
            return;
        }
        chargeStation.update(this);
        Vec3 pos1 = chargeStation.getBlockPos().relative(chargeStation.getBlockState().getValue(ElfChargeStationBlock.HORIZONTAL_FACING), 2).getCenter();
        this.lookControl.setLookAt(pos1.x, pos1.y, pos1.z);
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
        double motion = travelVector.length() * (getStress()/10f);
        extractCharge((float) motion);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        SantaLang.translate("gui.robo_elf.info_header").forGoggles(tooltip);
        SantaLang.translate("gui.robo_elf.title")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        IRotate.StressImpact.getFormattedStressText(getStress()/100f)
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
        compound.putInt("Stress", this.getStress());

        ListTag queue = new ListTag();
        for (Pair<UUID, TradeInfo> pair : this.queue) {
            CompoundTag entry = new CompoundTag();

            entry.putUUID("Owner", pair.getFirst());

            entry.putInt("Id", TRADES.indexOf(pair.getSecond()));
            queue.add(entry);
        }
        compound.put("TradesQueue", queue);

        ListTag crafted = new ListTag();
        for(Pair<UUID, ItemStack> pair : this.crafted) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Owner", pair.getFirst());
            entry.put("Item", pair.getSecond().save(this.registryAccess()));
            crafted.add(entry);
        }
        compound.put("CraftedItems", crafted);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Charge", Tag.TAG_ANY_NUMERIC))
            this.setCharge(compound.getFloat("Charge"));
        if(compound.contains("Stress", Tag.TAG_ANY_NUMERIC))
            this.setStress(compound.getInt("Stress"));
        queue.clear();
        for (Tag tag : compound.getList("TradesQueue", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) tag;

            UUID uuid = entry.getUUID("Owner");
            int index = entry.getInt("Id");
            if(index < 0 || index >= TRADES.size()) continue;
            TradeInfo info = TRADES.get(index);
            queue.offer(Pair.of(uuid, info));
        }
        crafted.clear();
        var list = compound.getList("CraftedItems", Tag.TAG_COMPOUND);
        for(int i = 0; i < Math.min(list.size(), MAX_QUEUE); i++) {
            CompoundTag entry = list.getCompound(i);
            UUID uuid = entry.getUUID("Owner");
            ItemStack itemStack = ItemStack.parseOptional(this.registryAccess(), entry.getCompound("Item"));
            crafted.offer(Pair.of(uuid, itemStack));
        }
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
            if(!state.getValue(ElfChargeStationBlock.HORIZONTAL_FACING).getOpposite().equals(direction)) continue;
            return pos.relative(direction);
        }
        return null;
    }

    public boolean isCharging() {
        return chargeStation != null;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(player.isShiftKeyDown() && insertCharge(1)<=0) {
            player.causeFoodExhaustion(2);
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(Items.COOKIE) && getCharge() == 0) {
            stress(-1);
            stack.consume(1, player);
            return InteractionResult.SUCCESS;
        } else if(stack.is(Items.ROTTEN_FLESH)) {
            stress(5);
            stack.consume(1, player);
            return InteractionResult.SUCCESS;
        }
        player.openMenu(this, buf -> {
            buf.writeCollection(TRADES, ($, info) -> TradeInfo.STREAM_CODEC.encode(buf, info));
            buf.writeInt(getId());
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void dropEquipment() {
        this.spawnAtLocation(getMainHandItem());
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RoboElfMenu(containerId, playerInventory, TRADES, this);
    }

    public void enqueueWork(int craftIndex, int amount, Player player) {
        if(queue.size()+amount >= MAX_QUEUE) return;
        if(crafted.size()+amount>=MAX_QUEUE) return;
        if(craftIndex < 0 || craftIndex >= TRADES.size()) return;
        TradeInfo info = TRADES.get(craftIndex);
        for(int i = 0; i < amount; i++)
            queue.offer(Pair.of(player.getUUID(), info));
        SantaAttachmentTypes.trust(player, amount*info.getTrustGain());
        //TODO: Extract items from player

    }

    public void setCrafted(UUID owner, ItemStack stack) {
        this.crafted.offer(Pair.of(owner, stack));
        //TODO: Sort!!
    }
}
