package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BundleTooltipComponent.class)
public abstract class BundleTooltipComponentMixin {
    @Shadow
    @Final
    private static Identifier BUNDLE_PROGRESS_BAR_BORDER_TEXTURE;
    @Shadow
    @Final
    private BundleContentsComponent bundleContents;
    @Shadow
    abstract List<ItemStack> firstStacksInContents(int numberOfStacksShown);
    @Shadow
    abstract void drawSelectedItemTooltip(TextRenderer textRenderer, DrawContext drawContext, int x, int y, int width);
    @Shadow
    abstract void drawItem(int index, int x, int y, List<ItemStack> stacks, int seed, TextRenderer textRenderer, DrawContext drawContext);
    @Shadow
    abstract int getRowsHeight();
    @Shadow
    abstract void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext);
    @Shadow
    @Final
    private static Text BUNDLE_FULL;
    @Shadow
    @Final
    private static Text BUNDLE_EMPTY;
    @Shadow
    abstract Identifier getProgressBarFillTexture();
    @Unique
    @Final
    private final int SLOT_SIZE = 20;

    @Unique
    private int getColumns() {
        return Math.max(4, MathHelper.ceil(Math.sqrt(this.bundleContents.getNumberOfStacksShown())));
    }

    @Inject(method = "getNumVisibleSlots", at = @At("HEAD"), cancellable = true)
    private void getNumVisibleSlots(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(this.bundleContents.getNumberOfStacksShown());
        }
    }

    @Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
    public void getWidth(TextRenderer textRenderer, CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(this.SLOT_SIZE * getColumns());
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "getRowsHeight", at = @At("HEAD"), cancellable = true)
    private void getRowsHeight(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(((BundleTooltipComponent) (Object) this).getRows() * this.SLOT_SIZE);
        }
    }

    @Final
    @Inject(method = "getRows", at = @At("HEAD"), cancellable = true)
    public final void getRows(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(MathHelper.ceilDiv(this.bundleContents.getNumberOfStacksShown(), this.getColumns()));
        }
    }

    @Inject(method = "getXMargin", at = @At("HEAD"), cancellable = true)
    private void getXMargin(int width, CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue((width - this.SLOT_SIZE * getColumns()) / 2);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "drawNonEmptyTooltip", at = @At("HEAD"), cancellable = true)
    private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context, CallbackInfo ci) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            List<ItemStack> list = this.firstStacksInContents(this.bundleContents.getNumberOfStacksShown());
            int i = x + ((BundleTooltipComponent) (Object) this).getXMargin(width) + (this.SLOT_SIZE * this.getColumns()) - 2;
            int j = y + ((BundleTooltipComponent) (Object) this).getRows() * this.SLOT_SIZE;
            int k = 1;

            for(int l = 1; l <= ((BundleTooltipComponent) (Object) this).getRows(); ++l) {
                for(int m = 1; m <= this.getColumns(); ++m) {
                    int n = i - m * this.SLOT_SIZE;
                    int o = j - l * this.SLOT_SIZE;
                    if (k <= list.size()) {
                        this.drawItem(k, n, o, list, k, textRenderer, context);
                        ++k;
                    }
                }
            }

            this.drawSelectedItemTooltip(textRenderer, context, x, y, width);
            this.drawProgressBar(x + ((BundleTooltipComponent) (Object) this).getXMargin(width), y + this.getRowsHeight() + 4, textRenderer, context);
            ci.cancel();
        }
    }

    @Inject(method = "getProgressBarFill", at = @At("HEAD"), cancellable = true)
    private void getProgressBarFill(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(MathHelper.clamp(MathHelper.multiplyFraction(this.bundleContents.getOccupancy(), this.SLOT_SIZE * getColumns() - 2), 0, this.SLOT_SIZE * getColumns() - 2));
        }
    }

    @Inject(method = "getProgressBarLabel", at = @At("HEAD"), cancellable = true)
    private void getProgressBarLabel(CallbackInfoReturnable<Text> cir) {
        if (ImprovedInventoryConfig.bundleProgressBarFraction) {
            if (this.bundleContents.isEmpty()) {
                cir.setReturnValue(BUNDLE_EMPTY);
            } else if (this.bundleContents.getOccupancy().compareTo(Fraction.ONE) == 0) {
                cir.setReturnValue(BUNDLE_FULL);
            } else {
                cir.setReturnValue(Text.of(bundleContents.getOccupancy().getNumerator() * (64 / bundleContents.getOccupancy().getDenominator()) + "/64"));
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "drawProgressBar", at = @At("HEAD"), cancellable = true)
    private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext, CallbackInfo ci) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getProgressBarFillTexture(), x + 1, y, ((BundleTooltipComponent) (Object) this).getProgressBarFill(), 13);
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_PROGRESS_BAR_BORDER_TEXTURE, x, y, this.SLOT_SIZE * getColumns(), 13);
            Text text = ((BundleTooltipComponent) (Object) this).getProgressBarLabel();
            if (text != null) {
                drawContext.drawCenteredTextWithShadow(textRenderer, text, x + this.SLOT_SIZE * getColumns() / 2, y + 3, -1);
            }
            ci.cancel();
        }
    }
}
