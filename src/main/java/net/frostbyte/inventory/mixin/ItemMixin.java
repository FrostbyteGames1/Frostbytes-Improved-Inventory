package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ExpandedTooltipInfo;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (ImprovedInventoryConfig.compassTooltip && itemStack.is(Items.COMPASS)) {
            ExpandedTooltipInfo.compassTooltipHandler(itemStack, builder);
        } else if (ImprovedInventoryConfig.compassTooltip && itemStack.is(Items.RECOVERY_COMPASS)) {
            ExpandedTooltipInfo.recoveryCompassTooltipHandler(builder);
        } else if (ImprovedInventoryConfig.clockTooltip && itemStack.is(Items.CLOCK)) {
            ExpandedTooltipInfo.clockTooltipHandler(builder);
        }
        if (ImprovedInventoryConfig.foodTooltip && itemStack.getComponents().has(DataComponents.FOOD)) {
            ExpandedTooltipInfo.foodTooltipHandler(itemStack, builder);
        }
    }
}
