package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.TooltipSlot;
import dev.su5ed.mffs.util.inventory.ColoredSlot;
import dev.su5ed.mffs.util.inventory.SlotActive;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private final ResourceLocation background;
    private final List<TooltipCoordinate> tooltips = new ArrayList<>();

    public BaseScreen(T menu, Inventory playerInventory, Component title, ResourceLocation background) {
        super(menu, playerInventory, title);

        this.background = background;
        this.height = this.imageHeight = 217;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderFg(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(this.background, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof TooltipSlot tooltipSlot && !this.hoveredSlot.hasItem()) {
            guiGraphics.renderComponentTooltip(this.font, tooltipSlot.getTooltips(), mouseX, mouseY);
        } else if (!this.tooltips.isEmpty()) {
            StreamEx.of(this.tooltips)
                .filter(coord -> isHovering(coord.x, coord.y, coord.width, coord.height, mouseX, mouseY))
                .forEach(coord -> guiGraphics.renderComponentTooltip(this.font, List.of(coord.tooltip), mouseX, mouseY));
        }

        this.tooltips.clear();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, GuiColors.DARK_GREY, false);
    }

    public void renderFg(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);
        if (slot instanceof ColoredSlot colored && colored.shouldTint()) {
            guiGraphics.fill(colored.tintItems() ? RenderType.guiOverlay() : RenderType.gui(), slot.x - 1, slot.y - 1, slot.x + 17, slot.y + 17, colored.getTintColor());
        }
    }

    @Override
    protected boolean isHovering(Slot slot, double mouseX, double mouseY) {
        return (!(slot instanceof SlotActive slotActive) || !slotActive.isDisabled()) && super.isHovering(slot, mouseX, mouseY);
    }

    protected void drawWithTooltip(GuiGraphics guiGraphics, float x, float y, int color, String name, Object... args) {
        drawWithTooltip(guiGraphics, x, y, color, ModUtil.translate("screen", name, args), ModUtil.translate("screen", name + ".tooltip"));
    }

    protected void drawWithTooltip(GuiGraphics guiGraphics, float x, float y, int color, Component message, Component tooltip) {
        String text = message.getString();
        int width = this.font.width(text);
        int height = this.font.lineHeight;

        guiGraphics.drawString(this.font, text, (int) x, (int) y, color, false);
        this.tooltips.add(new TooltipCoordinate((int) x, (int) y, width, height, tooltip));
    }

    public record TooltipCoordinate(int x, int y, int width, int height, Component tooltip) {}
}
