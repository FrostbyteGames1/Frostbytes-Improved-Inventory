package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipDisplay.class)
public abstract class TooltipDisplayMixin {

    @Inject(method = "shows", at = @At("HEAD"), cancellable = true)
    public void shows(DataComponentType<?> component, CallbackInfoReturnable<Boolean> cir) {
        if (ImprovedInventoryConfig.shulkerBoxTooltip && component == DataComponents.CONTAINER) {
            cir.setReturnValue(false);
        }
    }
}
