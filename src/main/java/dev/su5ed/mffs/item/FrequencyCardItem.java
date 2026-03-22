package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FrequencyCardItem extends BaseItem {

    private static final int MAX_FREQUENCY = 999999;
    /** NBT key used to persist the frequency value on the ItemStack. */
    public static final String NBT_FREQUENCY = "frequency";

    public FrequencyCardItem() {
        super(true); // show description on Shift
        setMaxStackSize(1);
    }

    /** Shift + right-click in air → generate a random frequency. */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD, null);
            if (card != null) {
                double reach = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                RayTraceResult hit = playerIn.rayTrace(reach, 1.0f);
                if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
                    if (!worldIn.isRemote) {
                        int frequency = worldIn.rand.nextInt(MAX_FREQUENCY + 1);
                        card.setFrequency(frequency);
                        playerIn.sendStatusMessage(
                            new TextComponentTranslation("info.mffs.frequency.generated",
                                new TextComponentString(String.valueOf(frequency))
                                    .setStyle(new Style().setColor(TextFormatting.AQUA))),
                            true);
                    }
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    /** Right-click on block → set that machine's Fortron frequency to this card's value. */
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos,
                                      EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te.hasCapability(ModCapabilities.FORTRON, null)) {
                ItemStack stack = playerIn.getHeldItem(hand);
                FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD, null);
                if (card != null) {
                    int frequency = card.getFrequency();
                    te.getCapability(ModCapabilities.FORTRON, null).setFrequency(frequency);
                    playerIn.sendStatusMessage(
                        new TextComponentTranslation("info.mffs.frequency.set",
                            new TextComponentString(String.valueOf(frequency))
                                .setStyle(new Style().setColor(TextFormatting.GREEN))),
                        true);
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void addInformationPre(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                                     net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformationPre(stack, worldIn, tooltip, flagIn);
        FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD, null);
        if (card != null) {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.frequency",
                TextFormatting.GREEN + String.valueOf(card.getFrequency())));
        }
    }

    /** Provide FrequencyCard capability for item stacks of this type. */
    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FrequencyCardProvider(stack);
    }

    // -----------------------------------------------------------------------
    // Frequency stored as NBT integer "frequency" on the ItemStack
    // -----------------------------------------------------------------------

    public static class FrequencyCardHandler implements FrequencyCard {
        private final ItemStack stack;

        public FrequencyCardHandler(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int getFrequency() {
            return this.stack.hasTagCompound() ? this.stack.getTagCompound().getInteger(NBT_FREQUENCY) : 0;
        }

        @Override
        public void setFrequency(int frequency) {
            NBTTagCompound tag = ModUtil.getOrCreateTag(this.stack);
            tag.setInteger(NBT_FREQUENCY, frequency);
        }
    }

    private static class FrequencyCardProvider implements ICapabilityProvider {
        private final ItemStack stack;
        private FrequencyCardHandler handler;

        FrequencyCardProvider(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == ModCapabilities.FREQUENCY_CARD;
        }

        @Override
        @Nullable
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == ModCapabilities.FREQUENCY_CARD && ModCapabilities.FREQUENCY_CARD != null) {
                if (this.handler == null) this.handler = new FrequencyCardHandler(this.stack);
                return ModCapabilities.FREQUENCY_CARD.cast(this.handler);
            }
            return null;
        }
    }
}
