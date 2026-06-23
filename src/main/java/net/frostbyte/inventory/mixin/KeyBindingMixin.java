package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (ImprovedInventoryConfig.slotCycleAltNum && client.player != null && client.interactionManager != null) {
            int slot = client.player.getInventory().getSelectedSlot();
            if (key.getCode() == InputUtil.GLFW_KEY_1) {
                if (InputUtil.isKeyPressed(client.getWindow(), InputUtil.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(client.getWindow(), InputUtil.GLFW_KEY_RIGHT_ALT)) {
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, 27 + slot, slot, SlotActionType.SWAP, client.player);
                    client.player.getInventory().markDirty();
                    ci.cancel();
                }
            }
            if (key.getCode() == InputUtil.GLFW_KEY_2) {
                if (InputUtil.isKeyPressed(client.getWindow(), InputUtil.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(client.getWindow(), InputUtil.GLFW_KEY_RIGHT_ALT)) {
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, 18 + slot, slot, SlotActionType.SWAP, client.player);
                    client.player.getInventory().markDirty();
                    ci.cancel();
                }
            }
            if (key.getCode() == InputUtil.GLFW_KEY_3) {
                if (InputUtil.isKeyPressed(client.getWindow(), InputUtil.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(client.getWindow(), InputUtil.GLFW_KEY_RIGHT_ALT)) {
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, 9 + slot, slot, SlotActionType.SWAP, client.player);
                    client.player.getInventory().markDirty();
                    ci.cancel();
                }
            }
        }
    }
}
