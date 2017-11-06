package com.builtbroken.mffs.content.cap;

import com.builtbroken.mffs.api.fortron.IFortronFrequency;
import com.builtbroken.mffs.api.utils.UnitDisplay;
import com.builtbroken.mffs.client.gui.base.GuiMFFS;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class GuiFortronCapacitor extends GuiMFFS<TileFortronCapacitor>
{

    /**
     * @param player
     * @param host
     */
    public GuiFortronCapacitor(EntityPlayer player, TileFortronCapacitor host)
    {
        super(new FortronCapacitorContainer(player, host), host);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        //this.buttonList.add(new TransferModeButton(1, width / 2 + 15, height / 2 - 37, this, host));
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        super.actionPerformed(guiButton);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(host.getInventoryName(), this.xSize / 2 - fontRendererObj.getStringWidth(host.getInventoryName()) / 2, 6, 4210752);
        GL11.glPushMatrix();
        GL11.glRotatef(-90, 0, 0, 1);
        drawTextWithTooltip("upgrade", -95, 140, mouseX, mouseY);
        GL11.glPopMatrix();

        Set<IFortronFrequency> freq = new HashSet<>();
        host.getLinkedDevices(freq);
        drawTextWithTooltip("linkedDevice", "%1: " + freq.size(), 8, 28, mouseX, mouseY);

        drawTextWithTooltip("transmissionRate", "%1: " + UnitDisplay.getDisplayShort(host.getTransmissionRate() * 20, UnitDisplay.Unit.LITER, UnitDisplay.UnitPrefix.MILLI) + "/s", 8, 40, mouseX, mouseY);
        drawTextWithTooltip("range", "%1: " + host.getTransmissionRange(), 8, 52, mouseX, mouseY);
        drawTextWithTooltip("frequency", "%1:", 8, 63, mouseX, mouseY);

        drawTextWithTooltip("fortron", "%1:", 8, 95, mouseX, mouseY);
        fontRendererObj.drawString(UnitDisplay.getDisplayShort(host.getFortronEnergy(), UnitDisplay.Unit.LITER, UnitDisplay.UnitPrefix.MILLI) + "/" + UnitDisplay.getDisplay(host.getFortronCapacity(), UnitDisplay.Unit.LITER, UnitDisplay.UnitPrefix.MILLI), 8, 105, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(var1, x, y);
        //drawForce(8, 115, (float) host.getFortronEnergy() / host.getFortronCapacity());
    }
}
