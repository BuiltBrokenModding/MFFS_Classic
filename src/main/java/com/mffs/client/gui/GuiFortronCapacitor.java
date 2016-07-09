package com.mffs.client.gui;

import com.mffs.MFFS;
import com.mffs.api.fortron.IFortronFrequency;
import com.mffs.api.utils.UnitDisplay;
import com.mffs.client.buttons.TransferModeButton;
import com.mffs.common.container.FortronCapacitorContainer;
import com.mffs.common.net.packet.EntityToggle;
import com.mffs.common.tile.type.TileFortronCapacitor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class GuiFortronCapacitor extends MFFSGui {

    /**
     * @param player
     * @param cap
     */
    public GuiFortronCapacitor(EntityPlayer player, TileFortronCapacitor cap) {
        super(new FortronCapacitorContainer(player, cap), cap);
    }

    public TileFortronCapacitor getCapacitor() {
        return (TileFortronCapacitor) this.frequencyTile;
    }

    @Override
    public void initGui() {
        this.textFieldPos = new Vector2d(50, 76);
        super.initGui();
        this.buttonList.add(new TransferModeButton(1, width / 2 + 15, height / 2 - 37, this, getCapacitor()));
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        if (guiButton.id == 1) {
            MFFS.channel.sendToServer(new EntityToggle(getCapacitor(), EntityToggle.TRANSFER_TOGGLE));
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        TileFortronCapacitor cap = getCapacitor();
        fontRendererObj.drawString(cap.getInventoryName(), this.xSize / 2 - fontRendererObj.getStringWidth(cap.getInventoryName()) / 2, 6, 4210752);
        GL11.glPushMatrix();
        GL11.glRotatef(-90, 0, 0, 1);
        drawTextWithTooltip("upgrade", -95, 140, mouseX, mouseY);
        GL11.glPopMatrix();

        Set<IFortronFrequency> freq = new HashSet<>();
        cap.getLinkedDevices(freq);
        drawTextWithTooltip("linkedDevice", "%1: " + freq.size(), 8, 28, mouseX, mouseY);

        drawTextWithTooltip("transmissionRate", "%1: " + UnitDisplay.getDisplayShort(cap.getTransmissionRate() * 20, UnitDisplay.Unit.LITER) + "/s", 8, 40, mouseX, mouseY);
        drawTextWithTooltip("range", "%1: " + cap.getTransmissionRange(), 8, 52, mouseX, mouseY);
        drawTextWithTooltip("frequency", "%1:", 8, 63, mouseX, mouseY);
        this.textFieldFrequency.drawTextBox();
        drawTextWithTooltip("fortron", "%1:", 8, 95, mouseX, mouseY);
        fontRendererObj.drawString(UnitDisplay.getDisplayShort(cap.getFortronEnergy(), UnitDisplay.Unit.LITER) + "/" + UnitDisplay.getDisplay(cap.getFortronCapacity(), UnitDisplay.Unit.LITER, UnitDisplay.UnitPrefix.MILLI), 8, 105, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
        super.drawGuiContainerBackgroundLayer(var1, x, y);

        drawSlot(153, 46);
        drawSlot(153, 66);
        drawSlot(153, 86);

        drawSlot(8, 73);
        drawSlot(26, 73);
        TileFortronCapacitor cap = getCapacitor();
        drawForce(8, 115, (float) cap.getFortronEnergy() / cap.getFortronCapacity());
    }
}
