package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow public double x;
    @Shadow public double y;
    @Shadow private boolean hasResolutionChanged;
    @Shadow private boolean cursorLocked;

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    public void lockCursor(CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab) {
            if (this.client.isWindowFocused()) {
                if (!this.cursorLocked) {
                    if (!MinecraftClient.IS_SYSTEM_MAC) {
                        KeyBinding.updatePressedStates();
                    }
                    this.cursorLocked = true;
                    InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212995, this.x, this.y);
                    this.client.setScreen(null);
                    this.client.attackCooldown = 10000;
                    this.hasResolutionChanged = true;
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "unlockCursor", at = @At("HEAD"), cancellable = true)
    public void unlockCursor(CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab) {
            if (this.cursorLocked) {
                this.cursorLocked = false;
                InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212993, this.x, this.y);
            }
            ci.cancel();
        }
    }

}