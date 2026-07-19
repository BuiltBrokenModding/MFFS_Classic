package dev.su5ed.mffs.compat.jade;

import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.*;

@WailaPlugin
public class MFFSJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Disguise camouflaged Force Field
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor && blockAccessor.getBlockEntity() instanceof ForceFieldBlockEntity forceField) {
                BlockState camo = forceField.getCamouflage();
                if (camo != null) {
                    return registration.blockAccessor()
                        .from(blockAccessor)
                        .blockState(camo)
                        .build();
                }
            }
            return accessor;
        });
    }
}
