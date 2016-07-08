package com.mffs.common.items.modules;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.modules.IModule;
import com.mffs.api.utils.UnitDisplay;
import com.mffs.api.utils.Util;
import com.mffs.api.vector.Vector3D;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public abstract class ItemModule extends Item implements IModule {

    /* Current fortronCost */
    private float fortronCost = 0.5F;

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param list
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        list.add(LanguageRegistry.instance().getStringLocalization("info.item.fortron") + " " + UnitDisplay.getDisplay(getFortronCost(1.0F) * 20.0F, UnitDisplay.Unit.LITER) + "/s");

        String tooltip = LanguageRegistry.instance().getStringLocalization(getUnlocalizedName() + ".tooltip");
        if (tooltip != null && tooltip.length() > 0) {
            list.addAll(Util.sepString(tooltip, 30));
        }
    }

    /**
     * @param projector
     * @param position
     * @return
     */
    @Override
    public Set<Vector3D> onPreCalculate(IFieldInteraction projector, Set<Vector3D> position) {

        return position;
    }


    /**
     * Calculates the projection field.
     *
     * @param projector The projector interface.
     * @param position  A set of positions.
     */
    @Override
    public void onCalculate(IFieldInteraction projector, Set<Vector3D> position) {
    }


    /**
     * Calls this on projection.
     *
     * @param projector The projector interface.
     * @param fields    A set of fields that are projected.
     * @return
     */
    @Override
    public boolean onProject(IProjector projector, Set<Vector3D> fields) {
        return false;
    }


    @Override
    public int onProject(IProjector projector, Vector3D position) {
        return 0;
    }

    @Override
    public boolean onCollideWithForcefield(World world, int x, int y, int z, Entity entity, ItemStack moduleStack) {
        return false;
    }

    /**
     * @param cost
     * @return
     */
    public ItemModule setCost(float cost) {
        this.fortronCost = cost;
        return this;
    }

    @Override
    public float getFortronCost(float amplifier) {
        return this.fortronCost;
    }

    @Override
    public boolean onDestroy(IProjector projector, Set<Vector3D> field) {
        return false;
    }

    @Override
    public boolean requireTicks(ItemStack moduleStack) {
        return false;
    }
}
