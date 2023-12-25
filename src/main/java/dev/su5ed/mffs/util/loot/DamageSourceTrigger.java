package dev.su5ed.mffs.util.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageType;

import java.util.Optional;

/**
 * Source: Mekanism's <a href="https://github.com/mekanism/Mekanism/blob/b0b9bec27bcfd4795ad1eb94f6f96c1cbc42a06d/src/main/java/mekanism/common/advancements/triggers/MekanismDamageTrigger.java">MekanismDamageTrigger</a>
 */
public class DamageSourceTrigger extends SimpleCriterionTrigger<DamageSourceTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceKey<DamageType> damageType) {
        // If it is just any damage regardless of killed or the player is dead (or is on hardcore and used up a totem of undying)
        // And the damage source matches
        trigger(player, instance -> (!instance.killed || player.isDeadOrDying()) && instance.damageType.equals(damageType));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceKey<DamageType> damageType, boolean killed) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
            ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("damageType").forGetter(TriggerInstance::damageType),
            Codec.BOOL.fieldOf("killed").forGetter(TriggerInstance::killed)
        ).apply(instance, TriggerInstance::new));

        public static TriggerInstance killed(ResourceKey<DamageType> damageType) {
            return new TriggerInstance(Optional.empty(), damageType, true);
        }
    }
}
