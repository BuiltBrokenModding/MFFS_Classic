package dev.su5ed.mffs;

import dev.su5ed.mffs.api.EventForceManipulate;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FrequencyGrid;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
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

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        onPlayerInteract(event, Fortron.Action.RIGHT_CLICK_BLOCK);
    }

    @SubscribeEvent
    public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().getBlockState(event.getPos()).is(ModBlocks.FORCE_FIELD.get())) {
            event.setCanceled(true);
        } else {
            onPlayerInteract(event, Fortron.Action.LEFT_CLICK_BLOCK);
        }
    }

    @SubscribeEvent
    public static void livingSpawnEvent(MobSpawnEvent event) {
        InterdictionMatrix interdictionMatrix = Fortron.getNearestInterdictionMatrix(event.getEntity().level(), BlockPos.containing(event.getX(), event.getY(), event.getZ()));
        if (interdictionMatrix != null && interdictionMatrix.hasModule(ModModules.ANTI_SPAWN)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && MFFSConfig.COMMON.giveGuidebookOnFirstJoin.get()) {
            ModObjects.GUIDEBOOK_TRIGGER.get().trigger(serverPlayer);
        }
    }

    private static void onPlayerInteract(PlayerInteractEvent event, Fortron.Action action) {
        Player player = event.getEntity();
        if (!player.isCreative()) {
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            InterdictionMatrix interdictionMatrix = Fortron.getNearestInterdictionMatrix(level, pos);
            if (interdictionMatrix != null) {
                BlockState state = level.getBlockState(pos);
                if (state.is(ModBlocks.BIOMETRIC_IDENTIFIER.get()) && Fortron.isPermittedByInterdictionMatrix(interdictionMatrix, player, FieldPermission.CONFIGURE_SECURITY_CENTER)) {
                    return;
                }
                if (!Fortron.hasPermission(level, pos, interdictionMatrix, action, player)) {
                    player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.no_permission", interdictionMatrix.getTitle()), false);
                    event.setCanceled(true);
                }
            }
        }
    }
}
