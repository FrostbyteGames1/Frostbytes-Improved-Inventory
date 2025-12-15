package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ExpandedTooltipInfo;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (ImprovedInventoryConfig.compassTooltip && stack.isOf(Items.COMPASS)) {
            ExpandedTooltipInfo.compassTooltipHandler(stack, tooltip);
        } else if (ImprovedInventoryConfig.compassTooltip && stack.isOf(Items.RECOVERY_COMPASS)) {
            ExpandedTooltipInfo.recoveryCompassTooltipHandler(tooltip);
        } else if (ImprovedInventoryConfig.clockTooltip && stack.isOf(Items.CLOCK)) {
            ExpandedTooltipInfo.clockTooltipHandler(tooltip);
        }
        if (ImprovedInventoryConfig.foodTooltip && stack.isFood()) {
            ExpandedTooltipInfo.foodTooltipHandler(stack, tooltip);
        }
    }
}
