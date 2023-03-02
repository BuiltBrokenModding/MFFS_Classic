package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.TooltipSlot;
import dev.su5ed.mffs.util.inventory.SlotActive;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
    public final void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderFg(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, this.background);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof TooltipSlot tooltipSlot && !this.hoveredSlot.hasItem()) {
            renderComponentTooltip(poseStack, tooltipSlot.getTooltips(), mouseX, mouseY);
        } else if (!this.tooltips.isEmpty()) {
            StreamEx.of(this.tooltips)
                .filter(coord -> isHovering(coord.x, coord.y, coord.width, coord.height, mouseX, mouseY))
                .forEach(coord -> renderComponentTooltip(poseStack, List.of(coord.tooltip), mouseX, mouseY));
        }

        this.tooltips.clear();
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, GuiColors.DARK_GREY);
    }

    public void renderFg(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderSlot(PoseStack poseStack, Slot slot) {
        super.renderSlot(poseStack, slot);
        if (slot instanceof SlotActive slotActive && slotActive.isDisabled()) {
            RenderSystem.disableDepthTest();
            fill(poseStack, slot.x - 1, slot.y - 1, slot.x + 17, slot.y + 17, getDisabledSlotColor());
        }
    }

    public int getDisabledSlotColor() {
        return GuiColors.DISABLED_SLOT_OVERLAY;
    }

    @Override
    protected boolean isHovering(Slot slot, double mouseX, double mouseY) {
        return (!(slot instanceof SlotActive slotActive) || !slotActive.isDisabled()) && super.isHovering(slot, mouseX, mouseY);
    }

    protected void drawWithTooltip(PoseStack poseStack, float x, float y, int color, String name, Object... args) {
        drawWithTooltip(poseStack, x, y, color, ModUtil.translate("screen", name, args), ModUtil.translate("screen", name + ".tooltip"));
    }

    protected void drawWithTooltip(PoseStack poseStack, float x, float y, int color, Component message, Component tooltip) {
        String text = message.getString();
        int width = this.font.width(text);
        int height = this.font.lineHeight;

        this.font.draw(poseStack, text, x, y, color);
        this.tooltips.add(new TooltipCoordinate((int) x, (int) y, width, height, tooltip));
    }

    public record TooltipCoordinate(int x, int y, int width, int height, Component tooltip) {}
}
