package com.builtbroken.mffs.client.gui.base;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mffs.MFFS;
import com.builtbroken.mffs.api.IBiometricIdentifierLink;
import com.builtbroken.mffs.prefab.tile.TileMFFS;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

/**
 * @author DarkCow
 */
public class GuiMFFS<H extends TileMFFS> extends GuiContainerBase<H>
{
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(MFFS.DOMAIN, "textures/gui/gui.buttons.32pix.png");

    private GuiButton2 onButton;
    private GuiButton2 offButton;

    /**
     * @param container
     */
    public GuiMFFS(Container container, H host)
    {
        super(container, host);
        this.baseTexture = SharedAssets.GUI_EMPTY_FILE;
        ySize = 217;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int x = guiLeft - 18;
        int y = guiTop + 10;

        //Power buttons
        onButton = add(GuiButton9px.newOnButton(10, x, y - 10));
        offButton = add(GuiButton9px.newOffButton(11, x + 9, y - 10));
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        onButton.setEnabled(!host.isActive());
        offButton.setEnabled(host.isActive());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(var1, x, y);
        drawContainerSlots();

        if ((this.host instanceof IBiometricIdentifierLink)) //TODO is needed?
        {
            drawBulb(167, 4, ((IBiometricIdentifierLink) this.host).getBiometricIdentifier() != null);
        }
    }
}
