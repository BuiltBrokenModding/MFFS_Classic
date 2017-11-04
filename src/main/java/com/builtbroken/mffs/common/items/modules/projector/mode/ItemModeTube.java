package com.builtbroken.mffs.common.items.modules.projector.mode;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mffs.api.IFieldInteraction;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.render.ModelPlane;
import com.builtbroken.mffs.api.vector.Vector3D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ItemModeTube extends ItemModeCube implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "FFF", "   ", "FFF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction projector)
    {
        Set<Vector3D> fieldBlocks = new HashSet();
        ForgeDirection direction = projector.getDirection();
        IPos3D posScale = projector.getPositiveScale();
        IPos3D negScale = projector.getNegativeScale();

        for (double x = -negScale.xi(); x <= posScale.xi(); x += 0.5F)
        {
            for (double z = -negScale.zi(); z <= posScale.zi(); z += 0.5F)
            {
                for (double y = -negScale.yi(); y <= posScale.yi(); y += 0.5F)
                {
                    if ((direction != ForgeDirection.UP) && (direction != ForgeDirection.DOWN) && (y == -negScale.yi() || (y == posScale.yi())))
                    {
                        fieldBlocks.add(new Vector3D(x, y, z));
                    }
                    else if ((direction != ForgeDirection.NORTH) && (direction != ForgeDirection.SOUTH) && ((z == -negScale.zi()) || (z == posScale.zi())))
                    {
                        fieldBlocks.add(new Vector3D(x, y, z));
                    }
                    else if ((direction != ForgeDirection.WEST) && (direction != ForgeDirection.EAST) && ((x == -negScale.xi()) || (x == posScale.xi())))
                    {
                        fieldBlocks.add(new Vector3D(x, y, z));
                    }
                }
            }
        }
        return fieldBlocks;
    }

    @SideOnly(Side.CLIENT)

    public void render(IProjector projector, double x, double y, double z, float f, long ticks)
    {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
        ModelPlane.INSTNACE.render();
        GL11.glTranslatef(1.0F, 0.0F, 0.0F);
        ModelPlane.INSTNACE.render();
        GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.5F, 0.0F, 0.0F);
        ModelPlane.INSTNACE.render();
        GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
        ModelPlane.INSTNACE.render();
    }
}
