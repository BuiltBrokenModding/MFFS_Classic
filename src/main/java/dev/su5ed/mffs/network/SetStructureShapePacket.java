package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public record SetStructureShapePacket(ResourceKey<Level> level, String structId, Optional<VoxelShape> shape) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetStructureShapePacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("structure_shape"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetStructureShapePacket> STREAM_CODEC = StreamCodec.composite(
        ResourceKey.streamCodec(Registries.DIMENSION),
        SetStructureShapePacket::level,
        ByteBufCodecs.STRING_UTF8,
        SetStructureShapePacket::structId,
        ByteBufCodecs.optional(CustomStructureSavedData.VOXEL_SHAPE_STREAM_CODEC),
        SetStructureShapePacket::shape,
        SetStructureShapePacket::new
    );

    public SetStructureShapePacket(ResourceKey<Level> level, String structId, VoxelShape shape) {
        this(level, structId, Optional.ofNullable(shape));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
