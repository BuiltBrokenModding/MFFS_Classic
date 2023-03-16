package dev.su5ed.mffs.network;

import dev.su5ed.mffs.render.CustomProjectorModeClientHandler;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record SetStructureShapePacket(ResourceKey<Level> level, String id, @Nullable VoxelShape shape) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceKey(this.level);
        buf.writeUtf(this.id);
        if (this.shape != null) {
            buf.writeBoolean(true);
            CompoundTag tag = new CompoundTag();
            tag.put("shape", CustomStructureSavedData.VOXEL_SHAPE_CODEC.encodeStart(NbtOps.INSTANCE, this.shape).getOrThrow(false, s -> {}));
            buf.writeNbt(tag);
        }
        else {
            buf.writeBoolean(false);
        }
    }

    public static SetStructureShapePacket decode(FriendlyByteBuf buf) {
        ResourceKey<Level> level = buf.readResourceKey(Registry.DIMENSION_REGISTRY);
        String id = buf.readUtf();
        VoxelShape shape = buf.readBoolean() ? CustomStructureSavedData.VOXEL_SHAPE_CODEC.decode(NbtOps.INSTANCE, buf.readNbt().get("shape")).getOrThrow(false, s -> {}).getFirst() : null;
        return new SetStructureShapePacket(level, id, shape);
    }

    public void processClientPacket(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CustomProjectorModeClientHandler.setShape(this.level, this.id, this.shape));
    }
}
