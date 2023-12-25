package dev.su5ed.mffs.item;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CustomProjectorModeItem extends BaseItem {
    public static final String TAG_PATTERN_ID = "pattern_id";
    private static final String TAG_MODE = "mode";
    public static final String TAG_POINT_PRIMARY = "primary_point";
    public static final String TAG_POINT_SECONDARY = "secondary_point";

    private CustomStructureSavedData structureManager;

    public CustomProjectorModeItem() {
        super(new ExtendedItemProperties(new Item.Properties().stacksTo(1)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(usedHand);
            CompoundTag tag = stack.getOrCreateTag();

            if (player.isShiftKeyDown()) {
                CustomStructureSavedData data = getOrCreateData(serverLevel);
                if (tag.contains(TAG_POINT_PRIMARY) && tag.contains(TAG_POINT_SECONDARY)) {
                    BlockPos primary = getPos(tag, TAG_POINT_PRIMARY);
                    BlockPos secondary = getPos(tag, TAG_POINT_SECONDARY);
                    int distance = MFFSConfig.COMMON.maxCustomModeScale.get();
                    if (primary.closerThan(secondary, distance)) {
                        tag.remove(TAG_POINT_PRIMARY);
                        tag.remove(TAG_POINT_SECONDARY);

                        String id = getOrCreateId(tag);
                        data.join(id, level, serverPlayer, primary, secondary, getMode(tag) == Mode.ADDITIVE);

                        player.displayClientMessage(ModUtil.translate("item", "custom_mode.data_saved"), true);
                        ModObjects.FIELD_SHAPE_TRIGGER.trigger(serverPlayer);
                    } else {
                        player.displayClientMessage(ModUtil.translate("item", "custom_mode.too_far", distance), true);
                    }
                    return InteractionResultHolder.success(stack);
                } else if (tag.contains(TAG_PATTERN_ID)) {
                    String id = tag.getString(TAG_PATTERN_ID);
                    data.clear(level, serverPlayer, id);
                    tag.remove(TAG_PATTERN_ID);
                    player.displayClientMessage(ModUtil.translate("item", "custom_mode.clear"), true);
                    return InteractionResultHolder.success(stack);
                }
            } else if (tag.contains(TAG_POINT_PRIMARY) && !tag.contains(TAG_POINT_SECONDARY)) {
                HitResult result = player.pick(player.getBlockReach(), 0, true);
                if (result instanceof BlockHitResult blockHitResult) {
                    selectBlock(player, tag, blockHitResult.getBlockPos(), TAG_POINT_SECONDARY, ChatFormatting.GOLD);
                    return InteractionResultHolder.success(stack);
                }
            } else {
                Mode mode = setMode(tag, getMode(tag).next());
                player.displayClientMessage(ModUtil.translate("item", "custom_mode.changed_mode", mode.getName().withStyle(ChatFormatting.GREEN)), true);
                return InteractionResultHolder.consume(stack);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            if (!tag.contains(TAG_POINT_PRIMARY) || tag.contains(TAG_POINT_SECONDARY)) {
                tag.remove(TAG_POINT_SECONDARY);
                selectBlock(player, tag, pos, TAG_POINT_PRIMARY, ChatFormatting.GREEN);
            } else {
                selectBlock(player, tag, pos, TAG_POINT_SECONDARY, ChatFormatting.GOLD);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void selectBlock(Player player, CompoundTag tag, BlockPos pos, String key, ChatFormatting color) {
        tag.put(key, NbtUtils.writeBlockPos(pos));
        MutableComponent component = ModUtil.translate("item", "custom_mode.set_" + key).withStyle(color);
        player.displayClientMessage(component, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();

        Mode mode = getMode(tag);
        tooltipComponents.add(ModUtil.translate("item", "custom_mode.mode", mode.getName().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        String id = tag.getString(TAG_PATTERN_ID);
        if (!id.isEmpty()) {
            tooltipComponents.add(ModUtil.translate("item", "custom_mode.pattern_id", Component.literal(id).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (tag.contains(TAG_POINT_PRIMARY)) {
            BlockPos primary = getPos(tag, TAG_POINT_PRIMARY);
            tooltipComponents.add(ModUtil.translate("item", "custom_mode." + TAG_POINT_PRIMARY,
                Component.literal(primary.toShortString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

            if (tag.contains(TAG_POINT_SECONDARY)) {
                BlockPos secondary = getPos(tag, TAG_POINT_SECONDARY);
                tooltipComponents.add(ModUtil.translate("item", "custom_mode." + TAG_POINT_SECONDARY,
                    Component.literal(secondary.toShortString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
                tooltipComponents.add(ModUtil.translate("item", "custom_mode.set_" + TAG_POINT_SECONDARY).withStyle(ChatFormatting.GOLD));
            } else {
                tooltipComponents.add(ModUtil.translate("item", "custom_mode.set_" + TAG_POINT_PRIMARY).withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public Mode getMode(CompoundTag tag) {
        return tag.contains(TAG_MODE) ? Mode.valueOf(tag.getString(TAG_MODE)) : Mode.ADDITIVE;
    }

    public Mode setMode(CompoundTag tag, Mode mode) {
        tag.putString(TAG_MODE, mode.name());
        return mode;
    }

    public static BlockPos getPos(CompoundTag tag, String key) {
        return NbtUtils.readBlockPos(tag.getCompound(key));
    }

    public String getOrCreateId(CompoundTag tag) {
        if (!tag.contains(TAG_PATTERN_ID)) {
            String id = RandomStringUtils.randomAlphanumeric(8).toUpperCase(Locale.ROOT);
            tag.putString(TAG_PATTERN_ID, id);
            return id;
        }
        return tag.getString(TAG_PATTERN_ID);
    }

    @SuppressWarnings("resource")
    public CustomStructureSavedData getOrCreateData(ServerLevel level) {
        if (this.structureManager == null) {
            this.structureManager = level.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                CustomStructureSavedData::new,
                tag -> {
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
            CompoundTag tag = stack.getOrCreateTag();
            CustomStructureSavedData data = getOrCreateData(serverLevel);
            String id = tag.getString(TAG_PATTERN_ID);
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

    private enum Mode {
        ADDITIVE,
        SUBTRACTIVE;

        private static final Mode[] VALUES = values();

        public Mode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public MutableComponent getName() {
            return ModUtil.translate("item", "custom_mode.mode." + name().toLowerCase(Locale.ROOT));
        }
    }
}
