package dev.su5ed.mffs.setup;

import com.mojang.serialization.Codec;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.item.CustomProjectorModeItem.Mode;
import dev.su5ed.mffs.item.CustomProjectorModeItem.StructureCoords;
import dev.su5ed.mffs.item.FrequencyCardItem.FrequencyCardAttachment;
import dev.su5ed.mffs.item.IdentificationCardItem.IdentificationCardAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModDataComponentTypes {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MFFSMod.MODID);

    public static final Supplier<DataComponentType<BlockPos>> REMOTE_LINK_POS = DATA_COMPONENT_TYPES.register(
        "remote_link_pos", () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT_TYPES.register(
        "energy", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<FrequencyCardAttachment>> FREQUENCY_CARD_DATA = DATA_COMPONENT_TYPES.register(
        "frequency_card_data", () -> DataComponentType.<FrequencyCardAttachment>builder().persistent(FrequencyCardAttachment.CODEC).networkSynchronized(FrequencyCardAttachment.STREAM_CODEC).build());

    public static final Supplier<DataComponentType<IdentificationCardAttachment>> IDENTIFICATION_CARD_DATA = DATA_COMPONENT_TYPES.register(
        "identification_card_data", () -> DataComponentType.<IdentificationCardAttachment>builder().persistent(IdentificationCardAttachment.CODEC).build());

    public static final Supplier<DataComponentType<String>> PATTERN_ID = DATA_COMPONENT_TYPES.register(
        "pattern_id", () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    public static final Supplier<DataComponentType<StructureCoords>> STRUCTURE_COORDS = DATA_COMPONENT_TYPES.register(
        "structure_coords", () -> DataComponentType.<StructureCoords>builder().persistent(StructureCoords.CODEC).networkSynchronized(StructureCoords.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<Mode>> STRUCTURE_MODE = DATA_COMPONENT_TYPES.register(
        "structure_mode", () -> DataComponentType.<Mode>builder().persistent(Mode.CODEC).networkSynchronized(Mode.STREAM_CODEC).build());

    public static void init(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }

    private ModDataComponentTypes() {}
}
