package dev.su5ed.mffs.util;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.item.InfiniteCardItem;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.IFluidBlock;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public final class ModUtil {

    public static BlockPos rotateByAngle(BlockPos pos, double yaw, double pitch, double roll) {
        Vec3 vec = rotateByAngleExact(Vec3.atLowerCornerOf(pos), yaw, pitch, roll);
        return new BlockPos(Math.round(vec.x()), Math.round(vec.y()), Math.round(vec.z()));
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
                if (!slotStack.isEmpty() && ItemStack.isSameItemSameTags(stack, slotStack)) {
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
        return stack.getCapability(ModCapabilities.FREQUENCY_CARD).isPresent() || stack.getItem() instanceof InfiniteCardItem;
    }

    public static boolean isIdentificationCard(ItemStack stack) {
        return stack.getCapability(ModCapabilities.IDENTIFICATION_CARD).isPresent();
    }

    public static boolean isModule(ItemStack stack) {
        return stack.getCapability(ModCapabilities.MODULE).isPresent();
    }

    public static boolean isModule(ItemStack stack, Module.Category category) {
        return stack.getCapability(ModCapabilities.MODULE)
            .map(module -> module.getCategory() == category)
            .orElse(false);
    }

    public static boolean isModule(ItemStack stack, Module module) {
        return stack.getCapability(ModCapabilities.MODULE)
            .filter(mod -> mod == module)
            .isPresent();
    }

    public static boolean isProjectorMode(ItemStack stack) {
        return stack.getCapability(ModCapabilities.PROJECTOR_MODE).isPresent();
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

    private ModUtil() {}
}
