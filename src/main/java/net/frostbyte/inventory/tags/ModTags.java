package net.frostbyte.inventory.tags;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Block> SHEARS_MINEABLE;
    public static final TagKey<Block> HAS_GUI;

    public static void registerModTags() {
        ImprovedInventory.LOGGER.info("Registered tags for Frostbyte's Improved Inventory");
    }

    static {
        SHEARS_MINEABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mineable/shears"));
        HAS_GUI = TagKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("has_gui"));
    }
}
