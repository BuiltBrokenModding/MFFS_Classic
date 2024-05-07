package dev.su5ed.mffs.util;

import com.mojang.serialization.Codec;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModObjects;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.IFluidBlock;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public final class ModUtil {
    public static final Codec<FieldPermission> FIELD_PERMISSION_CODEC = Codec.STRING.xmap(FieldPermission::valueOf, FieldPermission::name);
    public static final StreamCodec<FriendlyByteBuf, FieldPermission> FIELD_PERMISSION_STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(FieldPermission.class);
    public static final StreamCodec<FriendlyByteBuf, InterdictionMatrix.ConfiscationMode> CONFISCATION_MODE_STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(InterdictionMatrix.ConfiscationMode.class);
    public static final StreamCodec<FriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, Vec3::x,
        ByteBufCodecs.DOUBLE, Vec3::y,
        ByteBufCodecs.DOUBLE, Vec3::z,
        Vec3::new
    );

    public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, @Nullable V> nullable() {
        return codec -> new StreamCodec<>() {
            @Nullable
            public V decode(B buf) {
                return buf.readBoolean() ? codec.decode(buf) : null;
            }

            public void encode(B buf, @Nullable V value) {
                if (value != null) {
                    buf.writeBoolean(true);
                    codec.encode(buf, value);
                } else {
                    buf.writeBoolean(false);
                }
            }
        };
    }

    public static Vec3 rotateByAngleExact(Vec3 pos, double yaw, double pitch, double roll) {
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);
        double rollRadians = Math.toRadians(roll);
        double x = pos.x();
        double y = pos.y();
        double z = pos.z();

        double mulX = x * Math.cos(yawRadians) * Math.cos(pitchRadians) + z * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) - Math.sin(yawRadians) * Math.cos(rollRadians)) + y * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) + Math.sin(yawRadians) * Math.sin(rollRadians));
        double mulZ = x * Math.sin(yawRadians) * Math.cos(pitchRadians) + z * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) + Math.cos(yawRadians) * Math.cos(rollRadians)) + y * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) - Math.cos(yawRadians) * Math.sin(rollRadians));
        double mulY = -x * Math.sin(pitchRadians) + z * Math.cos(pitchRadians) * Math.sin(rollRadians) + y * Math.cos(pitchRadians) * Math.cos(rollRadians);

        return new Vec3(mulX, mulY, mulZ);
    }

    public static boolean moveItemStackTo(ItemStack stack, List<Slot> slots) {
        int i = 0;
        boolean success = false;

        if (stack.isStackable()) {
            while (!stack.isEmpty() && i < slots.size()) {
                Slot slot = slots.get(i);
                ItemStack slotStack = slot.getItem();
                if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(stack, slotStack)) {
                    int total = stack.getCount() + slotStack.getCount();
                    int maxCount = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());

                    if (total <= maxCount) {
                        stack.setCount(0);
                        slotStack.setCount(total);
                        slot.setChanged();
                        success = true;
                    } else if (slotStack.getCount() < maxCount) {
                        stack.shrink(maxCount - slotStack.getCount());
                        slotStack.setCount(maxCount);
                        slot.setChanged();
                        success = true;
                    }
                }
                i++;
            }
        }

        i = 0;
        if (!stack.isEmpty()) {
            while (i < slots.size()) {
                Slot slot = slots.get(i);
                ItemStack slotStack = slot.getItem();
                if (slotStack.isEmpty() && slot.mayPlace(stack)) {
                    if (stack.getCount() > slot.getMaxStackSize()) {
                        slot.set(stack.split(slot.getMaxStackSize()));
                    } else {
                        slot.set(stack.split(stack.getCount()));
                    }

                    slot.setChanged();
                    return true;
                }
                i++;
            }
        }

        return success;
    }

    public static BlockPos normalize(BlockPos pos, BlockPos other) {
        if (other.getX() <= pos.getX()) {
            pos = pos.east();
        }
        if (other.getZ() <= pos.getZ()) {
            pos = pos.south();
        }
        if (other.getY() <= pos.getY()) {
            pos = pos.above();
        }
        return pos;
    }

    public static MutableComponent translate(FieldPermission permission) {
        return translate("info", "field_permission." + permission.name().toLowerCase(Locale.ROOT));
    }

    public static MutableComponent translateTooltip(FieldPermission permission) {
        return translate("info", "field_permission." + permission.name().toLowerCase(Locale.ROOT) + ".tooltip");
    }

    public static MutableComponent translate(String prefix, String key, Object... args) {
        return Component.translatable(translationKey(prefix, key), args);
    }

    public static String translationKey(String prefix, String key) {
        return prefix + "." + MFFSMod.MODID + "." + key;
    }

    public static double distance(BlockPos first, BlockPos second) {
        double d0 = second.getX() - first.getX();
        double d1 = second.getY() - first.getY();
        double d2 = second.getZ() - first.getZ();
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static boolean isLiquidBlock(Block block) {
        return block instanceof LiquidBlock || block instanceof IFluidBlock;
    }

    public static boolean isCard(ItemStack stack) {
        return stack.getCapability(ModCapabilities.FREQUENCY_CARD) != null;
    }

    public static boolean isIdentificationCard(ItemStack stack) {
        return stack.getCapability(ModCapabilities.IDENTIFICATION_CARD) != null;
    }

    public static boolean isModule(ItemStack stack) {
        return stack.getCapability(ModCapabilities.MODULE_TYPE) != null;
    }

    public static boolean isModule(ItemStack stack, Module.Category category) {
        ModuleType<?> moduleType = stack.getCapability(ModCapabilities.MODULE_TYPE);
        return moduleType != null && moduleType.getCategories().contains(category);
    }

    public static boolean isModule(ItemStack stack, ModuleType<?> module) {
        ModuleType<?> moduleType = stack.getCapability(ModCapabilities.MODULE_TYPE);
        return moduleType == module;
    }

    public static boolean isProjectorMode(ItemStack stack) {
        return stack.getCapability(ModCapabilities.PROJECTOR_MODE) != null;
    }

    @Nullable
    public static <T extends Enum<T>> T getEnumConstantSafely(Class<T> clazz, String name) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    // Source: https://github.com/XFactHD/FramedBlocks/blob/518dcd984b874a0d3abac34d0f2ee6e9360062d7/src/main/java/xfacthd/framedblocks/api/util/Utils.java#L65
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to) {
            return shape;
        }

        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(
                buffer[1],
                Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static void shockEntity(Entity entity, int damage) {
        entity.hurt(entity.level().damageSources().source(ModObjects.FIELD_SHOCK_TYPE), damage);
        if (entity instanceof ServerPlayer serverPlayer) {
            ModObjects.DAMAGE_TRIGGER.get().trigger(serverPlayer, ModObjects.FIELD_SHOCK_TYPE);
        }
    }

    @SuppressWarnings("unused") // Called from injected hook
    public static void onSetBlock(Level level, BlockPos pos, BlockState state) {
        SetBlockEvent event = new SetBlockEvent(level, pos, state);
        NeoForge.EVENT_BUS.post(event);
    }

    private ModUtil() {
    }
}
