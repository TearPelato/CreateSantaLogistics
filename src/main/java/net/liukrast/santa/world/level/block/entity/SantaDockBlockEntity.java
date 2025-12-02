package net.liukrast.santa.world.level.block.entity;

import com.simibubi.create.AllSoundEvents;
import net.liukrast.santa.registry.SantaBlockEntityTypes;
import net.liukrast.santa.world.inventory.SantaDockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SantaDockBlockEntity extends BaseContainerBlockEntity {
    
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

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

}
