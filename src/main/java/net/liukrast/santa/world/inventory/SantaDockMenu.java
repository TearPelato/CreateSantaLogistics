package net.liukrast.santa.world.inventory;

import net.liukrast.santa.registry.SantaMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SantaDockMenu extends AbstractContainerMenu {
    private final Container container;
    public final BlockPos pos;
    @Nullable
    public final String address;

    public SantaDockMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, new SimpleContainer(18), buf.readBlockPos(), buf.readUtf());
    }

    public SantaDockMenu(int containerId, Inventory playerInventory, Container container, BlockPos pos, @Nullable String address) {
        super(SantaMenuTypes.SANTA_DOCK.get(), containerId);
        checkContainerSize(container, 18);
        this.container = container;
        this.address = address;
        this.pos = pos;
        container.startOpen(playerInventory.player);
        int i = (-2) * 18;
        for(int j = 0; j < 2; j++) {
            for(int k = 0; k < 9; k++) {
                this.addSlot(new Slot(container, k + j * 9, 16 + k * 18, 19 + j * 18));
            }
        }
        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 16 + j1 * 18, 139 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(playerInventory, i1, 16 + i1 * 18, 197 + i));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (index < 2 * 9) {
                if (!this.moveItemStackTo(itemStack1, 2 * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, 2 * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
