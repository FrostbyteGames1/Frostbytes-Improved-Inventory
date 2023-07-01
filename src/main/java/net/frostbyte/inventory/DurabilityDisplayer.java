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

    private void message() {
        String m = "[" + Formatting.GOLD + "Improved Inventory" + Formatting.WHITE + "] " + "Durability Display: ";
        if (duraDisplay) {
            m = m + Formatting.GREEN + "Active";
        }else {
            m = m + Formatting.RED + "Inactive";
        }
        mc.player.sendMessage(Text.literal(m), false);
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (!mc.player.isSpectator() && duraDisplay && !mc.options.hudHidden) {
            int x = mc.getWindow().getScaledWidth();
            int y = mc.getWindow().getScaledHeight();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, duraSlot);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 91,0,0,22,22, 22,22);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 68,0,0,22,22, 22,22);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 45,0,0,22,22, 22,22);
            drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/dura_slot.png"),x - 22,y - 22,0,0,22,22, 22,22);
            if (!mc.player.getInventory().getStack(39).isEmpty()) {
                int dura = mc.player.getInventory().getStack(39).getMaxDamage() - mc.player.getInventory().getStack(39).getDamage();
                if (dura > 0f) {
                    drawContext.drawText(mc.textRenderer, String.valueOf(dura), x - 20, y - 77, mc.player.getInventory().getStack(39).getItemBarColor(), true);
                    drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 90);
                } else {
                    drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 88);
                }
            }
            if (!mc.player.getInventory().getStack(38).isEmpty()) {
                int dura = mc.player.getInventory().getStack(38).getMaxDamage() - mc.player.getInventory().getStack(38).getDamage();
                if (dura > 0f) {
                    drawContext.drawText(mc.textRenderer, String.valueOf(dura), x - 20, y - 54, mc.player.getInventory().getStack(38).getItemBarColor(), true);
                    drawContext.drawItem(mc.player.getInventory().getStack(38), x - 19, y - 67);
                } else {
                    drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 65);
                }
            }
            if (!mc.player.getInventory().getStack(37).isEmpty()) {
                int dura = mc.player.getInventory().getStack(37).getMaxDamage() - mc.player.getInventory().getStack(37).getDamage();
                if (dura > 0f) {
                    drawContext.drawText(mc.textRenderer, String.valueOf(dura), x - 20, y - 31, mc.player.getInventory().getStack(37).getItemBarColor(), true);
                    drawContext.drawItem(mc.player.getInventory().getStack(37), x - 19, y - 44);
                } else {
                    drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 42);
                }
            }
            if (!mc.player.getInventory().getStack(36).isEmpty()) {
                int dura = mc.player.getInventory().getStack(36).getMaxDamage() - mc.player.getInventory().getStack(36).getDamage();
                if (dura > 0f) {
                    drawContext.drawText(mc.textRenderer, String.valueOf(dura), x - 20, y - 8, mc.player.getInventory().getStack(36).getItemBarColor(), true);
                    drawContext.drawItem(mc.player.getInventory().getStack(36), x - 19, y - 21);
                } else {
                    drawContext.drawItem(mc.player.getInventory().getStack(39), x - 19, y - 20);
                }
            }
        }
    }
}
