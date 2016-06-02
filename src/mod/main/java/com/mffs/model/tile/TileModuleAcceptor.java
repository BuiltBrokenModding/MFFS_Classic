package com.mffs.model.tile;

import com.mffs.api.modules.IModule;
import com.mffs.api.modules.IModuleAcceptor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pwaln on 6/2/2016.
 */
public abstract class TileModuleAcceptor extends TileFortron implements IModuleAcceptor {

    public int clientFortronCost = 0;
    protected int capacityBase = 500;
    protected int capacityBoost = 5;
    /* This is the index of first module slot, and the number of slots. */
    protected byte module_index, module_size;

    @Override
    public void start() {
        super.start();
        this.tank.setCapacity(0);
    }

    @Override
    public ItemStack getModule(IModule paramIModule) {
        ItemStack returnStack = new ItemStack((Item) paramIModule, 0);

        for (ItemStack comparedModule : getModuleStacks()) {
            if (comparedModule.getItem() == paramIModule) {
                returnStack.stackSize += comparedModule.stackSize;
            }
        }
        return returnStack;
    }

    @Override
    public int getModuleCount(IModule paramIModule, int... paramVarArgs) {
        int count = 0;
        if (paramVarArgs != null) {
            for (int slot : paramVarArgs) {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() == paramIModule) {
                    count += stack.stackSize;
                }
            }
        } else {
            for (ItemStack stack : getModuleStacks()) {
                if (stack != null && stack.getItem() == paramIModule) {
                    count += stack.stackSize;
                }
            }
        }
        return count;
    }

    @Override
    public Set<ItemStack> getModuleStacks(int... paramVarArgs) {
        Set<ItemStack> stacks = new HashSet<>();
        if (paramVarArgs != null) {
            for (int slot : paramVarArgs) {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule) {
                    stacks.add(stack);
                }
            }
        } else {
            for (int i = module_index; i < module_index + module_size; i++) {
                ItemStack stack = getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof IModule) {
                    stacks.add(stack);
                }
            }
        }
        return stacks;
    }

    @Override
    public Set<IModule> getModules(int... paramVarArgs) {
        Set<IModule> stacks = new HashSet<>();
        if (paramVarArgs != null) {
            for (int slot : paramVarArgs) {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule) {

                }
            }
        } else {
            for (int i = module_index; i < module_index + module_size; i++) {
                ItemStack stack = getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof IModule) {

                }
            }
        }
        return stacks;
    }

    @Override
    public int getFortronCost() {
        float cost = 0.0F;
        for (ItemStack stack : getModuleStacks()) {
            if (stack != null) {
                cost += ((IModule) stack.getItem()).getFortronCost(1.0F);
            }
        }
        return Math.round(cost);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("fortronCost", clientFortronCost);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.clientFortronCost = nbt.getInteger("fortronCost");
    }

    @Override
    public void fireEvents(int... slots) {
        super.fireEvents(slots);
        this.tank.setCapacity(getModuleCount(null) * capacityBoost + capacityBase * 1000);
    }
}
