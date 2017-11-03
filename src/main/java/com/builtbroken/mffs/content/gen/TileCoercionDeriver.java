package com.builtbroken.mffs.content.gen;

import cofh.api.energy.IEnergyHandler;
import com.builtbroken.mc.api.energy.IEnergyBuffer;
import com.builtbroken.mc.api.energy.IEnergyBufferProvider;
import com.builtbroken.mc.framework.energy.UniversalEnergySystem;
import com.builtbroken.mc.framework.energy.data.AbstractEnergyBuffer;
import com.builtbroken.mffs.MFFSSettings;
import com.builtbroken.mffs.ModularForceFieldSystem;
import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.common.items.card.ItemCardFrequency;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleScale;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleSpeed;
import com.builtbroken.mffs.prefab.ModuleInventory;
import com.builtbroken.mffs.prefab.tile.TileModuleAcceptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Fortron generator
 *
 * @author Calclavia, DarkCow
 */
public final class TileCoercionDeriver extends TileModuleAcceptor implements IEnergyHandler, IEnergyBufferProvider
{
    //Inventory slots
    public static final int SLOT_FREQUENCY = 0;
    public static final int SLOT_BATTERY = 1;
    public static final int SLOT_FUEL = 2;

    //Battery
    private CoercionEnergyBuffer energyBuffer;

    /** How much time is left for the fuel */
    public int fuelTimer = 0;

    /** Should be output power instead of consume it */
    public boolean outputPower;

