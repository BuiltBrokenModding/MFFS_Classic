package dev.su5ed.mffs.setup;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.item.CustomProjectorModeItem.Mode;
import dev.su5ed.mffs.item.CustomProjectorModeItem.StructureCoords;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public final class ModDataComponentTypes {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MFFSMod.MODID);

    public static final Supplier<DataComponentType<BlockPos>> REMOTE_LINK_POS = DATA_COMPONENT_TYPES.register(
        "remote_link_pos", () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT_TYPES.register(
        "energy", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Integer>> CARD_FREQUENCY = DATA_COMPONENT_TYPES.register(
        "card_frequency", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    // Identification Card
    public static final Supplier<DataComponentType<GameProfile>> ID_CARD_PROFILE = DATA_COMPONENT_TYPES.register(
        "id_card_profile", () -> DataComponentType.<GameProfile>builder().persistent(ExtraCodecs.GAME_PROFILE).networkSynchronized(ByteBufCodecs.GAME_PROFILE).build());
    public static final Supplier<DataComponentType<List<FieldPermission>>> ID_CARD_PERMISSIONS = DATA_COMPONENT_TYPES.register(
        "id_card_permissions", () -> DataComponentType.<List<FieldPermission>>builder().persistent(ModUtil.FIELD_PERMISSION_CODEC.listOf()).networkSynchronized(ModUtil.FIELD_PERMISSION_STREAM_CODEC.apply(ByteBufCodecs.list())).build());

    // Custom Projector mode
    public static final Supplier<DataComponentType<String>> PATTERN_ID = DATA_COMPONENT_TYPES.register(
        "pattern_id", () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    public static final Supplier<DataComponentType<StructureCoords>> STRUCTURE_COORDS = DATA_COMPONENT_TYPES.register(
        "structure_coords", () -> DataComponentType.<StructureCoords>builder().persistent(StructureCoords.CODEC).networkSynchronized(StructureCoords.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<Mode>> STRUCTURE_MODE = DATA_COMPONENT_TYPES.register(
        "structure_mode", () -> DataComponentType.<Mode>builder().persistent(Mode.CODEC).networkSynchronized(Mode.STREAM_CODEC).build());

    public static void init(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }

    private ModDataComponentTypes() {
    }
}
