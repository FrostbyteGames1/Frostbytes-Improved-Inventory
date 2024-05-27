package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.util.Tuple;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.awt.*;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class GUITextRenderer implements HudRenderCallback, ClientTickEvents.EndTick {
    MinecraftClient mc;
    ClientPlayerEntity player;
    public static ArrayList<Tuple<String, Integer>> lines = new ArrayList<>();
    private void addInfoLine(String text, int color) {
        lines.add(new Tuple<>(text, color));
    }
    @Override
    public void onEndTick(MinecraftClient client) {
        mc = client;
        player = client.player;
        if (player == null) {
            return;
        }

        lines.clear();
        addInfoLine(client.getCurrentFps() + " FPS", Color.WHITE.getRGB());
        addInfoLine(player.getBlockPos().toShortString(), Color.WHITE.getRGB());
        if (mc.options.sprintKey.isPressed() || player.isSprinting()) {
            addInfoLine("Sprinting", new Color(255, 170, 0).getRGB());
        }
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (mc.options.hudHidden || mc.getDebugHud().shouldShowDebugHud()) {
            return;
        }
        for (Tuple<String, Integer> pair : lines) {
            drawContext.drawTextWithShadow(mc.textRenderer, pair.x, 4, 4 + 10 * lines.indexOf(pair), pair.y);
        }
    }
}
