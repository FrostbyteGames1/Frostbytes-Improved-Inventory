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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlotCycler implements ClientTickEvents.EndTick, HudRenderCallback {

    public KeyBinding cycleUpKey;
    public KeyBinding cycleDownKey;
    MinecraftClient mc;
    Identifier extraSlots = new Identifier(ImprovedInventory.MOD_ID, "textures/extra_slots.png");

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(cycleUpKey = new KeyBinding("Cycle Slot Up", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_J, "Improved Inventory"));
        KeyBindingHelper.registerKeyBinding(cycleDownKey = new KeyBinding("Cycle Slot Down", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_H, "Improved Inventory"));
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;

        if (player==null)
            return;
        if (cycleUpKey.wasPressed()){
            cycleUp(player);
        }
        if (cycleDownKey.wasPressed()){
            cycleDown(player);
        }

        player.getInventory().markDirty();
    }

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        if (!mc.player.isSpectator() && !mc.options.hudHidden) {
            int x = 0;
            int y = 0;
            mc = MinecraftClient.getInstance();
            if (mc != null) {
                int width = mc.getWindow().getScaledWidth();
                int height = mc.getWindow().getScaledHeight();
                x = width / 2;
                y = height;
            }
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, extraSlots);
            DrawableHelper.drawTexture(matrixStack,x + 98,y - 23,0,0,62,24, 62,24);
            for (int i = 3; i > 0; i--) {
                if (!mc.player.getInventory().getStack(i * 9 + mc.player.getInventory().selectedSlot).isEmpty()) {
                    mc.getItemRenderer().renderInGui(new MatrixStack(), mc.player.getInventory().getStack(i * 9 + mc.player.getInventory().selectedSlot), x + 101, y - 19);
                    break;
                }
            }
            mc.getItemRenderer().renderInGui(matrixStack, mc.player.getInventory().getMainHandStack(), x + 121, y - 19);
            for (int i = 1; i < 4; i++) {
                if (!mc.player.getInventory().getStack(i * 9 + mc.player.getInventory().selectedSlot).isEmpty()) {
                    mc.getItemRenderer().renderInGui(new MatrixStack(), mc.player.getInventory().getStack(i * 9 + mc.player.getInventory().selectedSlot), x + 141, y - 19);
                    break;
                }
            }
        }
    }

    void cycleDown(ClientPlayerEntity player) {
        int currentSlot = player.getInventory().selectedSlot;
        int targetSlot = -1;
        for (int i = 1; i < 4; i++) {
            if (!player.getInventory().getStack(i * 9 + currentSlot).isEmpty()) {
                targetSlot = i * 9 + currentSlot;
                break;
            }
        }
        if (targetSlot == 1 * 9 + currentSlot) {
            ItemStack top = player.getInventory().getStack(1 * 9 + currentSlot);
            ItemStack middle = player.getInventory().getStack(2 * 9 + currentSlot);
            ItemStack bottom = player.getInventory().getStack(3 * 9 + currentSlot);
            ItemStack current = player.getInventory().getStack(currentSlot);
            player.getInventory().setStack(1 * 9 + currentSlot, current);
            player.getInventory().setStack(2 * 9 + currentSlot, top);
            player.getInventory().setStack(3 * 9 + currentSlot, middle);
            player.getInventory().setStack(currentSlot, bottom);
        } else if (targetSlot == 2 * 9 + currentSlot) {
            ItemStack middle = player.getInventory().getStack(2 * 9 + currentSlot);
            ItemStack bottom = player.getInventory().getStack(3 * 9 + currentSlot);
            ItemStack current = player.getInventory().getStack(currentSlot);
            player.getInventory().setStack(2 * 9 + currentSlot, current);
            player.getInventory().setStack(3 * 9 + currentSlot, middle);
            player.getInventory().setStack(currentSlot, bottom);
        } else if (targetSlot == 3 * 9 + currentSlot) {
            ItemStack bottom = player.getInventory().getStack(3 * 9 + currentSlot);
            ItemStack current = player.getInventory().getStack(currentSlot);
            player.getInventory().setStack(3 * 9 + currentSlot, current);
            player.getInventory().setStack(currentSlot, bottom);
        }
    }

    void cycleUp(ClientPlayerEntity player) {
        int currentSlot = player.getInventory().selectedSlot;
        int targetSlot = -1;
        for (int i = 1; i < 4; i++) {
            if (!player.getInventory().getStack(i * 9 + currentSlot).isEmpty()) {
                targetSlot = i * 9 + currentSlot;
                break;
            }
        }
        if (targetSlot == 1 * 9 + currentSlot) {
            ItemStack top = player.getInventory().getStack(1 * 9 + currentSlot);
            ItemStack middle = player.getInventory().getStack(2 * 9 + currentSlot);
            ItemStack bottom = player.getInventory().getStack(3 * 9 + currentSlot);
            ItemStack current = player.getInventory().getStack(currentSlot);
            player.getInventory().setStack(1 * 9 + currentSlot, middle);
            player.getInventory().setStack(2 * 9 + currentSlot, bottom);
            player.getInventory().setStack(3 * 9 + currentSlot, current);
            player.getInventory().setStack(currentSlot, top);
        } else if (targetSlot == 2 * 9 + currentSlot) {
            ItemStack middle = player.getInventory().getStack(2 * 9 + currentSlot);
            ItemStack bottom = player.getInventory().getStack(3 * 9 + currentSlot);
            ItemStack current = player.getInventory().getStack(currentSlot);
            player.getInventory().setStack(2 * 9 + currentSlot, bottom);
            player.getInventory().setStack(3 * 9 + currentSlot, current);
            player.getInventory().setStack(currentSlot, middle);
        } else if (targetSlot == 3 * 9 + currentSlot) {
            ItemStack bottom = player.getInventory().getStack(3 * 9 + currentSlot);
            ItemStack current = player.getInventory().getStack(currentSlot);
            player.getInventory().setStack(3 * 9 + currentSlot, current);
            player.getInventory().setStack(currentSlot, bottom);
        }
    }

}
