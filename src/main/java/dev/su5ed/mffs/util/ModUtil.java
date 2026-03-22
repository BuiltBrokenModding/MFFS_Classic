package dev.su5ed.mffs.util;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Utility class for MFFS.
 * Capability checks use stack.hasCapability() instead of stack.getCapability() != null.
 */
public final class ModUtil {
    public static final Integer TICKS_PER_SECOND = 20;

    /**
     * Rotates a position vector by exact Euler angles (yaw, pitch, roll) in degrees.
     */
    public static Vec3d rotateByAngleExact(Vec3d pos, double yaw, double pitch, double roll) {
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);
        double rollRadians = Math.toRadians(roll);
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        double mulX = x * Math.cos(yawRadians) * Math.cos(pitchRadians)
            + z * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) - Math.sin(yawRadians) * Math.cos(rollRadians))
            + y * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) + Math.sin(yawRadians) * Math.sin(rollRadians));
        double mulZ = x * Math.sin(yawRadians) * Math.cos(pitchRadians)
            + z * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) + Math.cos(yawRadians) * Math.cos(rollRadians))
            + y * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) - Math.cos(yawRadians) * Math.sin(rollRadians));
        double mulY = -x * Math.sin(pitchRadians)
            + z * Math.cos(pitchRadians) * Math.sin(rollRadians)
            + y * Math.cos(pitchRadians) * Math.cos(rollRadians);

        return new Vec3d(mulX, mulY, mulZ);
    }

    /**
     * Attempts to merge the given ItemStack into the provided slots.
     */
    public static boolean moveItemStackTo(ItemStack stack, List<Slot> slots) {
        int i = 0;
        boolean success = false;

        if (stack.isStackable()) {
            while (!stack.isEmpty() && i < slots.size()) {
                Slot slot = slots.get(i);
                ItemStack slotStack = slot.getStack();
                if (!slotStack.isEmpty() && ItemStack.areItemsEqual(stack, slotStack) && ItemStack.areItemStackTagsEqual(stack, slotStack)) {
                    int total = stack.getCount() + slotStack.getCount();
                    int maxCount = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

                    if (total <= maxCount) {
                        stack.setCount(0);
                        slotStack.setCount(total);
                        slot.onSlotChanged();
                        success = true;
                    } else if (slotStack.getCount() < maxCount) {
                        stack.shrink(maxCount - slotStack.getCount());
                        slotStack.setCount(maxCount);
                        slot.onSlotChanged();
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
                ItemStack slotStack = slot.getStack();
                if (slotStack.isEmpty() && slot.isItemValid(stack)) {
                    if (stack.getCount() > slot.getSlotStackLimit()) {
                        slot.putStack(stack.splitStack(slot.getSlotStackLimit()));
                    } else {
                        slot.putStack(stack.splitStack(stack.getCount()));
                    }

                    slot.onSlotChanged();
                    return true;
                }
                i++;
            }
        }

        return success;
    }

    /**
     * Normalizes a BlockPos relative to another position.
     */
    public static BlockPos normalize(BlockPos pos, BlockPos other) {
        if (other.getX() <= pos.getX()) {
            pos = pos.east();
        }
        if (other.getZ() <= pos.getZ()) {
            pos = pos.south();
        }
        if (other.getY() <= pos.getY()) {
            pos = pos.up();
        }
        return pos;
    }

    public static TextComponentTranslation translate(FieldPermission permission) {
        return translate("info", "field_permission." + permission.name().toLowerCase(Locale.ROOT));
    }

    public static TextComponentTranslation translateTooltip(FieldPermission permission) {
        return translate("info", "field_permission." + permission.name().toLowerCase(Locale.ROOT) + ".tooltip");
    }

    public static TextComponentTranslation translate(String prefix, String key, Object... args) {
        return new TextComponentTranslation(translationKey(prefix, key), args);
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
        return block instanceof BlockLiquid || block instanceof BlockFluidBase;
    }

    /**
     * Check if stack is a frequency card via capability.
     */
    public static boolean isCard(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(ModCapabilities.FREQUENCY_CARD, null);
    }

    public static boolean isIdentificationCard(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(ModCapabilities.IDENTIFICATION_CARD, null);
    }

    public static boolean isModule(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(ModCapabilities.MODULE_TYPE, null);
    }

    @SuppressWarnings("unchecked")
    public static boolean isModule(ItemStack stack, Module.Category category) {
        if (stack.isEmpty() || !stack.hasCapability(ModCapabilities.MODULE_TYPE, null)) return false;
        ModuleType<?> moduleType = stack.getCapability(ModCapabilities.MODULE_TYPE, null);
        return moduleType != null && moduleType.getCategories().contains(category);
    }

    public static boolean isModule(ItemStack stack, ModuleType<?> module) {
        if (stack.isEmpty() || !stack.hasCapability(ModCapabilities.MODULE_TYPE, null)) return false;
        ModuleType<?> moduleType = stack.getCapability(ModCapabilities.MODULE_TYPE, null);
        return moduleType == module;
    }

    public static boolean isProjectorMode(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(ModCapabilities.PROJECTOR_MODE, null);
    }

    @Nullable
    public static <T extends Enum<T>> T getEnumConstantSafely(Class<T> clazz, String name) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Deals shock damage to an entity.
     */
    public static void shockEntity(Entity entity, int damage) {
        entity.attackEntityFrom(DamageSource.GENERIC, damage);
    }

    /**
     * Ensures an ItemStack has a tag compound, creating one if absent.
     */
    public static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    /**
     * Called from the Mixin-injected hook when a block is set.
     * Posts a SetBlockEvent to the Forge event bus.
     */
    @SuppressWarnings("unused") // Called from injected hook
    public static void onSetBlock(net.minecraft.world.World level, BlockPos pos, net.minecraft.block.state.IBlockState state) {
        SetBlockEvent event = new SetBlockEvent(level, pos, state);
        MinecraftForge.EVENT_BUS.post(event);
    }

    private ModUtil() {}
}
