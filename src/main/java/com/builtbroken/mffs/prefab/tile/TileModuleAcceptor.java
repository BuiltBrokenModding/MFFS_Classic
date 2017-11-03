package com.builtbroken.mffs.prefab.tile;

import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.api.modules.IModuleContainer;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleCapacity;
import com.builtbroken.mffs.prefab.ModuleInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;

import java.util.Set;

/**
 * Prefab for any tile that accepts modules
 *
 * @author Calclavia
 */
@Deprecated //Being replaced by IInventoryProvider
public abstract class TileModuleAcceptor extends TileFortron implements IModuleContainer
{
    /** Amount of fortron to store */
    protected int fortronCapacity = 500; //TODO move to static settings
    /** fortron boost per capacity card {@link ItemModuleCapacity} */
    protected int fortronCapacityBoostPerCard = 5; //TODO move to static settings

    protected ModuleInventory moduleInventory;

    /**
     * Called any time the machine changes (moved, reset, loaded, etc)
     * <p>
     * Should be used to update settings dependent on the internals or
     * externals of the machine
     * <p>
     * Called from {@link #validate()} {@link #markDirty()} {@link #start()}
     */
    protected void updateSettings()
    {
        this.tank.setCapacity((getModuleCount(ItemModuleCapacity.class) * this.fortronCapacityBoostPerCard + this.fortronCapacity) * FluidContainerRegistry.BUCKET_VOLUME);
    }

    @Override
    public int getModuleCount(Class<? extends IFieldModule> paramIModule, int... slots)
    {
        return moduleInventory.getModuleCount(paramIModule, slots);
    }

    @Override
    public Set<ItemStack> getModuleStacks(int... slots)
    {
        return moduleInventory.getModuleStacks(slots);
    }

    @Override
    public Set<IFieldModule> getModules(int... slots)
    {
        return moduleInventory.getModules(slots);
    }

    public int getFortronCost()
    {
        return calculateFortronCost();
    }

    /**
     * @return
     */
    public int calculateFortronCost() //TODO why do we have a get and calculate method?
    {
        float cost = 0.0F;
        for (ItemStack stack : getModuleStacks())
        {
            if (stack != null)
            {
                cost += stack.stackSize * ((IFieldModule) stack.getItem()).getFortronCost(getAmplifier());
            }
        }
        return Math.round(cost);
    }

    @Override
    public void start()
    {
        super.start();
        updateSettings();
    }

    @Override
    public void validate()
    {
        super.validate();
        updateSettings();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        updateSettings();
    }

    //TODO document
    public float getAmplifier()
    {
        return 1.0F;
    }
}
