package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ExpandedTooltipInfo;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At("HEAD"), cancellable = true)
    public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if (ImprovedInventoryConfig.shulkerBoxTooltip && !ExpandedTooltipInfo.getShulkerInventory((ItemStack) (Object) this).isEmpty()) {
            cir.setReturnValue(List.of());
        }
    }
}
