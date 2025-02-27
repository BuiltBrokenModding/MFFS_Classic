package dev.su5ed.mffs.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.setup.ModDataComponentTypes;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CustomProjectorModeItem extends BaseItem {
    private CustomStructureSavedData structureManager;

    public CustomProjectorModeItem(Properties properties) {
        super(new ExtendedItemProperties(properties.stacksTo(1)));
    }

    public record StructureCoords(@Nullable BlockPos primary, @Nullable BlockPos secondary) {
        public static final Codec<StructureCoords> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("primary", null).forGetter(StructureCoords::primary),
            BlockPos.CODEC.optionalFieldOf("secondary", null).forGetter(StructureCoords::secondary)
        ).apply(instance, StructureCoords::new));

        public static final StreamCodec<FriendlyByteBuf, StructureCoords> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ModUtil.nullable()),
            StructureCoords::primary,
            BlockPos.STREAM_CODEC.apply(ModUtil.nullable()),
            StructureCoords::secondary,
            StructureCoords::new
        );

        public boolean canBuild() {
            return this.primary != null && this.secondary != null;
        }

        public boolean selectPrimary() {
            return this.primary == null || this.secondary != null;
        }
        
        public boolean selectSecondary() {
            return this.primary != null && this.secondary == null;
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(usedHand);
            StructureCoords coords = stack.get(ModDataComponentTypes.STRUCTURE_COORDS);

            if (player.isShiftKeyDown()) {
                CustomStructureSavedData data = getOrCreateData(serverLevel);
                if (coords != null && coords.canBuild()) {
                    BlockPos primary = coords.primary();
                    BlockPos secondary = coords.secondary();
                    int distance = MFFSConfig.COMMON.maxCustomModeScale.get();
                    if (primary.closerThan(secondary, distance)) {
                        stack.remove(ModDataComponentTypes.STRUCTURE_COORDS);

                        String id = getOrCreateId(stack);
                        data.join(id, level, serverPlayer, primary, secondary, getMode(stack) == Mode.ADDITIVE);

                        player.displayClientMessage(ModUtil.translate("item", "custom_mode.data_saved"), true);
                        ModObjects.FIELD_SHAPE_TRIGGER.get().trigger(serverPlayer);
                    } else {
                        player.displayClientMessage(ModUtil.translate("item", "custom_mode.too_far", distance), true);
                    }
                    return InteractionResult.SUCCESS;
                } else if (stack.has(ModDataComponentTypes.PATTERN_ID)) {
                    String id = stack.get(ModDataComponentTypes.PATTERN_ID);
                    data.clear(level, serverPlayer, id);
                    stack.remove(ModDataComponentTypes.PATTERN_ID);
                    player.displayClientMessage(ModUtil.translate("item", "custom_mode.clear"), true);
                    return InteractionResult.SUCCESS;
                }
            } else if (coords != null && coords.selectSecondary()) {
                HitResult result = player.pick(player.blockInteractionRange(), 0, true);
                if (result instanceof BlockHitResult blockHitResult) {
                    stack.set(ModDataComponentTypes.STRUCTURE_COORDS, new StructureCoords(coords.primary(), blockHitResult.getBlockPos()));
                    selectBlock(player, "secondary_point", ChatFormatting.GOLD);
                    return InteractionResult.SUCCESS;
                }
            } else {
                Mode mode = setMode(stack, getMode(stack).next());
                player.displayClientMessage(ModUtil.translate("item", "custom_mode.changed_mode", mode.getName().withStyle(ChatFormatting.GREEN)), true);
                return InteractionResult.CONSUME;
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            StructureCoords coords = stack.get(ModDataComponentTypes.STRUCTURE_COORDS);
            if (coords == null || coords.selectPrimary()) {
                stack.set(ModDataComponentTypes.STRUCTURE_COORDS, new StructureCoords(pos, null));
                selectBlock(player, "primary_point", ChatFormatting.GREEN);
            } else if (coords != null) {
                stack.set(ModDataComponentTypes.STRUCTURE_COORDS, new StructureCoords(coords.primary(), pos));
                selectBlock(player, "secondary_point", ChatFormatting.GOLD);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void selectBlock(Player player, String key, ChatFormatting color) {
        MutableComponent component = ModUtil.translate("item", "custom_mode.set_" + key).withStyle(color);
        player.displayClientMessage(component, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        Mode mode = getMode(stack);
        tooltipComponents.add(ModUtil.translate("item", "custom_mode.mode", mode.getName().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        String id = stack.get(ModDataComponentTypes.PATTERN_ID);
        if (id != null) {
            tooltipComponents.add(ModUtil.translate("item", "custom_mode.pattern_id", Component.literal(id).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        }
        StructureCoords coords = stack.get(ModDataComponentTypes.STRUCTURE_COORDS);
        if (coords != null && coords.primary() != null) {
            tooltipComponents.add(ModUtil.translate("item", "custom_mode.set_primary_point",
                Component.literal(coords.primary().toShortString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

            if (coords.secondary() != null) {
                tooltipComponents.add(ModUtil.translate("item", "custom_mode.secondary_point",
                    Component.literal(coords.secondary().toShortString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
                tooltipComponents.add(ModUtil.translate("item", "custom_mode.set_secondary_point").withStyle(ChatFormatting.GOLD));
            } else {
                tooltipComponents.add(ModUtil.translate("item", "custom_mode.set_primary_point").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
    }

    public Mode getMode(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.STRUCTURE_MODE, Mode.ADDITIVE);
    }

    public Mode setMode(ItemStack stack, Mode mode) {
        stack.set(ModDataComponentTypes.STRUCTURE_MODE, mode);
        return mode;
    }

    public String getOrCreateId(ItemStack stack) {
        if (!stack.has(ModDataComponentTypes.PATTERN_ID)) {
            String id = RandomStringUtils.randomAlphanumeric(8).toUpperCase(Locale.ROOT);
            stack.set(ModDataComponentTypes.PATTERN_ID, id);
            return id;
        }
        return stack.get(ModDataComponentTypes.PATTERN_ID);
    }

    @SuppressWarnings("resource")
    public CustomStructureSavedData getOrCreateData(ServerLevel level) {
        if (this.structureManager == null) {
            this.structureManager = level.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                CustomStructureSavedData::new,
                (tag, provider) -> {
                    CustomStructureSavedData data = new CustomStructureSavedData();
                    data.load(tag);
                    return data;
                }
            ), CustomStructureSavedData.NAME);
        }
        return this.structureManager;
    }

    public Map<Vec3, BlockState> getFieldBlocks(Projector projector, ItemStack stack) {
        if (projector.be().getLevel() instanceof ServerLevel serverLevel) {
            CustomStructureSavedData data = getOrCreateData(serverLevel);
            String id = stack.get(ModDataComponentTypes.PATTERN_ID);
            CustomStructureSavedData.Structure structure = data.get(id);
            if (structure != null) {
                return structure.getRealBlocks();
            }
        }
        return Map.of();
    }

    public class CustomProjectorModeCapability implements ProjectorMode {
        private final ItemStack stack;

        public CustomProjectorModeCapability(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Set<Vec3> getExteriorPoints(Projector projector) {
            return getFieldBlocks(projector, this.stack).keySet();
        }

        @Override
        public Set<Vec3> getInteriorPoints(Projector projector) {
            return getExteriorPoints(projector);
        }

        @Override
        public boolean isInField(Projector projector, Vec3 position) {
            return false;
        }
    }

    public enum Mode implements StringRepresentable {
        ADDITIVE,
        SUBTRACTIVE;

        private static final Mode[] VALUES = values();
        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Mode.class);

        public Mode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public MutableComponent getName() {
            return ModUtil.translate("item", "custom_mode.mode." + name().toLowerCase(Locale.ROOT));
        }

        @Override
        public String getSerializedName() {
            return name();
        }
    }
}
