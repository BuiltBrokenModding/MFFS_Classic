package dev.su5ed.mffs.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends Item {

    private final int capacity;
    private final int maxTransfer;

    public BatteryItem() {
        this(100000, 1000);
    }

    public BatteryItem(int capacity, int maxTransfer) {
        setMaxStackSize(1);
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }

    public int getCapacity() { return this.capacity; }
    public int getMaxTransfer() { return this.maxTransfer; }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energy != null) {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.stored_energy",
                TextFormatting.GRAY + String.valueOf(energy.getEnergyStored()),
                TextFormatting.GRAY + String.valueOf(energy.getMaxEnergyStored())));
        }
    }

    /** Show the durability bar when not fully charged. */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getChargeLevel(stack) < 1.0f;
    }

    /** 0.0 = full (no bar), 1.0 = empty (full bar). */
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - getChargeLevel(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        float charge = getChargeLevel(stack);
        return MathHelper.hsvToRGB(charge / 3.0F, 1.0F, 1.0F);
    }

    private static float getChargeLevel(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energy != null && energy.getMaxEnergyStored() > 0) {
            return energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
        }
        return 0.0f;
    }
}
