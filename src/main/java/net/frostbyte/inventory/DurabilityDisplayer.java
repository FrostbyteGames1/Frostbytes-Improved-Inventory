package net.frostbyte.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
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
            DrawableHelper.drawTexture(matrixStack,x - 22,y - 91,0,0,22,22, 22,22);
            DrawableHelper.drawTexture(matrixStack,x - 22,y - 68,0,0,22,22, 22,22);
            DrawableHelper.drawTexture(matrixStack,x - 22,y - 45,0,0,22,22, 22,22);
            DrawableHelper.drawTexture(matrixStack,x - 22,y - 22,0,0,22,22, 22,22);
            int red = 11141120;
            int orange = 16755200;
            int green = 5635925;
            if (!mc.player.getInventory().getStack(39).isEmpty()) {
                int headDura = mc.player.getInventory().getStack(39).getMaxDamage() - mc.player.getInventory().getStack(39).getDamage();
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(39), x - 19, y - 88);
                if (headDura > mc.player.getInventory().getStack(39).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(headDura), x - 20, y - 79, green, true);
                } else if (headDura > mc.player.getInventory().getStack(39).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(headDura), x - 20, y - 79, orange, true);
                } else if (headDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(headDura), x - 20, y - 79, red, true);
                }
            }
            if (!mc.player.getInventory().getStack(38).isEmpty()) {
                int chestDura = mc.player.getInventory().getStack(38).getMaxDamage() - mc.player.getInventory().getStack(38).getDamage();
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(38), x - 19, y - 65);
                if (chestDura > mc.player.getInventory().getStack(38).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(chestDura), x - 20, y - 56, green, true);
                } else if (chestDura > mc.player.getInventory().getStack(38).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(chestDura), x - 20, y - 56, orange, true);
                } else if (chestDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(chestDura), x - 20, y - 56, red, true);
                }
            }
            if (!mc.player.getInventory().getStack(37).isEmpty()) {
                int legDura = mc.player.getInventory().getStack(37).getMaxDamage() - mc.player.getInventory().getStack(37).getDamage();
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(37), x - 19, y - 42);
                if (legDura > mc.player.getInventory().getStack(37).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(legDura), x - 20, y - 33, green, true);
                } else if (legDura > mc.player.getInventory().getStack(37).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(legDura), x - 20, y - 33, orange, true);
                } else if (legDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(legDura), x - 20, y - 33, red, true);
                }
            }
            if (!mc.player.getInventory().getStack(36).isEmpty()) {
                int footDura = mc.player.getInventory().getStack(36).getMaxDamage() - mc.player.getInventory().getStack(36).getDamage();
                mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getStack(36), x - 19, y - 19);
                if (footDura > mc.player.getInventory().getStack(36).getMaxDamage() * 0.5f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(footDura), x - 20, y - 10, green, true);
                } else if (footDura > mc.player.getInventory().getStack(37).getMaxDamage() * 0.25f) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(footDura), x - 20, y - 10, orange, true);
                } else if (footDura > 0) {
                    mc.textRenderer.drawWithShadow(matrixStack, String.valueOf(footDura), x - 20, y - 10, red, true);
                }
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
