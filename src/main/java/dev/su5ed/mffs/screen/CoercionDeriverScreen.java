package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity.EnergyMode;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SwitchEnergyModePacket;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoercionDeriverScreen extends FortronScreen<CoercionDeriverMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/coercion_deriver.png");

    private static final int BATTERY_SLOT_X = 9;
    private static final int BATTERY_SLOT_Y = 83;
    private static final int FUEL_SLOT_X = 29;
    private static final int FUEL_SLOT_Y = 83;
    private static final int CATALYST_LIST_LIMIT = 8;
    private static final DecimalFormat MULT_FORMAT = new DecimalFormat("#.##");

    public CoercionDeriverScreen(CoercionDeriverMenu menu, InventoryPlayer playerInventory) {
        super(menu, playerInventory, BACKGROUND);
        this.frequencyBoxX = 30;
        this.frequencyBoxY = 43;
        this.frequencyLabelX = 8;
        this.frequencyLabelY = 30;
        this.fortronEnergyBarX = 8;
        this.fortronEnergyBarY = 115;
        this.fortronEnergyBarWidth = 103;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new TextButton(
            this.width / 2 - 10, this.height / 2 - 28, 58, 20,
            () -> ((CoercionDeriverMenu) this.inventorySlots).blockEntity.getEnergyMode().translate(),
            () -> {
                CoercionDeriverMenu menu = (CoercionDeriverMenu) this.inventorySlots;
                EnergyMode mode = menu.blockEntity.getEnergyMode().next();
                menu.blockEntity.setEnergyMode(mode);
                Network.sendToServer(new SwitchEnergyModePacket(menu.blockEntity.getPos(), mode));
            }
        ));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        CoercionDeriverMenu menu = (CoercionDeriverMenu) this.inventorySlots;

        // Rotated "upgrade" label
        GlStateManager.pushMatrix();
        GlStateManager.translate(140, 95, 0);
        GlStateManager.rotate(-90.0F, 0, 0, 1);
        GlStateManager.translate(-140, -95, 0);
        this.fontRenderer.drawString(ModUtil.translate("screen", "upgrade").getFormattedText(), 140, 95, GuiColors.DARK_GREY);
        GlStateManager.popMatrix();

        // Progress status
        String progressKey = "progress." + (menu.blockEntity.isActive() ? "running" : "idle");
        String progressText = ModUtil.translate("screen", "progress").getFormattedText()
            + ModUtil.translate("screen", progressKey).getFormattedText();
        this.fontRenderer.drawString(progressText, 8, 70, GuiColors.DARK_GREY);

        // Fortron stored
        int energy = menu.blockEntity.fortronStorage.getStoredFortron();
        this.fontRenderer.drawString(ModUtil.translate("screen", "fortron.short", energy).getFormattedText(), 8, 105, GuiColors.DARK_GREY);

        // Fortron cost
        boolean inversed = menu.blockEntity.isInversed();
        int displayFortron = menu.blockEntity.fortronProducedLastTick * ModUtil.TICKS_PER_SECOND;
        String sign = inversed ? "-" : "+";
        int costColor = inversed ? 0xAA0000 : 0x00AA00;
        this.fontRenderer.drawString(ModUtil.translate("screen", "fortron_cost", sign, displayFortron).getFormattedText(), 114, 117, costColor);
    }

    @Override
    protected void renderCustomTooltips(int mouseX, int mouseY) {
        super.renderCustomTooltips(mouseX, mouseY);

        CoercionDeriverMenu menu = (CoercionDeriverMenu) this.inventorySlots;
        // Only show when the player isn't holding an item and the fuel slot is empty
        if (!this.mc.player.inventory.getItemStack().isEmpty()) return;

        // Battery slot: always show the current FE-to-Fortron conversion rate
        int batteryScreenX = this.guiLeft + BATTERY_SLOT_X;
        int batteryScreenY = this.guiTop + BATTERY_SLOT_Y;
        if (mouseX >= batteryScreenX && mouseX < batteryScreenX + 16
                && mouseY >= batteryScreenY && mouseY < batteryScreenY + 16) {
            int rate = MFFSConfig.coercionDriverFePerFortron;
            drawHoveringText(
                java.util.Collections.singletonList(
                    TextFormatting.GRAY + "Rate: " + TextFormatting.YELLOW + rate
                        + TextFormatting.GRAY + " FE -> " + TextFormatting.YELLOW + "1 Fortron"),
                mouseX, mouseY);
        }

        // Fuel slot: show catalyst list only when slot is empty
        if (!menu.blockEntity.fuelSlot.isEmpty()) return;

        int slotScreenX = this.guiLeft + FUEL_SLOT_X;
        int slotScreenY = this.guiTop + FUEL_SLOT_Y;
        if (mouseX >= slotScreenX && mouseX < slotScreenX + 16
                && mouseY >= slotScreenY && mouseY < slotScreenY + 16) {
            // Collect only entries whose item is currently registered
            List<MFFSConfig.CatalystEntry> valid = new ArrayList<>();
            for (MFFSConfig.CatalystEntry e : MFFSConfig.catalystEntries) {
                if (e.toItemStack() != null) valid.add(e);
            }
            if (!valid.isEmpty()) {
                renderCatalystTooltip(mouseX, mouseY, valid);
            }
        }
    }

    private void renderCatalystTooltip(int mouseX, int mouseY, List<MFFSConfig.CatalystEntry> entries) {
        final float SCALE = 0.8F;

        int maxShow = Math.min(CATALYST_LIST_LIMIT, entries.size());
        int remainder = entries.size() - maxShow;

        List<ItemStack> icons = new ArrayList<>(maxShow + 1);
        List<String> lines  = new ArrayList<>(maxShow + 1);
        for (int i = 0; i < maxShow; i++) {
            MFFSConfig.CatalystEntry e = entries.get(i);
            ItemStack stack = e.toItemStack(); // non-null: already validated
            double pct = e.multiplier * 100.0;
            icons.add(stack);
            lines.add(TextFormatting.WHITE + stack.getDisplayName()
                + " " + TextFormatting.DARK_GRAY + e.burnTicks + "t"
                + " " + TextFormatting.GREEN + "+" + MULT_FORMAT.format(pct) + "%");
        }
        if (remainder > 0) {
            icons.add(null);
            lines.add(TextFormatting.GRAY + "+ " + remainder + " more");
        }

        String header = TextFormatting.GOLD + "Catalysts:";
        final int iconW = 18; // logical: 16px icon + 2px gap (shrinks to ~14px on screen)
        final int rowH  = 16; // logical row height — tighter than 18, items still fit
        final int pad   = 4;
        final int fontH = this.fontRenderer.FONT_HEIGHT;

        int maxTextW = this.fontRenderer.getStringWidth(header);
        for (String s : lines) {
            int w = iconW + this.fontRenderer.getStringWidth(s);
            if (w > maxTextW) maxTextW = w;
        }
        int tooltipW = maxTextW + pad * 2;
        int tooltipH = (fontH + 2) + 3 + lines.size() * rowH + pad * 2;

        // Boundary clamping uses screen-space dimensions (logical × SCALE)
        int screenW = Math.round(tooltipW * SCALE);
        int screenH = Math.round(tooltipH * SCALE);

        int ttX = mouseX + 12;
        int ttY = mouseY - 12;
        if (ttX + screenW + 6 > this.width)  ttX = mouseX - screenW - 12;
        if (ttY + screenH + 8 > this.height) ttY = this.height - screenH - 8;
        if (ttY < 4) ttY = 4;

        // Convert clamped screen position to logical coords (used for all drawing below)
        int lX = Math.round(ttX / SCALE);
        int lY = Math.round(ttY / SCALE);

        this.zLevel = 300.0F;
        this.mc.getRenderItem().zLevel = 300.0F;

        GlStateManager.pushMatrix();
        GlStateManager.scale(SCALE, SCALE, 1.0F);

        // ---- Draw tooltip background ----
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();

        int bg      = 0xF0100010;
        int borderA = 0x505000FF;
        int borderB = (borderA & 0xFEFEFE) >> 1 | (borderA & 0xFF000000);
        drawGradientRect(lX - 3, lY - 4,             lX + tooltipW + 3, lY - 3,             bg, bg);
        drawGradientRect(lX - 3, lY + tooltipH + 3,  lX + tooltipW + 3, lY + tooltipH + 4,  bg, bg);
        drawGradientRect(lX - 3, lY - 3,             lX + tooltipW + 3, lY + tooltipH + 3,  bg, bg);
        drawGradientRect(lX - 4, lY - 3,             lX - 3,            lY + tooltipH + 3,  bg, bg);
        drawGradientRect(lX + tooltipW + 3, lY - 3,  lX + tooltipW + 4, lY + tooltipH + 3,  bg, bg);
        drawGradientRect(lX - 3,            lY - 2,  lX - 2,            lY + tooltipH + 2,  borderA, borderB);
        drawGradientRect(lX + tooltipW + 2, lY - 2,  lX + tooltipW + 3, lY + tooltipH + 2,  borderA, borderB);
        drawGradientRect(lX - 3, lY - 3,             lX + tooltipW + 3, lY - 2,             borderA, borderA);
        drawGradientRect(lX - 3, lY + tooltipH + 2,  lX + tooltipW + 3, lY + tooltipH + 3,  borderB, borderB);

        // ---- Header ----
        this.fontRenderer.drawStringWithShadow(header, lX + pad, lY + pad, 0xFFFFFF);

        // ---- Icons ----
        GlStateManager.enableDepth();
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();

        int rowStartY = lY + pad + fontH + 5;
        for (int i = 0; i < icons.size(); i++) {
            ItemStack icon = icons.get(i);
            if (icon != null) {
                this.mc.getRenderItem().renderItemAndEffectIntoGUI(icon, lX + pad, rowStartY + i * rowH);
            }
        }

        // ---- Text for each row ----
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();

        for (int i = 0; i < lines.size(); i++) {
            int textX = (icons.get(i) != null) ? lX + pad + iconW : lX + pad;
            int textY = rowStartY + i * rowH + (rowH - fontH) / 2;
            this.fontRenderer.drawStringWithShadow(lines.get(i), textX, textY, 0xFFFFFF);
        }

        // ---- Restore GL state ----
        GlStateManager.enableDepth();
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();

        this.zLevel = 0.0F;
        this.mc.getRenderItem().zLevel = 0.0F;
    }
}

