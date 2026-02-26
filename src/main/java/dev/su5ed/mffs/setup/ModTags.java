package dev.su5ed.mffs.setup;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static dev.su5ed.mffs.MFFSMod.location;

public final class ModTags {
    public static final TagKey<Item> FORTRON_FUEL = itemTag("fortron_fuel");
    public static final TagKey<Block> FORCEFIELD_REPLACEABLE = blockTag("forcefield_replaceable");

    public static final TagKey<Block> STABILIZATION_BLACKLIST = blockTag("stabilization_blacklist");
    public static final TagKey<Block> DISINTEGRATION_BLACKLIST = blockTag("disintegration_blacklist");

    public static final TagKey<Item> INGOTS_STEEL = cItemTag("ingots/steel");

    private static TagKey<Item> itemTag(String name) {
        return ItemTags.create(location(name));
    }

    private static TagKey<Item> cItemTag(String name) {
        return ItemTags.create(Identifier.fromNamespaceAndPath("c", name));
    }

    private static TagKey<Block> blockTag(String name) {
        return BlockTags.create(location(name));
    }

    private ModTags() {}
}
