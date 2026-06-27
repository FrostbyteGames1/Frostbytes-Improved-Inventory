package net.frostbyte.inventory.mixin;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {

    @Inject(method = "defaultErrorCallback", at = @At("HEAD"), cancellable = true)
    public void defaultErrorCallback(int errorCode, long description, CallbackInfo ci) {
        if (errorCode == 65539) {
            ci.cancel();
        }
    }

}
