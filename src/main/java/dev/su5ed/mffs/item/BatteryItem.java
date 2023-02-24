package dev.su5ed.mffs.item;

import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.CustomEnergyStorage;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends Item {
    private final int capacity;
    private final int maxTransfer;

    public BatteryItem() {
        this(100000, 1000);
    }

    public BatteryItem(int capacity, int maxTransfer) {
        super(ModItems.itemProperties().stacksTo(1));

        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        stack.getCapability(ForgeCapabilities.ENERGY)
            .ifPresent(energy -> tooltipComponents.add(ModUtil.translate("info", "stored_energy", energy.getEnergyStored(), energy.getMaxEnergyStored())
                .withStyle(ChatFormatting.GRAY)));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BatteryEnergyStorage(this.capacity, this.maxTransfer);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
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
        return stack.getCapability(ForgeCapabilities.ENERGY)
            .map(energy -> energy.getEnergyStored() / (float) energy.getMaxEnergyStored())
            .orElse(0.0F);
    }

    private static class BatteryEnergyStorage implements ICapabilityProvider, INBTSerializable<Tag> {
        private final CustomEnergyStorage storage;
        private final LazyOptional<IEnergyStorage> optional;

        public BatteryEnergyStorage(int capacity, int maxTransfer) {
            this.storage = new CustomEnergyStorage(capacity, maxTransfer, () -> true, () -> {});
            this.optional = LazyOptional.of(() -> this.storage);
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return ForgeCapabilities.ENERGY.orEmpty(cap, this.optional);
        }

        @Override
        public Tag serializeNBT() {
            return this.storage.serializeNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            this.storage.deserializeNBT(nbt);
        }
    }
}
