package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class DurabilityDisplayer implements HudElement {
    Minecraft mc = Minecraft.getInstance();
    Identifier duraSlot = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "textures/dura_slot.png");
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        assert mc.player != null;
        if (!mc.player.isSpectator() && ImprovedInventoryConfig.duraDisplay && !mc.gui.hud.isHidden()) {
            int x = mc.getWindow().getGuiScaledWidth() - ImprovedInventoryConfig.duraDisplayOffsetX;
            if (ImprovedInventoryConfig.duraDisplayHorizontalAnchor) { // LEFT
                x = 22 + ImprovedInventoryConfig.duraDisplayOffsetX;
            } else if (ImprovedInventoryConfig.duraDisplayOrientation) { // RIGHT & HORIZONTAL
                x -= 23 * 3;
            }
            int y = mc.getWindow().getGuiScaledHeight() - ImprovedInventoryConfig.duraDisplayOffsetY;
            if (ImprovedInventoryConfig.duraDisplayVerticalAnchor) { // TOP
                y = 22 - ImprovedInventoryConfig.duraDisplayOffsetY;
                if (!mc.player.getInventory().getItem(39).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(39), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(39), x - 20, y - 19);
                    if (ImprovedInventoryConfig.duraDisplayOrientation) { // HORIZONTAL
                        x += 23;
                    } else { // VERTICAL
                        y += 23;
                    }
                }
                if (!mc.player.getInventory().getItem(38).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(38), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(38), x - 20, y - 19);
                    if (ImprovedInventoryConfig.duraDisplayOrientation) { // HORIZONTAL
                        x += 23;
                    } else { // VERTICAL
                        y += 23;
                    }
                }
                if (!mc.player.getInventory().getItem(37).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(37), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(37), x - 20, y - 19);
                    if (ImprovedInventoryConfig.duraDisplayOrientation) { // HORIZONTAL
                        x += 23;
                    } else { // VERTICAL
                        y += 23;
                    }
                }
                if (!mc.player.getInventory().getItem(36).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(36), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(36), x - 20, y - 19);
                }
            } else { // BOTTOM
                if (ImprovedInventoryConfig.duraDisplayOrientation) { // HORIZONTAL
                    x += 23 * 3;
                }
                if (!mc.player.getInventory().getItem(36).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(36), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(36), x - 20, y - 19);
                    if (ImprovedInventoryConfig.duraDisplayOrientation) { // HORIZONTAL
                        x -= 23;
                    } else { // VERTICAL
                        y -= 23;
                    }
                }
                if (!mc.player.getInventory().getItem(37).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(37), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(37), x - 20, y - 19);
                    if (ImprovedInventoryConfig.duraDisplayOrientation) { // HORIZONTAL
                        x -= 23;
                    } else { // VERTICAL
                        y -= 23;
                    }
                }
                if (!mc.player.getInventory().getItem(38).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(38), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(38), x - 20, y - 19);
                    if (ImprovedInventoryConfig.duraDisplayOrientation) { // HORIZONTAL
                        x -= 23;
                    } else { // VERTICAL
                        y -= 23;
                    }
                }
                if (!mc.player.getInventory().getItem(39).isEmpty()) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    graphics.item(mc.player.getInventory().getItem(39), x - 19, y - 19);
                    graphics.itemDecorations(mc.font, mc.player.getInventory().getItem(39), x - 20, y - 19);
                }
            }
        }
    }
}
