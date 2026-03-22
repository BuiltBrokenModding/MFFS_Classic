package dev.su5ed.mffs.item;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

public class CustomProjectorModeItem extends BaseItem {

    public CustomProjectorModeItem() {
        super(true); // show description
        setMaxStackSize(1);
    }

    /** Expose PROJECTOR_MODE capability via CustomProjectorModeCapability. */
    @Override
    @Nullable
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new net.minecraftforge.common.capabilities.ICapabilityProvider() {
            private final CustomProjectorModeCapability mode = new CustomProjectorModeCapability(stack);

            @Override
            public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == dev.su5ed.mffs.setup.ModCapabilities.PROJECTOR_MODE;
            }

            @Override
            @Nullable
            public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable EnumFacing facing) {
                if (capability == dev.su5ed.mffs.setup.ModCapabilities.PROJECTOR_MODE && dev.su5ed.mffs.setup.ModCapabilities.PROJECTOR_MODE != null) {
                    return dev.su5ed.mffs.setup.ModCapabilities.PROJECTOR_MODE.cast(this.mode);
                }
                return null;
            }
        };
    }

    // -----------------------------------------------------------------------
    // StructureCoords — stored in NBT; replaces DataComponent<StructureCoords>
    // -----------------------------------------------------------------------

    public static class StructureCoords {
        private static final String NBT_PRIMARY   = "primary";
        private static final String NBT_SECONDARY = "secondary";

        @Nullable public final BlockPos primary;
        @Nullable public final BlockPos secondary;

        public StructureCoords(@Nullable BlockPos primary, @Nullable BlockPos secondary) {
            this.primary   = primary;
            this.secondary = secondary;
        }

        public boolean canBuild() {
            return this.primary != null && this.secondary != null;
        }

        public boolean selectPrimary() {
            return this.primary == null || this.secondary != null;
        }

        public boolean selectSecondary() {
            return this.primary != null && this.secondary == null;
        }

        /** Read from an ItemStack's NBT. Returns null if not present. */
        @Nullable
        public static StructureCoords fromStack(ItemStack stack) {
            if (!stack.hasTagCompound()) return null;
            NBTTagCompound nbt = stack.getTagCompound();
            if (!nbt.hasKey(NBT_PRIMARY)) return null;
            BlockPos primary = BlockPos.fromLong(nbt.getLong(NBT_PRIMARY));
            BlockPos secondary = nbt.hasKey(NBT_SECONDARY) ? BlockPos.fromLong(nbt.getLong(NBT_SECONDARY)) : null;
            return new StructureCoords(primary, secondary);
        }

        /** Write to an ItemStack's NBT. Pass null to remove. */
        public static void toStack(ItemStack stack, @Nullable StructureCoords coords) {
            if (coords == null) {
                if (stack.hasTagCompound()) {
                    stack.getTagCompound().removeTag(NBT_PRIMARY);
                    stack.getTagCompound().removeTag(NBT_SECONDARY);
                }
                return;
            }
            NBTTagCompound nbt = ModUtil.getOrCreateTag(stack);
            if (coords.primary != null) {
                nbt.setLong(NBT_PRIMARY, coords.primary.toLong());
            } else {
                nbt.removeTag(NBT_PRIMARY);
            }
            if (coords.secondary != null) {
                nbt.setLong(NBT_SECONDARY, coords.secondary.toLong());
            } else {
                nbt.removeTag(NBT_SECONDARY);
            }
        }
    }

    // -----------------------------------------------------------------------
    // NBT helpers for Mode and Pattern ID
    // -----------------------------------------------------------------------

    private static final String NBT_MODE       = "mode";
    private static final String NBT_PATTERN_ID = "pattern_id";

    public Mode getMode(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_MODE)) return Mode.ADDITIVE;
        try { return Mode.valueOf(stack.getTagCompound().getString(NBT_MODE)); }
        catch (IllegalArgumentException e) { return Mode.ADDITIVE; }
    }

    public Mode setMode(ItemStack stack, Mode mode) {
        ModUtil.getOrCreateTag(stack).setString(NBT_MODE, mode.name());
        return mode;
    }

    @Nullable
    public String getId(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_PATTERN_ID)
            ? stack.getTagCompound().getString(NBT_PATTERN_ID) : null;
    }

    public String getOrCreateId(ItemStack stack) {
        String id = getId(stack);
        if (id == null) {
            id = RandomStringUtils.randomAlphanumeric(8).toUpperCase(Locale.ROOT);
            ModUtil.getOrCreateTag(stack).setString(NBT_PATTERN_ID, id);
        }
        return id;
    }

    // -----------------------------------------------------------------------
    // Right-click in air: shift = save/clear; no-shift = cycle mode
    // -----------------------------------------------------------------------

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            StructureCoords coords = StructureCoords.fromStack(stack);
            if (playerIn.isSneaking()) {
                CustomStructureSavedData data = getOrCreateData(worldIn);
                if (coords != null && coords.canBuild()) {
                    BlockPos primary   = coords.primary;
                    BlockPos secondary = coords.secondary;
                    int maxDist = MFFSConfig.maxCustomModeScale;
                    // BlockPos.distanceSq(x,y,z,useCenter=false) = Euclidean distance squared from corner
                    if (primary.distanceSq(secondary.getX(), secondary.getY(), secondary.getZ()) < (double) maxDist * maxDist) {
                        StructureCoords.toStack(stack, null);
                        String id = getOrCreateId(stack);
                        if (playerIn instanceof EntityPlayerMP serverPlayer) {
                            data.join(id, worldIn, serverPlayer, primary, secondary, getMode(stack) == Mode.ADDITIVE);
                        }
                        playerIn.sendStatusMessage(new TextComponentTranslation("item.mffs.custom_mode.data_saved"), true);
                    } else {
                        playerIn.sendStatusMessage(new TextComponentTranslation("item.mffs.custom_mode.too_far", maxDist), true);
                    }
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                } else {
                    String id = getId(stack);
                    if (id != null) {
                        if (data != null && playerIn instanceof EntityPlayerMP serverPlayer) {
                            data.clear(worldIn, serverPlayer, id);
                        }
                        ModUtil.getOrCreateTag(stack).removeTag(NBT_PATTERN_ID);
                        playerIn.sendStatusMessage(new TextComponentTranslation("item.mffs.custom_mode.clear"), true);
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
            } else if (coords != null && coords.selectSecondary()) {
                // Match player.pick(): find the block the player is aiming at,
                // including air positions. Use solid-block raytrace first; fall back to
                // the position at end of look vector when no solid block is within range.
                Vec3d eyePos = playerIn.getPositionEyes(0);
                Vec3d lookVec = playerIn.getLook(0);
                double range = 5.0;
                Vec3d endPos = new Vec3d(eyePos.x + lookVec.x * range, eyePos.y + lookVec.y * range, eyePos.z + lookVec.z * range);
                RayTraceResult result = worldIn.rayTraceBlocks(eyePos, endPos, false, true, true);
                BlockPos targetPos;
                if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    targetPos = result.getBlockPos();
                } else if (result != null && result.typeOfHit == RayTraceResult.Type.MISS && result.getBlockPos() != null) {
                    targetPos = result.getBlockPos();
                } else {
                    targetPos = new BlockPos(endPos);
                }
                StructureCoords.toStack(stack, new StructureCoords(coords.primary, targetPos));
                selectBlock(playerIn, "secondary_point", TextFormatting.GOLD);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            } else {
                Mode mode = setMode(stack, getMode(stack).next());
                playerIn.sendStatusMessage(
                    new TextComponentTranslation("item.mffs.custom_mode.changed_mode",
                        TextFormatting.GREEN + mode.getDisplayName()), true);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    // -----------------------------------------------------------------------
    // Right-click on block: set primary/secondary position
    // Uses onItemUseFirst to fire BEFORE block activation,
    // Returns SUCCESS to prevent block GUIs.
    // -----------------------------------------------------------------------

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
                                           EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            StructureCoords coords = StructureCoords.fromStack(stack);
            if (coords == null || coords.selectPrimary()) {
                StructureCoords.toStack(stack, new StructureCoords(pos, null));
                selectBlock(player, "primary_point", TextFormatting.GREEN);
            } else {
                StructureCoords.toStack(stack, new StructureCoords(coords.primary, pos));
                selectBlock(player, "secondary_point", TextFormatting.GOLD);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private void selectBlock(EntityPlayer player, String key, TextFormatting color) {
        player.sendStatusMessage(
            new TextComponentTranslation("item.mffs.custom_mode.set_" + key).setStyle(
                new net.minecraft.util.text.Style().setColor(color)), true);
    }

    // -----------------------------------------------------------------------
    // Tooltip
    // -----------------------------------------------------------------------

    @Override
    @SideOnly(Side.CLIENT)
    protected void addInformationPre(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                                     ITooltipFlag flagIn) {
        super.addInformationPre(stack, worldIn, tooltip, flagIn);
        Mode mode = getMode(stack);
        tooltip.add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
            "item.mffs.custom_mode.mode", TextFormatting.GRAY + mode.getDisplayName()));
        String id = getId(stack);
        if (id != null) {
            tooltip.add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                "item.mffs.custom_mode.pattern_id", TextFormatting.GRAY + id));
        }
        StructureCoords coords = StructureCoords.fromStack(stack);
        if (coords != null && coords.primary != null) {
            tooltip.add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                "item.mffs.custom_mode.set_primary_point",
                TextFormatting.GRAY + posToString(coords.primary)));
            if (coords.secondary != null) {
                tooltip.add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "item.mffs.custom_mode.secondary_point",
                    TextFormatting.GRAY + posToString(coords.secondary)));
            } else {
                tooltip.add(TextFormatting.LIGHT_PURPLE + net.minecraft.client.resources.I18n.format(
                    "item.mffs.custom_mode.set_primary_point"));
            }
        }
    }

    private static String posToString(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    // -----------------------------------------------------------------------
    // GetFieldBlocks / SavedData
    // -----------------------------------------------------------------------

    public Map<net.minecraft.util.math.Vec3d, net.minecraft.block.state.IBlockState> getFieldBlocks(
            Projector projector, ItemStack stack) {
        CustomStructureSavedData data = getOrCreateData(projector.be().getWorld());
        if (data != null) {
            String id = getId(stack);
            if (id != null) {
                CustomStructureSavedData.Structure structure = data.get(id);
                if (structure != null) {
                    return structure.getRealBlocks();
                }
            }
        }
        return Collections.emptyMap();
    }

    public Set<net.minecraft.util.math.Vec3d> getFieldPoints(Projector projector, ItemStack stack) {
        CustomStructureSavedData data = getOrCreateData(projector.be().getWorld());
        if (data != null) {
            String id = getId(stack);
            if (id != null) {
                CustomStructureSavedData.Structure structure = data.get(id);
                if (structure != null) {
                    return structure.getRealShape();
                }
            }
        }
        return Collections.emptySet();
    }

    @Nullable
    public CustomStructureSavedData getOrCreateData(World world) {
        if (world.isRemote) return null;
        // Always use overworld MapStorage
        // Don't cache on the Item singleton — MapStorage caches internally
        net.minecraft.world.WorldServer overworld = world.getMinecraftServer().getWorld(0);
        CustomStructureSavedData data = (CustomStructureSavedData)
            overworld.getMapStorage().getOrLoadData(CustomStructureSavedData.class, CustomStructureSavedData.NAME);
        if (data == null) {
            data = new CustomStructureSavedData();
            overworld.getMapStorage().setData(CustomStructureSavedData.NAME, data);
        }
        return data;
    }

    // -----------------------------------------------------------------------
    // ProjectorMode capability inner class
    // -----------------------------------------------------------------------

    public class CustomProjectorModeCapability implements ProjectorMode {
        private final ItemStack stack;

        public CustomProjectorModeCapability(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Set<net.minecraft.util.math.Vec3d> getExteriorPoints(Projector projector) {
            return getFieldPoints(projector, this.stack);
        }

        @Override
        public Set<net.minecraft.util.math.Vec3d> getInteriorPoints(Projector projector) {
            return getExteriorPoints(projector);
        }

        @Override
        public boolean isInField(Projector projector, net.minecraft.util.math.Vec3d position) {
            return false;
        }
    }

    public enum Mode {
        ADDITIVE,
        SUBTRACTIVE;

        private static final Mode[] VALUES = values();

        public Mode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public String getDisplayName() {
            return net.minecraft.client.resources.I18n.format(
                "item.mffs.custom_mode.mode." + name().toLowerCase(Locale.ROOT));
        }
    }
}
