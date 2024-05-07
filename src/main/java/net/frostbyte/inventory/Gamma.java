package net.frostbyte.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Gamma implements ClientTickEvents.EndTick {

    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private double gamma;
    private boolean enabled;
    public KeyBinding gammaKey;
    MinecraftClient mc;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(gammaKey = new KeyBinding("Toggle Gamma", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_G, "Improved Inventory"));
    }
    @Override
    public void onEndTick(MinecraftClient client) {
        mc = client;

        if (mc.player == null) {
            return;
        }

        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("gamma"))
                gamma = json.getAsJsonPrimitive("gamma").getAsDouble() / 100;
        } catch (IOException e) {
            ImprovedInventory.LOGGER.error(e.getMessage());
        }

        if (gammaKey.wasPressed()) {
            enabled = !enabled;
            if (enabled) {
                client.inGameHud.setOverlayMessage(Text.of("Gamma set to " + gamma).getWithStyle(Style.EMPTY.withFormatting(Formatting.GOLD)).getFirst(), false);
            } else {
                client.inGameHud.setOverlayMessage(Text.of("Gamma disabled").getWithStyle(Style.EMPTY.withFormatting(Formatting.GOLD)).getFirst(), false);
            }
        }

        if (enabled) {
            if (mc.options.getGamma().getValue() != gamma) {
                mc.options.getGamma().setValue(gamma);
            }
        } else {
            if (mc.options.getGamma().getValue() > 1.0) {
                mc.options.getGamma().setValue(1.0);
            }
            if (mc.options.getGamma().getValue() < 0.0) {
                mc.options.getGamma().setValue(0.0);
            }
        }
    }
}
