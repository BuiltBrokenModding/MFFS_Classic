package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class ModuleItem<T extends Module> extends BaseItem {
    private static final DecimalFormat FORTRON_COST_FORMAT = new DecimalFormat("#.##");

    protected final ModuleType<T> module;

    public ModuleItem(ModuleType<T> module) {
        super(false); // description=false; module items don't have description text
        this.module = module;
    }

    public ModuleType<T> getModule() {
        return this.module;
    }

    /** Provide MODULE_TYPE capability for item stacks of this type. */
    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ModuleTypeProvider(this.module);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.fortron_usage",
            TextFormatting.GRAY + FORTRON_COST_FORMAT.format(this.module.getFortronCost(1) * 20)));
        if (this.module == dev.su5ed.mffs.setup.ModModules.ANTI_PERSONNEL) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("info.mffs.module.anti_personnel.requires_biometric"));
        } else if (this.module == dev.su5ed.mffs.setup.ModModules.BLOCK_ALTER) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("info.mffs.module.block_alter.requires_biometric"));
        } else if (this.module == dev.su5ed.mffs.setup.ModModules.BLOCK_ACCESS) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("info.mffs.module.block_access.requires_biometric"));
        }
    }

    private static class ModuleTypeProvider implements ICapabilityProvider {
        private final ModuleType<?> moduleType;

        ModuleTypeProvider(ModuleType<?> moduleType) {
            this.moduleType = moduleType;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == ModCapabilities.MODULE_TYPE;
        }

        @Override
        @Nullable
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == ModCapabilities.MODULE_TYPE && ModCapabilities.MODULE_TYPE != null) {
                return ModCapabilities.MODULE_TYPE.cast(this.moduleType);
            }
            return null;
        }
    }
}
