package net.frostbyte.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class Gamma {
    static double standardBrightness = 1;
    public static boolean enabled;
    public static KeyMapping gammaKey;
    public void setKeyMappings() {
        KeyMappingHelper.registerKeyMapping(gammaKey = new KeyMapping("key.toggle_gamma", InputConstants.Type.KEYSYM, InputConstants.KEY_G, ImprovedInventory.KEYBIND_CATEGORY));
    }

    public static void gammaHandler(Minecraft client) {
        if (client.screen != null && client.screen.isPauseScreen() && !enabled) {
            standardBrightness = Math.min(client.options.gamma().get(), 100);
        }

        if (gammaKey.consumeClick()) {
            enabled = !enabled;
            if (enabled) {
                standardBrightness = client.options.gamma().get();
                client.options.gamma().set((double) ImprovedInventoryConfig.gamma);
                Component message = Component.translatable("info.gamma_changed").append(ImprovedInventoryConfig.gamma + "%");
                message.getStyle().applyFormat(ChatFormatting.GREEN);
                client.gui.setOverlayMessage(message, false);
            } else {
                client.options.gamma().set(standardBrightness);
                Component message = Component.translatable("info.gamma_changed").append((int) (standardBrightness * 100) + "%");
                message.getStyle().applyFormat(ChatFormatting.RED);
                client.gui.setOverlayMessage(message, false);
            }
        }

        if (client.level == null) {
            enabled = false;
            client.options.gamma().set(Math.min(standardBrightness, 100));
        }
    }
}
