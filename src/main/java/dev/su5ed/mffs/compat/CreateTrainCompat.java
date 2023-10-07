package dev.su5ed.mffs.compat;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class CreateTrainCompat {
    private static final String CREATE_MODID = "create";
    private static final ResourceLocation TRAIN_ENTITY_TYPE = new ResourceLocation(CREATE_MODID, "carriage_contraption");
    private static final Supplier<Boolean> CREATE_LOADED = Suppliers.memoize(() -> ModList.get().isLoaded(CREATE_MODID));

    public static boolean isTrainPassenger(Entity entity) {
        if (!CREATE_LOADED.get()) {
            return false;
        }
        Entity vehicle = entity.getVehicle();
        if (vehicle != null) {
            ResourceLocation name = ForgeRegistries.ENTITY_TYPES.getKey(vehicle.getType());
            return TRAIN_ENTITY_TYPE.equals(name);
        }
        return false;
    }

    private CreateTrainCompat() {}
}
