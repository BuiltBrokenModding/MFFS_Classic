package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class ModuleItem extends Item {
    private static final DecimalFormat FORTRON_COST_FORMAT = new DecimalFormat("#.##");

    private final Module module;

    private Lazy<Component> description;

    public ModuleItem(Properties properties, Module module) {
        super(properties);

        this.module = module;
    }

    public ModuleItem withDescription() {
        this.description = Lazy.of(() -> {
            String name = ForgeRegistries.ITEMS.getKey(this).getPath();
            return ModUtil.translate("item", name + ".description").withStyle(ChatFormatting.GRAY);
        });
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        tooltipComponents.add(Component.literal("Fortron: " + FORTRON_COST_FORMAT.format(this.module.getFortronCost(1) * 20) + " L/s").withStyle(ChatFormatting.GRAY));
        if (this.description != null) {
            tooltipComponents.add(this.description.get());
        }
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
