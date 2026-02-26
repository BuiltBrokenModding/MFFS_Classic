package dev.su5ed.mffs.util.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class GuideBookTrigger extends SimpleCriterionTrigger<GuideBookTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        trigger(player, instance -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
        public static final TriggerInstance INSTANCE = new TriggerInstance(Optional.empty());
        private static final Codec<TriggerInstance> CODEC = MapCodec.unitCodec(INSTANCE);
    }
}
