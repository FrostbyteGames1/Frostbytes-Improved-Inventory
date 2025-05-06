package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class DurabilityDisplayer implements HudRenderCallback {
    MinecraftClient mc = MinecraftClient.getInstance();
    Identifier duraSlot = Identifier.of(ImprovedInventory.MOD_ID, "textures/dura_slot.png");
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        assert mc.player != null;
        if (!mc.player.isSpectator() && ImprovedInventoryConfig.duraDisplay && !mc.options.hudHidden) {
            int x = mc.getWindow().getScaledWidth() - ImprovedInventoryConfig.duraDisplayOffsetX;
            if (ImprovedInventoryConfig.duraDisplayHorizontalAnchor) {
                x = 22 + ImprovedInventoryConfig.duraDisplayOffsetX;
            }
            int y = mc.getWindow().getScaledHeight() - ImprovedInventoryConfig.duraDisplayOffsetY;
            if (ImprovedInventoryConfig.duraDisplayVerticalAnchor) {
                y = 22 - ImprovedInventoryConfig.duraDisplayOffsetY;
                if (!mc.player.getInventory().getStack(39).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(39), x - 20, y - 19);
                    y += 23;
                }
                if (!mc.player.getInventory().getStack(38).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(38), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(38), x - 20, y - 19);
                    y += 23;
                }
                if (!mc.player.getInventory().getStack(37).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(37), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(37), x - 20, y - 19);
                    y += 23;
                }
                if (!mc.player.getInventory().getStack(36).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(36), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(36), x - 20, y - 19);
                }
            } else {
                if (!mc.player.getInventory().getStack(36).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(36), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(36), x - 20, y - 19);
                    y -= 23;
                }
                if (!mc.player.getInventory().getStack(37).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(37), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(37), x - 20, y - 19);
                    y -= 23;
                }
                if (!mc.player.getInventory().getStack(38).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(38), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(38), x - 20, y - 19);
                    y -= 23;
                }
                if (!mc.player.getInventory().getStack(39).isEmpty()) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, duraSlot, x - 22, y - 22, 0, 0, 22, 22, 22, 22);
                    drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 19);
                    drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(39), x - 20, y - 19);
                }
            }
        }
    }
}
