package dev.su5ed.mffs.render;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.item.CustomProjectorModeItem;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.StructureDataRequestPacket;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Client-side cache for custom projector mode structure shapes,
 * and event handler that renders selection highlights while the item is held.
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MFFSMod.MODID)
public final class CustomProjectorModeClientHandler {
    private static final float MIN_ALPHA    = 0.1F;
    private static final float MAX_ALPHA    = BlockHighlighter.LIGHT_RED.alpha();
    private static final int   PERIOD_TICKS = 30;

    // Map: dimension ID → (structure ID → pre-computed outline)
    private static final Map<Integer, Map<String, BlockHighlighter.PreparedOutline>> STRUCTURE_SHAPES = new HashMap<>();

    private CustomProjectorModeClientHandler() {}

    // -------------------------------------------------------------------------
    // Shape cache – called by SetStructureShapePacket
    // -------------------------------------------------------------------------

    /**
     * Called by SetStructureShapePacket to update client-side shape data.
     * The expensive contour computation happens here (once), not per frame.
     */
    public static void setShape(int dimension, String structId, @Nullable Set<BlockPos> shape) {
        Map<String, BlockHighlighter.PreparedOutline> map = STRUCTURE_SHAPES.computeIfAbsent(dimension, d -> new HashMap<>());
        if (shape != null) {
            map.put(structId, BlockHighlighter.prepare(shape));
        } else {
            map.remove(structId);
        }
    }

    /** Get a cached pre-computed outline, or null if not yet received from server. */
    @Nullable
    public static BlockHighlighter.PreparedOutline getOutline(int dimension, String structId) {
        Map<String, BlockHighlighter.PreparedOutline> map = STRUCTURE_SHAPES.get(dimension);
        return map != null ? map.get(structId) : null;
    }

    /**
     * Return the cached outline for the item stack's pattern ID, or request it
     * from the server if not yet cached.
     */
    @Nullable
    private static BlockHighlighter.PreparedOutline getOrRequestOutline(ItemStack stack, int dimensionId) {
        String id = ModItems.CUSTOM_MODE.getId(stack);
        if (id == null) return null;
        Map<String, BlockHighlighter.PreparedOutline> map = STRUCTURE_SHAPES.get(dimensionId);
        if (map == null || !map.containsKey(id)) {
            Network.CHANNEL.sendToServer(new StructureDataRequestPacket(id));
            // Mark as pending so we don't spam the server
            setShape(dimensionId, id, null);
            return null;
        }
        return map.get(id);
    }

    // -------------------------------------------------------------------------
    // Render event
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void renderLevel(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        float partialTick = event.getPartialTicks();

        // Find a custom-mode item in the player's hands
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand  = mc.player.getHeldItemOffhand();
        ItemStack stack = null;
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof CustomProjectorModeItem) {
            stack = mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() instanceof CustomProjectorModeItem) {
            stack = offHand;
        }
        if (stack == null) return;

        // ---- Selection point highlights ----
        CustomProjectorModeItem.StructureCoords coords = CustomProjectorModeItem.StructureCoords.fromStack(stack);

        if (coords != null && coords.primary != null) {
            BlockHighlighter.highlightBlock(partialTick, coords.primary, BlockHighlighter.LIGHT_GREEN);

            if (coords.secondary != null) {
                // Both points selected: show both highlights + area outline
                BlockHighlighter.highlightBlock(partialTick, coords.secondary, BlockHighlighter.LIGHT_RED);
                BlockHighlighter.highlightArea(partialTick, coords.primary, coords.secondary);
            } else {
                // Primary selected, preview a potential secondary.
                // Prefer current block hit, otherwise preview a point in air along look direction.
                BlockPos hovered = null;
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    hovered = mc.objectMouseOver.getBlockPos();
                } else {
                    Vec3d eyePos = mc.player.getPositionEyes(partialTick);
                    Vec3d lookVec = mc.player.getLook(partialTick);
                    double range = 5.0;
                    Vec3d endPos = new Vec3d(eyePos.x + lookVec.x * range, eyePos.y + lookVec.y * range, eyePos.z + lookVec.z * range);
                    hovered = new BlockPos(endPos);
                }

                if (hovered != null) {
                float tick  = mc.world.getTotalWorldTime() % PERIOD_TICKS;
                float alpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA)
                    * 0.5F * (float) Math.abs(Math.sin(2 * Math.PI * tick / PERIOD_TICKS) + 1.0);
                BlockHighlighter.highlightBlock(partialTick, hovered,
                    BlockHighlighter.LIGHT_RED.withAlpha(alpha));
                BlockHighlighter.highlightArea(partialTick, coords.primary, hovered);
                }
            }
        }

        // ---- Pattern ID shape highlight ----
        String id = ModItems.CUSTOM_MODE.getId(stack);
        if (id != null) {
            int dimensionId = mc.world.provider.getDimension();
            BlockHighlighter.PreparedOutline outline = getOrRequestOutline(stack, dimensionId);
            if (outline != null && !outline.isEmpty()) {
                BlockHighlighter.renderPrepared(partialTick, outline, null);
            }
        }
    }
}
