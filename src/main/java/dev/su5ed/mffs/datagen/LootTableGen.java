package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import one.util.streamex.StreamEx;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static dev.su5ed.mffs.MFFSMod.location;

final class LootTableGen {
    private LootTableGen() {}
    
    public static class ModBlockLoot extends BlockLootSubProvider {
        private final List<Block> blocks = StreamEx.of(ModBlocks.COERCION_DERIVER, ModBlocks.FORTRON_CAPACITOR, ModBlocks.PROJECTOR, ModBlocks.BIOMETRIC_IDENTIFIER, ModBlocks.INTERDICTION_MATRIX)
            .<Block>map(Supplier::get)
            .toList();

        protected ModBlockLoot() {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
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

    public static class ModItemLoot implements LootTableSubProvider {
        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
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
}
