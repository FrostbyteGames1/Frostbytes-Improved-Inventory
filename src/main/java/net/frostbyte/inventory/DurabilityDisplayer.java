package net.frostbyte.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DurabilityDisplayer implements ClientTickEvents.EndTick, HudRenderCallback {

    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    boolean duraDisplay = true;
    MinecraftClient mc;
    Identifier duraSlot = new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png");

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;

        if (player == null) {
            return;
        }

        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("duraDisplay"))
                duraDisplay = json.getAsJsonPrimitive("duraDisplay").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        assert mc.player != null;
        if (!mc.player.isSpectator() && duraDisplay && !mc.options.hudHidden) {
            int x = mc.getWindow().getScaledWidth();
            int y = mc.getWindow().getScaledHeight();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, duraSlot);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 91,0,0,22,22, 22,22);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 68,0,0,22,22, 22,22);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 45,0,0,22,22, 22,22);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 22,0,0,22,22, 22,22);
            if (!mc.player.getInventory().getStack(39).isEmpty()) {
                drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 88);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(39), x - 20, y - 88);
            }
            if (!mc.player.getInventory().getStack(38).isEmpty()) {
                drawContext.drawItem(mc.player.getInventory().getStack(38), x - 19, y - 65);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(38), x - 20, y - 65);
            }
            if (!mc.player.getInventory().getStack(37).isEmpty()) {
                drawContext.drawItem(mc.player.getInventory().getStack(37), x - 19, y - 42);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(37), x - 20, y - 42);
            }
            if (!mc.player.getInventory().getStack(36).isEmpty()) {
                drawContext.drawItem(mc.player.getInventory().getStack(36), x - 19, y - 19);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(36), x - 20, y - 19);
            }
        }
    }
}
