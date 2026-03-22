package dev.su5ed.mffs.blockentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

/**
 * Ticking base for tile entities that need a per-tick update.
 * Extends {@link BaseTileEntity} and adds {@link ITickable} so Minecraft
 * registers instances in its tickable-TE list.
 *
 * <p>Non-ticking tile entities (e.g. {@link ForceFieldBlockEntity}) should
 * extend {@link BaseTileEntity} directly to avoid unnecessary tick overhead.
 */
public abstract class BaseBlockEntity extends BaseTileEntity implements ITickable {

    private long tickCounter;

    // Required no-arg constructor for TileEntity registration
    protected BaseBlockEntity() {
        super();
    }

    public long getTicks() {
        return this.tickCounter;
    }

    // ITickable.update() - dispatches to client/server tick
    @Override
    public void update() {
        ++this.tickCounter;
        if (this.world != null) {
            if (this.world.isRemote) {
                tickClient();
            } else {
                tickServer();
            }
        }
    }

    public void tickClient() {}

    public void tickServer() {}

    /**
     * Called when the block is broken to gather drops. Subclasses should add items to the list.
     */
    @Override
    public void provideAdditionalDrops(List<? super ItemStack> drops) {}

    /**
     * Return a display name for use in GUI titles.
     */
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(getBlockType().getTranslationKey() + ".name");
    }
}
