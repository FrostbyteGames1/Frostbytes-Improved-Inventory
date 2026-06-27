package net.frostbyte.inventory.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void click(InputConstants.Key key, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (ImprovedInventoryConfig.slotCycleAltNum && client.player != null && client.gameMode != null) {
            int slot = client.player.getInventory().getSelectedSlot();
            if (key.getValue() == InputConstants.KEY_1) {
                if (InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_LALT) || InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_RALT)) {
                    client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, 27 + slot, slot, ContainerInput.SWAP, client.player);
                    ci.cancel();
                }
            }
            if (key.getValue() == InputConstants.KEY_2) {
                if (InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_LALT) || InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_RALT)) {
                    client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, 18 + slot, slot, ContainerInput.SWAP, client.player);
                    ci.cancel();
                }
            }
            if (key.getValue() == InputConstants.KEY_3) {
                if (InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_LALT) || InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_RALT)) {
                    client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, 9 + slot, slot, ContainerInput.SWAP, client.player);
                    ci.cancel();
                }
            }
        }
    }
}
