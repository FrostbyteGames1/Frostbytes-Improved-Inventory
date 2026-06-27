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

public class DebugEntrySprinting implements DebugScreenEntry {
    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "sprinting");

    public DebugEntrySprinting() {
    }

    @Override
    public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.isSprinting()) {
            displayer.addToGroup(GROUP, Component.translatable("info.sprint_indicator").getString());
        }
    }
}
