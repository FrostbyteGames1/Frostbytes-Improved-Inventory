package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BundleTooltipComponent.class)
public abstract class BundleTooltipComponentMixin {
    @Shadow
    @Final
    private BundleContentsComponent bundleContents;
    
    @Unique
    private static int getModifiedBundleTooltipColumns(int size) {
        return Math.max(4, MathHelper.ceil(Math.sqrt(size)));
    }

    @Inject(method = "getNumVisibleSlots", at = @At("HEAD"), cancellable = true)
    private void getNumVisibleSlots(CallbackInfoReturnable<Integer> cir) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return;
        }
        cir.setReturnValue(this.bundleContents.size());
    }

    @ModifyVariable(method = "drawNonEmptyTooltip", at = @At("STORE"))
    private boolean changeIfToDrawExtraItemsCount(boolean original) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return original;
        }
        return false;
    }

    @ModifyConstant(
        method = "drawNonEmptyTooltip",
        constant = @Constant(intValue = 4),
        slice = @Slice(
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/tooltip/BundleTooltipComponent;drawSelectedItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/gui/DrawContext;III)V"
            )
        )
    )
    private int modifyDrawnColumnsCount(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return getModifiedBundleTooltipColumns(this.bundleContents.size());
    }

    @ModifyConstant(method = "drawNonEmptyTooltip", constant = @Constant(intValue = 96))
    private int modifyRightAlignmentForItems(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return 24 * getModifiedBundleTooltipColumns(this.bundleContents.size());
    }

    @ModifyConstant(method = "getXMargin", constant = @Constant(intValue = 96))
    private int getXMargin(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return 24 * getModifiedBundleTooltipColumns(this.bundleContents.size());
    }

    @ModifyConstant(method = "getWidth", constant = @Constant(intValue = 96))
    private int getWidth(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return 24 * getModifiedBundleTooltipColumns(this.bundleContents.size());
    }

    @ModifyConstant(method = "getProgressBarFill", constant = @Constant(intValue = 94))
    private int getProgressBarFill(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return 24 * getModifiedBundleTooltipColumns(this.bundleContents.size()) - 2;
    }

    @ModifyConstant(method = "getRows", constant = @Constant(intValue = 4))
    private int getRows(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return getModifiedBundleTooltipColumns(this.bundleContents.size());
    }

    @ModifyConstant(method = "drawProgressBar", constant = @Constant(intValue = 96))
    private int changeProgressBorderWidth(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return 24 * getModifiedBundleTooltipColumns(this.bundleContents.size());
    }

    @ModifyConstant(method = "drawProgressBar", constant = @Constant(intValue = 48))
    private int changeTextCenterHorizontalOffset(int constant) {
        if (!ImprovedInventoryConfig.expandedBundleTooltip) {
            return constant;
        }
        return 12 * getModifiedBundleTooltipColumns(this.bundleContents.size());
    }

    @Inject(method = "drawProgressBar", at = @At("TAIL"))
    private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext, CallbackInfo ci) {
        if (ImprovedInventoryConfig.bundleProgressBarFraction && bundleContents.getOccupancy().compareTo(Fraction.ZERO) > 0 && bundleContents.getOccupancy().compareTo(Fraction.ONE) < 0) {
            int den = bundleContents.getOccupancy().getDenominator();
            int mult = 64 / den;
            int num = bundleContents.getOccupancy().getNumerator() * mult;
            String fraction = num + "/64";
            int xPos = x + (ImprovedInventoryConfig.expandedBundleTooltip ? 12 * getModifiedBundleTooltipColumns(this.bundleContents.size()) : 48);
            int yPos = y + 3;
            drawContext.drawCenteredTextWithShadow(textRenderer, fraction, xPos, yPos, Colors.WHITE);
        }
    }
}
