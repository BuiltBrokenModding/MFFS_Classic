package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.init.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CoercionDeriverBlockEntity extends AnimatedBlockEntity {

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state);
    }
}
