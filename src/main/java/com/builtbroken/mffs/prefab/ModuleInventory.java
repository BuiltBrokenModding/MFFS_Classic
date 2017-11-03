package com.builtbroken.mffs.prefab;

import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.api.modules.IModuleContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/2/2017.
 */
public class ModuleInventory implements IModuleContainer
{
    /** First slot to start looking for modules */
    public final int start;
    /** Stopping point for looking for modules, not searched */
    public final int end;

    protected int[] moduleSlots;

    protected IInventory inventory;

    public ModuleInventory(IInventory inventory, int start, int end)
    {
        this.inventory = inventory;
        this.start = start;
        this.end = end;

        //Fill slot array
        moduleSlots = new int[end - start]; //TODO find a way to cache to save ram
        for (int i = start; start < end; i++)
        {
            moduleSlots[i - start] = i;
        }
    }

    @Override
    public int getModuleCount(Class<? extends IFieldModule> paramIModule, int... slots)
    {
        int count = 0;
        for (int slot : slots != null ? slots : moduleSlots)
        {
            //Get slot content and match to class
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null && paramIModule.isAssignableFrom(stack.getItem().getClass()))
            {
                count += stack.stackSize;
            }
        }
        return count;
    }

    @Override
    public Set<ItemStack> getModuleStacks(int... slots)
    {
        Set<ItemStack> stacks = new HashSet<>();
        for (int slot : slots != null ? slots : moduleSlots)
        {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null && stack.getItem() instanceof IFieldModule)
            {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    @Override
    public Set<IFieldModule> getModules(int... slots)
    {
        Set<IFieldModule> stacks = new HashSet<>();
        for (int slot : slots != null ? slots : moduleSlots)
        {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null && stack.getItem() instanceof IFieldModule)
            {
                stacks.add((IFieldModule) stack.getItem());
            }
        }
        return stacks;
    }

}
