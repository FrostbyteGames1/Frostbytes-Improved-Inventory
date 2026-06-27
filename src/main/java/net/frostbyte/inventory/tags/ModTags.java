package net.frostbyte.inventory.tags;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> SHEARS_MINEABLE;
    public static final TagKey<Block> HAS_GUI;

    public static void registerModTags() {
        ImprovedInventory.LOGGER.info("Registered tags for Frostbyte's Improved Inventory");
    }

    static {
        SHEARS_MINEABLE = TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("mineable/shears"));
        HAS_GUI = TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("has_gui"));
    }
}
