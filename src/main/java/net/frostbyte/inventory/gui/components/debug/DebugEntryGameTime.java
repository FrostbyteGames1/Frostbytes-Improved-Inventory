package net.frostbyte.inventory.gui.components.debug;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DebugEntryGameTime implements DebugScreenEntry {
    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "game_time");

    public DebugEntryGameTime() {
    }

    @Override
    public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            displayer.addToGroup(GROUP, Component.translatable("info.game_time").getString() + minecraft.level.getOverworldClockTime());
        }
    }
}
