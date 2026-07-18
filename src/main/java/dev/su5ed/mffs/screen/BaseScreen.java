package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.TooltipSlot;
import dev.su5ed.mffs.util.inventory.ColoredSlot;
import dev.su5ed.mffs.util.inventory.SlotActive;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private final Identifier background;
    private final List<TooltipCoordinate> tooltips = new ArrayList<>();

    public BaseScreen(T menu, Inventory playerInventory, Component title, Identifier background) {
        super(menu, playerInventory, title, 176, 217);

        this.background = background;
        this.height = 217;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(guiGraphics, mouseX, mouseY, partialTick);

        extractContents(guiGraphics, mouseX, mouseY, partialTick);
        extractCarriedItem(guiGraphics, mouseX, mouseY);
        extractSnapbackItem(guiGraphics);

        extractForeground(guiGraphics, mouseX, mouseY, partialTick);
        extractTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.background, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);

        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof TooltipSlot tooltipSlot && !this.hoveredSlot.hasItem()) {
            List<ClientTooltipComponent> clientTooltips = tooltipSlot.getTooltips().stream()
                .map(t -> ClientTooltipComponent.create(t.getVisualOrderText()))
                .toList();
            if (!clientTooltips.isEmpty()) {
                guiGraphics.tooltip(this.font, clientTooltips, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
            }
        } else if (!this.tooltips.isEmpty()) {
            StreamEx.of(this.tooltips)
                .filter(coord -> isHovering(coord.x, coord.y, coord.width, coord.height, mouseX, mouseY))
                .forEach(coord -> {
                    ClientTooltipComponent tooltip = ClientTooltipComponent.create(coord.tooltip.getVisualOrderText());
                    guiGraphics.tooltip(this.font, List.of(tooltip), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
                });
        }

        this.tooltips.clear();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, GuiColors.DARK_GREY, false);
    }

    public void extractForeground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY) {
        super.extractSlot(guiGraphics, slot, mouseX, mouseY);

        if (slot instanceof ColoredSlot colored && colored.shouldTint()) {
            guiGraphics.fill(RenderPipelines.GUI, slot.x - 1, slot.y - 1, slot.x + 17, slot.y + 17, colored.getTintColor());
        }
    }

    @Override
    protected boolean isHovering(Slot slot, double mouseX, double mouseY) {
        return (!(slot instanceof SlotActive slotActive) || !slotActive.isDisabled()) && super.isHovering(slot, mouseX, mouseY);
    }

    protected void drawWithTooltip(GuiGraphicsExtractor guiGraphics, float x, float y, int color, String name, Object... args) {
        drawWithTooltip(guiGraphics, x, y, color, ModUtil.translate("screen", name, args), ModUtil.translate("screen", name + ".tooltip"));
    }

    protected void drawWithTooltip(GuiGraphicsExtractor guiGraphics, float x, float y, int color, Component message, Component tooltip) {
        String text = message.getString();
        int width = this.font.width(text);
        int height = this.font.lineHeight;

        guiGraphics.text(this.font, text, (int) x, (int) y, color, false);
        this.tooltips.add(new TooltipCoordinate((int) x, (int) y, width, height, tooltip));
    }

    public record TooltipCoordinate(int x, int y, int width, int height, Component tooltip) {
    }
}
