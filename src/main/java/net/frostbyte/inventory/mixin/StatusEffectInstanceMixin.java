package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.duck.StatusEffectInstanceDuck;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements StatusEffectInstanceDuck {
    @Unique
    private int maxDuration;
    @Shadow
    private int duration;

    @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;IIZZZLnet/minecraft/entity/effect/StatusEffectInstance;)V", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.maxDuration = this.duration;
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyFrom(StatusEffectInstance instance, CallbackInfo ci) {
        maxDuration = ((StatusEffectInstanceMixin) (Object) instance).maxDuration;
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "upgrade", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;duration:I", opcode = Opcodes.PUTFIELD))
    private void upgrade(StatusEffectInstance instance, CallbackInfoReturnable<Boolean> cir) {
        maxDuration = ((StatusEffectInstanceMixin) (Object) instance).maxDuration;
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
