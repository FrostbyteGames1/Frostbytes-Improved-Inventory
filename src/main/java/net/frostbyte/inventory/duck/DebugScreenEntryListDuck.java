package net.frostbyte.inventory.duck;

import net.frostbyte.inventory.gui.components.debug.DebugScreenSideEnum;
import net.minecraft.resources.Identifier;

public interface DebugScreenEntryListDuck {
    void setSide(Identifier location, DebugScreenSideEnum side);
    DebugScreenSideEnum getSide(Identifier location);
}
