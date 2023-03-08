package dev.su5ed.mffs.api.card;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

// TODO capability
public interface CoordLink {
    void setLink(ItemStack stack, BlockPos pos);

    @Nullable
    BlockPos getLink(ItemStack stack);
}
