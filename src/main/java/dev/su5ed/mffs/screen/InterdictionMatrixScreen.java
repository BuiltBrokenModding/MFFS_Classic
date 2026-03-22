package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.menu.InterdictionMatrixMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SwitchConfiscationModePacket;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class InterdictionMatrixScreen extends FortronScreen<InterdictionMatrixMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/interdiction_matrix.png");

    public InterdictionMatrixScreen(InterdictionMatrixMenu menu, InventoryPlayer playerInventory) {
        super(menu, playerInventory, BACKGROUND);
        this.frequencyBoxX = 110;
        this.frequencyBoxY = 91;
        this.frequencyLabelX = 8;
        this.frequencyLabelY = 93;
        this.fortronEnergyBarX = 8;
        this.fortronEnergyBarY = 120;
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new TextButton(
            this.width / 2 - 80, this.height / 2 - 65, 50, 20,
            () -> ((InterdictionMatrixMenu) this.inventorySlots).blockEntity.getConfiscationMode().translation,
            () -> {
                InterdictionMatrixMenu menu = (InterdictionMatrixMenu) this.inventorySlots;
                InterdictionMatrix.ConfiscationMode mode = menu.blockEntity.getConfiscationMode().next();
                menu.blockEntity.setConfiscationMode(mode);
                Network.sendToServer(new SwitchConfiscationModePacket(menu.blockEntity.getPos(), mode));
            }
        ));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        InterdictionMatrixMenu menu = (InterdictionMatrixMenu) this.inventorySlots;

        this.fontRenderer.drawString(ModUtil.translate("screen", "warn_range",
            menu.blockEntity.getWarningRange()).getFormattedText(), 35, 19, GuiColors.DARK_GREY);
        this.fontRenderer.drawString(ModUtil.translate("screen", "action_range",
            menu.blockEntity.getActionRange()).getFormattedText(), 100, 19, GuiColors.DARK_GREY);
        this.fontRenderer.drawString(ModUtil.translate("screen", "filter_mode").getFormattedText(), 9, 32, GuiColors.DARK_GREY);

        drawWithTooltip(8, 110, GuiColors.DARK_GREY, "fortron",
            menu.blockEntity.fortronStorage.getStoredFortron(),
            menu.blockEntity.fortronStorage.getFortronCapacity());

        int fortronCostColor = 0xAA0000;
        this.fontRenderer.drawString(ModUtil.translate("screen", "fortron_cost", "-",
            menu.getClientFortronCost() * 20).getFormattedText(), 120, 122, fortronCostColor);

        if (menu.getClientBiometricWarning()) {
            int wx = 162, wy = 5;
            float scale = 1.25f;
            addTooltipRegion(wx, wy, (int) (this.fontRenderer.getStringWidth("⚠") * scale), (int) (this.fontRenderer.FONT_HEIGHT * scale),
                ModUtil.translate("info", "interdiction_matrix.biometric_warning"));
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

