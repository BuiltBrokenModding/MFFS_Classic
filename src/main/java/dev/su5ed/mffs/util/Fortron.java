package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronFrequency;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.render.particle.BeamColor;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import dev.su5ed.mffs.setup.ModFluids;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

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

    public static <T extends BlockEntity & FortronFrequency> void transferFortron(T transmitter, Set<T> frequencyTiles, TransferMode transferMode, int limit) {
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
                    case EQUALIZE: {
                        for (T machine : frequencyTiles) {
                            if (machine != null) {
                                double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                                int amountToSet = (int) (totalFortron * capacityPercentage);
                                doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                            }
                        }

                        break;
                    }
                    case DISTRIBUTE: {
                        final int amountToSet = totalFortron / frequencyTiles.size();

                        for (T machine : frequencyTiles) {
                            if (machine != null) {
                                doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                            }
                        }

                        break;
                    }
                    case DRAIN: {
                        frequencyTiles.remove(transmitter);

                        for (T machine : frequencyTiles) {
                            if (machine != null) {
                                double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
                                int amountToSet = (int) (totalFortron * capacityPercentage);

                                if (amountToSet - machine.getFortronEnergy() > 0) {
                                    doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                                }
                            }
                        }

                        break;
                    }
                    case FILL: {
                        if (transmitter.getFortronEnergy() < transmitter.getFortronCapacity()) {
                            frequencyTiles.remove(transmitter);

                            // The amount of energy required to be full.
                            int requiredFortron = transmitter.getFortronCapacity() - transmitter.getFortronEnergy();

                            for (T machine : frequencyTiles) {
                                if (machine != null) {
                                    int amountToConsume = Math.min(requiredFortron, machine.getFortronEnergy());
                                    int amountToSet = -machine.getFortronEnergy() - amountToConsume;

                                    if (amountToConsume > 0) {
                                        doTransferFortron(transmitter, machine, amountToSet - machine.getFortronEnergy(), limit);
                                    }
                                }
                            }
                        }

                        break;
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
    public static <T extends BlockEntity & FortronFrequency> void doTransferFortron(T transmitter, T receiver, int joules, int limit) {
        if (transmitter != null && receiver != null) {
            Level level = transmitter.getLevel();
            boolean isCamo = transmitter instanceof ModuleAcceptor acceptor && acceptor.getModuleCount(ModItems.CAMOUFLAGE_MODULE.get()) > 0;
            
            T source, destination;
            if (joules > 0) {
                source = transmitter;
                destination = receiver;
            } else {
                source = receiver;
                destination = transmitter;
            }

            // Take energy from receiver.
            int transfer = Math.min(Math.abs(joules), limit);
            int transmitted = transmitter.provideFortron(receiver.requestFortron(transfer, FluidAction.SIMULATE), FluidAction.SIMULATE);
            int received = receiver.requestFortron(transmitter.provideFortron(transmitted, FluidAction.EXECUTE), FluidAction.EXECUTE);

            // Draw Beam Effect
            if (level instanceof ClientLevel clientLevel && received > 0 && !isCamo) {
                renderBeam(clientLevel, Vec3.atCenterOf(source.getBlockPos()), Vec3.atCenterOf(destination.getBlockPos()), BeamColor.BLUE, 20);
            }
        }
    }
    
    public static void renderBeam(ClientLevel level, Vec3 target, Vec3 position, BeamColor color, int lifetime) {
        level.addParticle(new BeamParticleOptions(target, color, lifetime), position.x(), position.y(), position.z(), 0, 0, 0);
    }

    private Fortron() {}
}
