package net.frostbyte.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.inventory.ContainerInput;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class SlotCycler implements HudElement {
    public static KeyMapping cycleUpKey;
    public static KeyMapping cycleDownKey;
    Minecraft client;
    final Identifier PREVIEW_SLOTS = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "textures/extra_slots.png");

    public void setKeyMappings() {
        KeyMappingHelper.registerKeyMapping(cycleUpKey = new KeyMapping("key.cycle_up", InputConstants.Type.KEYSYM, InputConstants.KEY_J, ImprovedInventory.KEYBIND_CATEGORY));
        KeyMappingHelper.registerKeyMapping(cycleDownKey = new KeyMapping("key.cycle_down", InputConstants.Type.KEYSYM, InputConstants.KEY_H, ImprovedInventory.KEYBIND_CATEGORY));
    }

    public static void slotCycleHandler(Minecraft client) {
        if (SlotCycler.cycleUpKey.isDown()){
            SlotCycler.cycleUp(client, client.player);
            
        }
        if (SlotCycler.cycleDownKey.isDown()){
            SlotCycler.cycleDown(client, client.player);
            
        }
    }

    public static void cycleDown(Minecraft client, LocalPlayer player) {
        if (client.gameMode == null || client.player == null) {
            return;
        }
        int current = player.getInventory().getSelectedSlot();
        int target = current;
        int top = 9 + current;
        int middle = 18 + current;
        int bottom = 27 + current;
        for (int i = 1; i < 4; i++) {
            if (!player.getInventory().getItem(i * 9 + current).isEmpty()) {
                target = i * 9 + current;
                break;
            }
        }
        if (target == top) {
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, top, current, ContainerInput.SWAP, client.player);
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, middle, current, ContainerInput.SWAP, client.player);
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, bottom, current, ContainerInput.SWAP, client.player);
        } else if (target == middle) {
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, middle, current, ContainerInput.SWAP, client.player);
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, bottom, current, ContainerInput.SWAP, client.player);
        } else if (target == bottom) {
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, bottom, current, ContainerInput.SWAP, client.player);
        }
    }

    public static void cycleUp(Minecraft client, LocalPlayer player) {
        if (client.gameMode == null || client.player == null) {
            return;
        }
        int current = player.getInventory().getSelectedSlot();
        int target = current;
        int top = 9 + current;
        int middle = 18 + current;
        int bottom = 27 + current;
        for (int i = 1; i < 4; i++) {
            if (!player.getInventory().getItem(i * 9 + current).isEmpty()) {
                target = i * 9 + current;
                break;
            }
        }
        if (target == top) {
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, bottom, current, ContainerInput.SWAP, client.player);
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, middle, current, ContainerInput.SWAP, client.player);
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, top, current, ContainerInput.SWAP, client.player);
        } else if (target == middle) {
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, bottom, current, ContainerInput.SWAP, client.player);
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, middle, current, ContainerInput.SWAP, client.player);
        } else if (target == bottom) {
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, bottom, current, ContainerInput.SWAP, client.player);
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        client = Minecraft.getInstance();
        assert client.player != null;
        if (!client.player.isSpectator() && ImprovedInventoryConfig.slotCycle && !client.options.hideGui) {
            int width = client.getWindow().getGuiScaledWidth();
            int height = client.getWindow().getGuiScaledHeight() - ImprovedInventoryConfig.slotCycleOffsetY;
            int x = width / 2;
            if (client.player.getMainArm().equals(HumanoidArm.LEFT)) {
                x -= ImprovedInventoryConfig.slotCycleOffsetX;
            } else {
                x += ImprovedInventoryConfig.slotCycleOffsetX;
            }
            assert client.player != null;
            if (client.player.getMainArm().equals(HumanoidArm.LEFT)) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, PREVIEW_SLOTS, x - 160, height - 23, 0, 0, 62, 24, 62, 24);
            } else {
                graphics.blit(RenderPipelines.GUI_TEXTURED, PREVIEW_SLOTS, x + 98, height - 23, 0, 0, 62, 24, 62, 24);
            }
            // Item in top slot
            if (client.player.getMainArm().equals(HumanoidArm.LEFT)) {
                graphics.item(client.player.getInventory().getItem(27 + client.player.getInventory().getSelectedSlot()), x - 157, height - 19);
                graphics.itemDecorations(client.font, client.player.getInventory().getItem(27 + client.player.getInventory().getSelectedSlot()), x - 157, height - 19);
            } else {
                graphics.item(client.player.getInventory().getItem(27 + client.player.getInventory().getSelectedSlot()), x + 101, height - 19);
                graphics.itemDecorations(client.font, client.player.getInventory().getItem(27 + client.player.getInventory().getSelectedSlot()), x + 101, height - 19);
            }
            // Item in middle slot
            if (client.player.getMainArm().equals(HumanoidArm.LEFT)) {
                graphics.item(client.player.getInventory().getItem(18 + client.player.getInventory().getSelectedSlot()), x - 137, height - 19);
                graphics.itemDecorations(client.font, client.player.getInventory().getItem(18 + client.player.getInventory().getSelectedSlot()), x - 137, height - 19);
            } else {
                graphics.item(client.player.getInventory().getItem(18 + client.player.getInventory().getSelectedSlot()), x + 121, height - 19);
                graphics.itemDecorations(client.font, client.player.getInventory().getItem(18 + client.player.getInventory().getSelectedSlot()), x + 121, height - 19);
            }
            // Item in bottom slot
            if (client.player.getMainArm().equals(HumanoidArm.LEFT)) {
                graphics.item(client.player.getInventory().getItem(9 + client.player.getInventory().getSelectedSlot()), x - 117, height - 19);
                graphics.itemDecorations(client.font, client.player.getInventory().getItem(9 + client.player.getInventory().getSelectedSlot()), x - 117, height - 19);
            } else {
                graphics.item(client.player.getInventory().getItem(9 + client.player.getInventory().getSelectedSlot()), x + 141, height - 19);
                graphics.itemDecorations(client.font, client.player.getInventory().getItem(9 + client.player.getInventory().getSelectedSlot()), x + 141, height - 19);
            }
        }
    }
}
