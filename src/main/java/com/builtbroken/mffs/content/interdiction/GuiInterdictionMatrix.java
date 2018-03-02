package com.builtbroken.mffs.content.interdiction;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mffs.client.gui.base.GuiMFFS;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public class GuiInterdictionMatrix extends GuiMFFS<TileInterdictionMatrix>
{

    /**
     * @param pl     The Container of this GUI.
     * @param host The TileEntity of this GUI.
     */
    public GuiInterdictionMatrix(EntityPlayer pl, TileInterdictionMatrix host)
    {
        super(new InterdictionContainer(pl, host), host);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 80, this.height / 2 - 65, 50, 20, LanguageUtility.getLocal("gui.host.banned")));
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        super.actionPerformed(guiButton);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(host.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(host.getInventoryName()) / 2, 6, 4210752);
        drawTextWithTooltip("warn", "%1: " + host.getWarningRange(), 27, 17, mouseX, mouseY);
        drawTextWithTooltip("action", "%1: " + host.getActionRange(), 90, 17, mouseX, mouseY);

        drawTextWithTooltip("filterMode", "%1:", 8, 26, mouseX, mouseY);


        drawTextWithTooltip("frequency", "%1:", 8, 84, mouseX, mouseY);

        drawTextWithTooltip("fortron", "%1: "
                + com.builtbroken.mffs.api.utils.UnitDisplay.getDisplayShort(host.getFortronEnergy(), com.builtbroken.mffs.api.utils.UnitDisplay.Unit.LITER) + "/"
                + com.builtbroken.mffs.api.utils.UnitDisplay.getDisplayShort(host.getFortronCapacity(), com.builtbroken.mffs.api.utils.UnitDisplay.Unit.LITER), 8, 110, mouseX, mouseY);
        this.fontRendererObj.drawString("" + com.builtbroken.mffs.api.utils.UnitDisplay.getDisplayShort(host.getFortronCost() * 20, com.builtbroken.mffs.api.utils.UnitDisplay.Unit.LITER) + "/s", 118, 121, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(var1, x, y);
        //drawForce(8, 120, entity.getFortronEnergy() > 0 ? ((float) entity.getFortronEnergy()) / entity.getFortronCapacity() : 0);
    }
}
