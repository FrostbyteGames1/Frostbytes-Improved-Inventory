package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.LightType;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class TextDisplayer implements HudRenderCallback {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void onHudRender(DrawContext drawContext, float tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && !mc.player.isSpectator() && mc.world != null && ImprovedInventoryConfig.textDisplay && !mc.options.hudHidden && mc.currentScreen == null && !mc.options.debugEnabled) {
            int x = 1 + ImprovedInventoryConfig.textDisplayOffsetX;
            int y = 1 + ImprovedInventoryConfig.textDisplayOffsetY;
            for (String line : ImprovedInventoryConfig.textDisplayLeft) {
                switch (line) {
                    case "Biome":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("biome." + mc.world.getBiome(mc.player.getBlockPos()).getKey().get().getValue().toTranslationKey()), x, y, Colors.WHITE);
                        break;
                    case "Block Light":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.block_light").getString() + mc.world.getLightLevel(LightType.BLOCK, mc.player.getBlockPos()), x, y, Colors.WHITE);
                        break;
                    case "Coordinates":
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("XYZ: %.3f / %.3f / %.3f", mc.player.getX(), mc.player.getY(), mc.player.getZ()), x, y, Colors.WHITE);
                        break;
                    case "Day":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.day_number").getString() + (mc.world.getTimeOfDay() / 24000L % Integer.MAX_VALUE), x, y, Colors.WHITE);
                        break;
                    case "Dimension":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("dimension." + mc.world.getDimensionEntry().getKey().get().getValue().toTranslationKey()), x, y, Colors.WHITE);
                        break;
                    case "Entity Count":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.entity_count").getString() + mc.world.getRegularEntityCount(), x, y, Colors.WHITE);
                        break;
                    case "Facing Direction":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.facing_direction").getString() + mc.player.getHorizontalFacing().asString(), x, y, Colors.WHITE);
                        break;
                    case "FPS":
                        drawContext.drawTextWithShadow(mc.textRenderer, mc.getCurrentFps() + " FPS", x, y, Colors.WHITE);
                        break;
                    case "Local Difficulty":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.local_difficulty").getString() + ": " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty(), x, y, Colors.WHITE);
                        break;
                    case "Memory Usage":
                        drawContext.drawTextWithShadow(mc.textRenderer, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "/" + (Runtime.getRuntime().maxMemory() / 1048576) + " MB", x, y, Colors.WHITE);
                        break;
                    case "Slime Chunk":
                        if (mc.isIntegratedServerRunning()) {
                            ServerWorld serverWorld = Objects.requireNonNull(mc.getServer()).getWorld(mc.world.getRegistryKey());
                            if (serverWorld != null && ChunkRandom.getSlimeRandom(new ChunkPos(mc.player.getBlockPos()).x, new ChunkPos(mc.player.getBlockPos()).z, serverWorld.getSeed(), 987234911L).nextInt(10) == 0) {
                                drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.slime_chunk"), x, y, Colors.WHITE);
                            } else {
                                y -= 10;
                            }
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Speed":
                        Vec3d playerPosVec = mc.player.getPos();
                        double travelledX = playerPosVec.x - mc.player.prevX;
                        double travelledZ = playerPosVec.z - mc.player.prevZ;
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.3f m/s", MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ)) * 20), x, y, Colors.WHITE);
                        break;
                    case "Sprint Indicator":
                        if (mc.player.isSprinting()) {
                            drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.sprint_indicator"), x, y, Color.ORANGE.getRGB());
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Targeted Block/Entity":
                        String target = Blocks.AIR.getName().getString();
                        if (mc.crosshairTarget != null) {
                            if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                target = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock().getName().getString();
                            } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                                List<Entity> entities = mc.world.getOtherEntities(mc.player, Box.from(mc.crosshairTarget.getPos()).expand(1));
                                target = entities.isEmpty() ? Blocks.AIR.getName().getString() : entities.getFirst().getName().getString();
                            }
                        }
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.crosshair_target") + target, x, y, Colors.WHITE);
                        break;
                    case "Time (Game)":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.in_game_time").getString() + (mc.world.getTimeOfDay() % 24000L), x, y, Colors.WHITE);
                        break;
                    case "Time (Real)":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.local_time").getString() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")), x, y, Colors.WHITE);
                        break;
                    default:
                        drawContext.drawTextWithShadow(mc.textRenderer, "", x, y, Colors.WHITE);
                }
                y += 10;
            }
            y = 1 + ImprovedInventoryConfig.textDisplayOffsetY;
            for (String line : ImprovedInventoryConfig.textDisplayRight) {
                x = mc.getWindow().getScaledWidth() - 1 - ImprovedInventoryConfig.textDisplayOffsetX;
                switch (line) {
                    case "Biome":
                        x -= mc.textRenderer.getWidth(Text.translatable("biome." + mc.world.getBiome(mc.player.getBlockPos()).getKey().get().getValue().toTranslationKey()));
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("biome." + mc.world.getBiome(mc.player.getBlockPos()).getKey().get().getValue().toTranslationKey()), x, y, Colors.WHITE);
                        break;
                    case "Block Light":
                        x -= mc.textRenderer.getWidth(Text.translatable("info.block_light").getString() + mc.world.getLightLevel(LightType.BLOCK, mc.player.getBlockPos()));
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.block_light").getString() + mc.world.getLightLevel(LightType.BLOCK, mc.player.getBlockPos()), x, y, Colors.WHITE);
                        break;
                    case "Coordinates":
                        x -= mc.textRenderer.getWidth(String.format("XYZ: %.3f / %.3f / %.3f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("XYZ: %.3f / %.3f / %.3f", mc.player.getX(), mc.player.getY(), mc.player.getZ()), x, y, Colors.WHITE);
                        break;
                    case "Day":
                        x -= mc.textRenderer.getWidth(Text.translatable("info.day_number").getString() + (mc.world.getTimeOfDay() / 24000L % Integer.MAX_VALUE));
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.day_number").getString() + (mc.world.getTimeOfDay() / 24000L % Integer.MAX_VALUE), x, y, Colors.WHITE);
                        break;
                    case "Dimension":
                        x -= mc.textRenderer.getWidth(Text.translatable("dimension." + mc.world.getDimensionEntry().getKey().get().getValue().toTranslationKey()));
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("dimension." + mc.world.getDimensionEntry().getKey().get().getValue().toTranslationKey()), x, y, Colors.WHITE);
                        break;
                    case "Entity Count":
                        x -= mc.textRenderer.getWidth(Text.translatable("info.entity_count").getString() + mc.world.getRegularEntityCount());
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.entity_count").getString() + mc.world.getRegularEntityCount(), x, y, Colors.WHITE);
                        break;
                    case "Facing Direction":
                        x -= mc.textRenderer.getWidth(Text.translatable("info.facing_direction").getString() + mc.player.getHorizontalFacing().asString());
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.facing_direction").getString() + mc.player.getHorizontalFacing().asString(), x, y, Colors.WHITE);
                        break;
                    case "FPS":
                        x -= mc.textRenderer.getWidth(mc.getCurrentFps() + " FPS");
                        drawContext.drawTextWithShadow(mc.textRenderer, mc.getCurrentFps() + " FPS", x, y, Colors.WHITE);
                        break;
                    case "Local Difficulty":
                        x -= mc.textRenderer.getWidth(Text.translatable("info.local_difficulty").getString() + ": " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty());
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.local_difficulty").getString() + ": " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty(), x, y, Colors.WHITE);
                        break;
                    case "Memory Usage":
                        x -= mc.textRenderer.getWidth(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "/" + (Runtime.getRuntime().maxMemory() / 1048576) + " MB");
                        drawContext.drawTextWithShadow(mc.textRenderer, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "/" + (Runtime.getRuntime().maxMemory() / 1048576) + " MB", x, y, Colors.WHITE);
                        break;
                    case "Slime Chunk":
                        if (mc.isIntegratedServerRunning()) {
                            ServerWorld serverWorld = Objects.requireNonNull(mc.getServer()).getWorld(mc.world.getRegistryKey());
                            if (serverWorld != null && ChunkRandom.getSlimeRandom(new ChunkPos(mc.player.getBlockPos()).x, new ChunkPos(mc.player.getBlockPos()).z, serverWorld.getSeed(), 987234911L).nextInt(10) == 0) {
                                x -= mc.textRenderer.getWidth(Text.translatable("info.slime_chunk"));
                                drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.slime_chunk"), x, y, Colors.WHITE);
                            } else {
                                y -= 10;
                            }
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Speed":
                        Vec3d playerPosVec = mc.player.getPos();
                        double travelledX = playerPosVec.x - mc.player.prevX;
                        double travelledZ = playerPosVec.z - mc.player.prevZ;
                        x -= mc.textRenderer.getWidth(String.format("%.3f BPS", MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ)) * 20));
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.3f m/s", MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ)) * 20), x, y, Colors.WHITE);
                        break;
                    case "Sprint Indicator":
                        if (mc.player.isSprinting()) {
                            x -= mc.textRenderer.getWidth(Text.translatable("info.sprint_indicator"));
                            drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.sprint_indicator"), x, y, Color.ORANGE.getRGB());
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Targeted Block/Entity":
                        String target = Blocks.AIR.getName().getString();
                        if (mc.crosshairTarget != null) {
                            if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                target = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock().getName().getString();
                            } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                                List<Entity> entities = mc.world.getOtherEntities(mc.player, Box.from(mc.crosshairTarget.getPos()).expand(1));
                                target = entities.isEmpty() ? Blocks.AIR.getName().getString() : entities.getFirst().getName().getString();
                            }
                        }
                        x -= mc.textRenderer.getWidth(Text.translatable("info.crosshair_target").getString() + target);
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.crosshair_target").getString() + target, x, y, Colors.WHITE);
                        break;
                    case "Time (Game)":
                        x -= mc.textRenderer.getWidth(Text.translatable("info.in_game_time").getString() + (mc.world.getTimeOfDay() % 24000L));
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.in_game_time").getString() + (mc.world.getTimeOfDay() % 24000L), x, y, Colors.WHITE);
                        break;
                    case "Time (Real)":
                        x -= mc.textRenderer.getWidth(Text.translatable("info.local_time").getString() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("info.local_time").getString() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")), x, y, Colors.WHITE);
                        break;
                    default:
                        drawContext.drawTextWithShadow(mc.textRenderer, "", x, y, Colors.WHITE);
                }
                y += 10;
            }
        }
    }
}