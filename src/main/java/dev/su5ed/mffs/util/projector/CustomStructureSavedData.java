package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SetStructureShapePacket;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Persists custom projector mode structures via WorldSavedData.
 */
public class CustomStructureSavedData extends WorldSavedData {
    public static final String NAME = MFFSMod.MODID + "_custom_structures";

    private final Map<String, Structure> structures = new HashMap<>();

    public CustomStructureSavedData() {
        super(NAME);
    }

    public CustomStructureSavedData(String name) {
        super(name);
    }

    @Nullable
    public Structure get(String id) {
        return this.structures.get(id);
    }

    public void clear(World world, EntityPlayerMP player, String id) {
        this.structures.remove(id);
        markDirty();
        sendToClient(world.provider.getDimension(), id, null, player);
    }

    private Structure getOrCreate(String id) {
        return this.structures.computeIfAbsent(id, s -> new Structure());
    }

    public void join(String id, World world, EntityPlayerMP player, BlockPos min, BlockPos max, boolean add) {
        Structure structure = getOrCreate(id);

        // Calculate the area bounds
        int minX = Math.min(min.getX(), max.getX());
        int minY = Math.min(min.getY(), max.getY());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxX = Math.max(min.getX(), max.getX());
        int maxY = Math.max(min.getY(), max.getY());
        int maxZ = Math.max(min.getZ(), max.getZ());

        // Add or remove block positions and record their states
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (add) {
                        structure.shape.add(pos);
                        IBlockState state = world.getBlockState(pos);
                        if (!state.getBlock().isAir(state, world, pos)) {
                            structure.blocks.put(pos, state);
                        }
                    } else {
                        structure.shape.remove(pos);
                        structure.blocks.remove(pos);
                    }
                }
            }
        }
        structure.invalidateCache();
        markDirty();
        sendToClient(world.provider.getDimension(), id, structure.shape, player);
    }

    public void remove(String id, BlockPos pos) {
        Structure structure = get(id);
        if (structure != null) {
            structure.blocks.remove(pos);
            structure.shape.remove(pos);
            structure.invalidateCache();
            markDirty();
        }
    }

    private static void sendToClient(int dimension, String id, @Nullable Set<BlockPos> shape, EntityPlayerMP player) {
        SetStructureShapePacket packet = new SetStructureShapePacket(dimension, id, shape != null ? shape : Collections.emptySet());
        Network.sendTo(packet, player);
    }

    // -----------------------------------------------------------------------
    // WorldSavedData NBT serialization
    // -----------------------------------------------------------------------

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.structures.clear();
        NBTTagCompound structuresTag = nbt.getCompoundTag("structures");
        for (String key : structuresTag.getKeySet()) {
            NBTTagCompound structTag = structuresTag.getCompoundTag(key);
            Structure structure = new Structure();

            // Read shape positions
            NBTTagList shapeList = structTag.getTagList("shape", Constants.NBT.TAG_LONG);
            for (NBTBase tag : shapeList) {
                if (tag instanceof net.minecraft.nbt.NBTTagLong longTag) {
                    structure.shape.add(BlockPos.fromLong(longTag.getLong()));
                }
            }

            // Read blocks
            NBTTagList blocksList = structTag.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < blocksList.tagCount(); i++) {
                NBTTagCompound blockTag = blocksList.getCompoundTagAt(i);
                BlockPos pos = BlockPos.fromLong(blockTag.getLong("pos"));
                Block block = Block.REGISTRY.getObject(new ResourceLocation(blockTag.getString("block")));
                if (block != null) {
                    int meta = blockTag.getInteger("meta");
                    structure.blocks.put(pos, block.getStateFromMeta(meta));
                }
            }

            this.structures.put(key, structure);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound structuresTag = new NBTTagCompound();
        for (Map.Entry<String, Structure> entry : this.structures.entrySet()) {
            NBTTagCompound structTag = new NBTTagCompound();
            Structure structure = entry.getValue();

            // Write shape positions
            NBTTagList shapeList = new NBTTagList();
            for (BlockPos pos : structure.shape) {
                shapeList.appendTag(new net.minecraft.nbt.NBTTagLong(pos.toLong()));
            }
            structTag.setTag("shape", shapeList);

            // Write blocks
            NBTTagList blocksList = new NBTTagList();
            for (Map.Entry<BlockPos, IBlockState> blockEntry : structure.blocks.entrySet()) {
                NBTTagCompound blockTag = new NBTTagCompound();
                blockTag.setLong("pos", blockEntry.getKey().toLong());
                ResourceLocation regName = Block.REGISTRY.getNameForObject(blockEntry.getValue().getBlock());
                if (regName != null) {
                    blockTag.setString("block", regName.toString());
                    blockTag.setInteger("meta", blockEntry.getValue().getBlock().getMetaFromState(blockEntry.getValue()));
                    blocksList.appendTag(blockTag);
                }
            }
            structTag.setTag("blocks", blocksList);

            structuresTag.setTag(entry.getKey(), structTag);
        }
        nbt.setTag("structures", structuresTag);
        return nbt;
    }

    // -----------------------------------------------------------------------
    // Structure inner class
    // -----------------------------------------------------------------------

    public static class Structure {
        final Set<BlockPos> shape = new HashSet<>();
        final Map<BlockPos, IBlockState> blocks = new HashMap<>();
        @Nullable
        private Set<BlockPos> relativeShape;
        @Nullable
        private Set<Vec3d> realShape;
        @Nullable
        private Map<BlockPos, IBlockState> relativeBlocks;
        @Nullable
        private Map<Vec3d, IBlockState> realBlocks;

        public Set<BlockPos> shape() {
            return shape;
        }

        public Map<Vec3d, IBlockState> getRealBlocks() {
            if (this.realBlocks == null) {
                Map<Vec3d, IBlockState> map = new HashMap<>();
                for (Map.Entry<BlockPos, IBlockState> entry : getRelativeBlocks().entrySet()) {
                    map.put(new Vec3d(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()), entry.getValue());
                }
                this.realBlocks = map;
            }
            return this.realBlocks;
        }

        public Set<Vec3d> getRealShape() {
            if (this.realShape == null) {
                Set<Vec3d> set = new HashSet<>();
                for (BlockPos pos : getRelativeShape()) {
                    set.add(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
                }
                this.realShape = set;
            }
            return this.realShape;
        }

        public Set<BlockPos> getRelativeShape() {
            if (this.relativeShape == null) {
                this.relativeShape = computeRelativeShape();
            }
            return this.relativeShape;
        }

        public Map<BlockPos, IBlockState> getRelativeBlocks() {
            if (this.relativeBlocks == null) {
                this.relativeBlocks = computeRelativeBlocks();
            }
            return this.relativeBlocks;
        }

        void invalidateCache() {
            this.relativeShape = null;
            this.realShape = null;
            this.relativeBlocks = null;
            this.realBlocks = null;
        }

        private Set<BlockPos> computeRelativeShape() {
            if (this.shape.isEmpty()) return Collections.emptySet();

            BlockPos center = computeCenter();
            Set<BlockPos> set = new HashSet<>();
            for (BlockPos pos : this.shape) {
                set.add(pos.subtract(center));
            }
            return set;
        }

        private Map<BlockPos, IBlockState> computeRelativeBlocks() {
            if (this.shape.isEmpty()) return Collections.emptyMap();

            BlockPos center = computeCenter();

            Map<BlockPos, IBlockState> map = new HashMap<>();
            for (Map.Entry<BlockPos, IBlockState> entry : this.blocks.entrySet()) {
                map.put(entry.getKey().subtract(center), entry.getValue());
            }
            return map;
        }

        private BlockPos computeCenter() {
            // Use bounding-box center matching 1.21's VoxelShape bounds.
            // VoxelShape.max(axis) = blockMax + 1. We add +1 here to match.
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
            for (BlockPos pos : this.shape) {
                if (pos.getX() < minX) minX = pos.getX();
                if (pos.getY() < minY) minY = pos.getY();
                if (pos.getZ() < minZ) minZ = pos.getZ();
                if (pos.getX() > maxX) maxX = pos.getX();
                if (pos.getY() > maxY) maxY = pos.getY();
                if (pos.getZ() > maxZ) maxZ = pos.getZ();
            }
            if (minX == maxX) maxX++;
            if (minY == maxY) maxY++;
            if (minZ == maxZ) maxZ++;
            return new BlockPos(
                (int) Math.floor((minX + maxX + 1) / 2.0),
                (int) Math.floor((minY + maxY + 1) / 2.0),
                (int) Math.floor((minZ + maxZ + 1) / 2.0)
            );
        }
    }
}
