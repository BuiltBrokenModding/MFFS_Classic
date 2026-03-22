package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class ProjectorModeItem extends Item {
    private final ProjectorMode projectorMode;

    public ProjectorModeItem(ProjectorMode projectorMode) {
        setMaxStackSize(1);
        this.projectorMode = projectorMode;
    }

    public ProjectorMode getProjectorMode() {
        return this.projectorMode;
    }

    /** Expose PROJECTOR_MODE capability so the item can be placed in the projector's mode slot. */
    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ProjectorModeProvider(this.projectorMode);
    }

    private static class ProjectorModeProvider implements ICapabilityProvider {
        private final ProjectorMode mode;

        ProjectorModeProvider(ProjectorMode mode) {
            this.mode = mode;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == ModCapabilities.PROJECTOR_MODE;
        }

        @Override
        @Nullable
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == ModCapabilities.PROJECTOR_MODE && ModCapabilities.PROJECTOR_MODE != null) {
                return ModCapabilities.PROJECTOR_MODE.cast(this.mode);
            }
            return null;
        }
    }
}
