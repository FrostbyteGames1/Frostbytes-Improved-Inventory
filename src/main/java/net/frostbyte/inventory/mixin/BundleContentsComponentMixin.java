package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.component.type.BundleContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public abstract class BundleContentsComponentMixin {

    @Shadow
    public abstract int size();

    @Inject(method = "getNumberOfStacksShown", at = @At("HEAD"), cancellable = true)
    public void getNumberOfStacksShown(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(this.size());
        }
    }

}
