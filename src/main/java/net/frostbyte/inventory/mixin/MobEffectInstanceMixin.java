package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.duck.MobEffectInstanceDuck;
import net.minecraft.world.effect.MobEffectInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements MobEffectInstanceDuck {
    @Shadow
    private int duration;
    @Unique
    private int maxDuration;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.maxDuration = this.duration;
    }

    @Inject(method = "copyBlendState", at = @At("RETURN"))
    private void copyFrom(MobEffectInstance instance, CallbackInfo ci) {
        maxDuration = ((MobEffectInstanceMixin) (Object) instance).maxDuration;
    }

    @Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/world/effect/MobEffectInstance;duration:I", opcode = Opcodes.PUTFIELD))
    private void upgrade(MobEffectInstance takeOver, CallbackInfoReturnable<Boolean> cir) {
        maxDuration = ((MobEffectInstanceMixin) (Object) takeOver).maxDuration;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public int getMaxDuration() {
        return maxDuration;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }
}
