package net.liukrast.santa.world;

import net.liukrast.santa.SantaConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SantaContainer extends SavedData implements Container {
    public static final int ROWS = 6;
    public static final int COLS = 13;
    private final NonNullList<ItemStack> items = NonNullList.withSize(ROWS * COLS, ItemStack.EMPTY);

    public static SantaContainer get(ServerLevel level) {
        return level
                .getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(SantaContainer::new, SantaContainer::load), SantaConstants.MOD_ID + "_santa_container");
    }

    public static SantaContainer load(CompoundTag tag, HolderLookup.Provider registries) {
        SantaContainer container = new SantaContainer();
        ContainerHelper.loadAllItems(tag, container.items, registries);
        return container;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack itemstack = ContainerHelper.removeItem(items, slot, amount);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public void setChanged() {
        setDirty();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ContainerHelper.saveAllItems(tag, this.items, registries);
        return tag;
    }
}
