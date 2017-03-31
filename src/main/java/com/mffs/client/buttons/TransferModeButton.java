package com.mffs.client.buttons;

import com.builtbroken.mc.imp.transform.vector.Point;
import com.mffs.client.gui.base.MFFSGui;
import com.mffs.common.TransferMode;
import com.mffs.common.tile.type.TileFortronCapacitor;
import net.minecraft.client.Minecraft;

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
        super(id, x, y, new Point(), mainGui);
        this.mode = mode;
    }

    @Override
    public void drawButton(Minecraft minecraft, int x, int y) {
        this.displayString = "transferMode" + TransferMode.NAME_NORMALIZED[mode.getTransferMode().ordinal()];
        this.offset = offset.newPos(this.offset.x(), (18 * mode.getTransferMode().ordinal()));
        super.drawButton(minecraft, x, y);
    }
}
