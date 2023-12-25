package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.loot.DamageSourceTrigger;
import dev.su5ed.mffs.util.loot.FieldShapeTrigger;
import dev.su5ed.mffs.util.loot.GuideBookTrigger;
import dev.su5ed.mffs.util.loot.MenuInventoryTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

import static dev.su5ed.mffs.MFFSMod.location;
import static net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems;

public class AdvancementsGen implements AdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        AdvancementHolder root = Advancement.Builder.advancement()
            .display(ModItems.COERCION_DERIVER_ITEM.get(), title("root"), description("root"), location("textures/block/machine_block.png"), AdvancementType.TASK, false, false, false)
            .addCriterion("has_steel_compound", hasItems(ModItems.STEEL_COMPOUND.get()))
            .save(saver, id("root"));
        AdvancementHolder steelCompound = Advancement.Builder.advancement()
            .parent(root)
            .display(ModItems.STEEL_COMPOUND.get(), title("steel_compound"), description("steel_compound"), null, AdvancementType.TASK, true, true, false)
            .addCriterion("has_steel_compound", hasItems(ModItems.STEEL_COMPOUND.get()))
            .save(saver, id("steel_compound"));
        AdvancementHolder smeltSteel = Advancement.Builder.advancement()
            .parent(steelCompound)
            .display(ModItems.STEEL_INGOT.get(), title("smelt_steel"), description("smelt_steel"), null, AdvancementType.TASK, true, true, false)
            .addCriterion("has_steel_ingot", hasItems(ModItems.STEEL_INGOT.get()))
            .save(saver, id("smelt_steel"));
        AdvancementHolder projector = Advancement.Builder.advancement()
            .parent(smeltSteel)
            .display(ModItems.PROJECTOR_ITEM.get(), title("projector"), description("projector"), null, AdvancementType.TASK, true, true, false)
            .addCriterion("has_projector", hasItems(ModItems.PROJECTOR_ITEM.get()))
            .save(saver, id("projector"));
        Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.SHOCK_MODULE.get(), title("field_shock"), description("field_shock"), null, AdvancementType.GOAL, true, true, false)
            .addCriterion("shocked", ModObjects.DAMAGE_TRIGGER.get().createCriterion(DamageSourceTrigger.TriggerInstance.killed(ModObjects.FIELD_SHOCK_TYPE)))
            .save(saver, id("field_shock"));
        Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.SPONGE_MODULE.get(), title("sponge_module"), description("sponge_module"), null, AdvancementType.TASK, true, true, false)
            .addCriterion("has_sponge_module", hasItems(ModItems.SPONGE_MODULE.get()))
            .save(saver, id("sponge_module"));
        AdvancementHolder camouflage = Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.CAMOUFLAGE_MODULE.get(), title("camouflage"), description("camouflage"), null, AdvancementType.TASK, true, true, false)
            .addCriterion("has_camouflage", hasItems(ModItems.CAMOUFLAGE_MODULE.get()))
            .save(saver, id("camouflage"));
        Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.CUSTOM_MODE.get(), title("field_shape"), description("field_shape"), null, AdvancementType.GOAL, true, true, false)
            .addCriterion("create_field_shape", ModObjects.FIELD_SHAPE_TRIGGER.get().createCriterion(FieldShapeTrigger.TriggerInstance.INSTANCE))
            .save(saver, id("field_shape"));
        Advancement.Builder.advancement()
            .parent(camouflage)
            .display(Items.ENDER_PEARL, title("custom_camouflage"), description("custom_camouflage"), null, AdvancementType.GOAL, true, true, false)
            .addCriterion("use_custom_camouflage", ModObjects.MENU_INVENTORY_TRIGGER.get().createCriterion(MenuInventoryTrigger.TriggerInstance.create(ModMenus.PROJECTOR_MENU.get(), true, ModItems.CUSTOM_MODE, ModItems.CAMOUFLAGE_MODULE)))
            .save(saver, id("custom_camouflage"));

        Advancement.Builder.advancement()
            .addCriterion("guidebook", ModObjects.GUIDEBOOK_TRIGGER.get().createCriterion(GuideBookTrigger.TriggerInstance.INSTANCE))
            .rewards(AdvancementRewards.Builder.loot(location("grant_book_on_first_join")))
            .save(saver, id("grant_book_on_first_join"));
    }

    private static Component title(String name) {
        return ModUtil.translate("advancements", name + ".title");
    }

    private static Component description(String name) {
        return ModUtil.translate("advancements", name + ".description");
    }

    private static String id(String name) {
        return MFFSMod.MODID + ":" + name;
    }
}
