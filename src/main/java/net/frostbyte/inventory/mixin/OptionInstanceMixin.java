package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.Gamma;
import net.frostbyte.inventory.Zoom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(OptionInstance.class)
public abstract class OptionInstanceMixin<T> {

    @Shadow
    @Final
    private Component caption;

    @Shadow
    @Final
    private T initialValue;

    @Shadow
    private T value;

    @Shadow
    @Final
    private Consumer<T> onValueUpdate;

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public void set(T value, CallbackInfo ci) {
        if (Gamma.enabled && this.caption.equals(Component.translatable("options.gamma"))) {
            T newValue = validateGamma(value).orElseGet(() -> this.initialValue);
            if (!Minecraft.getInstance().isRunning()) {
                this.value = newValue;
            } else {
                if (!Objects.equals(this.value, newValue)) {
                    this.value = newValue;
                    this.onValueUpdate.accept(this.value);
                }

            }
            ci.cancel();
        }
        if (Zoom.zoomKey.isDown() && this.caption.equals(Component.translatable("options.fov"))) {
            T newValue = validateFov(value).orElseGet(() -> this.initialValue);
            if (!Minecraft.getInstance().isRunning()) {
                this.value = newValue;
            } else {
                if (!Objects.equals(this.value, newValue)) {
                    this.value = newValue;
                    this.onValueUpdate.accept(this.value);
                }
            }
            ci.cancel();
        }
    }

    @Unique
    Optional<T> validateGamma(final T value) {
        double val = (double) value;
        return val >= 0.0F && val <= Double.MAX_VALUE ? Optional.of(value) : Optional.empty();
    }

    @Unique
    Optional<T> validateFov(final T value) {
        int val = (int) value;
        return val >= 1 && val <= 110 ? Optional.of(value) : Optional.empty();
    }

}
