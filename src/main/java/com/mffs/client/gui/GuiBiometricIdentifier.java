package com.mffs.client.gui;

import com.mffs.client.gui.base.MFFSGui;
import com.mffs.common.container.entity.BiometricContainer;
import com.mffs.common.tile.type.TileBiometricIdentifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
public class GuiBiometricIdentifier extends MFFSGui {

    /**
     * @param player
     * @param bio
     */
    public GuiBiometricIdentifier(EntityPlayer player, TileBiometricIdentifier bio) {
        super(new BiometricContainer(player, bio), bio);
    }

    public TileBiometricIdentifier getEntity() {
        return (TileBiometricIdentifier) this.frequencyTile;
    }

    @Override
    public void initGui() {
        this.textFieldPos = new Vector2d(33, 118);
        super.initGui();

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        TileBiometricIdentifier entity = getEntity();
        this.fontRendererObj.drawString(entity.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(entity.getInventoryName()) / 2, 6, 4210752);
        this.fontRendererObj.drawString("Frequency", 33, 108, 4210752);
        this.fontRendererObj.drawString(EnumChatFormatting.AQUA + "id and Group Cards", 40, 25, 4210752);

        textFieldFrequency.drawTextBox();
        super.drawGuiContainerForegroundLayer(x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

        super.drawGuiContainerBackgroundLayer(f, x, y);
        drawSlot(7, 113);//Freqency grid


        for (int var4 = 0; var4 < 9; var4++)
            for(int y1 = 0; y1 < 4; y1++)
                drawSlot(8 + var4 * 18, 35 + y1 * 18);


    }

}
