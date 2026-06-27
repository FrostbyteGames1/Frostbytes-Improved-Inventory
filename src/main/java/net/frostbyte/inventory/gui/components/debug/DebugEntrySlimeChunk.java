package net.frostbyte.inventory.gui.components.debug;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DebugEntrySlimeChunk implements DebugScreenEntry {
    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "slime_chunk");

    public DebugEntrySlimeChunk() {
    }

    @Override
    public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.player;
        if (entity != null && minecraft.level != null && minecraft.hasSingleplayerServer()) {
            if (minecraft.getSingleplayerServer() != null) {
                ChunkPos chunkPos = ChunkPos.containing(entity.getOnPos());
                //noinspection DataFlowIssue
                if (WorldgenRandom.seedSlimeChunk(chunkPos.x(), chunkPos.z(), minecraft.getSingleplayerServer().getLevel(minecraft.level.dimension()).getSeed(), 987234911L).nextInt(10) == 0) {
                    displayer.addToGroup(GROUP, Component.translatable("info.slime_chunk").getString());
                }
            } else {
                displayer.addToGroup(GROUP, Component.literal("Failed to get slime chunk data from server").getString());
            }
        } else if (entity != null && minecraft.level instanceof WorldGenLevel worldGenLevel) {
            ChunkPos chunkPos = ChunkPos.containing(entity.getOnPos());
            if (WorldgenRandom.seedSlimeChunk(chunkPos.x(), chunkPos.z(), worldGenLevel.getSeed(), 987234911L).nextInt(10) == 0) {
                displayer.addToGroup(GROUP, Component.translatable("info.slime_chunk").getString());
            }
        }
    }
}
