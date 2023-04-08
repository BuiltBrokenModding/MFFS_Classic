package dev.su5ed.mffs.util;

import com.google.gson.JsonObject;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class FieldShapeTrigger extends SimpleCriterionTrigger<FieldShapeTrigger.TriggerInstance> {
    private final ResourceLocation id;

    public FieldShapeTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @NotNull
    public TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext context) {
        return new TriggerInstance(player);
    }

    public void trigger(ServerPlayer player) {
        trigger(player, instance -> true);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(EntityPredicate.Composite player) {
            super(ModObjects.FIELD_SHAPE_TRIGGER.get().getId(), player);
        }

        public static TriggerInstance create() {
            return new TriggerInstance(EntityPredicate.Composite.ANY);
        }
    }
}