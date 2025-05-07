package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.LightType;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class TextDisplayer implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && !mc.player.isSpectator() && mc.world != null && ImprovedInventoryConfig.textDisplay && !mc.options.hudHidden && mc.currentScreen == null && !mc.inGameHud.getDebugHud().shouldShowDebugHud()) {
            int x = 1 + ImprovedInventoryConfig.textDisplayOffsetX;
            int y = 1 + ImprovedInventoryConfig.textDisplayOffsetY;
            for (String line : ImprovedInventoryConfig.textDisplayLeft) {
                switch (line) {
                    case "Biome":
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("biome." + mc.world.getBiome(mc.player.getBlockPos()).getIdAsString().replace(':', '.')), x, y, Colors.WHITE);
                        break;
                    case "Block Light":
                        drawContext.drawTextWithShadow(mc.textRenderer, "Block Light: " + mc.world.getLightLevel(LightType.BLOCK, mc.player.getBlockPos()), x, y, Colors.WHITE);
                        break;
                    case "Coordinates":
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("XYZ: %.3f / %.3f / %.3f", mc.player.getPos().getX(), mc.player.getPos().getY(), mc.player.getPos().getZ()), x, y, Colors.WHITE);
                        break;
                    case "Day":
                        drawContext.drawTextWithShadow(mc.textRenderer, "Day " + (mc.world.getTimeOfDay() / 24000L % Integer.MAX_VALUE), x, y, Colors.WHITE);
                        break;
                    case "Dimension":
                        drawContext.drawTextWithShadow(mc.textRenderer, mc.world.getDimensionEntry().getIdAsString(), x, y, Colors.WHITE);
                        break;
                    case "Entity Count":
                        drawContext.drawTextWithShadow(mc.textRenderer, "Entity Count: " + mc.world.getRegularEntityCount(), x, y, Colors.WHITE);
                        break;
                    case "Facing Direction":
                        drawContext.drawTextWithShadow(mc.textRenderer, "Facing: " + mc.player.getHorizontalFacing().asString(), x, y, Colors.WHITE);
                        break;
                    case "FPS":
                        drawContext.drawTextWithShadow(mc.textRenderer, mc.getCurrentFps() + " FPS", x, y, Colors.WHITE);
                        break;
                    case "Local Difficulty":
                        drawContext.drawTextWithShadow(mc.textRenderer, "Local Difficulty: " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty(), x, y, Colors.WHITE);
                        break;
                    case "Memory Usage":
                        drawContext.drawTextWithShadow(mc.textRenderer, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "/" + (Runtime.getRuntime().maxMemory() / 1048576) + " MB", x, y, Colors.WHITE);
                        break;
                    case "Slime Chunk":
                        if (mc.isIntegratedServerRunning()) {
                            ServerWorld serverWorld = Objects.requireNonNull(mc.getServer()).getWorld(mc.world.getRegistryKey());
                            if (serverWorld != null && ChunkRandom.getSlimeRandom(new ChunkPos(mc.player.getBlockPos()).x, new ChunkPos(mc.player.getBlockPos()).z, serverWorld.getSeed(), 987234911L).nextInt(10) == 0) {
                                drawContext.drawTextWithShadow(mc.textRenderer, "Slime Chunk", x, y, Colors.WHITE);
                            } else {
                                y -= 10;
                            }
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Speed":
                        Vec3d playerPosVec = mc.player.getPos();
                        double travelledX = playerPosVec.x - mc.player.lastX;
                        double travelledZ = playerPosVec.z - mc.player.lastZ;
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.3f m/s", MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ)) * 20), x, y, Colors.WHITE);
                        break;
                    case "Sprint Indicator":
                        if (mc.player.isSprinting()) {
                            drawContext.drawTextWithShadow(mc.textRenderer, "Sprinting", x, y, Color.ORANGE.getRGB());
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Targeted Block/Entity":
                        String target = "Air";
                        if (mc.crosshairTarget != null) {
                            if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                target = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock().getName().getString();
                            } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                                List<Entity> entities = mc.world.getOtherEntities(mc.player, Box.from(mc.crosshairTarget.getPos()).expand(1));
                                target = entities.isEmpty() ? "Air" : entities.getFirst().getName().getString();
                            }
                        }
                        drawContext.drawTextWithShadow(mc.textRenderer, "Targeted: " + target, x, y, Colors.WHITE);
                        break;
                    case "Time (Game)":
                        drawContext.drawTextWithShadow(mc.textRenderer, "Game Time: " + (mc.world.getTimeOfDay() % 24000L), x, y, Colors.WHITE);
                        break;
                    case "Time (Real)":
                        drawContext.drawTextWithShadow(mc.textRenderer, "Real Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")), x, y, Colors.WHITE);
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
                        x -= mc.textRenderer.getWidth(Text.translatable("biome." + mc.world.getBiome(mc.player.getBlockPos()).getIdAsString().replace(':', '.')));
                        drawContext.drawTextWithShadow(mc.textRenderer, Text.translatable("biome." + mc.world.getBiome(mc.player.getBlockPos()).getIdAsString().replace(':', '.')), x, y, Colors.WHITE);
                        break;
                    case "Block Light":
                        x -= mc.textRenderer.getWidth("Block Light: " + mc.world.getLightLevel(LightType.BLOCK, mc.player.getBlockPos()));
                        drawContext.drawTextWithShadow(mc.textRenderer, "Block Light: " + mc.world.getLightLevel(LightType.BLOCK, mc.player.getBlockPos()), x, y, Colors.WHITE);
                        break;
                    case "Coordinates":
                        x -= mc.textRenderer.getWidth(String.format("XYZ: %.3f / %.3f / %.3f", mc.player.getPos().getX(), mc.player.getPos().getY(), mc.player.getPos().getZ()));
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("XYZ: %.3f / %.3f / %.3f", mc.player.getPos().getX(), mc.player.getPos().getY(), mc.player.getPos().getZ()), x, y, Colors.WHITE);
                        break;
                    case "Day":
                        x -= mc.textRenderer.getWidth("Day " + (mc.world.getTimeOfDay() / 24000L % Integer.MAX_VALUE));
                        drawContext.drawTextWithShadow(mc.textRenderer, "Day " + (mc.world.getTimeOfDay() / 24000L % Integer.MAX_VALUE), x, y, Colors.WHITE);
                        break;
                    case "Dimension":
                        x -= mc.textRenderer.getWidth(mc.world.getDimensionEntry().getIdAsString());
                        drawContext.drawTextWithShadow(mc.textRenderer, mc.world.getDimensionEntry().getIdAsString(), x, y, Colors.WHITE);
                        break;
                    case "Entity Count":
                        x -= mc.textRenderer.getWidth("Entity Count: " + mc.world.getRegularEntityCount());
                        drawContext.drawTextWithShadow(mc.textRenderer, "Entity Count: " + mc.world.getRegularEntityCount(), x, y, Colors.WHITE);
                        break;
                    case "Facing Direction":
                        x -= mc.textRenderer.getWidth("Facing: " + mc.player.getHorizontalFacing().asString());
                        drawContext.drawTextWithShadow(mc.textRenderer, "Facing: " + mc.player.getHorizontalFacing().asString(), x, y, Colors.WHITE);
                        break;
                    case "FPS":
                        x -= mc.textRenderer.getWidth(mc.getCurrentFps() + " FPS");
                        drawContext.drawTextWithShadow(mc.textRenderer, mc.getCurrentFps() + " FPS", x, y, Colors.WHITE);
                        break;
                    case "Local Difficulty":
                        x -= mc.textRenderer.getWidth("Local Difficulty: " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty());
                        drawContext.drawTextWithShadow(mc.textRenderer, "Local Difficulty: " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty(), x, y, Colors.WHITE);
                        break;
                    case "Memory Usage":
                        x -= mc.textRenderer.getWidth(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "/" + (Runtime.getRuntime().maxMemory() / 1048576) + " MB");
                        drawContext.drawTextWithShadow(mc.textRenderer, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "/" + (Runtime.getRuntime().maxMemory() / 1048576) + " MB", x, y, Colors.WHITE);
                        break;
                    case "Slime Chunk":
                        if (mc.isIntegratedServerRunning()) {
                            ServerWorld serverWorld = Objects.requireNonNull(mc.getServer()).getWorld(mc.world.getRegistryKey());
                            if (serverWorld != null && ChunkRandom.getSlimeRandom(new ChunkPos(mc.player.getBlockPos()).x, new ChunkPos(mc.player.getBlockPos()).z, serverWorld.getSeed(), 987234911L).nextInt(10) == 0) {
                                x -= mc.textRenderer.getWidth("Slime Chunk");
                                drawContext.drawTextWithShadow(mc.textRenderer, "Slime Chunk", x, y, Colors.WHITE);
                            } else {
                                y -= 10;
                            }
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Speed":
                        Vec3d playerPosVec = mc.player.getPos();
                        double travelledX = playerPosVec.x - mc.player.lastX;
                        double travelledZ = playerPosVec.z - mc.player.lastZ;
                        x -= mc.textRenderer.getWidth(String.format("%.3f BPS", MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ)) * 20));
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.3f m/s", MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ)) * 20), x, y, Colors.WHITE);
                        break;
                    case "Sprint Indicator":
                        if (mc.player.isSprinting()) {
                            x -= mc.textRenderer.getWidth("Sprinting");
                            drawContext.drawTextWithShadow(mc.textRenderer, "Sprinting", x, y, Color.ORANGE.getRGB());
                        } else {
                            y -= 10;
                        }
                        break;
                    case "Targeted Block/Entity":
                        String target = "Air";
                        if (mc.crosshairTarget != null) {
                            if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                target = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock().getName().getString();
                            } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                                List<Entity> entities = mc.world.getOtherEntities(mc.player, Box.from(mc.crosshairTarget.getPos()).expand(1));
                                target = entities.isEmpty() ? "Air" : entities.getFirst().getName().getString();
                            }
                        }
                        x -= mc.textRenderer.getWidth("Targeted: " + target);
                        drawContext.drawTextWithShadow(mc.textRenderer, "Targeted: " + target, x, y, Colors.WHITE);
                        break;
                    case "Time (Game)":
                        x -= mc.textRenderer.getWidth("Game Time: " + (mc.world.getTimeOfDay() % 24000L));
                        drawContext.drawTextWithShadow(mc.textRenderer, "Game Time: " + (mc.world.getTimeOfDay() % 24000L), x, y, Colors.WHITE);
                        break;
                    case "Time (Real)":
                        x -= mc.textRenderer.getWidth("Real Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
                        drawContext.drawTextWithShadow(mc.textRenderer, "Real Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")), x, y, Colors.WHITE);
                        break;
                    default:
                        drawContext.drawTextWithShadow(mc.textRenderer, "", x, y, Colors.WHITE);
                }
                y += 10;
            }
        }
    }
}