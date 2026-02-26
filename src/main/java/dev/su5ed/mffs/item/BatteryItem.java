package dev.su5ed.mffs.item;

import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import java.util.function.Consumer;

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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        EnergyHandler energy = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        if (energy != null) {
            tooltipAdder.accept(ModUtil.translate("info", "stored_energy",
                    Component.literal(String.valueOf(energy.getAmountAsInt())).withStyle(ChatFormatting.GRAY),
                    Component.literal(String.valueOf(energy.getCapacityAsInt())).withStyle(ChatFormatting.GRAY))
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
        EnergyHandler energy = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        return energy != null ? energy.getAmountAsInt() / (float) energy.getCapacityAsInt() : 0;
    }
}
