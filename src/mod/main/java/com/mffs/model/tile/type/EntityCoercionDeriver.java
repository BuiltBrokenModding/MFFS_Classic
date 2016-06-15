package com.mffs.model.tile.type;

import com.mffs.MFFSConfig;
import com.mffs.api.modules.IModule;
import com.mffs.model.items.card.CardFrequency;
import com.mffs.model.items.modules.upgrades.ModuleScale;
import com.mffs.model.items.modules.upgrades.ModuleSpeed;
import com.mffs.model.tile.TileElectrical;
import net.minecraft.item.ItemStack;

/**
 * Created by pwaln on 6/1/2016.
 */
public final class EntityCoercionDeriver extends TileElectrical {

    public static final int FUEL_PROCESS_TIME = 200;
    public static final int MULTIPLE_PRODUCTION = 4;
    public static final float UE_FORTRON_RATIO = 0.001F;
    public static final int ENERGY_LOSS = 1;
    public static final int SLOT_FREQUENCY = 0;
    public static final int SLOT_BATTERY = 1;
    public static final int SLOT_FUEL = 2;
    private static final int DEFAULT_WATTAGE = 5000000;
    public int processTime = 0;
    public boolean isInversed;

    public EntityCoercionDeriver() {
        this.storage.setCapacity(30);
        this.module_index = 3;
        storage.setCapacity(Math.round(getWattage()));
        storage.setMaxTransfer(Math.round(getWattage() / 20L));
    }

    @Override
    public void start() {
        super.start();
        storage.setCapacity(Math.round(getWattage()));
        storage.setMaxTransfer(Math.round(getWattage() / 20L));
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {

            if (isActive()) {
                if (isInversed && MFFSConfig.ENABLE_ELECTRICITY) {
                    if (storage.getEnergyStored() < storage.getMaxEnergyStored()) {
                        int produce = (int) Math.floor(requestFortron(getProductionRate() / 20, true) / 0.001);
                        storage.receiveEnergy(produce, false);
                    }
                    //TODO: recharge from battery!
                } else if (getFortronEnergy() < getFortronCapacity()) {
                    //CHeck slot 1 for batteries etc
                    //TODO: Discharge battery
                    if (!MFFSConfig.ENABLE_ELECTRICITY && isItemValidForSlot(SLOT_FUEL, getStackInSlot(SLOT_FUEL))
                            || storage.extractEnergy(storage.getMaxExtract(), true) >= storage.getMaxExtract()) {
                        provideFortron(getProductionRate(), true);
                        storage.extractEnergy(storage.getMaxExtract(), false);
                        if (processTime == 0 && isItemValidForSlot(SLOT_FUEL, getStackInSlot(SLOT_FUEL))) {
                            decrStackSize(SLOT_FUEL, 1);
                            this.processTime = (200 * Math.max(getModuleCount(ModuleScale.class) / 20, 1));
                        }
                        if (processTime > 0) {
                            processTime--;
                        }
                    }
                }
                return;
            }//animation
            animation++;
        }
    }

    @Override
    public void fireEvents(int... slots) {
        super.fireEvents(slots);
        storage.setCapacity(Math.round(getWattage()));
        storage.setMaxTransfer(Math.round(getWattage() / 20L));
    }

    @Override
    public int getSizeInventory() {
        return 6;
    }

    public float getWattage() {
        return (DEFAULT_WATTAGE + DEFAULT_WATTAGE * (getModuleCount(ModuleSpeed.class) / 8.0F));
    }

    public int getProductionRate() {
        if (isActive()) {
            int production = (int) (getWattage() / 20.0F * 0.001F * MFFSConfig.FORTRON_PRODUCTION_MULTIPLIER);

            if (this.processTime > 0) {
                production *= 4;
            }

            return production;
        }

        return 0;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
        if (itemStack != null) {
            if (slotID >= this.module_index) {
                return itemStack.getItem() instanceof IModule;
            }

            switch (slotID) {
                case 0:
                    return itemStack.getItem() instanceof CardFrequency;
                case 1://battery
                    return false;
                //case 2:
                //  return (itemStack.isItemEqual(new ItemStack(Item., 1, 4))) || (itemStack.isItemEqual(new ItemStack(Item.field_94583_ca)));
            }

        }
        return false;
    }
}
