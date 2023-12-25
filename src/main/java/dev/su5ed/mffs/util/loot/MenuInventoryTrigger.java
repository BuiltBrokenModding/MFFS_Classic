package dev.su5ed.mffs.util.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MenuInventoryTrigger extends SimpleCriterionTrigger<MenuInventoryTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
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

    public record TriggerInstance(Optional<ContextAwarePredicate> player, MenuType<?> menuType, boolean active, List<ItemPredicate> items) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
            BuiltInRegistries.MENU.byNameCodec().fieldOf("menuType").forGetter(TriggerInstance::menuType),
            Codec.BOOL.fieldOf("active").forGetter(TriggerInstance::active),
            ItemPredicate.CODEC.listOf().fieldOf("items").forGetter(TriggerInstance::items)
        ).apply(instance, TriggerInstance::new));

        @SafeVarargs
        public static TriggerInstance create(MenuType<?> menuType, boolean active, Holder<Item>... items) {
            List<ItemPredicate> predicates = Stream.of(items)
                .map(holder -> new ItemPredicate(Optional.empty(), Optional.of(HolderSet.direct(holder)), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, List.of(), List.of(), Optional.empty(), Optional.empty()))
                .toList();
            return new TriggerInstance(Optional.empty(), menuType, active, predicates);
        }
    }
}
