package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FrequencyCardItem extends Item implements FrequencyCard {
    private static final int MAX_FREQUENCY_DIGITS = 6;

    public FrequencyCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getFrequency(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt("frequency");
    }

    @Override
    public void setFrequency(ItemStack stack, int frequency) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("frequency", frequency);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);
            int frequency = level.random.nextInt(10 ^ MAX_FREQUENCY_DIGITS - 1);
            setFrequency(stack, frequency);
            player.displayClientMessage(ModUtil.translate("info", "frequency.generated", frequency).withStyle(ChatFormatting.AQUA), true);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        tooltipComponents.add(ModUtil.translate("info", "frequency", getFrequency(stack)).withStyle(ChatFormatting.GRAY));
    }
}
