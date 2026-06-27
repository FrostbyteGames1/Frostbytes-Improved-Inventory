package net.frostbyte.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

@Environment(EnvType.CLIENT)
public class Zoom {
    public static int standardFOV = 0;
    public static int scrollAmount = 0;
    public static KeyMapping zoomKey;

    public void setKeyMappings() {
        KeyMappingHelper.registerKeyMapping(zoomKey = new KeyMapping("key.zoom", InputConstants.Type.KEYSYM, InputConstants.KEY_C, ImprovedInventory.KEYBIND_CATEGORY));
    }

    public static void zoomHandler(Minecraft client) {
        if (standardFOV == 0 && !zoomKey.isDown()) {
            standardFOV = Math.max(client.options.fov().get(), 30);
        }
        if (client.player == null) {
            return;
        }
        if (client.screen == null) {
            if (zoomKey.isDown()) {
                if (client.options.fov().get() != ImprovedInventoryConfig.zoomFOV - scrollAmount) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
                    }
                    client.options.fov().set(ImprovedInventoryConfig.zoomFOV - scrollAmount);
                }
            } else {
                if (client.options.fov().get() == ImprovedInventoryConfig.zoomFOV - scrollAmount) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    }
                    client.options.fov().set(standardFOV);
                    scrollAmount = 0;
                } else if (client.options.fov().get() != standardFOV) {
                    standardFOV = client.options.fov().get();
                }
            }
        } else {
            if (client.screen.isPauseScreen()) {
                if (client.options.fov().get() == ImprovedInventoryConfig.zoomFOV - scrollAmount) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    }
                    client.options.fov().set(standardFOV);
                    scrollAmount = 0;
                } else if (client.options.fov().get() != standardFOV) {
                    standardFOV = client.options.fov().get();
                }
            } else {
                if (client.options.fov().get() != standardFOV) {
                    if (ImprovedInventoryConfig.zoomSound) {
                        client.player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
                    }
                    client.options.fov().set(standardFOV);
                    scrollAmount = 0;
                }
            }
        }
    }
}
