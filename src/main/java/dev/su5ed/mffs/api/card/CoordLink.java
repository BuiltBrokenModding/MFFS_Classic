package dev.su5ed.mffs.api.card;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface CoordLink {
    void setLink(ItemStack stack, BlockPos position);

    BlockPos getLink(ItemStack stack);
}
