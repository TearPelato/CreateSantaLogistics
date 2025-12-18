package net.liukrast.santa.client.gui.screens;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.SantaLang;
import net.liukrast.santa.network.protocol.game.RoboElfCraftPacket;
import net.liukrast.santa.registry.SantaAttachmentTypes;
import net.liukrast.santa.world.entity.TradeInfo;
import net.liukrast.santa.world.inventory.RoboElfMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class RoboElfScreen extends AbstractSimiContainerScreen<RoboElfMenu> {
    private static final ResourceLocation TEXTURE = SantaConstants.id("textures/gui/robo_elf.png");

    private static final Component TRUST_LEVEL = Component.translatable("container.santa_logistics.robo_elf.trust_level");
    private static final Component TRUST_GAIN = Component.translatable("container.santa_logistics.robo_elf.trust_gain");
    private static final Component ENERGY_USAGE = Component.translatable("container.santa_logistics.robo_elf.energy_usage");
    private static final Component PROCESS_TIME = Component.translatable("container.santa_logistics.robo_elf.process_time");

    private int selected = -1;
    private int scroll = 0;
    private int lastTrust = 0;
    private final int questsPerPage;
    int queueAmount = 1;
    private IconButton confirmButton;
    public RoboElfScreen(RoboElfMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        questsPerPage = 3;
        imageHeight = questsPerPage*24+42+115;
        imageWidth = 224;
    }

    @Override
    protected void init() {
        super.init();
        confirmButton = new IconButton(leftPos+imageWidth-40, topPos+112, AllIcons.I_3x3);
        confirmButton.withCallback(() -> {
            if(selected < 0 || selected >= menu.trades.size()) return;
            PacketDistributor.sendToServer(new RoboElfCraftPacket(menu.entity.getId(), selected, queueAmount));
        });
        confirmButton.setToolTip(SantaLang.translate("gui.robo_elf.queue").component());
        confirmButton.visible = selected > 0 && selected < menu.trades.size();
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var mc = Minecraft.getInstance();
        assert mc.player != null;
        int real = mc.player.getData(SantaAttachmentTypes.TRUST);
        int re = Math.round((lastTrust+real)/2f);
        lastTrust = Math.abs(real-re) <= 1 ? real : re;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int guiLeft = getGuiLeft();
        int guiTop = getGuiTop();
        guiGraphics.blit(TEXTURE, guiLeft, guiTop, 0, 0, 224, 42);

        /* TRUST */
        String trust = lastTrust + "☺";
        int tw = font.width(trust);
        int st = guiLeft+imageWidth-24-tw;
        guiGraphics.blit(TEXTURE, st, guiTop+20, 224, 38, 8, 16);
        for(int i = 0; i < tw; i++) {
            guiGraphics.blit(TEXTURE, st+8+i, guiTop+ 20, 232, 38, 1, 16);
        }
        guiGraphics.blit(TEXTURE, st+8+tw, guiTop+20, 233, 38, 8, 16);
        renderTextWithTooltip(guiGraphics, trust, st+8, guiTop+24, 0xb59370, TRUST_LEVEL, mouseX, mouseY);

        /* TRADES */
        for(int i = 0; i < questsPerPage; i++) {
            guiGraphics.blit(TEXTURE, guiLeft, guiTop + 42 + 24*i, 0, 42, 224, 24);
            if(i+scroll >= menu.trades.size()) continue;
            TradeInfo info = menu.trades.get(i+scroll);
            var ing = info.getIngredients();

            for(int k = 0; k < ing.length; k++) {
                ItemStack c = ing[k];
                if(c.isEmpty()) continue;
                renderItem(guiGraphics, c,guiLeft + 25 + k*20, guiTop + 45 + 24*i, mouseX, mouseY);
            }

            renderTextWithTooltip(
                    guiGraphics,
                    "+" + info.getTrustGain() + "☺",
                    guiLeft + 85, guiTop + 51 + 24*i,
                    0x603d39, TRUST_GAIN,
                    mouseX, mouseY
            );

            renderTextWithTooltip(
                    guiGraphics,
                    "-" + info.getEnergy() + "⚡",
                    guiLeft + 85+30, guiTop + 51 + 24*i,
                    0x603d39, ENERGY_USAGE,
                    mouseX, mouseY
            );

            renderTextWithTooltip(
                    guiGraphics,
                    info.getProcessTime() + "⌚",
                    guiLeft + 85+60, guiTop + 51 + 24*i,
                    0x603d39, PROCESS_TIME,
                    mouseX, mouseY
            );
            renderItem(guiGraphics, info.getResult(), guiLeft + 181, guiTop + 45 + 24*i, mouseX, mouseY);
            if(selected == i+scroll || inBox(mouseX, mouseY, guiLeft, guiTop + 42 + 24*i, 224, 24))
                guiGraphics.blit(TEXTURE, guiLeft+23, guiTop + 44 + 24*i, 23, 181, 178, 20);
        }
        guiGraphics.blit(TEXTURE, guiLeft, guiTop+42+24*questsPerPage, 0, 66, 224, 115);
        if(scroll > 0) {
            guiGraphics.blit(TEXTURE, guiLeft+(imageWidth>>1)-12, guiTop+16, 229, 9, 24, 10);
        }
        if(scroll<menu.trades.size()-3) {
            guiGraphics.blit(TEXTURE, guiLeft+(imageWidth>>1)-12, guiTop+38+questsPerPage*24, 229, 0, 24, 10);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        int guiLeft = getGuiLeft();
        int guiTop = getGuiTop();
        for(int i = 0; i < questsPerPage; i++) {
            if(i+scroll >= menu.trades.size()) continue;
            if(inBox((int) mouseX, (int) mouseY, guiLeft, guiTop + 42 + 24*i, 224, 24)) {
                selected = i+scroll;
                confirmButton.visible = true;
                return true;
            }
        }
        super.mouseClicked(mouseX, mouseY, pButton);
        selected = -1;
        confirmButton.visible = false;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Math.clamp((int)(scroll-scrollY), 0, menu.trades.size()-3);
        return true;
    }

    private boolean inBox(int x, int y, int u, int v, int w, int h) {
        return x >= u && x < u+w && y >= v && y < v+h;
    }

    private void renderTextWithTooltip(GuiGraphics guiGraphics, String text, int x, int y, int color, Component tooltip, int mouseX, int mouseY) {
        int w = font.width(text);
        guiGraphics.drawString(font, text, x, y, color, false);
        if(inBox(mouseX, mouseY, x, y, w, font.lineHeight))
            guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
    }

    private void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, x, y, 224, 20, 18, 18);
        graphics.renderItem(stack, x+1, y+1);
        graphics.renderItemDecorations(font, stack, x+1, y+1);
        if(inBox(mouseX, mouseY, x, y, 18, 18))
            graphics.renderTooltip(font, stack, mouseX, mouseY);
    }
}
