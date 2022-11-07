package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronFrequency;
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
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
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

    public static void transferFortron(FortronFrequency transmitter, Set<? extends FortronFrequency> frequencyTiles, TransferMode transferMode, int limit) {
        if (transmitter != null && frequencyTiles.size() > 1) {
            // Check spread mode. Equal, Give All, Take All
            int totalFortron = 0;
            int totalCapacity = 0;

            for (FortronFrequency machine : frequencyTiles) {
                if (machine != null) {
                    totalFortron += machine.getFortronEnergy();
                    totalCapacity += machine.getFortronCapacity();
                }
            }

            if (totalFortron > 0 && totalCapacity > 0) {
                // Test each mode and based on the mode, spread Fortron energy.
                switch (transferMode) {
                    case EQUALIZE -> {
                        for (FortronFrequency machine : frequencyTiles) {
                            if (machine != null) {
                                double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                                int amountToSet = (int) (totalFortron * capacityPercentage);
                                doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                            }
                        }
                    }
                    case DISTRIBUTE -> {
                        final int amountToSet = totalFortron / frequencyTiles.size();

                        for (FortronFrequency machine : frequencyTiles) {
                            if (machine != null) {
                                doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                            }
                        }
                    }
                    case DRAIN -> {
                        frequencyTiles.remove(transmitter);

                        for (FortronFrequency machine : frequencyTiles) {
                            if (machine != null) {
                                double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                                int amountToSet = (int) (totalFortron * capacityPercentage);

                                if (amountToSet - machine.getFortronEnergy() > 0) {
                                    doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                                }
                            }
                        }
                    }
                    case FILL -> {
                        if (transmitter.getFortronEnergy() < transmitter.getFortronCapacity()) {
                            frequencyTiles.remove(transmitter);

                            // The amount of energy required to be full.
                            int requiredFortron = transmitter.getFortronCapacity() - transmitter.getFortronEnergy();

                            for (FortronFrequency machine : frequencyTiles) {
                                if (machine != null) {
                                    int amountToConsume = Math.min(requiredFortron, machine.getFortronEnergy());
                                    int amountToSet = -machine.getFortronEnergy() - amountToConsume;

                                    if (amountToConsume > 0) {
                                        doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                                    }
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
    public static void doTransferFortron(FortronFrequency transmitter, FortronFrequency receiver, int joules, int limit) {
        if (transmitter != null && receiver != null) { // TODO stop machines from sending power to themselves
            BlockEntity transmitterBe = (BlockEntity) transmitter; 
            BlockEntity receiverBe = (BlockEntity) receiver;
            boolean isCamo = transmitter instanceof ModuleAcceptor acceptor && acceptor.getModuleCount(ModItems.CAMOUFLAGE_MODULE.get()) > 0;
            
            BlockEntity source, destination;
            if (joules > 0) {
                source = transmitterBe;
                destination = receiverBe;
            } else {
                source = receiverBe;
                destination = transmitterBe;
            }

            // Take energy from receiver.
            int transfer = Math.min(Math.abs(joules), limit);
            int transmitted = transmitter.provideFortron(receiver.requestFortron(transfer, FluidAction.SIMULATE), FluidAction.SIMULATE);
            int received = receiver.requestFortron(transmitter.provideFortron(transmitted, FluidAction.EXECUTE), FluidAction.EXECUTE);

            // Draw Beam Effect
            if (received > 0 && !isCamo) {
                Level level = transmitterBe.getLevel();
                Vec3 target = Vec3.atCenterOf(destination.getBlockPos());
                Vec3 position = Vec3.atCenterOf(source.getBlockPos());
                BeamColor color = BeamColor.BLUE;
                int lifetime = 20;
                if (level instanceof ClientLevel clientLevel) {
                    renderBeam(clientLevel, target, position, color, lifetime);
                }
                else {
                    DrawBeamPacket packet = new DrawBeamPacket(target, position, color, lifetime);
                    Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(source.getBlockPos())), packet);
                }
            }
        }
    }
    
    public static void renderBeam(ClientLevel level, Vec3 target, Vec3 position, BeamColor color, int lifetime) {
        level.addParticle(new BeamParticleOptions(target, color, lifetime), position.x(), position.y(), position.z(), 0, 0, 0);
    }

    private Fortron() {}
}
