package dev.su5ed.mffs.block;

import dev.su5ed.mffs.setup.ModObjects;

public class FortronCapacitorBlock extends BaseEntityBlock {

    public FortronCapacitorBlock(Properties properties) {
        super(properties.noOcclusion(), ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY);
    }
}
