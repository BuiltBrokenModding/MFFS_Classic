package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class ProjectorModeItem extends Item {
    private final ProjectorMode projectorMode;
    
    public ProjectorModeItem(Properties properties, ProjectorMode projectorMode) {
        super(properties.stacksTo(1));
        
        this.projectorMode = projectorMode;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ProjectorModeCapabilityProvider(); 
    }

    private class ProjectorModeCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<ProjectorMode> optional = LazyOptional.of(() -> ProjectorModeItem.this.projectorMode);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return ModCapabilities.PROJECTOR_MODE.orEmpty(cap, this.optional);
        }
    } 
}
