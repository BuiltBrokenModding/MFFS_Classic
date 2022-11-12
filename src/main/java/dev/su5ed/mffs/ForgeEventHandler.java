package dev.su5ed.mffs;

import dev.su5ed.mffs.api.EventForceManipulate;
import dev.su5ed.mffs.api.fortron.FrequencyGrid;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventHandler {

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {
        FrequencyGrid.reinitiate();
    }

	@SubscribeEvent
    public static void eventPreForceManipulate(EventForceManipulate.EventPreForceManipulate event) {
		BlockEntity be = event.getLevel().getBlockEntity(event.getBeforePos());

        if (be instanceof FortronBlockEntity fortronBlockEntity) {
			fortronBlockEntity.setMarkSendFortron(false);
        }
    }
}
