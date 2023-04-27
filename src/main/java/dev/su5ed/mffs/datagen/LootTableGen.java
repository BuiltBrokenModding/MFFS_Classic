package dev.su5ed.mffs.datagen;

import com.mojang.datafixers.util.Pair;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.su5ed.mffs.MFFSMod.location;

class LootTableGen extends LootTableProvider {

    public LootTableGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return List.of(
            Pair.of(ModBlockLoot::new, LootContextParamSets.BLOCK),
            Pair.of(ModItemLoot::new, LootContextParamSets.EMPTY)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((location, lootTable) -> LootTables.validate(validationtracker, location, lootTable));
    }

    private static class ModBlockLoot extends BlockLoot {
        private final List<Block> blocks = StreamEx.of(ModBlocks.COERCION_DERIVER, ModBlocks.FORTRON_CAPACITOR, ModBlocks.PROJECTOR, ModBlocks.BIOMETRIC_IDENTIFIER)
            .<Block>map(Supplier::get)
            .toList();

        @Override
        protected void addTables() {
            for (Block block : this.blocks) {
                add(block, LootTable.lootTable()
                    .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block))))
                    .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(DynamicLoot.dynamicEntry(BaseEntityBlock.CONTENT_KEY)))));
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return this.blocks;
        }
    }

    private static class ModItemLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            Item book = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book"));
            CompoundTag tag = new CompoundTag();
            tag.putString("patchouli:book", MFFSMod.MODID + ":handbook");
            consumer.accept(location("grant_book_on_first_join"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(book))
                    .apply(SetNbtFunction.setTag(tag))));
        }
    }

    @Override
    public String getName() {
        return MFFSMod.NAME + super.getName();
    }
}
