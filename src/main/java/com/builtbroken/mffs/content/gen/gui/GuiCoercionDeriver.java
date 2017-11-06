package com.builtbroken.mffs.content.gen.gui;

import com.builtbroken.jlib.data.science.units.UnitDisplay;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mffs.client.gui.base.GuiMFFS;
import com.builtbroken.mffs.content.gen.TileCoercionDeriver;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class GuiCoercionDeriver extends GuiMFFS<TileCoercionDeriver>
{
    private static final int TABS = 4;

    private GuiImageButton mainWindowButton;
    private GuiImageButton upgradesWindowButton;
    private GuiImageButton linksWindowButton;
    private GuiImageButton settingsWindowButton;


    protected int id = 0;

    public GuiCoercionDeriver(EntityPlayer player, TileCoercionDeriver tileentity, int id)
    {
        super(new ContainerCoercionDeriver(player, tileentity, id), tileentity);
        this.id = id;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int x = guiLeft - 18;
        int y = guiTop + 10;

        //Menu Tabs
        mainWindowButton = addButton(GuiImageButton.newButton18(0, x, y, 0, 0).setTexture(GUI_BUTTONS));
        upgradesWindowButton = addButton(GuiImageButton.newButton18(1, x, y + 19 * 2, 2, 0).setTexture(GUI_BUTTONS));
        linksWindowButton = addButton(GuiImageButton.newButton18(2, x, y + 19 * 3, 7, 0).setTexture(GUI_BUTTONS));
        settingsWindowButton = addButton(GuiImageButton.newButton18(3, x, y + 19 * 4, 5, 0).setTexture(GUI_BUTTONS));
}


    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRendererObj.drawString(host.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(host.getInventoryName()) / 2, 6, 4210752);


        drawTextWithTooltip("upgrade", -95, 140, x, y);

        /*if ((this.buttonList.get(1) instanceof GuiButton)) {
            if (!entity.isInversed) {
                ((GuiButton) this.buttonList.get(1)).displayString = LanguageRegistry.instance().getStringLocalization("gui.deriver.derive");
            } else {
                ((GuiButton) this.buttonList.get(1)).displayString = LanguageRegistry.instance().getStringLocalization("gui.deriver.integrate");
            }
        }*/

        renderUniversalDisplay(85, 30, host.getBattery().getMaxBufferSize(), x, y, UnitDisplay.Unit.JOULES);
        //this.fontRendererObj.drawString(UnitDisplay.getDisplayShort(240L, UnitDisplay.Unit.VOLTAGE), 85, 40, 4210752);

        drawTextWithTooltip("progress", "%1: " + (host.isActive() ? LanguageRegistry.instance().getStringLocalization("gui.deriver.running") : LanguageRegistry.instance().getStringLocalization("gui.deriver.idle")), 8, 70, x, y);
        drawTextWithTooltip("fortron", "%1: " + host.getFortronCreationRate(), 8, 105, x, y);

        this.fontRendererObj.drawString((host.outputPower ? EnumChatFormatting.RED + "-" : EnumChatFormatting.GREEN + "+") + host.getFortronCreationRate(), 118, 117, 4210752);

        super.drawGuiContainerForegroundLayer(x, y);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);

        //drawBar(50, 84, 1.0F);


        //drawForce(8, 115, host.getFortronEnergy() > 0 ? ((float) host.getFortronEnergy()) / host.getFortronCapacity() : 0);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        final int buttonId = button.id;
        //Turn sentry on
        if (buttonId == 10)
        {
           // host.sendPacketToServer(new PacketTile(host, 3, true));
        }
        //Turn sentry off
        else if (buttonId == 11)
        {
            //host.sendPacketToServer(new PacketTile(host, 3, false));
        }
        //Tab switch buttons
        else if (buttonId >= 0 && buttonId < TABS && buttonId != id)
        {
            //host.sendPacketToServer(new PacketOpenGUI(host, buttonId));
        }
    }
}
