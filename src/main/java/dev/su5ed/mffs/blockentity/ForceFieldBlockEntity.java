package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.network.InitialDataRequestPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.render.BlockEntityRenderDelegate;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModModules;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ForceFieldBlockEntity extends BaseTileEntity {

    /**
     * Client-side queue of positions that need world.checkLight() called.
     * Populated in handleCustomUpdateTag; drained N-per-tick by the client
     * tick handler in ModClientSetup to avoid synchronous BFS spikes.
     */
    public static final Queue<BlockPos> PENDING_LIGHT_CHECKS = new ConcurrentLinkedQueue<>();

    private BlockPos projector;
    private IBlockState camouflage;
    private int clientBlockLight;
    // Cached result of camouflage.getLightOpacity() so getLightOpacity() on the block
    // can return immediately after the TE lookup without re-entering the camo lookup chain.
    // 0 when no camouflage is set (force field is transparent).
    private int cachedLightOpacity;

    public ForceFieldBlockEntity() {
        super();
    }

    public int getClientBlockLight() {
        return this.clientBlockLight;
    }

    public BlockPos getProjectorPos() {
        return this.projector;
    }

    public void setProjector(BlockPos position) {
        this.projector = position;
        markDirty();
    }

    public IBlockState getCamouflage() {
        return this.camouflage;
    }

    public int getCachedLightOpacity() {
        return this.cachedLightOpacity;
    }

    public void setCamouflage(IBlockState camouflage) {
        this.camouflage = camouflage;
        this.cachedLightOpacity = (camouflage != null && this.world != null)
            ? camouflage.getLightOpacity(this.world, this.pos)
            : 0;
        markDirty();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.world.isRemote) {
            InitialDataRequestPacket packet = new InitialDataRequestPacket(this.pos);
            Network.sendToServer(packet);
            if (this.camouflage != null) {
                BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, this.camouflage);
            }
        }
    }

    @Override
    public void invalidate() {
        if (this.world != null && this.world.isRemote) {
            BlockEntityRenderDelegate.INSTANCE.removeDelegateOf(this);
        }
        super.invalidate();
    }

    public Optional<Projector> getProjector() {
        if (this.projector == null) return Optional.empty();
        return Optional.ofNullable(this.world.getTileEntity(this.projector))
            .filter(te -> te.hasCapability(ModCapabilities.PROJECTOR, null))
            .map(te -> (Projector) te.getCapability(ModCapabilities.PROJECTOR, null));
    }

    /**
     * Called from UpdateBlockEntityPacket to apply server-sent NBT on the client.
     */
    @SuppressWarnings("deprecation")
    public void handleCustomUpdateTag(NBTTagCompound tag) {
        if (tag.hasKey("projector")) {
            int[] coords = tag.getIntArray("projector");
            this.projector = new BlockPos(coords[0], coords[1], coords[2]);
        }
        this.clientBlockLight = tag.getInteger("clientBlockLight");

        // Deserialize camouflage from update tag
        if (tag.hasKey("camouflage")) {
            String blockName = tag.getString("camouflage");
            int meta = tag.getInteger("camouflageMeta");
            Block block = Block.REGISTRY.getObject(new ResourceLocation(blockName));
            if (block != null) {
                this.camouflage = block.getStateFromMeta(meta);
            }
        } else {
            this.camouflage = null;
        }

        // Defer world.checkLight to a rate-limited client tick drain instead of
        // calling it immediately — avoids synchronous BFS spikes on chunk load.
        PENDING_LIGHT_CHECKS.add(this.pos);
        updateRenderClient();

        if (this.camouflage != null) {
            BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, this.camouflage);
        } else {
            BlockEntityRenderDelegate.INSTANCE.removeDelegateOf(this);
        }
    }

    /**
     * Called from tickServer or projector to push update data to clients.
     */
    public NBTTagCompound getCustomUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        if (this.projector != null) {
            tag.setIntArray("projector", new int[]{this.projector.getX(), this.projector.getY(), this.projector.getZ()});
        }
        int light = getProjector()
            .map(projector -> Math.round((float) Math.min(projector.getModuleCount(ModModules.GLOW), 64) / 64 * 15))
            .orElse(0);
        tag.setInteger("clientBlockLight", light);
        // Include camouflage in update tag for client sync
        if (this.camouflage != null) {
            ResourceLocation regName = Block.REGISTRY.getNameForObject(this.camouflage.getBlock());
            if (regName != null) {
                tag.setString("camouflage", regName.toString());
                tag.setInteger("camouflageMeta", this.camouflage.getBlock().getMetaFromState(this.camouflage));
            }
        }
        return tag;
    }

    @Override
    protected void saveTag(NBTTagCompound compound) {
        super.saveTag(compound);

        if (this.projector != null) {
            compound.setIntArray("projector", new int[]{this.projector.getX(), this.projector.getY(), this.projector.getZ()});
        }
        if (this.camouflage != null) {
            ResourceLocation regName = Block.REGISTRY.getNameForObject(this.camouflage.getBlock());
            if (regName != null) {
                compound.setString("camouflage", regName.toString());
                compound.setInteger("camouflageMeta", this.camouflage.getBlock().getMetaFromState(this.camouflage));
            }
        }
    }

    @Override
    protected void loadTag(NBTTagCompound compound) {
        super.loadTag(compound);

        if (compound.hasKey("projector")) {
            int[] coords = compound.getIntArray("projector");
            this.projector = new BlockPos(coords[0], coords[1], coords[2]);
        }
        if (compound.hasKey("camouflage")) {
            String blockName = compound.getString("camouflage");
            int meta = compound.getInteger("camouflageMeta");
            Block block = Block.REGISTRY.getObject(new ResourceLocation(blockName));
            if (block != null) {
                this.camouflage = block.getStateFromMeta(meta);
            }
        }
    }

    public void updateRenderClient() {
        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 3);
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }
}
