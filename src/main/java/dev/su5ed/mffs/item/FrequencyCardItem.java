package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FrequencyCardItem extends Item {
    private static final int MAX_FREQUENCY_DIGITS = 6;

    public FrequencyCardItem(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FrequencyCardCapability();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && player.isShiftKeyDown()) {
            player.getItemInHand(usedHand).getCapability(ModCapabilities.FREQUENCY_CARD).ifPresent(card -> {
                int frequency = level.random.nextInt(10 ^ MAX_FREQUENCY_DIGITS - 1);
                card.setFrequency(frequency);
                player.displayClientMessage(ModUtil.translate("info", "frequency.generated", frequency).withStyle(ChatFormatting.AQUA), true);
            });
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        stack.getCapability(ModCapabilities.FREQUENCY_CARD).map(FrequencyCard::getFrequency)
            .ifPresent(frequency -> tooltipComponents.add(ModUtil.translate("info", "frequency", frequency).withStyle(ChatFormatting.GRAY)));
    }

    public static class FrequencyCardCapability implements ICapabilityProvider, FrequencyCard, INBTSerializable<CompoundTag> {
        private final LazyOptional<FrequencyCard> optional = LazyOptional.of(() -> this);

        private int frequency;

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return ModCapabilities.FREQUENCY_CARD.orEmpty(cap, this.optional);
        }

        @Override
        public int getFrequency() {
            return this.frequency;
        }

        @Override
        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("frequency", this.frequency);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.frequency = tag.getInt("frequency");
        }
    }
}
