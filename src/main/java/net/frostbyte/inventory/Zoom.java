package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class Zoom implements ClientTickEvents.EndTick {
    public static int standardFOV = 0;
    public static int scrollAmount = 0;
    public static KeyBinding zoomKey;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(zoomKey = new KeyBinding("Zoom (CTRL+Scroll to Adjust)", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_C, "Improved Inventory"));
    }
    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        if (!zoomKey.isPressed() && standardFOV == 0) {
            standardFOV = client.options.getFov().getValue();
        }

        if (client.currentScreen == null) {
            if (zoomKey.isPressed()) {
                if (client.options.getFov().getValue() != ImprovedInventoryConfig.zoomFOV - scrollAmount) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0F, 1.0F);
                    }
                    client.options.getFov().setValue(ImprovedInventoryConfig.zoomFOV - scrollAmount);
                }
            } else {
                if (client.options.getFov().getValue() != standardFOV) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    }
                    client.options.getFov().setValue(standardFOV);
                    scrollAmount = 0;
                }
            }
        } else {
            if (client.currentScreen.shouldPause()) {
                if (client.options.getFov().getValue() == ImprovedInventoryConfig.zoomFOV - scrollAmount) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    }
                    client.options.getFov().setValue(standardFOV);
                    scrollAmount = 0;
                } else if (client.options.getFov().getValue() != standardFOV) {
                    standardFOV = client.options.getFov().getValue();
                }
            } else {
                if (client.options.getFov().getValue() != standardFOV) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    }
                    client.options.getFov().setValue(standardFOV);
                    scrollAmount = 0;
                }
            }
        }
    }
}
