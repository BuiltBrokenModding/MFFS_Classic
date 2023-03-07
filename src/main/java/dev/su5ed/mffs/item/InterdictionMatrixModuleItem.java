package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.setup.ModBlocks;
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

import java.util.List;

public class InterdictionMatrixModuleItem<T extends Module & InterdictionMatrixModule> extends ModuleItem<T> {

    public InterdictionMatrixModuleItem(ExtendedItemProperties properties, T module) {
        super(properties, module);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ModuleCapabilityProvider();
    }

    @Override
    protected void appendHoverTextPre(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(ModBlocks.INTERDICTION_MATRIX.get().getName().withStyle(ChatFormatting.DARK_RED));

        super.appendHoverTextPre(stack, level, tooltipComponents, isAdvanced);
    }

    public class ModuleCapabilityProvider extends ModuleItem<?>.ModuleCapabilityProvider {
        private final LazyOptional<T> optional = LazyOptional.of(() -> InterdictionMatrixModuleItem.this.module);

        @Override
        public <U> LazyOptional<U> getCapability(Capability<U> cap, @Nullable Direction side) {
            if (cap == ModCapabilities.INTERDICTION_MATRIX_MODULE) {
                return this.optional.cast();
            }
            return super.getCapability(cap, side);
        }
    }
}
