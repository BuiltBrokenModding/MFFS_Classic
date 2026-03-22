package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SwitchTransferModePacket;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class FortronCapacitorScreen extends FortronScreen<FortronCapacitorMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/fortron_capacitor.png");

    public FortronCapacitorScreen(FortronCapacitorMenu menu, InventoryPlayer playerInventory) {
        super(menu, playerInventory, BACKGROUND);
        this.frequencyBoxX = 50;
        this.frequencyBoxY = 76;
        this.frequencyLabelX = 8;
        this.frequencyLabelY = 63;
        this.fortronEnergyBarX = 8;
        this.fortronEnergyBarY = 115;
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new IconCycleButton<>(
            this.width / 2 + 15, this.height / 2 - 37, 18, 18, 0, 0, 18,
            ((FortronCapacitorMenu) this.inventorySlots).blockEntity::getTransferMode,
            value -> Network.sendToServer(new SwitchTransferModePacket(
                ((FortronCapacitorMenu) this.inventorySlots).blockEntity.getPos(), value.next()))
        ));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        FortronCapacitorMenu menu = (FortronCapacitorMenu) this.inventorySlots;

        // Rotated "upgrade" label
        GlStateManager.pushMatrix();
        GlStateManager.translate(140, 95, 0);
        GlStateManager.rotate(-90.0F, 0, 0, 1);
        GlStateManager.translate(-140, -95, 0);
        this.fontRenderer.drawString(ModUtil.translate("screen", "upgrade").getFormattedText(), 140, 95, GuiColors.DARK_GREY);
        GlStateManager.popMatrix();

        this.fontRenderer.drawString(ModUtil.translate("screen", "linked_devices",
            menu.blockEntity.getDevicesByFrequency().size()).getFormattedText(), 8, 28, GuiColors.DARK_GREY);
        this.fontRenderer.drawString(ModUtil.translate("screen", "transmission_rate",
            menu.blockEntity.getTransmissionRate() * 2).getFormattedText(), 8, 40, GuiColors.DARK_GREY);
        this.fontRenderer.drawString(ModUtil.translate("screen", "range",
            menu.blockEntity.getTransmissionRange()).getFormattedText(), 8, 52, GuiColors.DARK_GREY);

        this.fontRenderer.drawString(ModUtil.translate("screen", "fortron.name").getFormattedText(), 8, 95, GuiColors.DARK_GREY);
        this.fontRenderer.drawString(ModUtil.translate("screen", "fortron.value",
            menu.blockEntity.fortronStorage.getStoredFortron(),
            menu.blockEntity.fortronStorage.getFortronCapacity()).getFormattedText(), 8, 105, GuiColors.DARK_GREY);
        this.fontRenderer.drawString(ModUtil.translate("screen", "fortron_cost", "-",
            menu.getClientFortronCost() * 20).getFormattedText(), 120, 117, 0xAA0000);
    }
}
