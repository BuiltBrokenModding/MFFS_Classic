package dev.su5ed.mffs.setup;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static dev.su5ed.mffs.MFFSMod.location;

public final class ModTags {
    public static final TagKey<Item> FORTRON_CATALYST = itemTag("fortron_catalyst");
    
    private static TagKey<Item> itemTag(String name) {
        return ItemTags.create(location(name));
    }
    
    private ModTags() {}
}
