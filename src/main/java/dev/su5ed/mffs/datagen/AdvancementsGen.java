package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.DamageSourceTrigger;
import dev.su5ed.mffs.util.FieldShapeTrigger;
import dev.su5ed.mffs.util.MenuInventoryTrigger;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

import static net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems;

public class AdvancementsGen extends AdvancementProvider {

    public AdvancementsGen(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, fileHelper);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement root = Advancement.Builder.advancement()
            .display(ModItems.COERCION_DERIVER_ITEM.get(), title("root"), description("root"), MFFSMod.location("textures/block/machine_block.png"), FrameType.TASK, false, false, false)
            .addCriterion("has_steel_compound", hasItems(ModItems.STEEL_COMPOUND.get()))
            .save(consumer, id("root"));
        Advancement steelCompound = Advancement.Builder.advancement()
            .parent(root)
            .display(ModItems.STEEL_COMPOUND.get(), title("steel_compound"), description("steel_compound"), null, FrameType.TASK, true, true, false)
            .addCriterion("has_steel_compound", hasItems(ModItems.STEEL_COMPOUND.get()))
            .save(consumer, id("steel_compound"));
        Advancement smeltSteel = Advancement.Builder.advancement()
            .parent(steelCompound)
            .display(ModItems.STEEL_INGOT.get(), title("smelt_steel"), description("smelt_steel"), null, FrameType.TASK, true, true, false)
            .addCriterion("has_steel_ingot", hasItems(ModItems.STEEL_INGOT.get()))
            .save(consumer, id("smelt_steel"));
        Advancement projector = Advancement.Builder.advancement()
            .parent(smeltSteel)
            .display(ModItems.PROJECTOR_ITEM.get(), title("projector"), description("projector"), null, FrameType.TASK, true, true, false)
            .addCriterion("has_projector", hasItems(ModItems.PROJECTOR_ITEM.get()))
            .save(consumer, id("projector"));
        Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.SHOCK_MODULE.get(), title("field_shock"), description("field_shock"), null, FrameType.GOAL, true, true, false)
            .addCriterion("shocked", DamageSourceTrigger.TriggerInstance.killed(ModObjects.FIELD_SHOCK))
            .save(consumer, id("field_shock"));
        Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.SPONGE_MODULE.get(), title("sponge_module"), description("sponge_module"), null, FrameType.TASK, true, true, false)
            .addCriterion("has_sponge_module", hasItems(ModItems.SPONGE_MODULE.get()))
            .save(consumer, id("sponge_module"));
        Advancement camouflage = Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.CAMOUFLAGE_MODULE.get(), title("camouflage"), description("camouflage"), null, FrameType.TASK, true, true, false)
            .addCriterion("has_camouflage", hasItems(ModItems.CAMOUFLAGE_MODULE.get()))
            .save(consumer, id("camouflage"));
        Advancement.Builder.advancement()
            .parent(projector)
            .display(ModItems.CUSTOM_MODE.get(), title("field_shape"), description("field_shape"), null, FrameType.GOAL, true, true, false)
            .addCriterion("create_field_shape", FieldShapeTrigger.TriggerInstance.create())
            .save(consumer, id("field_shape"));
        Advancement.Builder.advancement()
            .parent(camouflage)
            .display(Items.ENDER_PEARL, title("custom_camouflage"), description("custom_camouflage"), null, FrameType.GOAL, true, true, false)
            .addCriterion("use_custom_camouflage", MenuInventoryTrigger.TriggerInstance.create(ModMenus.PROJECTOR_MENU.get(), true, ModItems.CUSTOM_MODE.get(), ModItems.CAMOUFLAGE_MODULE.get()))
            .save(consumer, id("custom_camouflage"));
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