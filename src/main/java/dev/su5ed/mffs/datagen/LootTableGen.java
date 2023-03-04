package dev.su5ed.mffs.datagen;

import com.mojang.datafixers.util.Pair;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

class LootTableGen extends LootTableProvider {

    public LootTableGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return List.of(
            Pair.of(ModBlockLoot::new, LootContextParamSets.BLOCK)
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

    @Override
    public String getName() {
        return MFFSMod.NAME + super.getName();
    }
}
