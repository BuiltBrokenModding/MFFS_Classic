package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.network.Network;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.List;

/**
 * Non-ticking base for tile entities that do not need a per-tick update.
 * Does NOT implement {@link net.minecraft.util.ITickable}, so Minecraft never adds
 * instances to its tickable-TE list and {@code update()} is never called.
 *
 * <p>Ticking tile entities should extend {@link BaseBlockEntity} instead, which
 * adds {@code ITickable} and the {@code tickClient()} / {@code tickServer()} dispatch.
 */
public abstract class BaseTileEntity extends TileEntity {

    // Required no-arg constructor for TileEntity registration
    protected BaseTileEntity() {
        super();
    }

    // Override to only refresh when the block type actually changes.
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    /**
     * Called just before the block and tile entity are removed from the world (both survival
     * and creative breaks).  Override to perform cleanup — e.g. destroying a force field or
     * draining stored Fortron to neighbours.  Always call {@code super.preRemoveSideEffects(pos)}.
     */
    public void preRemoveSideEffects(BlockPos pos) {}

    /**
     * Called when the block is broken to gather drops. Subclasses should add items to the list.
     */
    public void provideAdditionalDrops(List<? super ItemStack> drops) {}

    /**
     * Return a display name for use in GUI titles.
     */
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(getBlockType().getTranslationKey() + ".name");
    }

    // -------------------------------------------------------------------------
    // NBT serialization (replaces ValueOutput/ValueInput / saveAdditional/loadAdditional)
    // -------------------------------------------------------------------------

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        saveCommonTag(compound);
        saveTag(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        loadCommonTag(compound);
        loadTag(compound);
    }

    /** Called by both writeToNBT and getUpdateTag. Override to write shared state. */
    protected void saveCommonTag(NBTTagCompound compound) {}

    /** Called by writeToNBT only. Override to write server-only / extra state. */
    protected void saveTag(NBTTagCompound compound) {}

    /** Called by readFromNBT and handleUpdateTag. Override to read shared state. */
    protected void loadCommonTag(NBTTagCompound compound) {}

    /** Called by readFromNBT only. Override to read server-only / extra state. */
    protected void loadTag(NBTTagCompound compound) {}

    // -------------------------------------------------------------------------
    // Client sync (SPacketUpdateTileEntity / getUpdateTag)
    // -------------------------------------------------------------------------

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        saveCommonTag(tag);
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        loadCommonTag(tag);
    }

    /**
     * Sends a network packet to all players tracking this tile entity's chunk.
     * Replaces PacketDistributor.sendToPlayersTrackingChunk.
     */
    public <T extends IMessage> void sendToChunk(T msg) {
        if (this.world != null && !this.world.isRemote) {
            Network.sendToAllAround(msg, this.world, this.pos, 64);
        }
    }
}
