package dev.su5ed.mffs.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FrequencyCardItem extends BaseItem {
    private static final int MAX_FREQUENCY = 999999;

    public FrequencyCardItem() {
        super(new ExtendedItemProperties(new Item.Properties().stacksTo(1)).description());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
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
                return InteractionResultHolder.consume(stack);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        return Optional.ofNullable(level.getBlockEntity(context.getClickedPos()))
            .map(be -> level.getCapability(ModCapabilities.FORTRON, be.getBlockPos(), be.getBlockState(), be, null))
            .map(fortron -> {
                if (!level.isClientSide) {
                    int frequency = Objects.requireNonNull(stack.getCapability(ModCapabilities.FREQUENCY_CARD)).getFrequency();
                    fortron.setFrequency(frequency);
                    context.getPlayer().displayClientMessage(ModUtil.translate("info", "frequency.set",
                        Component.literal(String.valueOf(frequency)).withStyle(ChatFormatting.GREEN)), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            })
            .orElse(InteractionResult.PASS);
    }

    @Override
    public void appendHoverTextPre(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverTextPre(stack, context, tooltipComponents, isAdvanced);

        FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD);
        if (card != null) {
            tooltipComponents.add(ModUtil.translate("info", "frequency",
                Component.literal(String.valueOf(card.getFrequency())).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static class FrequencyCardAttachment implements FrequencyCard {
        public static final Codec<FrequencyCardAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("frequency").forGetter(FrequencyCardAttachment::getFrequency)
        ).apply(instance, FrequencyCardAttachment::new));
        public static final StreamCodec<FriendlyByteBuf, FrequencyCardAttachment> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            FrequencyCardAttachment::getFrequency,
            FrequencyCardAttachment::new
        );

        private int frequency;

        public FrequencyCardAttachment() {}

        private FrequencyCardAttachment(int frequency) {
            this.frequency = frequency;
        }

        @Override
        public int getFrequency() {
            return this.frequency;
        }

        @Override
        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }
}
