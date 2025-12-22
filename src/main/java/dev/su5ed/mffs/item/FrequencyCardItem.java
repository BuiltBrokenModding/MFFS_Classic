package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModDataComponentTypes;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class FrequencyCardItem extends BaseItem {
    private static final int MAX_FREQUENCY = 999999;

    public FrequencyCardItem(Properties properties) {
        super(new ExtendedItemProperties(properties.stacksTo(1)).description());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);
            FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD);
            if (card != null) {
                if (!level.isClientSide) {
                    int frequency = level.random.nextInt(MAX_FREQUENCY + 1);
                    card.setFrequency(frequency);
                    player.displayClientMessage(ModUtil.translate("info", "frequency.generated",
                        Component.literal(String.valueOf(frequency)).withStyle(ChatFormatting.AQUA)), true);
                }
                return InteractionResult.CONSUME;
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        return Optional.ofNullable(level.getBlockEntity(context.getClickedPos()))
            .map(be -> level.getCapability(ModCapabilities.FORTRON, be.getBlockPos(), be.getBlockState(), be, null))
            .<InteractionResult>map(fortron -> {
                if (!level.isClientSide) {
                    int frequency = Objects.requireNonNull(stack.getCapability(ModCapabilities.FREQUENCY_CARD)).getFrequency();
                    fortron.setFrequency(frequency);
                    context.getPlayer().displayClientMessage(ModUtil.translate("info", "frequency.set",
                        Component.literal(String.valueOf(frequency)).withStyle(ChatFormatting.GREEN)), true);
                }
                return InteractionResult.SUCCESS;
            })
            .orElse(InteractionResult.PASS);
    }

    @Override
    protected void appendHoverTextPre(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverTextPre(stack, context, tooltipDisplay, tooltipAdder, flag);

        FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD);
        if (card != null) {
            tooltipAdder.accept(ModUtil.translate("info", "frequency",
                Component.literal(String.valueOf(card.getFrequency())).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public record FrequencyCardHandler(ItemStack stack) implements FrequencyCard {
        @Override
        public int getFrequency() {
            return this.stack.getOrDefault(ModDataComponentTypes.CARD_FREQUENCY, 0);
        }

        @Override
        public void setFrequency(int frequency) {
            this.stack.set(ModDataComponentTypes.CARD_FREQUENCY, frequency);
        }
    }
}
