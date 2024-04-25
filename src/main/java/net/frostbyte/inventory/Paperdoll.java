package net.frostbyte.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public class Paperdoll implements HudRenderCallback {
    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    boolean paperdoll = true;
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("paperdoll"))
                paperdoll = json.getAsJsonPrimitive("paperdoll").getAsBoolean();
        } catch (IOException e) {
            ImprovedInventory.LOGGER.error(e.getMessage());
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        if (!mc.player.isSpectator() && paperdoll && !mc.options.hudHidden && mc.currentScreen == null && !mc.inGameHud.getDebugHud().shouldShowDebugHud()) {
            drawEntity(drawContext, 0, 0, 64, 64, 20, 0.0625F, 64, 20, mc.player);
        }
    }

}
