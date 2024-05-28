package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

@Environment(EnvType.CLIENT)
public class Paperdoll implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        if (!mc.player.isSpectator() && ImprovedInventoryConfig.paperdoll && !mc.options.hudHidden && mc.currentScreen == null && !mc.inGameHud.getDebugHud().shouldShowDebugHud()) {
            int y = 0;
            drawEntity(drawContext, 0, y, 64, y + 64, 20, 0.0625F, 64, y + 20, mc.player);
        }
    }

}
