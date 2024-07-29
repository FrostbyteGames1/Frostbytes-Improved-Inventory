package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class Gamma implements ClientTickEvents.EndTick {
    double standardBrightness = 1;
    private boolean enabled;
    public static KeyBinding gammaKey;
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

        if (mc.currentScreen != null && mc.currentScreen.shouldPause() && !enabled) {
            standardBrightness = mc.options.getGamma().getValue();
        }

        if (gammaKey.wasPressed()) {
            enabled = !enabled;
            if (enabled) {
                standardBrightness = mc.options.getGamma().getValue();
                mc.options.getGamma().setValue((double) ImprovedInventoryConfig.gamma);
                client.inGameHud.setOverlayMessage(Text.of("Gamma set to " + ImprovedInventoryConfig.gamma + "%").getWithStyle(Style.EMPTY.withFormatting(Formatting.GREEN)).getFirst(), false);
            } else {
                mc.options.getGamma().setValue(standardBrightness);
                client.inGameHud.setOverlayMessage(Text.of("Gamma set to " + ((int) (standardBrightness * 100)) + "%").getWithStyle(Style.EMPTY.withFormatting(Formatting.RED)).getFirst(), false);
            }
        }

        if (client.world == null) {
            mc.options.getGamma().setValue(standardBrightness);
        }
    }
}
