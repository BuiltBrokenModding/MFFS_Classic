package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.util.TooltipSlot;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.ColoredSlot;
import dev.su5ed.mffs.util.inventory.SlotActive;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseScreen<T extends Container> extends GuiContainer {
    protected final ResourceLocation background;
    private final List<TooltipCoordinate> tooltips = new ArrayList<>();
    private int lastMouseX, lastMouseY;

    public BaseScreen(T menu, InventoryPlayer playerInventory, ResourceLocation background) {
        super(menu);
        this.background = background;
        this.ySize = 217;
    }

    @Override
    public void initGui() {
        super.initGui();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        this.tooltips.clear(); // repopulated each frame by drawGuiContainerForegroundLayer
        super.drawScreen(mouseX, mouseY, partialTick);
        renderFg(mouseX, mouseY, partialTick);
        // Tooltip rendering is deferred to onDrawScreenPost so it runs after JEI/HEI overlays
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    /** Renders tooltips after all overlays (including JEI/HEI) have drawn. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() != this) return;
        renderCustomTooltips(this.lastMouseX, this.lastMouseY);
        renderHoveredToolTip(this.lastMouseX, this.lastMouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.background);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        // Draw colored slot tints
        for (Slot slot : this.inventorySlots.inventorySlots) {
            if (slot instanceof ColoredSlot colored && colored.shouldTint()) {
                Gui.drawRect(
                    this.guiLeft + slot.xPos - 1, this.guiTop + slot.yPos - 1,
                    this.guiLeft + slot.xPos + 17, this.guiTop + slot.yPos + 17,
                    colored.getTintColor()
                );
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Center title
        int titleX = (this.xSize - this.fontRenderer.getStringWidth(getTitle())) / 2;
        this.fontRenderer.drawString(getTitle(), titleX, 6, GuiColors.DARK_GREY);
    }

    /** Returns the screen title as a plain string. Override in subclasses if needed. */
    protected String getTitle() {
        if (this.inventorySlots instanceof FortronMenu<?> fm) {
            return fm.blockEntity.getDisplayName().getFormattedText();
        }
        return "";
    }

    /** Called after super.drawScreen() for additional foreground rendering. */
    public void renderFg(int mouseX, int mouseY, float partialTick) {}

    /** Renders custom tooltips registered via drawWithTooltip(). */
    protected void renderCustomTooltips(int mouseX, int mouseY) {
        // Slot tooltips (TooltipSlot with no item)
        if (this.mc.player.inventory.getItemStack().isEmpty()) {
            int relX = mouseX - this.guiLeft;
            int relY = mouseY - this.guiTop;
            for (Slot slot : this.inventorySlots.inventorySlots) {
                if (slot instanceof TooltipSlot tooltipSlot && !slot.getHasStack()) {
                    if (relX >= slot.xPos && relX < slot.xPos + 16 && relY >= slot.yPos && relY < slot.yPos + 16) {
                        List<String> tooltipLines = new ArrayList<>();
                        for (ITextComponent comp : tooltipSlot.getTooltips()) {
                            tooltipLines.add(comp.getFormattedText());
                        }
                        if (!tooltipLines.isEmpty()) {
                            drawHoveringText(tooltipLines, mouseX, mouseY);
                        }
                        break;
                    }
                }
            }
        }
        // Custom region tooltips
        if (!this.tooltips.isEmpty()) {
            for (TooltipCoordinate coord : this.tooltips) {
                if (mouseX >= this.guiLeft + coord.x && mouseX < this.guiLeft + coord.x + coord.width
                        && mouseY >= this.guiTop + coord.y && mouseY < this.guiTop + coord.y + coord.height) {
                        drawHoveringText(Collections.singletonList(coord.tooltip.getFormattedText()), mouseX, mouseY);
                    break;
                }
            }
            this.tooltips.clear();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof BaseButton base) {
            base.firePress();
        } else if (button instanceof IconCycleButton<?> cycle) {
            cycle.firePress();
        } else if (button instanceof IconToggleButton toggle) {
            toggle.firePress();
        } else if (button instanceof TextButton text) {
            text.firePress();
        }
    }

    protected void drawWithTooltip(int x, int y, int color, String name, Object... args) {
        drawWithTooltip(x, y, color, ModUtil.translate("screen", name, args), ModUtil.translate("screen", name + ".tooltip"));
    }

    protected void drawWithTooltip(int x, int y, int color, ITextComponent message, ITextComponent tooltip) {
        String text = message.getFormattedText();
        this.fontRenderer.drawString(text, x, y, color);
        int width = this.fontRenderer.getStringWidth(text);
        int height = this.fontRenderer.FONT_HEIGHT;
        this.tooltips.add(new TooltipCoordinate(x, y, width, height, tooltip));
    }

    /** Registers a hover tooltip for an arbitrary screen region without drawing any text. */
    protected void addTooltipRegion(int x, int y, int width, int height, ITextComponent tooltip) {
        this.tooltips.add(new TooltipCoordinate(x, y, width, height, tooltip));
    }

    // Hovered-button tooltips (IconToggleButton) + disabled-slot item tooltips
    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        // Vanilla item tooltip for enabled slots
        super.renderHoveredToolTip(mouseX, mouseY);

        // Disabled slots (SlotActive.isEnabled()==false) are skipped by GuiContainer's
        // hover detection, so we render their tooltip manually.
        if (this.mc.player.inventory.getItemStack().isEmpty() && getSlotUnderMouse() == null) {
            for (Slot slot : this.inventorySlots.inventorySlots) {
                if (!slot.isEnabled() && slot.getHasStack() && isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY)) {
                    this.renderToolTip(slot.getStack(), mouseX, mouseY);
                    break;
                }
            }
        }

        for (GuiButton button : this.buttonList) {
            if (button instanceof IconToggleButton itb && itb.isHovered()) {
                drawHoveringText(Collections.singletonList(itb.getTooltip().getFormattedText()), mouseX, mouseY);
                break;
            }
        }
    }

    public static class TooltipCoordinate {
        public final int x, y, width, height;
        public final ITextComponent tooltip;

        public TooltipCoordinate(int x, int y, int width, int height, ITextComponent tooltip) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.tooltip = tooltip;
        }
    }
}

