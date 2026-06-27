package net.frostbyte.inventory.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.frostbyte.inventory.duck.MobEffectInstanceDuck;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "TAIL"))
    private void setStatusEffect(MobEffectInstance newEffect, Entity source, CallbackInfoReturnable<Boolean> cir, @Local(name = "effect") MobEffectInstance effect) {
        if (effect != null && effect.getAmplifier() == newEffect.getAmplifier()) {
            if (((MobEffectInstanceDuck) newEffect).getMaxDuration() < ((MobEffectInstanceDuck) effect).getMaxDuration()) {
                ((MobEffectInstanceDuck) newEffect).setMaxDuration(((MobEffectInstanceDuck) effect).getMaxDuration());
            }
        }
    }
}
