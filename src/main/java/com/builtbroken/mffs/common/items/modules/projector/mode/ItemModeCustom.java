package com.builtbroken.mffs.common.items.modules.projector.mode;

import com.builtbroken.mffs.api.IFieldInteraction;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.prefab.item.ItemMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by pwaln on 6/9/2016.
 */
public class ItemModeCustom extends ItemMode
{

    private static final String NBT_ID = "id";
    private static final String NBT_MODE = "mode";
    private static final String NBT_POINT_1 = "point1";
    private static final String NBT_POINT_2 = "point2";
    private static final String NBT_FIELD_BLOCK_LIST = "fieldPoints";
    private static final String NBT_FIELD_BLOCK_ID = "blockID";
    private static final String NBT_FIELD_BLOCK_METADATA = "blockMetadata";
    private static final String NBT_FIELD_SIZE = "fieldSize";
    private static final String NBT_FILE_SAVE_PREFIX = "custom_mode_";
    private final HashMap<String, Object> cache = new HashMap();

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param list
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy)
    {
        //super.addInformation(stack, usr, list, dummy);
        list.add("This Item has been disabled.");
    }

    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction paramIFieldInteraction)
    {
        return null;
    }

    @Override
    public Set<Vector3D> getInteriorPoints(IFieldInteraction paramIFieldInteraction)
    {
        return null;
    }

    @Override
    public boolean isInField(IFieldInteraction paramIFieldInteraction, Vector3D paramVector3D)
    {
        return false;
    }

    @Override
    public void render(IProjector paramIProjector, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat, long paramLong)
    {

    }
}
