package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.su5ed.mffs.MFFSMod.location;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MFFSMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> FIELD = registerSound("field");

    public static void init(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location(name)));
    }

    private ModSounds() {}
}
