package net.frostbyte.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DurabilityDisplayer implements HudRenderCallback {
    MinecraftClient mc = MinecraftClient.getInstance();
    Identifier duraSlot = Identifier.of(ImprovedInventory.MOD_ID, "textures/dura_slot.png");
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        assert mc.player != null;
        if (!mc.player.isSpectator() && ImprovedInventoryConfig.duraDisplay && !mc.options.hudHidden) {
            int x = mc.getWindow().getScaledWidth();
            int y = mc.getWindow().getScaledHeight();
            if (ImprovedInventoryConfig.duraDisplaySide) {
                x = 22;
            }
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, duraSlot);
            if (!mc.player.getInventory().getStack(36).isEmpty()) {
                drawContext.drawTexture(Identifier.of(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 22,0,0,22,22, 22,22);
                drawContext.drawItem(mc.player.getInventory().getStack(36), x - 19, y - 19);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(36), x - 20, y - 19);
                y -= 23;
            }
            if (!mc.player.getInventory().getStack(37).isEmpty()) {
                drawContext.drawTexture(Identifier.of(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 22,0,0,22,22, 22,22);
                drawContext.drawItem(mc.player.getInventory().getStack(37), x - 19, y - 19);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(37), x - 20, y - 22);
                y -= 23;
            }
            if (!mc.player.getInventory().getStack(38).isEmpty()) {
                drawContext.drawTexture(Identifier.of(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 22,0,0,22,22, 22,22);
                drawContext.drawItem(mc.player.getInventory().getStack(38), x - 19, y - 19);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(38), x - 20, y - 22);
                y -= 23;
            }
            if (!mc.player.getInventory().getStack(39).isEmpty()) {
                drawContext.drawTexture(Identifier.of(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 22,0,0,22,22, 22,22);
                drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 19);
                drawContext.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(39), x - 20, y - 22);
            }
        }
    }
}
