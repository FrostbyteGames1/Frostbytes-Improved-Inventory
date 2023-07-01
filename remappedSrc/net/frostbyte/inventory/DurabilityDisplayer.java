package net.frostbyte.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class DurabilityDisplayer implements ClientTickEvents.EndTick, HudRenderCallback {

    public KeyBinding toggleDuraDisplay;
    public boolean duraDisplay = true;
    public static boolean firstRun = true;
    MinecraftClient mc;
    Identifier duraSlot = new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png");

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(toggleDuraDisplay = new KeyBinding("Toggle Durability Display", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_COMMA, "Improved Inventory"));
    }

    public void processKeyBinds() {
        if (toggleDuraDisplay.wasPressed()) {
            duraDisplay = !duraDisplay;
            message();
        }
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;

        if (player == null) {
            return;
        }

        if (firstRun) {
            message();
            firstRun = false;
        }

        processKeyBinds();
    }

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        if (!mc.player.isSpectator() && duraDisplay && !mc.options.hudHidden) {
            int x = mc.getWindow().getScaledWidth();
            int y = mc.getWindow().getScaledHeight();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, duraSlot);
            DrawContext.drawTexture(matrixStack,x - 22,y - 91,0,0,22,22, 22,22);
            DrawContext.drawTexture(matrixStack,x - 22,y - 68,0,0,22,22, 22,22);
            DrawContext.drawTexture(matrixStack,x - 22,y - 45,0,0,22,22, 22,22);
            DrawContext.drawTexture(matrixStack,x - 22,y - 22,0,0,22,22, 22,22);
            int red = 16733525;
            int yellow = 16777045;
            int green = 5635925;
            if (!mc.player.getInventory().getStack(39).isEmpty()) {
                int headDura = mc.player.getInventory().getStack(39).getMaxDamage() - mc.player.getInventory().getStack(39).getDamage();
                matrixStack.push();
                matrixStack.scale(0.9f,0.9f,0.9f);
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(39), (int) ((x - 13) * 1.1f), (int) ((y - 86) * 1.1f));
                matrixStack.pop();
                matrixStack.push();
                matrixStack.scale(0.5f,0.5f,0.5f);
                if (headDura > mc.player.getInventory().getStack(39).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(headDura), (x - 18) * 2, (y - 76) * 2, green, true);
                } else if (headDura > mc.player.getInventory().getStack(39).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(headDura), (x - 18) * 2, (y - 76) * 2, yellow, true);
                } else if (headDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(headDura), (x - 18) * 2, (y - 76) * 2, red, true);
                }
                matrixStack.pop();
            }
            if (!mc.player.getInventory().getStack(38).isEmpty()) {
                int chestDura = mc.player.getInventory().getStack(38).getMaxDamage() - mc.player.getInventory().getStack(38).getDamage();
                matrixStack.push();
                matrixStack.scale(0.9f,0.9f,0.9f);
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(38), (int) ((x - 13) * 1.1f), (int) ((y - 63) * 1.1f));
                matrixStack.pop();
                matrixStack.push();
                matrixStack.scale(0.5f,0.5f,0.5f);
                if (chestDura > mc.player.getInventory().getStack(38).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(chestDura), (x - 18) * 2, (y - 53) * 2, green, true);
                } else if (chestDura > mc.player.getInventory().getStack(38).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(chestDura), (x - 18) * 2, (y - 53) * 2, yellow, true);
                } else if (chestDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(chestDura), (x - 18) * 2, (y - 53) * 2, red, true);
                }
                matrixStack.pop();
            }
            if (!mc.player.getInventory().getStack(37).isEmpty()) {
                int legDura = mc.player.getInventory().getStack(37).getMaxDamage() - mc.player.getInventory().getStack(37).getDamage();
                matrixStack.push();
                matrixStack.scale(0.9f,0.9f,0.9f);
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(37), (int) ((x - 13) * 1.1f), (int) ((y - 40) * 1.1f));
                matrixStack.pop();
                matrixStack.push();
                matrixStack.scale(0.5f,0.5f,0.5f);
                if (legDura > mc.player.getInventory().getStack(37).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(legDura), (x - 18) * 2, (y - 30) * 2, green, true);
                } else if (legDura > mc.player.getInventory().getStack(37).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(legDura), (x - 18) * 2, (y - 30) * 2, yellow, true);
                } else if (legDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(legDura), (x - 18) * 2, (y - 30) * 2, red, true);
                }
                matrixStack.pop();
            }
            if (!mc.player.getInventory().getStack(36).isEmpty()) {
                int footDura = mc.player.getInventory().getStack(36).getMaxDamage() - mc.player.getInventory().getStack(36).getDamage();
                matrixStack.push();
                matrixStack.scale(0.9f,0.9f,0.9f);
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(36), (int) ((x - 13) * 1.1f), (int) ((y - 17) * 1.1f));
                matrixStack.pop();
                matrixStack.push();
                matrixStack.scale(0.5f,0.5f,0.5f);
                if (footDura > mc.player.getInventory().getStack(36).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(footDura), (x - 18) * 2, (y - 7) * 2, green, true);
                } else if (footDura > mc.player.getInventory().getStack(37).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(footDura), (x - 18) * 2, (y - 7) * 2, yellow, true);
                } else if (footDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(footDura), (x - 18) * 2, (y - 7) * 2, red, true);
                }
                matrixStack.pop();
            }
        }
    }

    private void message() {
        String m = "[" + Formatting.GOLD + "Improved Inventory" + Formatting.WHITE + "] " + "Durability Display: ";
        if (duraDisplay) {
            m = m + Formatting.GREEN + "Active";
        }else {
            m = m + Formatting.RED + "Inactive";
        }
        mc.player.sendMessage(Text.literal(m), false);
    }
}
