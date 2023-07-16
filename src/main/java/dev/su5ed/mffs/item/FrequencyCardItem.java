package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FrequencyCardItem extends BaseItem {
    private static final int MAX_FREQUENCY = 999999;

    public FrequencyCardItem() {
        super(new ExtendedItemProperties(new Item.Properties().stacksTo(1)).description());
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FrequencyCardCapability();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);
            return stack.getCapability(ModCapabilities.FREQUENCY_CARD)
                .map(card -> {
                    if (!level.isClientSide) {
                        int frequency = level.random.nextInt(MAX_FREQUENCY + 1);
                        card.setFrequency(frequency);
                        player.displayClientMessage(ModUtil.translate("info", "frequency.generated",
                            Component.literal(String.valueOf(frequency)).withStyle(ChatFormatting.AQUA)), true);
                    }
                    return InteractionResultHolder.consume(stack);
                })
                .orElseGet(() -> InteractionResultHolder.pass(stack));
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        return Optional.ofNullable(level.getBlockEntity(context.getClickedPos()))
            .flatMap(be -> be.getCapability(ModCapabilities.FORTRON).resolve())
            .map(fortron -> {
                if (!level.isClientSide) {
                    int frequency = stack.getCapability(ModCapabilities.FREQUENCY_CARD).map(FrequencyCard::getFrequency).orElseThrow();
                    fortron.setFrequency(frequency);
                    context.getPlayer().displayClientMessage(ModUtil.translate("info", "frequency.set",
                        Component.literal(String.valueOf(frequency)).withStyle(ChatFormatting.GREEN)), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            })
            .orElse(InteractionResult.PASS);
    }

    @Override
    public void appendHoverTextPre(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverTextPre(stack, level, tooltipComponents, isAdvanced);

        stack.getCapability(ModCapabilities.FREQUENCY_CARD).map(FrequencyCard::getFrequency)
            .ifPresent(frequency -> tooltipComponents.add(ModUtil.translate("info", "frequency",
                Component.literal(String.valueOf(frequency)).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_GRAY)));
    }

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        INBTSerializable<CompoundTag> card = stack.getCapability(ModCapabilities.FREQUENCY_CARD).resolve().orElseThrow();
        tag.put("card_capability", card.serializeNBT());
        return tag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag tag) {
        super.readShareTag(stack, tag);

        if (tag != null) {
            stack.getCapability(ModCapabilities.FREQUENCY_CARD).ifPresent(card -> {
                CompoundTag cardTag = tag.getCompound("card_capability");
                card.deserializeNBT(cardTag);
            });
        }
    }

    public static class FrequencyCardCapability implements ICapabilityProvider, FrequencyCard {
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
