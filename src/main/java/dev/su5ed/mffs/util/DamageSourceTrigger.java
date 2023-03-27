package dev.su5ed.mffs.util;

import com.google.gson.JsonObject;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import one.util.streamex.StreamEx;

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
    protected TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite playerPredicate, DeserializationContext context) {
        String damage = GsonHelper.getAsString(json, "damage");
        DamageSource damageSource = StreamEx.of(ModObjects.DAMAGE_SOURCES)
            .findFirst(source -> source.getMsgId().equals(damage))
            .orElseThrow(() -> new IllegalArgumentException("Unknown DamageSource id " + damage));
        return new TriggerInstance(playerPredicate, damageSource, GsonHelper.getAsBoolean(json, "killed"));
    }

    public void trigger(ServerPlayer player, DamageSource damageSource) {
        // If it is just any damage regardless of killed or the player is dead (or is on hardcore and used up a totem of undying)
        // And the damage source matches
        trigger(player, instance -> (!instance.killed || player.isDeadOrDying()) && instance.damageSource == damageSource);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final DamageSource damageSource;
        private final boolean killed;

        public TriggerInstance(EntityPredicate.Composite playerPredicate, DamageSource damageSource, boolean killed) {
            super(ModObjects.DAMAGE_TRIGGER.get().getId(), playerPredicate);
            this.damageSource = damageSource;
            this.killed = killed;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("damage", this.damageSource.getMsgId());
            json.addProperty("killed", this.killed);
            return json;
        }

        public static TriggerInstance killed(DamageSource damageSource) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, damageSource, true);
        }
    }
}
