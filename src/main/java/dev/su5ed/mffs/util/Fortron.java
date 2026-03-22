package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.network.DrawBeamPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.render.particle.ParticleColor;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModModules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.Optional;

/**
 * A class with useful functions related to Fortron.
 *
 * @author Calclavia
 */
public final class Fortron {

    public static void transferFortron(FortronStorage transmitter, Collection<? extends FortronStorage> receivers, TransferMode transferMode, int limit) {
        if (transmitter != null && receivers.size() > 1) {
            int totalFortron = 0;
            int totalCapacity = 0;

            for (FortronStorage storage : receivers) {
                totalFortron += storage.getStoredFortron();
                totalCapacity += storage.getFortronCapacity();
            }

            receivers.remove(transmitter);

            if (totalFortron > 0 && totalCapacity > 0) {
                switch (transferMode) {
                    case EQUALIZE -> {
                        for (FortronStorage machine : receivers) {
                            double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                            int amountToSet = (int) (totalFortron * capacityPercentage);
                            doTransferFortron(transmitter, machine, amountToSet - machine.getStoredFortron(), limit);
                        }
                    }
                    case DISTRIBUTE -> {
                        final int amountToSet = totalFortron / receivers.size();
                        for (FortronStorage machine : receivers) {
                            doTransferFortron(transmitter, machine, amountToSet - machine.getStoredFortron(), limit);
                        }
                    }
                    case DRAIN -> {
                        for (FortronStorage machine : receivers) {
                            double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                            int amountToSet = (int) (totalFortron * capacityPercentage);
                            if (amountToSet - machine.getStoredFortron() > 0) {
                                doTransferFortron(transmitter, machine, amountToSet - machine.getStoredFortron(), limit);
                            }
                        }
                    }
                    case FILL -> {
                        if (transmitter.getStoredFortron() < transmitter.getFortronCapacity()) {
                            int requiredFortron = transmitter.getFortronCapacity() - transmitter.getStoredFortron();
                            for (FortronStorage machine : receivers) {
                                int amountToConsume = Math.min(requiredFortron, machine.getStoredFortron());
                                int amountToSet = -machine.getStoredFortron() - amountToConsume;
                                if (amountToConsume > 0) {
                                    doTransferFortron(transmitter, machine, amountToSet - machine.getStoredFortron(), limit);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tries to transfer Fortron to a specific machine from this capacitor. Renders an animation on
     * the client side.
     *
     * @param receiver : The machine to be transferred to.
     * @param joules   : The amount of energy to be transferred.
     */
    public static void doTransferFortron(FortronStorage transmitter, FortronStorage receiver, int joules, int limit) {
        boolean isCamo = transmitter instanceof ModuleAcceptor acceptor && acceptor.hasModule(ModModules.CAMOUFLAGE);
        if (joules > 0) {
            doTransferFortron(transmitter, receiver, joules, limit, isCamo);
        } else {
            doTransferFortron(receiver, transmitter, joules, limit, isCamo);
        }
    }

    /**
     * Render a beam particle on the client side.
     */
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public static void renderBeam(World world, Vec3d target, Vec3d position, ParticleColor color, int lifetime) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        dev.su5ed.mffs.render.particle.BeamParticle particle =
            new dev.su5ed.mffs.render.particle.BeamParticle(world, position, target, color, lifetime);
        mc.effectRenderer.addEffect(particle);
    }

    /**
     * Sends a draw-beam packet to all clients tracking the chunk at chunkPos.
     */
    public static void renderClientBeam(World world, Vec3d target, Vec3d position, BlockPos chunkPos, ParticleColor color, int lifetime) {
        DrawBeamPacket packet = new DrawBeamPacket(target, position, color, lifetime);
        Network.sendToAllAround(packet, world, chunkPos, 128);
    }

    public static boolean hasPermission(World world, BlockPos pos, FieldPermission permission, EntityPlayer player) {
        InterdictionMatrix interdictionMatrix = getNearestInterdictionMatrix(world, pos);
        return interdictionMatrix == null || isPermittedByInterdictionMatrix(interdictionMatrix, player, permission);
    }

    public static boolean hasPermission(World world, BlockPos pos, InterdictionMatrix interdictionMatrix, Action action, EntityPlayer player) {
        boolean hasPermission = true;
        if (action == Action.RIGHT_CLICK_BLOCK && world.getTileEntity(pos) != null && interdictionMatrix.hasModule(ModModules.BLOCK_ACCESS)) {
            hasPermission = isPermittedByInterdictionMatrix(interdictionMatrix, player, FieldPermission.USE_BLOCKS);
        }
        if (hasPermission && interdictionMatrix.hasModule(ModModules.BLOCK_ALTER)
                && (player.getHeldItemMainhand() != null || action == Action.LEFT_CLICK_BLOCK)) {
            hasPermission = isPermittedByInterdictionMatrix(interdictionMatrix, player, FieldPermission.PLACE_BLOCKS);
        }
        return hasPermission;
    }

    public static InterdictionMatrix getNearestInterdictionMatrix(World world, BlockPos pos) {
        return StreamEx.of(FrequencyGrid.instance(world.isRemote).get())
            .mapPartial(storage -> {
                TileEntity te = storage.getOwner();
                if (te.getWorld() != world) return Optional.empty();
                InterdictionMatrix im = te.getCapability(ModCapabilities.INTERDICTION_MATRIX, null);
                if (im == null) return Optional.empty();
                double dist = Math.sqrt(te.getPos().distanceSq(pos));
                return (im.isActive() && dist <= im.getActionRange())
                    ? Optional.of(im)
                    : Optional.empty();
            })
            .findFirst()
            .orElse(null);
    }

    public static boolean isPermittedByInterdictionMatrix(InterdictionMatrix interdictionMatrix, EntityPlayer player, FieldPermission... permissions) {
        if (interdictionMatrix != null && interdictionMatrix.isActive() && interdictionMatrix.getBiometricIdentifier() != null) {
            for (FieldPermission permission : permissions) {
                if (!interdictionMatrix.getBiometricIdentifier().isAccessGranted(player, permission)) {
                    return interdictionMatrix.hasModule(ModModules.INVERTER);
                }
            }
        }
        return !interdictionMatrix.hasModule(ModModules.INVERTER);
    }

    private static void doTransferFortron(FortronStorage source, FortronStorage destination, int joules, int limit, boolean isCamo) {
        int transfer = Math.min(Math.abs(joules), limit);

        // Simulate-check: how much can we extract and insert?
        int canExtract = source.extractFortron(transfer, true);
        int canInsert  = destination.insertFortron(canExtract, true);

        if (canInsert <= 0) return;

        // Actual transfer
        int extracted     = source.extractFortron(canInsert, false);
        int actualInsert  = destination.insertFortron(extracted, false);
        int transferred   = Math.min(extracted, actualInsert);

        // Return any excess that couldn't be inserted
        if (actualInsert < extracted) {
            source.insertFortron(extracted - actualInsert, false);
        }

        // Draw Beam Effect
        if (transferred > 0 && !isCamo) {
            TileEntity sourceTe = source.getOwner();
            BlockPos sourcePos = sourceTe.getPos();
            World world = sourceTe.getWorld();
            BlockPos destPos = destination.getOwner().getPos();
            Vec3d target   = new Vec3d(destPos.getX() + 0.5, destPos.getY() + 0.5, destPos.getZ() + 0.5);
            Vec3d position = new Vec3d(sourcePos.getX() + 0.5, sourcePos.getY() + 0.5, sourcePos.getZ() + 0.5);
            ParticleColor color = ParticleColor.BLUE_BEAM;
            int lifetime = 20;
            if (world.isRemote) {
                renderBeam(world, target, position, color, lifetime);
            } else {
                renderClientBeam(world, target, position, sourcePos, color, lifetime);
            }
        }
    }

    private Fortron() {}

    public enum Action {
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_BLOCK
    }
}
