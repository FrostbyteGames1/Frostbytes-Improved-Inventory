package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ExpandedTooltipInfo;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        if (ImprovedInventoryConfig.compassTooltip && stack.isOf(Items.COMPASS)) {
            ExpandedTooltipInfo.compassTooltipHandler(stack, textConsumer);
        } else if (ImprovedInventoryConfig.compassTooltip && stack.isOf(Items.RECOVERY_COMPASS)) {
            ExpandedTooltipInfo.recoveryCompassTooltipHandler(textConsumer);
        } else if (ImprovedInventoryConfig.clockTooltip && stack.isOf(Items.CLOCK)) {
            ExpandedTooltipInfo.clockTooltipHandler(textConsumer);
        }
        if (ImprovedInventoryConfig.foodTooltip && stack.getComponents().contains(DataComponentTypes.FOOD)) {
            ExpandedTooltipInfo.foodTooltipHandler(stack, textConsumer);
        }
    }
}
