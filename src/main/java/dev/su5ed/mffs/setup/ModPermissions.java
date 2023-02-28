package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionType;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

public final class ModPermissions {
    private static final List<PermissionNode<?>> PERMISSION_NODES = new ArrayList<>();

    public static final PermissionNode<Boolean> OVERRIDE_BIOMETRY = register("biometry.override", PermissionTypes.BOOLEAN,
        (player, uuid, context) -> player != null && player.server.getPlayerList().isOp(player.getGameProfile()));

    @SafeVarargs
    private static <T> PermissionNode<T> register(String nodeName, PermissionType<T> type, PermissionNode.PermissionResolver<T> defaultResolver, PermissionDynamicContextKey<T>... dynamics) {
        PermissionNode<T> node = new PermissionNode<>(MFFSMod.MODID, nodeName, type, defaultResolver, dynamics);
        PERMISSION_NODES.add(node);
        return node;
    }

    public static void gatherPermissionNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(PERMISSION_NODES);
    }

    private ModPermissions() {}
}
