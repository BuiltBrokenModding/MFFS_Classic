package com.mffs.common.tile;

import com.mffs.api.modules.IModule;
import com.mffs.api.modules.IModuleAcceptor;
import com.mffs.common.items.modules.upgrades.ModuleCapacity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public abstract class TileModuleAcceptor extends TileFortron implements IModuleAcceptor {

    protected int capacityBase = 500;
    protected int capacityBoost = 5;

    /* This is the index of first module slot, and the number of slots. */
    protected byte module_index, module_end = (byte) getSizeInventory();

    @Override
    public void start() {
        super.start();
        this.tank.setCapacity((getModuleCount(ModuleCapacity.class) * this.capacityBoost + this.capacityBase) * 1_000);
    }

    @Override
    public ItemStack getModule(Class<? extends IModule> module) {
        ItemStack returnStack = null;
        for (ItemStack stack : inventory) {
            if (stack != null && module.isAssignableFrom(stack.getItem().getClass())) {
                if (returnStack == null) {//We can do this, or call module.newInstance()
                    returnStack = new ItemStack(stack.getItem(), 0);
                }
                returnStack.stackSize += stack.stackSize;
            }
        }
        return returnStack;
    }

    @Override
    public int getModuleCount(Class<? extends IModule> paramIModule, int... paramVarArgs) {
        int count = 0;
        if (paramVarArgs != null && paramVarArgs.length > 0) {
            for (int slot : paramVarArgs) {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && paramIModule.isAssignableFrom(stack.getItem().getClass())) {
                    count += stack.stackSize;
                }
            }
        } else {
            for (ItemStack stack : getModuleStacks()) {
                if (stack != null && paramIModule.isAssignableFrom(stack.getItem().getClass())) {
                    count += stack.stackSize;
                }
            }
        }
        return count;
    }

    @Override
    public Set<ItemStack> getModuleStacks(int... paramVarArgs) {
        Set<ItemStack> stacks = new HashSet<>();
        if (paramVarArgs != null && paramVarArgs.length > 0) {
            for (int slot : paramVarArgs) {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule) {
                    stacks.add(stack);
                }
            }
        } else {
            for (int i = module_index; i < module_end; i++) {
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
        if (paramVarArgs != null && paramVarArgs.length > 0) {
            for (int slot : paramVarArgs) {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof IModule) {
                    stacks.add((IModule) stack.getItem());
                }
            }
        } else {
            for (int i = module_index; i < module_end; i++) {
                ItemStack stack = getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof IModule) {
                    stacks.add((IModule) stack.getItem());
                }
            }
        }
        return stacks;
    }

    @Override
    public int getFortronCost() {
        int cost = calculateFortronCost();
        return cost;
    }

    /**
     * @return
     */
    public int calculateFortronCost() {
        float cost = 0.0F;
        for (ItemStack stack : getModuleStacks()) {
            if (stack != null) {
                cost += stack.stackSize * ((IModule) stack.getItem()).getFortronCost(getAmplifier());
            }
        }
        return Math.round(cost);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public void fireEvents(int... slots) {
        this.tank.setCapacity(getModuleCount(ModuleCapacity.class) * capacityBoost + capacityBase * 1000);
    }

    public float getAmplifier() {
        return 1.0F;
    }
}
