package net.frostbyte.inventory.gui.components.debug;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DebugEntrySpeed implements DebugScreenEntry {
    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "speed");

    public DebugEntrySpeed() {
    }

    @Override
    public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            Vec3 playerPosVec = minecraft.player.position();
            double travelledX = playerPosVec.x - minecraft.player.xOld;
            double travelledZ = playerPosVec.z - minecraft.player.zOld;
            displayer.addToGroup(GROUP, "Speed: " + String.format("%.3f m/s", Math.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ)) * 20));
        }
    }
}
