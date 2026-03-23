package dev.su5ed.mffs.item;

import dev.su5ed.mffs.MFFSConfig;
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

import net.minecraft.client.gui.GuiScreen;

import java.text.DecimalFormat;
import java.util.List;

public class ModuleItem<T extends Module> extends BaseItem {
    protected static final DecimalFormat FORTRON_COST_FORMAT = new DecimalFormat("#.##");

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
        float cost = this.module.getFortronCost(1);
        if (cost < 0) {
            // Negative cost = Fortron discount; display as "+x F/s"
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.fortron_usage",
                TextFormatting.GREEN + "+" + FORTRON_COST_FORMAT.format(-cost)));
            if (GuiScreen.isShiftKeyDown()) {
                tooltip.add(TextFormatting.GRAY + I18n.format("info.mffs.module.warn.discount_hint"));
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.show_details",
                    TextFormatting.GRAY + I18n.format("info.mffs.key.shift")));
            }
        } else {
            // Positive cost = Fortron drain; display as "-x F/s"
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.fortron_usage",
                TextFormatting.GRAY + "-" + FORTRON_COST_FORMAT.format(cost)));
        }
        if (this.module == dev.su5ed.mffs.setup.ModModules.ANTI_PERSONNEL) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("info.mffs.module.anti_personnel.requires_biometric"));
            addDamagePerSecondTooltip(tooltip, MFFSConfig.antiPersonnelDamagePerSecond);
        } else if (this.module == dev.su5ed.mffs.setup.ModModules.ANTI_FRIENDLY) {
            addDamagePerSecondTooltip(tooltip, MFFSConfig.antiFriendlyDamagePerSecond);
        } else if (this.module == dev.su5ed.mffs.setup.ModModules.ANTI_HOSTILE) {
            addDamagePerSecondTooltip(tooltip, MFFSConfig.antiHostileDamagePerSecond);
        } else if (this.module == dev.su5ed.mffs.setup.ModModules.BLOCK_ALTER) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("info.mffs.module.block_alter.requires_biometric"));
        } else if (this.module == dev.su5ed.mffs.setup.ModModules.BLOCK_ACCESS) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("info.mffs.module.block_access.requires_biometric"));
        } else if (this.module == dev.su5ed.mffs.setup.ModModules.SHOCK) {
            tooltip.add(TextFormatting.GOLD + I18n.format("info.mffs.module.shock.damage_per_module",
                TextFormatting.YELLOW + FORTRON_COST_FORMAT.format(MFFSConfig.shockModuleDamagePerModule)));
        }
    }

    /** Adds a standard gold/yellow damage-per-second tooltip line. Override or call from subclasses to reuse. */
    protected void addDamagePerSecondTooltip(List<String> tooltip, float damagePerSecond) {
        tooltip.add(TextFormatting.GOLD + I18n.format("info.mffs.module.damage_per_second",
            TextFormatting.YELLOW + FORTRON_COST_FORMAT.format(damagePerSecond)));
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
