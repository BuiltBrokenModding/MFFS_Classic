package dev.su5ed.mffs.api.card;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface CoordLink {
    void setLink(ItemStack stack, BlockPos pos);

    @Nullable
    BlockPos getLink(ItemStack stack);
}
