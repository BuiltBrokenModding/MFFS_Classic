package dev.su5ed.mffs.util;

import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable resolution result for force field camouflage.
 * <p>
 * Exactly one of {@link #blockState()} (non-null) or {@link #dyeColor()} (!= -1)
 * is "present" per result, or both are empty meaning the field should fall back
 * to the default cyan tint.
 */
public final class CamoResult {
    public static final CamoResult EMPTY = new CamoResult(null, -1);

    private final @Nullable IBlockState blockState;
    private final int dyeColor;

    private CamoResult(@Nullable IBlockState blockState, int dyeColor) {
        this.blockState = blockState;
        this.dyeColor = dyeColor;
    }

    public static CamoResult ofBlock(IBlockState state) {
        return new CamoResult(state, -1);
    }

    public static CamoResult ofDye(int color) {
        return new CamoResult(null, color);
    }

    @Nullable
    public IBlockState blockState() {
        return this.blockState;
    }

    public int dyeColor() {
        return this.dyeColor;
    }

    public boolean isEmpty() {
        return this.blockState == null && this.dyeColor == -1;
    }
}
