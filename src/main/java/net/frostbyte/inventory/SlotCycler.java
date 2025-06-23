package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class SlotCycler implements HudRenderCallback {
    public static KeyBinding cycleUpKey;
    public static KeyBinding cycleDownKey;
    MinecraftClient mc;
    final Identifier PREVIEW_SLOTS = Identifier.of(ImprovedInventory.MOD_ID, "textures/extra_slots.png");

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(cycleUpKey = new KeyBinding("key.cycle_up", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_J, Text.translatable("key.categories.improved_inventory").getString()));
        KeyBindingHelper.registerKeyBinding(cycleDownKey = new KeyBinding("key.cycle_down", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_H, Text.translatable("key.categories.improved_inventory").getString()));
    }

    @SuppressWarnings("DataFlowIssue")
    public static void slotCycleHandler(MinecraftClient client) {
        if (SlotCycler.cycleUpKey.wasPressed()){
            SlotCycler.cycleUp(client, client.player);
            client.player.getInventory().markDirty();
        }
        if (SlotCycler.cycleDownKey.wasPressed()){
            SlotCycler.cycleDown(client, client.player);
            client.player.getInventory().markDirty();
        }
    }

    public static void cycleDown(MinecraftClient client, ClientPlayerEntity player) {
        int current = player.getInventory().getSelectedSlot();
        int target = current;
        int top = 9 + current;
        int middle = 18 + current;
        int bottom = 27 + current;
        for (int i = 1; i < 4; i++) {
            if (!player.getInventory().getStack(i * 9 + current).isEmpty()) {
                target = i * 9 + current;
                break;
            }
        }
        if (target == top) {
            assert client.interactionManager != null;
            assert client.player != null;
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, top, current, SlotActionType.SWAP, client.player);
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, middle, current, SlotActionType.SWAP, client.player);
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, bottom, current, SlotActionType.SWAP, client.player);
            client.player.getInventory().markDirty();
        } else if (target == middle) {
            assert client.interactionManager != null;
            assert client.player != null;
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, middle, current, SlotActionType.SWAP, client.player);
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, bottom, current, SlotActionType.SWAP, client.player);
            client.player.getInventory().markDirty();
        } else if (target == bottom) {
            assert client.interactionManager != null;
            assert client.player != null;
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, bottom, current, SlotActionType.SWAP, client.player);
            client.player.getInventory().markDirty();
        }
    }

    public static void cycleUp(MinecraftClient client, ClientPlayerEntity player) {
        int current = player.getInventory().getSelectedSlot();
        int target = current;
        int top = 9 + current;
        int middle = 18 + current;
        int bottom = 27 + current;
        for (int i = 1; i < 4; i++) {
            if (!player.getInventory().getStack(i * 9 + current).isEmpty()) {
                target = i * 9 + current;
                break;
            }
        }
        if (target == top) {
            assert client.interactionManager != null;
            assert client.player != null;
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, bottom, current, SlotActionType.SWAP, client.player);
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, middle, current, SlotActionType.SWAP, client.player);
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, top, current, SlotActionType.SWAP, client.player);
            client.player.getInventory().markDirty();
        } else if (target == middle) {
            assert client.interactionManager != null;
            assert client.player != null;
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, bottom, current, SlotActionType.SWAP, client.player);
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, middle, current, SlotActionType.SWAP, client.player);
            client.player.getInventory().markDirty();
        } else if (target == bottom) {
            assert client.interactionManager != null;
            assert client.player != null;
            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, bottom, current, SlotActionType.SWAP, client.player);
            client.player.getInventory().markDirty();
        }
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        mc = MinecraftClient.getInstance();
        assert mc.player != null;
        if (!mc.player.isSpectator() && ImprovedInventoryConfig.slotCycle && !mc.options.hudHidden) {
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight() - ImprovedInventoryConfig.slotCycleOffsetY;
            int x = width / 2;
            if (mc.player.getMainArm().equals(Arm.LEFT)) {
                x -= ImprovedInventoryConfig.slotCycleOffsetX;
            } else {
                x += ImprovedInventoryConfig.slotCycleOffsetX;
            }
            assert mc.player != null;
            if (mc.player.getMainArm().equals(Arm.LEFT)) {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, PREVIEW_SLOTS, x - 160, height - 23, 0, 0, 62, 24, 62, 24);
            } else {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, PREVIEW_SLOTS, x + 98, height - 23, 0, 0, 62, 24, 62, 24);
            }
            // Item in top slot
            if (mc.player.getMainArm().equals(Arm.LEFT)) {
                drawContext.drawItem(mc.player.getInventory().getStack(27 + mc.player.getInventory().getSelectedSlot()), x - 157, height - 19);
                drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(27 + mc.player.getInventory().getSelectedSlot()), x - 157, height - 19);
            } else {
                drawContext.drawItem(mc.player.getInventory().getStack(27 + mc.player.getInventory().getSelectedSlot()), x + 101, height - 19);
                drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(27 + mc.player.getInventory().getSelectedSlot()), x + 101, height - 19);
            }
            // Item in middle slot
            if (mc.player.getMainArm().equals(Arm.LEFT)) {
                drawContext.drawItem(mc.player.getInventory().getStack(18 + mc.player.getInventory().getSelectedSlot()), x - 137, height - 19);
                drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(18 + mc.player.getInventory().getSelectedSlot()), x - 137, height - 19);
            } else {
                drawContext.drawItem(mc.player.getInventory().getStack(18 + mc.player.getInventory().getSelectedSlot()), x + 121, height - 19);
                drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(18 + mc.player.getInventory().getSelectedSlot()), x + 121, height - 19);
            }
            // Item in bottom slot
            if (mc.player.getMainArm().equals(Arm.LEFT)) {
                drawContext.drawItem(mc.player.getInventory().getStack(9 + mc.player.getInventory().getSelectedSlot()), x - 117, height - 19);
                drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(9 + mc.player.getInventory().getSelectedSlot()), x - 117, height - 19);
            } else {
                drawContext.drawItem(mc.player.getInventory().getStack(9 + mc.player.getInventory().getSelectedSlot()), x + 141, height - 19);
                drawContext.drawStackOverlay(mc.textRenderer, mc.player.getInventory().getStack(9 + mc.player.getInventory().getSelectedSlot()), x + 141, height - 19);
            }
        }
    }
}
