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
    public void onHudRender(DrawContext drawContext, float tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        if (!mc.player.isSpectator() && ImprovedInventoryConfig.paperdoll && !mc.options.hudHidden && mc.currentScreen == null && !mc.options.debugEnabled) {
            if (ImprovedInventoryConfig.paperdollVerticalAnchor) {
                if (ImprovedInventoryConfig.paperdollHorizontalAnchor) {
                    drawEntity(
                        drawContext,
                        16 + ImprovedInventoryConfig.paperdollOffsetX,
                        48 + ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        -64,
                        0,
                        mc.player);
                } else {
                    drawEntity(
                        drawContext,
                        -16 + mc.getWindow().getScaledWidth() - ImprovedInventoryConfig.paperdollOffsetX,
                        48 + ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        64,
                        0,
                        mc.player);
                }
            } else {
                if (ImprovedInventoryConfig.paperdollHorizontalAnchor) {
                    drawEntity(
                        drawContext,
                        16 + ImprovedInventoryConfig.paperdollOffsetX,
                        -8 + mc.getWindow().getScaledHeight() - ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        -64,
                        0,
                        mc.player);
                } else {
                    drawEntity(
                        drawContext,
                        -16 + mc.getWindow().getScaledWidth() - ImprovedInventoryConfig.paperdollOffsetX,
                        -8 + mc.getWindow().getScaledHeight() - ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        64,
                        0,
                        mc.player);
                }
            }
        }
    }

}
