package com.builtbroken.mffs.client.gui.base;

import com.builtbroken.mc.imp.transform.region.Rectangle;
import com.builtbroken.mc.prefab.gui.GuiSlotType;
import com.builtbroken.mffs.api.utils.UnitDisplay;
import com.builtbroken.mffs.api.vector.Matrix2d;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.prefab.tile.TileFieldMatrix;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.Container;

/**
 * Created by Poopsicle360 on 7/17/2016.
 */
@SideOnly(Side.CLIENT)
public abstract class GuiMatrix<H extends TileFieldMatrix> extends GuiMFFS<H>
{
    public static final Matrix2d FORCE_BOUNDS = new Matrix2d(new Vector3D(175, 0, 0), new Vector3D(186, 107, 0));
    /**
     * @param container
     * @param matrix
     */
    public GuiMatrix(Container container, H matrix)
    {
        super(container, matrix);
    }

    /**
     * Creates the tooltips to be used.
     */
    @Override
    public void initGui()
    {
        super.initGui();
        String north = LanguageRegistry.instance().getStringLocalization("gui.projector." + (host.useAbsoluteDirection ? "north" : "front"));
        String south = LanguageRegistry.instance().getStringLocalization("gui.projector." + (host.useAbsoluteDirection ? "south" : "back"));
        String west = LanguageRegistry.instance().getStringLocalization("gui.projector." + (host.useAbsoluteDirection ? "west" : "left"));
        String east = LanguageRegistry.instance().getStringLocalization("gui.projector." + (host.useAbsoluteDirection ? "east" : "right"));

        for (int i = 1; i <= 2; i++)
        {
            tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x(), TileFieldMatrix.MATRIX_CENTER.y() - 18 * i, 18), north);
        }

        for (int i = 1; i <= 2; i++)
        {
            tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x(), TileFieldMatrix.MATRIX_CENTER.y() + 18 * i, 18), south);
        }

        for (int i = 1; i <= 2; i++)
        {
            tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x() + 18 * i, TileFieldMatrix.MATRIX_CENTER.y(), 18), east);
        }

        for (int i = 1; i <= 2; i++)
        {
            tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x() - 18 * i, TileFieldMatrix.MATRIX_CENTER.y(), 18), west);
        }

        this.tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x(), TileFieldMatrix.MATRIX_CENTER.y(), 18), LanguageRegistry.instance().getStringLocalization("gui.projector.mode"));

        tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x() - 18, TileFieldMatrix.MATRIX_CENTER.y() - 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.up"));
        tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x() + 18, TileFieldMatrix.MATRIX_CENTER.y() - 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.up"));

        tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x() - 18, TileFieldMatrix.MATRIX_CENTER.y() + 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.down"));
        tooltips.put(new Rectangle(TileFieldMatrix.MATRIX_CENTER.x() + 18, TileFieldMatrix.MATRIX_CENTER.y() + 18, 18), LanguageRegistry.instance().getStringLocalization("gui.projector.down"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(var1, x, y);

        drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x(), (int) TileFieldMatrix.MATRIX_CENTER.y(), GuiSlotType.NONE, 1f, 0.4F, 0.4F);

        for (int i = 1; i <= 2; i++)
        {
            drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x(), (int) TileFieldMatrix.MATRIX_CENTER.y() - 18 * i, GuiSlotType.ARR_UP);
        }

        for (int i = 1; i <= 2; i++)
        {
            drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x(), (int) TileFieldMatrix.MATRIX_CENTER.y() + 18 * i, GuiSlotType.ARR_DOWN);
        }

        for (int i = 1; i <= 2; i++)
        {
            drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x() + 18 * i, (int) TileFieldMatrix.MATRIX_CENTER.y(), GuiSlotType.ARR_RIGHT);
        }

        for (int i = 1; i <= 2; i++)
        {
            drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x() - 18 * i, (int) TileFieldMatrix.MATRIX_CENTER.y(), GuiSlotType.ARR_LEFT);
        }

        //UP
        drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x() - 18, (int) TileFieldMatrix.MATRIX_CENTER.y() - 18, GuiSlotType.ARR_UP_LEFT);
        drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x() + 18, (int) TileFieldMatrix.MATRIX_CENTER.y() - 18, GuiSlotType.ARR_UP_RIGHT);
        //DOWN
        drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x() - 18, (int) TileFieldMatrix.MATRIX_CENTER.y() + 18, GuiSlotType.ARR_DOWN_LEFT);
        drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x() + 18, (int) TileFieldMatrix.MATRIX_CENTER.y() + 18, GuiSlotType.ARR_DOWN_RIGHT);

        for (int i = -2; i <= 2; i++)
        {
            for (int i2 = -2; i2 <= 2; i2++)
            {
                if (Math.sqrt(i * i + i2 * i2) > 2)
                {
                    drawSlot((int) TileFieldMatrix.MATRIX_CENTER.x() + 18 * i, (int) TileFieldMatrix.MATRIX_CENTER.y() + 18 * i2);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (FORCE_BOUNDS.isIn(mouseX - this.guiLeft, mouseY - this.guiTop))
        {
            drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, UnitDisplay.getDisplayShort(host.getFortronEnergy(), UnitDisplay.Unit.LITER));
        }
        //drawForceVertical(175, 0, proj.getFortronEnergy() > 0 ? ((float) proj.getFortronEnergy()) / proj.getFortronCapacity() : 0);
    }
}
