package dev.su5ed.mffs.client;

import dev.su5ed.mffs.network.IMAZoneSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side registry of active Interdiction Matrix zones.
 *
 * The server sends zone data via {@link IMAZoneSyncPacket} on state/module changes and as
 * a periodic heartbeat.  Fires proximity warnings locally.
 */
@SideOnly(Side.CLIENT)
public final class ClientZoneTracker {

    private static final Map<BlockPos, ZoneInfo> ZONES = new HashMap<>();

    private ClientZoneTracker() {}

    /** Called from the packet handler (main thread) to add or remove a zone. */
    public static void updateZone(BlockPos pos, int actionRange, int warningRange, byte zoneType, boolean active) {
        if (active && warningRange > 0) {
            ZONES.put(pos, new ZoneInfo(actionRange, warningRange, zoneType));
        } else {
            ZONES.remove(pos);
        }
    }

    /** Drops all cached zones — called on world change / disconnect. */
    public static void clearAll() {
        ZONES.clear();
    }

    /**
     * Refreshes the action bar warning message when the player is inside the warning 
     * zone of any tracked zone.  Updates every 5 ticks.
     */
    public static void tick(Minecraft mc) {
        // Fastest possible exit when no IMs are on the network.
        if (ZONES.isEmpty()) return;
        if (mc.world == null || mc.player == null) return;
        if (mc.world.getTotalWorldTime() % 5 != 0) return;

        EntityPlayer player = mc.player;
        double px = player.posX;
        double py = player.posY + player.eyeHeight;
        double pz = player.posZ;

        for (Map.Entry<BlockPos, ZoneInfo> entry : ZONES.entrySet()) {
            BlockPos imPos = entry.getKey();
            ZoneInfo zone  = entry.getValue();

            double cx = imPos.getX() + 0.5;
            double cy = imPos.getY() + 0.5;
            double cz = imPos.getZ() + 0.5;
            double dist = Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy) + (pz - cz) * (pz - cz));

            if (dist > zone.actionRange && dist <= zone.warningRange) {
                int blocksToZone = (int)(dist - zone.actionRange);
                player.sendStatusMessage(buildMessage(zone.zoneType, blocksToZone), true);
                break; // show at most one warning at a time
            }
        }
    }

    private static ITextComponent buildMessage(byte zoneType, int blocksToZone) {
        // Zone-type segment: colored by zone severity
        String langKey;
        TextFormatting zoneColor;
        switch (zoneType) {
            case IMAZoneSyncPacket.ZONE_KILL:
                langKey   = "info.mffs.interdiction_matrix.warning.kill_zone";
                zoneColor = TextFormatting.RED;
                break;
            case IMAZoneSyncPacket.ZONE_CONFISCATION:
                langKey   = "info.mffs.interdiction_matrix.warning.confiscation_zone";
                zoneColor = TextFormatting.YELLOW;
                break;
            default:
                langKey   = "info.mffs.interdiction_matrix.warning.defense_zone";
                zoneColor = TextFormatting.WHITE;
                break;
        }
        TextComponentTranslation zoneTypePart = new TextComponentTranslation(langKey);
        zoneTypePart.getStyle().setColor(zoneColor);

        ITextComponent msg = new TextComponentString("Warning! Approaching ");
        msg.getStyle().setColor(TextFormatting.WHITE);
        TextComponentString distSuffix = new TextComponentString(": " + blocksToZone + "m");
        distSuffix.getStyle().setColor(TextFormatting.WHITE);
        msg.appendSibling(zoneTypePart);
        msg.appendSibling(distSuffix);
        return msg;
    }

    private static final class ZoneInfo {
        final int actionRange;
        final int warningRange;
        final byte zoneType;

        ZoneInfo(int actionRange, int warningRange, byte zoneType) {
            this.actionRange  = actionRange;
            this.warningRange = warningRange;
            this.zoneType = zoneType;
        }
    }
}
