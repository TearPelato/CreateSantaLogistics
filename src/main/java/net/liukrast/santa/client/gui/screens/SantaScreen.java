package net.liukrast.santa.client.gui.screens;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.SantaContainer;
import net.liukrast.santa.world.inventory.SantaMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SantaScreen extends AbstractContainerScreen<SantaMenu> {
    private static final ResourceLocation TEXTURE = SantaConstants.id("textures/gui/santa.png");

    public SantaScreen(SantaMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 248;
        this.imageHeight = 114 + SantaContainer.ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 42;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 248, 222);
        graphics.drawString(font, playerInventoryTitle, leftPos + inventoryLabelX, topPos + inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
