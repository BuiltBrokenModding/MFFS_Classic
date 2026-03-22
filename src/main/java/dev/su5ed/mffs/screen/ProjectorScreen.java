package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ProjectorScreen extends FortronScreen<ProjectorMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/projector.png");

    public ProjectorScreen(ProjectorMenu menu, InventoryPlayer playerInventory) {
        super(menu, playerInventory, BACKGROUND);
        this.frequencyBoxX = 48;
        this.frequencyBoxY = 91;
        this.frequencyLabelX = 9;
        this.frequencyLabelY = 78;
        this.fortronEnergyBarX = 8;
        this.fortronEnergyBarY = 120;
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        ProjectorMenu menu = (ProjectorMenu) this.inventorySlots;

        drawWithTooltip(32, 20, GuiColors.DARK_GREY, "matrix");
        drawWithTooltip(8, 110, GuiColors.DARK_GREY, "fortron",
            menu.blockEntity.fortronStorage.getStoredFortron(),
            menu.blockEntity.fortronStorage.getFortronCapacity());

        int cost = menu.getClientFortronCost() * 20;
        if (cost > 0) {
            String costText = TextFormatting.DARK_RED
                + ModUtil.translate("screen", "fortron_cost", "-", cost).getUnformattedText();
            this.fontRenderer.drawString(costText, 117, 122, GuiColors.DARK_GREY);
        }

        if (menu.getClientBiometricWarning()) {
            int wx = 162, wy = 5;
            float scale = 1.25f;
            addTooltipRegion(wx, wy, (int) (this.fontRenderer.getStringWidth("⚠") * scale), (int) (this.fontRenderer.FONT_HEIGHT * scale),
                ModUtil.translate("info", "projector.biometric_warning"));
            if ((System.currentTimeMillis() / 500) % 2 == 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(wx, wy, 0);
                GlStateManager.scale(scale, scale, 1.0f);
                this.fontRenderer.drawString(TextFormatting.RED + "⚠", 0, 0, 0xFFFFFF);
                GlStateManager.popMatrix();
            }
        }
    }
}
