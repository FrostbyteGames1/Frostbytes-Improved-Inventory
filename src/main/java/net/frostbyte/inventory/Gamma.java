package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class Gamma {
    static double standardBrightness = 1;
    public static boolean enabled;
    public static KeyBinding gammaKey;
    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(gammaKey = new KeyBinding("key.toggle_gamma", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_G, ImprovedInventory.KEYBIND_CATEGORY));
    }

    public static void gammaHandler(MinecraftClient mc) {
        if (mc.currentScreen != null && mc.currentScreen.shouldPause() && !enabled) {
            standardBrightness = Math.min(mc.options.getGamma().getValue(), 100);
        }

        if (gammaKey.wasPressed()) {
            enabled = !enabled;
            if (enabled) {
                standardBrightness = mc.options.getGamma().getValue();
                mc.options.getGamma().setValue((double) ImprovedInventoryConfig.gamma);
                mc.inGameHud.setOverlayMessage(Text.of(Text.translatable("info.gamma_changed").getString() + ImprovedInventoryConfig.gamma + "%").getWithStyle(Style.EMPTY.withFormatting(Formatting.GREEN)).getFirst(), false);
            } else {
                mc.options.getGamma().setValue(standardBrightness);
                mc.inGameHud.setOverlayMessage(Text.of(Text.translatable("info.gamma_changed").getString() + (int) (standardBrightness * 100) + "%").getWithStyle(Style.EMPTY.withFormatting(Formatting.RED)).getFirst(), false);
            }
        }

        if (mc.world == null) {
            enabled = false;
            mc.options.getGamma().setValue(Math.min(standardBrightness, 100));
        }
    }
}
