package com.mffs.client.buttons;

import com.mffs.api.TransferMode;
import com.mffs.client.gui.MFFSGui;
import com.mffs.common.tile.type.TileFortronCapacitor;
import net.minecraft.client.Minecraft;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
public class TransferModeButton extends GuiPressableButton {

    /* We need to store the capacitor here. */
    private TileFortronCapacitor mode;

    /**
     * @param id
     * @param x
     * @param y
     * @param mainGui
     * @param mode
     */
    public TransferModeButton(int id, int x, int y, MFFSGui mainGui, TileFortronCapacitor mode) {
        super(id, x, y, new Vector2d(), mainGui);
        this.mode = mode;
    }

    @Override
    public void drawButton(Minecraft minecraft, int x, int y) {
        this.displayString = "transferMode" + TransferMode.NAME_NORMALIZED[mode.getTransferMode().ordinal()];
        this.offset.y = (18 * mode.getTransferMode().ordinal());
        super.drawButton(minecraft, x, y);
    }
}
