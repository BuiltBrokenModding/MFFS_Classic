package dev.su5ed.mffs.item;

import dev.su5ed.mffs.render.ClientRenderHandler;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BiometricIdentifierItem extends BlockItem {

    public BiometricIdentifierItem() {
        super(ModBlocks.BIOMETRIC_IDENTIFIER.get(), ModItems.itemProperties());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);

        consumer.accept(ClientRenderHandler.biometricIdentifierItemRenderer());
    }
}
