package net.frostbyte.inventory.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        for (KeyBinding binding : MinecraftClient.getInstance().options.allKeys) {
            if (binding.matchesKey(new KeyInput(key.getCode(), -1, -1)) || binding.matchesMouse(new Click(MinecraftClient.getInstance().mouse.getX(), MinecraftClient.getInstance().mouse.getY(), new MouseInput(key.getCode(), -1)))) {
                ++binding.timesPressed;
            }
        }
        ci.cancel();
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        for (KeyBinding binding : MinecraftClient.getInstance().options.allKeys) {
            if (binding.matchesKey(new KeyInput(key.getCode(), -1, -1)) || binding.matchesMouse(new Click(MinecraftClient.getInstance().mouse.getX(), MinecraftClient.getInstance().mouse.getY(), new MouseInput(key.getCode(), -1)))) {
               binding.setPressed(pressed);
            }
        }
        ci.cancel();
    }
}
