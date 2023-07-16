package dev.su5ed.mffs.util;

import com.google.gson.JsonObject;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageType;

/**
 * Source: Mekanism's <a href="https://github.com/mekanism/Mekanism/blob/b0b9bec27bcfd4795ad1eb94f6f96c1cbc42a06d/src/main/java/mekanism/common/advancements/triggers/MekanismDamageTrigger.java">MekanismDamageTrigger</a>
 */
public class DamageSourceTrigger extends SimpleCriterionTrigger<DamageSourceTrigger.TriggerInstance> {
    private final ResourceLocation id;

    public DamageSourceTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate playerPredicate, DeserializationContext context) {
        String damage = GsonHelper.getAsString(json, "damage");
        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(damage));
        return new TriggerInstance(playerPredicate, key, GsonHelper.getAsBoolean(json, "killed"));
    }

    public void trigger(ServerPlayer player, ResourceKey<DamageType> damageType) {
        // If it is just any damage regardless of killed or the player is dead (or is on hardcore and used up a totem of undying)
        // And the damage source matches
        trigger(player, instance -> (!instance.killed || player.isDeadOrDying()) && instance.damageType.equals(damageType));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ResourceKey<DamageType> damageType;
        private final boolean killed;

        public TriggerInstance(ContextAwarePredicate predicate, ResourceKey<DamageType> damageType, boolean killed) {
            super(ModObjects.DAMAGE_TRIGGER.get().getId(), predicate);
            this.damageType = damageType;
            this.killed = killed;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("damage", this.damageType.location().toString());
            json.addProperty("killed", this.killed);
            return json;
        }

        public static TriggerInstance killed(ResourceKey<DamageType> damageType) {
            return new TriggerInstance(ContextAwarePredicate.ANY, damageType, true);
        }
    }
}
