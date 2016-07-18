package com.mffs.client.gui.base;

import com.mffs.api.IBlockFrequency;
import com.mffs.api.gui.GuiSlotType;
import com.mffs.api.vector.Matrix2d;
import com.mffs.common.tile.TileFieldMatrix;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.inventory.Container;

import javax.vecmath.Vector2d;

/**
 * Created by Poopsicle360 on 7/17/2016.
 */
public abstract class GuiMatrix extends MFFSGui {

    /* Center of the matrix */
    public static Vector2d centerMatrix = new Vector2d(110, 55);

    /**
     *
     * @param container
     * @param matrix
     */
    public GuiMatrix(Container container, TileFieldMatrix matrix) {
        super(container, matrix);
    }

    public TileFieldMatrix getMatrix() {
        return (TileFieldMatrix) this.frequencyTile;
    }

    /**
     * Creates the tooltips to be used.
     */
    @Override
    public void initGui() {
        super.initGui();
        TileFieldMatrix proj = getMatrix();
        String north = LanguageRegistry.instance().getStringLocalization("gui.projector."+(proj.isAbs ? "north" : "front"));
        String south = LanguageRegistry.instance().getStringLocalization("gui.projector."+(proj.isAbs ? "south" : "back"));
        String west = LanguageRegistry.instance().getStringLocalization("gui.projector."+(proj.isAbs ? "west" : "left"));
        String east = LanguageRegistry.instance().getStringLocalization("gui.projector"+(proj.isAbs ? "east" : "right"));

        for(int i = 1; i <= 2; i++)
            tooltips.put(new Matrix2d(centerMatrix.x , centerMatrix.y - 18 * i, 18), north);

        for(int i = 1; i <= 2; i++)
            tooltips.put(new Matrix2d(centerMatrix.x , centerMatrix.y + 18 * i, 18), south);

        for(int i = 1; i <= 2; i++)
            tooltips.put(new Matrix2d(centerMatrix.x + 18 * i, centerMatrix.y, 18), east);

        for(int i = 1; i <= 2; i++)
            tooltips.put(new Matrix2d(centerMatrix.x - 18 * i , centerMatrix.y, 18), west);

        this.tooltips.put(new Matrix2d(centerMatrix, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.mode"));

        tooltips.put(new Matrix2d(centerMatrix.x - 18, centerMatrix.y - 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.up"));
        tooltips.put(new Matrix2d(centerMatrix.x + 18, centerMatrix.y - 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.up"));

        tooltips.put(new Matrix2d(centerMatrix.x - 18, centerMatrix.y + 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.down"));
        tooltips.put(new Matrix2d(centerMatrix.x + 18, centerMatrix.y + 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.down"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
        super.drawGuiContainerBackgroundLayer(var1, x, y);

        drawSlot((int) centerMatrix.x, (int) centerMatrix.y, GuiSlotType.NONE, 1, 0.4F, 0.4F);

        for(int i = 1; i <= 2; i++)
            drawSlot((int) centerMatrix.x, (int) centerMatrix.y - 18 * i, GuiSlotType.ARR_UP);

        for(int i = 1; i <= 2; i++)
            drawSlot((int) centerMatrix.x, (int) centerMatrix.y + 18 * i, GuiSlotType.ARR_DOWN);

        for(int i = 1; i <= 2; i++)
            drawSlot((int) centerMatrix.x + 18 * i, (int) centerMatrix.y, GuiSlotType.ARR_RIGHT);

        for(int i = 1; i <= 2; i++)
            drawSlot((int) centerMatrix.x - 18 * i, (int) centerMatrix.y, GuiSlotType.ARR_LEFT);

        //UP
        drawSlot((int) centerMatrix.x - 18, (int) centerMatrix.y - 18, GuiSlotType.ARR_UP_LEFT);
        drawSlot((int) centerMatrix.x + 18, (int) centerMatrix.y - 18, GuiSlotType.ARR_UP_RIGHT);
        //DOWN
        drawSlot((int) centerMatrix.x - 18, (int) centerMatrix.y + 18, GuiSlotType.ARR_DOWN_LEFT);
        drawSlot((int) centerMatrix.x + 18, (int) centerMatrix.y + 18, GuiSlotType.ARR_DOWN_RIGHT);

        for(int i = -2; i <= 2; i++)
            for(int i2 = -2; i2 <= 2; i2++)
                if(Math.sqrt(i*i + i2 * i2) > 2)
                    drawSlot((int) centerMatrix.x + 18 * i, (int) centerMatrix.y + 18 * i2);
    }

}
