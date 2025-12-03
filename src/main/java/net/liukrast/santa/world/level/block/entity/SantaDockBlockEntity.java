package net.liukrast.santa.world.level.block.entity;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import net.liukrast.santa.registry.SantaBlockEntityTypes;
import net.liukrast.santa.world.inventory.SantaDockMenu;
import net.liukrast.santa.world.level.block.SantaDockBlock;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@NonnullDefault
public class SantaDockBlockEntity extends BaseContainerBlockEntity implements IHaveGoggleInformation {
    private SantaDocks.AddStatus status = SantaDocks.AddStatus.SUCCESS;

    /* CONTAINER */
    private NonNullList<ItemStack> items = NonNullList.withSize(18, ItemStack.EMPTY);
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            SantaDockBlockEntity.this.playSound(AllSoundEvents.DEPOT_PLOP.getMainEvent());
        }

        protected void onClose(Level level, BlockPos pos, BlockState state) {
            SantaDockBlockEntity.this.playSound(AllSoundEvents.DEPOT_PLOP.getMainEvent());
        }

        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int a, int b) {
        }

        protected boolean isOwnContainer(Player p_155060_) {
            if (p_155060_.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu)p_155060_.containerMenu).getContainer();
                return container == SantaDockBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public SantaDockBlockEntity(BlockPos pos, BlockState blockState) {
        super(SantaBlockEntityTypes.SANTA_DOCK.get(), pos, blockState);
    }

    /* NBT */
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("status", status.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        status = SantaDocks.AddStatus.values()[tag.getInt("status")];
    }

    /* SYNC */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag = super.getUpdateTag(registries);
        tag.putInt("status", status.ordinal());
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    /* CONTAINER */

    @Override
    public int getContainerSize() {
        return 18;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SantaDockMenu(containerId, inventory, this, getBlockPos(), null);
    }

    public void startOpen(Player player) {
        if(this.remove || player.isSpectator() || level == null) return;
        this.openersCounter.incrementOpeners(player, level, this.getBlockPos(), this.getBlockState());
    }

    public void stopOpen(Player player) {
        if(this.remove || player.isSpectator() || level == null) return;
        this.openersCounter.decrementOpeners(player, level, this.getBlockPos(), this.getBlockState());
    }

    public void recheckOpen() {
        if(this.remove || level == null) return;
        this.openersCounter.recheckOpeners(level, this.getBlockPos(), this.getBlockState());
    }

    void playSound(SoundEvent pSound) {
        if(level != null) {
            double d0 = (double) this.worldPosition.getX() + 0.5D;
            double d1 = (double) this.worldPosition.getY() + 0.5D;
            double d2 = (double) this.worldPosition.getZ() + 0.5D;
            this.level.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        var state = getBlockState().getValue(SantaDockBlock.STATE);
        tooltip.add(Component.literal("    ").append(Component.translatable(
                "block.santa_logistics.santa_dock.tooltip_0",
                Component.translatable("block.santa_logistics.santa_dock.status." + state.getSerializedName())
        )));
        if(state == SantaDockBlock.State.CONNECTED) {
            tooltip.add(Component.literal("    ").append(Component.translatable("block.santa_logistics.santa_dock.status.connected.tooltip_0")));
        } else if(state == SantaDockBlock.State.ERROR) {
            tooltip.add(Component.translatable("block.santa_logistics.santa_dock.status.error.tooltip_0"));
            tooltip.add(Component.translatable("block.santa_logistics.santa_dock.status.error."+status.toString().toLowerCase()));
        }
        return true;
    }

    public void setStatus(SantaDocks.AddStatus result) {
        this.status = result;
        setChanged();
        if (level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(getBlockPos());
    }

    public void playEffect() {
        var pos = this.worldPosition;
        AllSoundEvents.STOCK_LINK.playAt(level, worldPosition, 1.0f, 1.0f, false);
        level.addParticle(new WiFiParticle.Data(), pos.getX(), pos.getY()+0.75f, pos.getZ()+0.5f, 1, 1, 1);
    }
}
