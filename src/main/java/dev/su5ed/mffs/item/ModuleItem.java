package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class ModuleItem extends BaseItem {
    private static final DecimalFormat FORTRON_COST_FORMAT = new DecimalFormat("#.##");

    private final Module module;

    public ModuleItem(ExtendedItemProperties properties, Module module) {
        super(properties);

        this.module = module;
    }

    @Override
    protected void appendHoverTextPre(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverTextPre(stack, level, tooltipComponents, isAdvanced);

        tooltipComponents.add(Component.literal("Fortron: " + FORTRON_COST_FORMAT.format(this.module.getFortronCost(1) * 20) + " L/s").withStyle(ChatFormatting.GRAY));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ModuleCapabilityProvider();
    }

    public class ModuleCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<Module> optional = LazyOptional.of(() -> ModuleItem.this.module);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return ModCapabilities.MODULE.orEmpty(cap, this.optional);
        }
    }
}
