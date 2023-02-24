package dev.su5ed.mffs.util;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.item.InfiniteCardItem;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.List;

public final class ModUtil {

    public static BlockPos rotateByAngle(BlockPos pos, double yaw, double pitch, double roll) { // TODO replace with vec variant
        Vec3 vec = rotateByAngleVec(Vec3.atLowerCornerOf(pos), yaw, pitch, roll);
        return new BlockPos(Math.round(vec.x()), Math.round(vec.y()), Math.round(vec.z()));
    }

    public static Vec3 rotateByAngleVec(Vec3 pos, double yaw, double pitch, double roll) {
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
        Item item = stack.getItem();
        return item instanceof FrequencyCard || item instanceof InfiniteCardItem;
    }

    public static boolean isModule(ItemStack stack) {
        return stack.getCapability(ModCapabilities.MODULE).isPresent();
    }

    public static boolean isModule(ItemStack stack, Module module) {
        return stack.getCapability(ModCapabilities.MODULE)
            .filter(mod -> mod == module)
            .isPresent();
    }

    public static boolean isProjectorMode(ItemStack stack) {
        return stack.getCapability(ModCapabilities.PROJECTOR_MODE).isPresent();
    }

    private ModUtil() {}
}
