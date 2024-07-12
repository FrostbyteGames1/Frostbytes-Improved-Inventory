package net.frostbyte.inventory.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleOption.class)
public abstract class SimpleOptionMixin<T> {

    @Shadow @Final Text text;
    @Shadow T value;

    @Inject(method = "getCodec", at = @At("HEAD"), cancellable = true)
    public void getCodecGamma(CallbackInfoReturnable<Codec<Double>> info) {
        if (text.getString().equals(I18n.translate("options.gamma"))) {
            info.setReturnValue(Codec.DOUBLE);
        }
    }

    @Inject(method = "getCodec", at = @At("HEAD"), cancellable = true)
    public void getCodecFOV(CallbackInfoReturnable<Codec<Integer>> info) {
        if (text.getString().equals(I18n.translate("options.fov"))) {
            info.setReturnValue(Codec.INT);
        }
    }

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    public void setValue(T value, CallbackInfo info) {
        if (text.getString().equals(I18n.translate("options.gamma")) || text.getString().equals(I18n.translate("options.fov"))) {
            this.value = value;
            info.cancel();
        }
    }

}
