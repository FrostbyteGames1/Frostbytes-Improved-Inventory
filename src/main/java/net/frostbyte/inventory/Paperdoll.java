package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.extractEntityInInventoryFollowsMouse;

@Environment(EnvType.CLIENT)
public class Paperdoll implements HudElement {
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        assert client.player != null;
        if (!client.player.isSpectator() && ImprovedInventoryConfig.paperdoll && !client.options.hideGui && client.screen == null) {
            if (ImprovedInventoryConfig.paperdollVerticalAnchor) {
                if (ImprovedInventoryConfig.paperdollHorizontalAnchor) {
                    extractEntityInInventoryFollowsMouse(
                        graphics,
                        -16 + ImprovedInventoryConfig.paperdollOffsetX,
                        ImprovedInventoryConfig.paperdollOffsetY,
                        48 + ImprovedInventoryConfig.paperdollOffsetX,
                        64 + ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        64 + ImprovedInventoryConfig.paperdollOffsetX,
                        20 + ImprovedInventoryConfig.paperdollOffsetY,
                        client.player);
                } else {
                    extractEntityInInventoryFollowsMouse(
                        graphics,
                        client.getWindow().getGuiScaledWidth() - 48 - ImprovedInventoryConfig.paperdollOffsetX,
                        ImprovedInventoryConfig.paperdollOffsetY,
                        client.getWindow().getGuiScaledWidth() + 16 - ImprovedInventoryConfig.paperdollOffsetX,
                        64 + ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        client.getWindow().getGuiScaledWidth() - 64 - ImprovedInventoryConfig.paperdollOffsetX,
                        20 + ImprovedInventoryConfig.paperdollOffsetY,
                        client.player);
                }
            } else {
                if (ImprovedInventoryConfig.paperdollHorizontalAnchor) {
                    extractEntityInInventoryFollowsMouse(
                        graphics,
                        -16 + ImprovedInventoryConfig.paperdollOffsetX,
                        client.getWindow().getGuiScaledHeight() - 64 - ImprovedInventoryConfig.paperdollOffsetY,
                        48 + ImprovedInventoryConfig.paperdollOffsetX,
                        client.getWindow().getGuiScaledHeight() - ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        64 + ImprovedInventoryConfig.paperdollOffsetX,
                        client.getWindow().getGuiScaledHeight() - 44 + ImprovedInventoryConfig.paperdollOffsetY,
                        client.player);
                } else {
                    extractEntityInInventoryFollowsMouse(
                        graphics,
                        client.getWindow().getGuiScaledWidth() - 48 - ImprovedInventoryConfig.paperdollOffsetX,
                        client.getWindow().getGuiScaledHeight() - 64 - ImprovedInventoryConfig.paperdollOffsetY,
                        client.getWindow().getGuiScaledWidth() + 16 - ImprovedInventoryConfig.paperdollOffsetX,
                        client.getWindow().getGuiScaledHeight() - ImprovedInventoryConfig.paperdollOffsetY,
                        20,
                        0.0625F,
                        client.getWindow().getGuiScaledWidth() - 64 - ImprovedInventoryConfig.paperdollOffsetX,
                        client.getWindow().getGuiScaledHeight() - 44 + ImprovedInventoryConfig.paperdollOffsetY,
                        client.player);
                }
            }
        }
    }

}
