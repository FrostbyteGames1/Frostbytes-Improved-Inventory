package net.frostbyte.inventory.gui.components.debug;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class DebugEntryCoordinates implements DebugScreenEntry  {
    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "coordinates");

    public DebugEntryCoordinates() {
    }

    @Override
    public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();
        if (entity != null) {
            String coordinates = String.format(Locale.ROOT, "XYZ: %.3f / %.3f / %.3f", minecraft.getCameraEntity().getX(), minecraft.getCameraEntity().getY(), minecraft.getCameraEntity().getZ());
            displayer.addToGroup(GROUP, coordinates);
        }
    }
}
