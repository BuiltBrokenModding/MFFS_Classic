package dev.su5ed.mffs.item;

import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class BatteryItem extends Item {
    private final int capacity;
    private final int maxTransfer;

    public BatteryItem(Properties properties) {
        this(properties, 100000, 1000);
    }

    public BatteryItem(Properties properties, int capacity, int maxTransfer) {
        super(properties.stacksTo(1));

        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getMaxTransfer() {
        return this.maxTransfer;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);

        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            tooltipComponents.add(ModUtil.translate("info", "stored_energy",
                    Component.literal(String.valueOf(energy.getEnergyStored())).withStyle(ChatFormatting.GRAY),
                    Component.literal(String.valueOf(energy.getMaxEnergyStored())).withStyle(ChatFormatting.GRAY))
                .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getChargeLevel(stack) < 1;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float charge = getChargeLevel(stack);
        return (int) Math.round(charge * 13.0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float charge = getChargeLevel(stack);
        return Mth.hsvToRgb(charge / 3.0F, 1, 1);
    }

    private static float getChargeLevel(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null ? energy.getEnergyStored() / (float) energy.getMaxEnergyStored() : 0;
    }
}