    public TileCoercionDeriver()
    {
        this.fortronCapacity = MFFSSettings.COERCION_FORTRON_TANK_SIZE;
        this.moduleInventory = new ModuleInventory(this, 3, getSizeInventory());
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            if (isActive())
            {
                //Turn fortron back into power
                if (outputPower && MFFSSettings.COERCION_USE_POWER)
                {
                    if (getBattery().getEnergyStored() < getBattery().getMaxBufferSize())
                    {
                        requestFortron(getFortronCreationRate(), true);
                        getBattery().addEnergyToStorage(getFortronCreationRate(), true);
                    }
                    //TODO: recharge batteries
                }
                //Turn power into fortron, only produce if we have space
                else if ((getFortronEnergy() + getFortronCreationRate()) < getFortronCapacity())
                {
                    //TODO: Discharge batteries
                    if (canCreateFortron())
                    {
                        //Create fortron
                        provideFortron(getFortronCreationRate(), true);

                        //Eat power
                        getBattery().removeEnergyFromStorage(getPowerUsage(), true);

                        //Consume fuel, in power mode this boosts output
                        if (fuelTimer == 0 && isItemValidForSlot(SLOT_FUEL, getStackInSlot(SLOT_FUEL)))
                        {
                            decrStackSize(SLOT_FUEL, 1);
                            this.fuelTimer = (200 * Math.max(getModuleCount(ItemModuleScale.class) / 20, 1));
                        }

                        //Tick down processing time
                        if (fuelTimer > 0)
                        {
                            fuelTimer--;
                        }
                    }
                }
            }
        }
        else if (isActive())
        {
            animation++;
        }
    }

    protected boolean canCreateFortron()
    {
        //In no power mode, consume items
        return !MFFSSettings.COERCION_USE_POWER && isItemValidForSlot(SLOT_FUEL, getStackInSlot(SLOT_FUEL))
                //In power mode, consume energy
                || getBattery().getEnergyStored() >= getPowerUsage();
    }

    /**
     * How much fortron is created each cycle
     *
     * @return value greater than zero
     */
    public int getFortronCreationRate()
    {
        if (isActive())
        {
            int fortron = MFFSSettings.COERCION_OUTPUT_PER_TICK + MFFSSettings.COERCION_OUTPUT_PER_TICK * getModuleCount(ItemModuleSpeed.class);
            if (this.fuelTimer > 0)
            {
                fortron *= MFFSSettings.COERCION_FUEL_BONUS;
            }
            return fortron;
        }
        return 0;
    }

    //===========================================
    //========== Inventory Code =================
    //===========================================

    @Override
    public int getSizeInventory()
    {
        return 6;
    }


    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entityPlayer)
    {
        List<ItemStack> stack = super.getRemovedItems(entityPlayer);
        stack.add(new ItemStack(ModularForceFieldSystem.coercionDeriver));
        return stack;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
    {
        if (itemStack != null)
        {
            if (slotID >= moduleInventory.start)
            {
                return itemStack.getItem() instanceof IFieldModule;
            }
            switch (slotID)
            {
                case SLOT_FREQUENCY:
                    return itemStack.getItem() instanceof ItemCardFrequency;
                case SLOT_BATTERY://battery
                    return false;
                case SLOT_FUEL:
                    return itemStack.getItem() == Items.dye && itemStack.getItemDamage() == 4 || itemStack.getItem() == Items.quartz;
            }
        }
        return false;
    }

    //===========================================
    //========== Save/Load code =================
    //===========================================

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("process", fuelTimer);
        nbt.setBoolean("inverse", outputPower);
        if (energyBuffer != null)
        {
            nbt.setInteger("energy", energyBuffer.getEnergyStored());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        fuelTimer = nbt.getInteger("process");
        outputPower = nbt.getBoolean("inverse");
        getBattery().setEnergyStored(nbt.getInteger("energy"));
    }

    //===========================================
    //============= Power code ==================
    //===========================================

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        IEnergyBuffer buffer = getEnergyBuffer(from);
        if (buffer != null)
        {
            //Limits and converts to UE energy
            maxReceive = Math.min(getTransferLimit(), UniversalEnergySystem.RF_HANDLER.toUEEnergy(maxReceive));

            //Add energy
            int received = buffer.addEnergyToStorage(maxReceive, !simulate);

            //Convert result
            return UniversalEnergySystem.RF_HANDLER.fromUE(received);
        }
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        IEnergyBuffer buffer = getEnergyBuffer(from);
        if (buffer != null)
        {
            //Limits and converts to UE energy
            maxExtract = Math.min(getTransferLimit(), UniversalEnergySystem.RF_HANDLER.toUEEnergy(maxExtract));

            //Extract energy
            int extracted = buffer.removeEnergyFromStorage(maxExtract, !simulate);

            //Convert result
            return UniversalEnergySystem.RF_HANDLER.fromUE(extracted);
        }
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        IEnergyBuffer buffer = getEnergyBuffer(from);
        if (buffer != null)
        {
            return UniversalEnergySystem.RF_HANDLER.fromUE(buffer.getEnergyStored());
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        return UniversalEnergySystem.RF_HANDLER.fromUE(getBattery().getMaxBufferSize());
    }

    /**
     * Amount of power that can be moved in and out of the battery
     *
     * @return greater than zero
     */
    protected int getTransferLimit()
    {
        return Math.round(getBattery().getMaxBufferSize() * MFFSSettings.COERCION_BATTERY_TRANSFER_PERCENTAGE);
    }

    /**
     * Returns TRUE if the TileMFFS can connect on a given side.
     *
     * @param from
     */
    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return MFFSSettings.COERCION_USE_POWER;
    }

    /**
     * Battery storing power
     *
     * @return battery, will create if null
     */
    public CoercionEnergyBuffer getBattery()
    {
        if (energyBuffer == null)
        {
            energyBuffer = new CoercionEnergyBuffer(this);
        }
        return energyBuffer;
    }

    /**
     * How much power is consumed per tick
     *
     * @return greater than zero
     */
    public int getPowerUsage()
    {
        return MFFSSettings.COERCION_POWER_COST + MFFSSettings.COERCION_POWER_COST * getModuleCount(ItemModuleSpeed.class);
    }

    @Override
    public IEnergyBuffer getEnergyBuffer(ForgeDirection side)
    {
        return energyBuffer;
    }

    /**
     * Custom battery implementation to allow dynamic changes to take effect without resetting battery instance
     */
    public static class CoercionEnergyBuffer extends AbstractEnergyBuffer
    {
        protected TileCoercionDeriver host;

        public CoercionEnergyBuffer(TileCoercionDeriver host)
        {
            this.host = host;
        }

        @Override
        public int addEnergyToStorage(int energy, boolean doAction) //TODO implement transfer limits
        {
            //Block energy addition, still allow removal
            if (MFFSSettings.COERCION_USE_POWER)
            {
                return super.addEnergyToStorage(energy, doAction);
            }
            return 0;
        }

        @Override
        public int getMaxBufferSize()
        {
            return MFFSSettings.COERCION_BATTERY_SIZE; //TODO maybe implement scaling?
        }
    }
}
