package net.frostbyte.inventory.gui.components.debug;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class DebugEntryFacing implements DebugScreenEntry  {
    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "facing");

    public DebugEntryFacing() {
    }

    @Override
    public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();
        if (entity != null) {
            Direction direction = entity.getDirection();
            String var10000;
            switch (direction) {
                case NORTH -> var10000 = "Towards negative Z";
                case SOUTH -> var10000 = "Towards positive Z";
                case WEST -> var10000 = "Towards negative X";
                case EAST -> var10000 = "Towards positive X";
                default -> var10000 = "Invalid";
            }
            String faceString = var10000;
            String var10005 = String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, faceString, Mth.wrapDegrees(entity.getYRot()), Mth.wrapDegrees(entity.getXRot()));
            displayer.addToGroup(GROUP, var10005);
        }
    }
}
