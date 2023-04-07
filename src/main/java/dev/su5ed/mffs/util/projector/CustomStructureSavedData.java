package dev.su5ed.mffs.util.projector;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SetStructureShapePacket;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomStructureSavedData extends SavedData {
    public static final String NAME = MFFSMod.MODID + ":custom_structures";
    public static final Codec<AABB> AABB_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.fieldOf("minX").forGetter(a -> a.minX),
        Codec.DOUBLE.fieldOf("minY").forGetter(a -> a.minY),
        Codec.DOUBLE.fieldOf("minZ").forGetter(a -> a.minZ),
        Codec.DOUBLE.fieldOf("maxX").forGetter(a -> a.maxX),
        Codec.DOUBLE.fieldOf("maxY").forGetter(a -> a.maxY),
        Codec.DOUBLE.fieldOf("maxZ").forGetter(a -> a.maxZ)
    ).apply(instance, AABB::new));
    public static final Codec<VoxelShape> VOXEL_SHAPE_CODEC = AABB_CODEC.listOf().xmap(CustomStructureSavedData::shapeFromAABBs, VoxelShape::toAabbs);
    public static final Codec<Map<String, Structure>> CODEC = Codec.unboundedMap(Codec.STRING, Structure.CODEC);

    private final Map<String, Structure> structures = new HashMap<>();

    @Nullable
    public Structure get(String id) {
        return this.structures.get(id);
    }

    public void clear(Level level, ServerPlayer serverPlayer, String id) {
        this.structures.remove(id);
        sendToClient(level.dimension(), id, null, serverPlayer);
    }

    private Structure getOrCreate(String id) {
        return this.structures.computeIfAbsent(id, s -> new Structure());
    }

    public void join(String id, Level level, ServerPlayer serverPlayer, BlockPos min, BlockPos max, boolean add) {
        Structure structure = getOrCreate(id);
        BooleanOp op = add ? BooleanOp.OR : BooleanOp.ONLY_FIRST;
        BlockPos normalFrom = ModUtil.normalize(min, max);
        VoxelShape normalShape = Shapes.joinUnoptimized(structure.normalShape, Shapes.create(new AABB(normalFrom, ModUtil.normalize(max, normalFrom))), op);
        VoxelShape shape = Shapes.joinUnoptimized(structure.shape, Shapes.create(new AABB(min, normalizeAxis(min, max))), op);

        AABB area = new AABB(min, max);
        for (int x = (int) area.minX; x <= area.maxX; x++) {
            for (int y = (int) area.minY; y <= area.maxY; y++) {
                for (int z = (int) area.minZ; z <= area.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!state.isAir()) {
                        if (add) {
                            structure.blocks.put(pos, state.getBlock());
                        } else {
                            structure.blocks.remove(pos);
                        }
                    }
                }
            }
        }

        Structure newStruct = new Structure(shape, normalShape, structure.blocks);
        this.structures.put(id, newStruct);
        setDirty();
        sendToClient(level.dimension(), id, normalShape, serverPlayer);
    }

    private static void sendToClient(ResourceKey<Level> key, String id, VoxelShape shape, ServerPlayer player) {
        Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SetStructureShapePacket(key, id, shape));
    }

    private static BlockPos normalizeAxis(BlockPos min, BlockPos max) {
        if (min.getX() == max.getX()) {
            max = max.east();
        }
        if (min.getY() == max.getY()) {
            max = max.above();
        }
        if (min.getZ() == max.getZ()) {
            max = max.south();
        }
        return max;
    }

    public void remove(String id, BlockPos pos) {
        Structure structure = get(id);
        if (structure != null) {
            structure.blocks.remove(pos);
            setDirty();
        }
    }

    public void load(CompoundTag tag) {
        Tag structuresTag = tag.get("structures");
        this.structures.putAll(CODEC.decode(NbtOps.INSTANCE, structuresTag).getOrThrow(false, s -> {}).getFirst());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        Tag structuresTag = CODEC.encodeStart(NbtOps.INSTANCE, this.structures).getOrThrow(false, s -> {});
        tag.put("structures", structuresTag);
        return tag;
    }

    public static VoxelShape shapeFromAABBs(List<AABB> aabbs) {
        VoxelShape shape = Shapes.empty();
        for (AABB area : aabbs) {
            VoxelShape partial = Shapes.create(area);
            shape = Shapes.joinUnoptimized(shape, partial, BooleanOp.OR);
        }
        return shape;
    }

    public static class Structure {
        public static final Codec<Map<BlockPos, Block>> BLOCK_MAP_CODEC = Codec.pair(BlockPos.CODEC.fieldOf("pos").codec(), ForgeRegistries.BLOCKS.getCodec().fieldOf("state").codec()).listOf()
            .xmap(pairs -> StreamEx.of(pairs)
                    .mapToEntry(Pair::getFirst, Pair::getSecond)
                    .toMap(),
                map -> EntryStream.of(map)
                    .mapKeyValue(Pair::of)
                    .toList());
        public static final Codec<Structure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VOXEL_SHAPE_CODEC.fieldOf("shape").forGetter(Structure::shape),
            VOXEL_SHAPE_CODEC.fieldOf("normalShape").forGetter(Structure::normalShape),
            BLOCK_MAP_CODEC.fieldOf("blocks").forGetter(Structure::blocks)
        ).apply(instance, Structure::new));

        private final VoxelShape shape;
        private final VoxelShape normalShape;
        private final Map<BlockPos, Block> blocks;
        private final Lazy<Map<BlockPos, Block>> relativeBlocks = Lazy.of(this::computeRelativeBlocks);
        @Nullable
        private Map<Vec3, Block> realBlocks;

        public Structure() {
            this(Shapes.empty(), Shapes.empty(), new HashMap<>());
        }

        public Structure(VoxelShape shape, VoxelShape normalShape, Map<BlockPos, Block> blocks) {
            this.shape = shape;
            this.normalShape = normalShape;
            this.blocks = blocks;
        }

        public VoxelShape shape() {
            return shape;
        }

        public VoxelShape normalShape() {
            return normalShape;
        }

        public Map<BlockPos, Block> blocks() {
            return blocks;
        }

        public Map<BlockPos, Block> getRelativeBlocks() {
            return this.relativeBlocks.get();
        }

        public Map<Vec3, Block> getRealBlocks() {
            if (this.realBlocks == null) {
                Map<Vec3, Block> map = new HashMap<>();
                for (Map.Entry<BlockPos, Block> entry : getRelativeBlocks().entrySet()) {
                    map.put(Vec3.atLowerCornerOf(entry.getKey()), entry.getValue());
                }
                this.realBlocks = map;
            }
            return this.realBlocks;
        }

        private Map<BlockPos, Block> computeRelativeBlocks() {
            BlockPos median = new BlockPos(
                (this.shape.min(Direction.Axis.X) + this.shape.max(Direction.Axis.X)) / 2.0,
                (this.shape.min(Direction.Axis.Y) + this.shape.max(Direction.Axis.Y)) / 2.0,
                (this.shape.min(Direction.Axis.Z) + this.shape.max(Direction.Axis.Z)) / 2.0
            );
            // Use classic for loop in an effort to improve performance
            Map<BlockPos, Block> map = new HashMap<>();
            for (Map.Entry<BlockPos, Block> entry : this.blocks.entrySet()) {
                map.put(entry.getKey().subtract(median), entry.getValue());
            }
            return map;
        }
    }
}
