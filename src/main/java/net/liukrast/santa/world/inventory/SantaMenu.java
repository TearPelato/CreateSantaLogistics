package net.liukrast.santa.world.inventory;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.liukrast.santa.registry.SantaMenuTypes;
import net.liukrast.santa.world.SantaContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SantaMenu extends AbstractContainerMenu {
    private final Container container;

    public SantaMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, playerInventory, new SimpleContainer(SantaContainer.COLS * SantaContainer.ROWS));
    }

    public SantaMenu(int containerId, Inventory playerInventory, Container container) {
        super(SantaMenuTypes.SANTA_MENU.get(), containerId);
        checkContainerSize(container, SantaContainer.COLS * SantaContainer.ROWS);
        this.container = container;
        container.startOpen(playerInventory.player);
        int i = (SantaContainer.ROWS-4)*18;
        for (int j = 0; j < SantaContainer.ROWS; j++) {
            for (int k = 0; k < SantaContainer.COLS; k++) {
                this.addSlot(new Slot(container, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return PackageItem.isPackage(stack);
                    }
                });
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 36 + 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(playerInventory, i1, 36 + 8 + i1 * 18, 161 + i));
        }
    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemstack = itemStack1.copy();
            if (index < SantaContainer.COLS * SantaContainer.ROWS) {
                if (!this.moveItemStackTo(itemStack1, SantaContainer.ROWS * SantaContainer.COLS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, SantaContainer.ROWS * SantaContainer.COLS, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
