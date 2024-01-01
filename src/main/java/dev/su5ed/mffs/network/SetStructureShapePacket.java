package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;

public record SetStructureShapePacket(ResourceKey<Level> level, String structId, VoxelShape shape) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("structure_shape");

    public SetStructureShapePacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readUtf(), buf.readNullable(b -> CustomStructureSavedData.shapeFromAABBs(b.readCollection(ArrayList::new, SetStructureShapePacket::decodeAABB))));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(this.level);
        buf.writeUtf(this.structId);
        buf.writeNullable(this.shape, (b, s) -> b.writeCollection(s.toAabbs(), SetStructureShapePacket::encodeAABB));
    }

    @Override
    public ResourceLocation id() {
        return ID;
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
