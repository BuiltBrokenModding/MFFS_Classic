package dev.su5ed.mffs.item;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.render.particle.ParticleColor;
import dev.su5ed.mffs.setup.GuiIds;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FrequencyGrid;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RemoteControllerItem extends BaseItem implements CoordLink {

    public RemoteControllerItem() {
        super(true); // show description
        setMaxStackSize(1);
    }

    /** Shift + right-click on a MFFS machine: link the remote to that block. */
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos,
                                      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            ItemStack stack = playerIn.getHeldItem(hand);
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te.hasCapability(ModCapabilities.FORTRON, null)) {
                setLink(stack, pos);
                net.minecraft.block.state.IBlockState state = worldIn.getBlockState(pos);
                playerIn.sendStatusMessage(
                    new TextComponentTranslation("info.mffs.link",
                        state.getBlock().getLocalizedName(),
                        posToString(pos))
                    .setStyle(new net.minecraft.util.text.Style().setColor(TextFormatting.AQUA)),
                    true);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    /** Right-click in air (no shift): open the linked machine's GUI remotely. */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && !playerIn.isSneaking()) {
            // If the player already has a container open, do nothing.
            if (playerIn.openContainer != playerIn.inventoryContainer) {
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }

            BlockPos pos = getLink(stack);

            if (pos != null && worldIn.isBlockLoaded(pos)) {
                TileEntity te = worldIn.getTileEntity(pos);

                if (te != null && te.hasCapability(ModCapabilities.FORTRON, null)
                        && (Fortron.hasPermission(worldIn, pos, FieldPermission.USE_BLOCKS, playerIn)
                            || Fortron.hasPermission(worldIn, pos, FieldPermission.REMOTE_CONTROL, playerIn))) {

                    double distance = ModUtil.distance(playerIn.getPosition(), pos);
                    double requiredEnergy = distance * (1000.0 / 100.0);
                    // Search radius must cover at least the distance to the target machine
                    int searchRadius = Math.max(50, (int) Math.ceil(distance));
                    int frequency = Objects.requireNonNull(
                        te.getCapability(ModCapabilities.FORTRON, null)).getFrequency();

                    Vec3d eyePos = playerIn.getPositionVector().add(0, playerIn.getEyeHeight() - 0.2, 0);
                    if (drawEnergy(worldIn, playerIn.getPosition(), eyePos, frequency, (int) requiredEnergy, searchRadius)) {
                        // Open the GUI of the target block (determined by MFFSGuiHandler based on TileEntity type)
                        playerIn.openGui(MFFSMod.INSTANCE, GuiIds.REMOTE_CONTROLLER, worldIn,
                            pos.getX(), pos.getY(), pos.getZ());
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }

                    playerIn.sendStatusMessage(
                        new TextComponentTranslation("info.mffs.cannot_harness", Math.round(requiredEnergy))
                            .setStyle(new net.minecraft.util.text.Style().setColor(TextFormatting.RED)),
                        true);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    // -----------------------------------------------------------------------
    // Tooltip — show linked position if present
    // -----------------------------------------------------------------------

    @Override
    @SideOnly(Side.CLIENT)
    protected void addInformationPre(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                                     ITooltipFlag flagIn) {
        super.addInformationPre(stack, worldIn, tooltip, flagIn);
        BlockPos pos = getLink(stack);
        if (pos != null && worldIn != null) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te.hasCapability(ModCapabilities.FORTRON, null)) {
                net.minecraft.block.state.IBlockState state = worldIn.getBlockState(pos);
                tooltip.add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "info.mffs.link",
                    TextFormatting.GREEN + state.getBlock().getLocalizedName(),
                    TextFormatting.GRAY + posToString(pos)));
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "info.mffs.link",
                    TextFormatting.RED + "?",
                    TextFormatting.GRAY + posToString(pos)));
            }
        }
    }

    // -----------------------------------------------------------------------
    // CoordLink implementation — NBT (BlockPos.toLong / fromLong)
    // -----------------------------------------------------------------------

    @Override
    @Nullable
    public BlockPos getLink(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("link")) return null;
        return BlockPos.fromLong(stack.getTagCompound().getLong("link"));
    }

    @Override
    public void setLink(ItemStack stack, BlockPos pos) {
        ModUtil.getOrCreateTag(stack).setLong("link", pos.toLong());
    }

    // -----------------------------------------------------------------------
    // Fortron energy draw — simulate-then-commit (replaces Transaction pattern)
    // -----------------------------------------------------------------------

    private boolean drawEnergy(World world, BlockPos playerPos, Vec3d eyeTarget,
                                int frequency, int energy, int searchRadius) {
        List<FortronStorage> fortronTiles = FrequencyGrid.instance(world.isRemote).get(world, playerPos, searchRadius, frequency);
        // Sort by distance to the player's eye position
        fortronTiles.sort(Comparator.comparingDouble(fortron -> {
            BlockPos fp = fortron.getOwner().getPos();
            return new Vec3d(fp.getX() + 0.5, fp.getY() + 0.5, fp.getZ() + 0.5)
                .squareDistanceTo(eyeTarget);
        }));

        // First pass: simulate to find enough providers
        int total = 0;
        List<FortronStorage> transmitters = new ArrayList<>();
        for (FortronStorage fortron : fortronTiles) {
            int required = energy - total;
            int available = fortron.extractFortron(required, true); // simulate
            if (available > 0) {
                transmitters.add(fortron);
            }
            total += available;
            if (total >= energy) break;
        }

        if (total >= energy) {
            // Second pass: actually consume
            total = 0;
            for (FortronStorage fortron : transmitters) {
                int required = energy - total;
                total += fortron.extractFortron(required, false); // consume
                BlockPos fortronPos = fortron.getOwner().getPos();
                Vec3d center = new Vec3d(fortronPos.getX() + 0.5, fortronPos.getY() + 0.5, fortronPos.getZ() + 0.5);
                Fortron.renderClientBeam(world, eyeTarget, center, fortronPos, ParticleColor.BLUE_BEAM, 20);
            }
            return true;
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static String posToString(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
}
