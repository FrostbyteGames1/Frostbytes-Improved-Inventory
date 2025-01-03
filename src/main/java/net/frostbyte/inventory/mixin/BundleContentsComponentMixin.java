package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.component.type.BundleContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public abstract class BundleContentsComponentMixin {
    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "getNumberOfStacksShown", at = @At("HEAD"), cancellable = true)
    public void getNumberOfStacksShown(CallbackInfoReturnable<Integer> cir) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return;
        }
        cir.setReturnValue(((BundleContentsComponent) (Object) this).size());
    }
}
