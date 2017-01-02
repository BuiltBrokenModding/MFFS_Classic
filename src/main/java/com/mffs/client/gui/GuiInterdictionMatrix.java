package com.mffs.client.gui;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.transform.vector.Point;
import com.mffs.ModularForcefieldSystem;
import com.mffs.api.gui.GuiSlotType;
import com.mffs.client.gui.base.MFFSGui;
import com.mffs.common.container.entity.InterdictionContainer;
import com.mffs.common.net.packet.EntityToggle;
import com.mffs.common.tile.type.TileInterdictionMatrix;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * @author Calclavia
 */
public class GuiInterdictionMatrix extends MFFSGui {

    /**
     * @param pl The Container of this GUI.
     * @param matrix The TileEntity of this GUI.
     */
    public GuiInterdictionMatrix(EntityPlayer pl, TileInterdictionMatrix matrix) {
        super(new InterdictionContainer(pl, matrix), matrix);
    }

    @Override
    public void initGui() {
        this.textFieldPos = new Point(110.0D, 91.0D);
        super.initGui();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 80, this.height / 2 - 65, 50, 20, LanguageUtility.getLocal("gui.matrix.banned")));
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        if(guiButton.id == 1) {
            ModularForcefieldSystem.channel.sendToServer(new EntityToggle((TileInterdictionMatrix) this.frequencyTile, EntityToggle.FILTER_TOGGLE));
        }
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        TileInterdictionMatrix matrix = (TileInterdictionMatrix) this.frequencyTile;
        this.fontRendererObj.drawString(matrix.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(matrix.getInventoryName()) / 2, 6, 4210752);
        drawTextWithTooltip("warn", "%1: "+ matrix.getWarningRange(), 35, 19, mouseX, mouseY);
        drawTextWithTooltip("action", "%1: " + matrix.getActionRange(), 100, 19, mouseX, mouseY);

        drawTextWithTooltip("filterMode", "%1:", 9, 32, mouseX, mouseY);


        drawTextWithTooltip("frequency", "%1:", 8, 93, mouseX, mouseY);
        this.textFieldFrequency.drawTextBox();

        drawTextWithTooltip("fortron", "%1: "
                + com.mffs.api.utils.UnitDisplay.getDisplayShort(matrix.getFortronEnergy(), com.mffs.api.utils.UnitDisplay.Unit.LITER) + "/"
                + com.mffs.api.utils.UnitDisplay.getDisplayShort(matrix.getFortronCapacity(), com.mffs.api.utils.UnitDisplay.Unit.LITER), 8, 110, mouseX, mouseY);
        this.fontRendererObj.drawString("" + com.mffs.api.utils.UnitDisplay.getDisplayShort(matrix.getFortronCost() * 20, com.mffs.api.utils.UnitDisplay.Unit.LITER) + "/s", 118, 121, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
        super.drawGuiContainerBackgroundLayer(var1, x, y);

        for(int i = 0; i < 2; i++)
            for(int i2 = 0; i2 < 4; i2++)
                drawSlot(98 + i2 * 18, 30 + i * 18);

        for(int i = 0; i < 9; i++)
            if(((TileInterdictionMatrix) this.frequencyTile).getFilterMode())
                drawSlot(8 + i * 18, 68, GuiSlotType.NONE, 1.0F, 0.8F, 0.8F);
            else
                drawSlot(8 + i * 18, 68, GuiSlotType.NONE, 0.8F, 1.0F, 0.8F);

        drawSlot(68, 88);
        drawSlot(86, 88);

        drawForce(8, 120, 0.0F);
    }
}
