package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

@Environment(EnvType.CLIENT)
public class Paperdoll implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        if (!mc.player.isSpectator() && ImprovedInventoryConfig.paperdoll && !mc.options.hudHidden && mc.currentScreen == null && !mc.inGameHud.getDebugHud().shouldShowDebugHud()) {
            if (ImprovedInventoryConfig.paperdollVerticalAnchor) {
                if (ImprovedInventoryConfig.paperdollHorizontalAnchor) {
                    drawEntity(
                        drawContext,
                        -16 + ImprovedInventoryConfig.paperdollOffsetX,
                        ImprovedInventoryConfig.paperdollOffsetY,
                        48 + ImprovedInventoryConfig.paperdollOffsetX,
                        64 + ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        64 + ImprovedInventoryConfig.paperdollOffsetX,
                        20 + ImprovedInventoryConfig.paperdollOffsetY,
                        mc.player);
                } else {
                    drawEntity(
                        drawContext,
                        mc.getWindow().getScaledWidth() - 48 - ImprovedInventoryConfig.paperdollOffsetX,
                        ImprovedInventoryConfig.paperdollOffsetY,
                        mc.getWindow().getScaledWidth() + 16 - ImprovedInventoryConfig.paperdollOffsetX,
                        64 + ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        mc.getWindow().getScaledWidth() - 64 - ImprovedInventoryConfig.paperdollOffsetX,
                        20 + ImprovedInventoryConfig.paperdollOffsetY,
                        mc.player);
                }
            } else {
                if (ImprovedInventoryConfig.paperdollHorizontalAnchor) {
                    drawEntity(
                        drawContext,
                        -16 + ImprovedInventoryConfig.paperdollOffsetX,
                        mc.getWindow().getScaledHeight() - 64 - ImprovedInventoryConfig.paperdollOffsetY,
                        48 + ImprovedInventoryConfig.paperdollOffsetX,
                        mc.getWindow().getScaledHeight() - ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        64 + ImprovedInventoryConfig.paperdollOffsetX,
                        mc.getWindow().getScaledHeight() - 44 + ImprovedInventoryConfig.paperdollOffsetY,
                        mc.player);
                } else {
                    drawEntity(
                        drawContext,
                        mc.getWindow().getScaledWidth() - 48 - ImprovedInventoryConfig.paperdollOffsetX,
                        mc.getWindow().getScaledHeight() - 64 - ImprovedInventoryConfig.paperdollOffsetY,
                        mc.getWindow().getScaledWidth() + 16 - ImprovedInventoryConfig.paperdollOffsetX,
                        mc.getWindow().getScaledHeight() - ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        mc.getWindow().getScaledWidth() - 64 - ImprovedInventoryConfig.paperdollOffsetX,
                        mc.getWindow().getScaledHeight() - 44 + ImprovedInventoryConfig.paperdollOffsetY,
                        mc.player);
                }
            }
        }
    }

}
