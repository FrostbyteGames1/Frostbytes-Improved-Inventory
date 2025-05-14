package net.frostbyte.inventory.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.frostbyte.inventory.duck.StatusEffectInstanceDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @SuppressWarnings("LocalMayBeArgsOnly")
    @Inject(method = "setStatusEffect", at = @At(value = "TAIL"))
    private void setStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfo ci, @Local(ordinal = 1) StatusEffectInstance oldEffect) {
        if (oldEffect != null && oldEffect.getAmplifier() == effect.getAmplifier()) {
            if (((StatusEffectInstanceDuck) effect).getMaxDuration() < ((StatusEffectInstanceDuck) oldEffect).getMaxDuration()) {
                ((StatusEffectInstanceDuck) effect).setMaxDuration(((StatusEffectInstanceDuck) oldEffect).getMaxDuration());
            }
        }
    }
}
