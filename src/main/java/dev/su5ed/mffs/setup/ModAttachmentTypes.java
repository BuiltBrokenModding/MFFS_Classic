package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.item.FrequencyCardItem;
import dev.su5ed.mffs.item.IdentificationCardItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MFFSMod.MODID);

    public static final Supplier<AttachmentType<FrequencyCardItem.FrequencyCardAttachment>> FREQUENCY_CARD_DATE = ATTACHMENT_TYPES.register(
        "frequency_card_data", () -> AttachmentType.builder(FrequencyCardItem.FrequencyCardAttachment::new).serialize(FrequencyCardItem.FrequencyCardAttachment.CODEC).build());

    public static final Supplier<AttachmentType<IdentificationCardItem.IdentificationCardAttachment>> IDENTIFICATION_CARD_DATA = ATTACHMENT_TYPES.register(
        "identification_card_data", () -> AttachmentType.serializable(IdentificationCardItem.IdentificationCardAttachment::new).build());

    public static void init(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    private ModAttachmentTypes() {}
}
