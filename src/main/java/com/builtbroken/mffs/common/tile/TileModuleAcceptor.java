package com.builtbroken.mffs.common.tile;

import com.builtbroken.mffs.api.modules.IModule;
import com.builtbroken.mffs.api.modules.IModuleAcceptor;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleCapacity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;

import java.util.HashSet;
import java.util.Set;

/**
 * Prefab for any tile that accepts modules
 *
 * @author Calclavia
 */
public abstract class TileModuleAcceptor extends TileFortron implements IModuleAcceptor
{
    /** Amount of fortron to store */
    protected int fortronCapacity = 500; //TODO move to static settings
    /** fortron boost per capacity card {@link ItemModuleCapacity} */
    protected int fortronCapacityBoostPerCard = 5; //TODO move to static settings

    /** First slot to start looking for modules */
    protected int module_inventory_start = 0; //TODO move to static settings
    /** Stopping point for looking for modules, not searched */
    protected int module_inventory_end; //TODO move to static settings

    public TileModuleAcceptor()
    {
        module_inventory_end = getSizeInventory();
    }

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
    public ItemStack getModule(Class<? extends IModule> module)
    {
        ItemStack returnStack = null;
        for (ItemStack stack : inventory)
        {
            if (stack != null && module.isAssignableFrom(stack.getItem().getClass()))
            {
                if (returnStack == null)
                {//We can do this, or call module.newInstance()
                    returnStack = new ItemStack(stack.getItem(), 0);
                }
                returnStack.stackSize += stack.stackSize;
            }
        }
        return returnStack;
    }

    @Override
    public int getModuleCount(Class<? extends IModule> paramIModule, int... slots)
    {
        int count = 0;
        //If we have slots, scan though slots provided
        if (slots != null && slots.length > 0)
        {
            for (int slot : slots)
            {
                //Get slot content and match to class
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && paramIModule.isAssignableFrom(stack.getItem().getClass()))
                {
                    count += stack.stackSize;
                }
            }
        }
        else
        {
            for (ItemStack stack : getModuleStacks())
            {
                //Get slot content and match to class
                if (stack != null && paramIModule.isAssignableFrom(stack.getItem().getClass()))
                {
                    count += stack.stackSize;
                }
            }
        }
        return count;
    }

    @Override
    public Set<ItemStack> getModuleStacks(int... paramVarArgs)
    {
        Set<ItemStack> stacks = new HashSet<>();
        if (paramVarArgs != null && paramVarArgs.length > 0)
        {
            for (int slot : paramVarArgs)
            {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule)
                {
                    stacks.add(stack);
                }
            }
        }
        else
        {
            for (int slot = module_inventory_start; slot < module_inventory_end; slot++) //TODO replace with inventory iterator
            {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule)
                {
                    stacks.add(stack);
                }
            }
        }
        return stacks;
    }

    @Override
    public Set<IModule> getModules(int... paramVarArgs)
    {
        Set<IModule> stacks = new HashSet<>();
        if (paramVarArgs != null && paramVarArgs.length > 0)
        {
            for (int slot : paramVarArgs)
            {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule)
                {
                    stacks.add((IModule) stack.getItem());
                }
            }
        }
        else
        {
            for (int slot = module_inventory_start; slot < module_inventory_end; slot++) //TODO replace with inventory iterator
            {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule)
                {
                    stacks.add((IModule) stack.getItem());
                }
            }
        }
        return stacks;
    }

    @Override
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
                cost += stack.stackSize * ((IModule) stack.getItem()).getFortronCost(getAmplifier());
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
