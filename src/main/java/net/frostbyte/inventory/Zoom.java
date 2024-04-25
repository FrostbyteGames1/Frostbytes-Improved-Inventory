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
import net.minecraft.sound.SoundEvents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Zoom implements ClientTickEvents.EndTick {
    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    int standardFOV = 90;
    int zoomFOV = 30;
    public KeyBinding zoomKey;

    MinecraftClient mc;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(zoomKey = new KeyBinding("Zoom", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_C, "Improved Inventory"));
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
            if (json.has("zoomFOV"))
                zoomFOV = json.getAsJsonPrimitive("zoomFOV").getAsInt();
        } catch (IOException e) {
            ImprovedInventory.LOGGER.error(e.getMessage());
        }

        if (mc.currentScreen == null) {
            if (zoomKey.isPressed()) {
                if (mc.options.getFov().getValue() == standardFOV) {
                    mc.player.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0F, 1.0F);
                }
                mc.options.getFov().setValue(zoomFOV);
            } else {
                if (mc.options.getFov().getValue() != standardFOV) {
                    mc.player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    mc.options.getFov().setValue(standardFOV);
                }
            }
        } else {
            if (mc.currentScreen.shouldPause()) {
                if (mc.options.getFov().getValue() == zoomFOV) {
                    mc.player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    mc.options.getFov().setValue(standardFOV);
                } else if (mc.options.getFov().getValue() != standardFOV) {
                    standardFOV = mc.options.getFov().getValue();
                }
            } else {
                mc.player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
                mc.options.getFov().setValue(standardFOV);
            }
        }
    }
}
