package dev.su5ed.mffs.network;

import dev.su5ed.mffs.render.CustomProjectorModeClientHandler;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public record SetStructureShapePacket(ResourceKey<Level> level, String id, VoxelShape shape) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceKey(this.level);
        buf.writeUtf(this.id);
        buf.writeNullable(this.shape, (b, s) -> b.writeCollection(s.toAabbs(), SetStructureShapePacket::encodeAABB));
    }

    public static SetStructureShapePacket decode(FriendlyByteBuf buf) {
        ResourceKey<Level> level = buf.readResourceKey(Registries.DIMENSION);
        String id = buf.readUtf();
        VoxelShape shape = buf.readNullable(b -> CustomStructureSavedData.shapeFromAABBs(b.readCollection(ArrayList::new, SetStructureShapePacket::decodeAABB)));
        return new SetStructureShapePacket(level, id, shape);
    }

    public void processClientPacket(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CustomProjectorModeClientHandler.setShape(this.level, this.id, this.shape));
    }

    private static void encodeAABB(FriendlyByteBuf buf, AABB aabb) {
        buf.writeDouble(aabb.minX);
        buf.writeDouble(aabb.minY);
        buf.writeDouble(aabb.minZ);
        buf.writeDouble(aabb.maxX);
        buf.writeDouble(aabb.maxY);
        buf.writeDouble(aabb.maxZ);
    }

    private static AABB decodeAABB(FriendlyByteBuf buf) {
        return new AABB(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }
}
