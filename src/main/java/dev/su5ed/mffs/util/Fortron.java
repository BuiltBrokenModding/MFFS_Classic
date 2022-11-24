package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.network.DrawBeamPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.render.particle.BeamColor;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import dev.su5ed.mffs.setup.ModFluids;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;

/**
 * A class with useful functions related to Fortron.
 *
 * @author Calclavia
 */
public final class Fortron {
    public static FluidStack getFortron(int amount) {
        return new FluidStack(ModFluids.FORTRON_FLUID.get(), amount);
    }

    public static void transferFortron(FortronStorage transmitter, Set<? extends FortronStorage> frequencyTiles, TransferMode transferMode, int limit) {
        if (transmitter != null && frequencyTiles.size() > 1) {
            // Check spread mode. Equal, Give All, Take All
            int totalFortron = 0;
            int totalCapacity = 0;

            for (FortronStorage storage : frequencyTiles) {
                totalFortron += storage.getStoredFortron();
                totalCapacity += storage.getFortronCapacity();
            }

            if (totalFortron > 0 && totalCapacity > 0) {
                // Test each mode and based on the mode, spread Fortron energy.
                switch (transferMode) {
                    case EQUALIZE -> {
                        for (FortronStorage machine : frequencyTiles) {
                            double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                            int amountToSet = (int) (totalFortron * capacityPercentage);
                            doTransferFortron(transmitter, machine, amountToSet - machine.getStoredFortron(), limit);
                        }
                    }
                    case DISTRIBUTE -> {
                        final int amountToSet = totalFortron / frequencyTiles.size();

                        for (FortronStorage machine : frequencyTiles) {
                            doTransferFortron(transmitter, machine, amountToSet - machine.getStoredFortron(), limit);
                        }
                    }
                    case DRAIN -> {
                        frequencyTiles.remove(transmitter);

                        for (FortronStorage machine : frequencyTiles) {
                            double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                            int amountToSet = (int) (totalFortron * capacityPercentage);

                            if (amountToSet - machine.getStoredFortron() > 0) {
                                doTransferFortron(transmitter, machine, amountToSet - machine.getStoredFortron(), limit);
                            }
                        }
                    }
                    case FILL -> {
                        if (transmitter.getStoredFortron() < transmitter.getFortronCapacity()) {
                            frequencyTiles.remove(transmitter);

                            // The amount of energy required to be full.
                            int requiredFortron = transmitter.getFortronCapacity() - transmitter.getStoredFortron();

                            for (FortronStorage machine : frequencyTiles) {
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
     * @param receiver : The machine to be transfered to.
     * @param joules   : The amount of energy to be transfered.
     */
    public static void doTransferFortron(FortronStorage transmitter, FortronStorage receiver, int joules, int limit) {
        // TODO stop machines from sending power to themselves
        boolean isCamo = transmitter instanceof ModuleAcceptor acceptor && acceptor.getModuleCount(ModItems.CAMOUFLAGE_MODULE.get()) > 0;
        if (joules > 0) {
            doTransferFortron(transmitter, receiver, joules, limit, isCamo);
        } else {
            doTransferFortron(receiver, transmitter, joules, limit, isCamo);
        }
    }
    
    public static void renderBeam(ClientLevel level, Vec3 target, Vec3 position, BeamColor color, int lifetime) {
        level.addParticle(new BeamParticleOptions(target, color, lifetime), position.x(), position.y(), position.z(), 0, 0, 0);
    }
    
    private static void doTransferFortron(FortronStorage source, FortronStorage destination, int joules, int limit, boolean isCamo) {
        // Take energy from receiver.
        int transfer = Math.min(Math.abs(joules), limit);
        int available = destination.insertFortron(source.extractFortron(transfer, true), true);
        int transferred = source.extractFortron(destination.insertFortron(available, false), false);

        // Draw Beam Effect
        if (transferred > 0 && !isCamo) {
            BlockEntity sourceBe = source.getOwner();
            BlockPos sourcePos = sourceBe.getBlockPos();
            Level level = sourceBe.getLevel();
            Vec3 target = Vec3.atCenterOf(destination.getOwner().getBlockPos());
            Vec3 position = Vec3.atCenterOf(sourcePos);
            BeamColor color = BeamColor.BLUE;
            int lifetime = 20;
            if (level instanceof ClientLevel clientLevel) {
                renderBeam(clientLevel, target, position, color, lifetime);
            } else {
                DrawBeamPacket packet = new DrawBeamPacket(target, position, color, lifetime);
                Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(sourcePos)), packet);
            }
        }
    }

    private Fortron() {}
}
