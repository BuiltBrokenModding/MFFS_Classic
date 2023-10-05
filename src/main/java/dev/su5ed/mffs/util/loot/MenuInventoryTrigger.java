package dev.su5ed.mffs.util.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.su5ed.mffs.setup.ModObjects;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuInventoryTrigger extends SimpleCriterionTrigger<MenuInventoryTrigger.TriggerInstance> {
    private final ResourceLocation id;

    public MenuInventoryTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @NotNull
    public TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext context) {
        String name = GsonHelper.getAsString(json, "menu_type");
        MenuType<?> menuType = ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation(name));
        boolean active = GsonHelper.getAsBoolean(json, "active");
        if (menuType == null) {
            throw new IllegalArgumentException("Unknown MenuType " + name);
        }
        ItemPredicate[] predicates = ItemPredicate.fromJsonArray(json.get("items"));
        return new TriggerInstance(predicate, menuType, active, predicates);
    }

    public void trigger(ServerPlayer player, boolean active, IItemHandler itemHandler) {
        trigger(player, instance -> {
            if (instance.active != active) {
                return false;
            }
            List<ItemPredicate> list = new ObjectArrayList<>(instance.items);
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stackInSlot = itemHandler.getStackInSlot(i);
                list.removeIf(predicate -> predicate.matches(stackInSlot));
            }
            return list.isEmpty();
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MenuType<?> menuType;
        private final boolean active;
        private final ItemPredicate[] items;

        public TriggerInstance(ContextAwarePredicate predicate, MenuType<?> menuType, boolean active, ItemPredicate[] items) {
            super(ModObjects.MENU_INVENTORY_TRIGGER.get().getId(), predicate);

            this.menuType = menuType;
            this.items = items;
            this.active = active;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("menu_type", ForgeRegistries.MENU_TYPES.getKey(this.menuType).toString());
            json.addProperty("active", this.active);
            if (this.items.length > 0) {
                JsonArray predicatesJson = new JsonArray();
                for (ItemPredicate predicate : this.items) {
                    predicatesJson.add(predicate.serializeToJson());
                }
                json.add("items", predicatesJson);
            }
            return json;
        }

        public static TriggerInstance create(MenuType<?> menuType, boolean active, ItemLike... items) {
            ItemPredicate[] predicates = new ItemPredicate[items.length];
            for (int i = 0; i < items.length; ++i) {
                predicates[i] = new ItemPredicate(null, ImmutableSet.of(items[i].asItem()), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY);
            }
            return new TriggerInstance(ContextAwarePredicate.ANY, menuType, active, predicates);
        }
    }
}
