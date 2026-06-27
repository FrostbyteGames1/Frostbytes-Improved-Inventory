package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientBundleTooltip.class)
public abstract class ClientBundleTooltipMixin {
    @Shadow
    @Final
    private BundleContents contents;
    @Unique
    @Final
    private final int SLOT_SIZE = 20;

    @Shadow
    protected abstract List<ItemStackTemplate> getShownItems(final int amountOfItemsToShow);

    @Shadow
    public static int getProgressBarFill(final Fraction weight) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static @Nullable Component getProgressBarFillText(final Fraction weight) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    protected abstract void extractSlot(final int slotNumber, final int drawX, final int drawY, final List<ItemStackTemplate> shownItems, final int slotIndex, final Font font, final GuiGraphicsExtractor graphics);

    @Shadow
    protected abstract void extractSelectedItemTooltip(final Font font, final GuiGraphicsExtractor graphics, final int x, final int y, final int w);

    @Shadow
    protected abstract int itemGridHeight();

    @Shadow
    @Final
    private static Component BUNDLE_EMPTY_TEXT;

    @Shadow
    @Final
    private static Component BUNDLE_FULL_TEXT;

    @Shadow
    private static Identifier getProgressBarTexture(Fraction weight) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    @Final
    private static Identifier PROGRESSBAR_BORDER_SPRITE;

    @Shadow
    private static boolean shouldRenderItemSlot(List<? extends ItemInstance> shownItems, int slotNumber) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    protected abstract int slotCount();

    @Unique
    private int getColumns() {
        return (int) Math.max(4, Math.ceil(Math.sqrt(this.contents.getNumberOfItemsToShow())));
    }

    @Unique
    private int getRows() {
        return Math.ceilDiv(this.contents.getNumberOfItemsToShow(), this.getColumns());
    }

    @Inject(method = "getAmountOfHiddenItems", at = @At("HEAD"), cancellable = true)
    private void getAmountOfHiddenItems(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
    public void getWidth(Font font, CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(this.SLOT_SIZE * getColumns());
        }
    }

    @Inject(method = "itemGridHeight", at = @At("HEAD"), cancellable = true)
    private void itemGridHeight(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(getRows() * this.SLOT_SIZE);
        }
    }

    @Final
    @Inject(method = "gridSizeY", at = @At("HEAD"), cancellable = true)
    public final void gridSizeY(CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(Mth.positiveCeilDiv(this.slotCount(), this.getRows()));
        }
    }

    @Inject(method = "extractBundleWithItemsTooltip", at = @At("HEAD"), cancellable = true)
    private void extractBundleWithItemsTooltip(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics, Fraction weight, CallbackInfo ci) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            List<ItemStackTemplate> shownItems = this.getShownItems(this.contents.getNumberOfItemsToShow());
            int xStartPos = x + ((w - getColumns() * SLOT_SIZE) / 2) + (getColumns() * SLOT_SIZE);
            int yStartPos = y + getRows() * SLOT_SIZE;
            int slotNumber = 1;

            for(int rowNumber = 1; rowNumber <= getRows(); ++rowNumber) {
                for(int columnNumber = 1; columnNumber <= getColumns(); ++columnNumber) {
                    int drawX = xStartPos - columnNumber * SLOT_SIZE;
                    int drawY = yStartPos - rowNumber * SLOT_SIZE;
                    if (shouldRenderItemSlot(shownItems, slotNumber)) {
                        this.extractSlot(slotNumber, drawX, drawY, shownItems, slotNumber, font, graphics);
                        ++slotNumber;
                    }
                }
            }

            this.extractSelectedItemTooltip(font, graphics, x, y, w);
            extractProgressbar(x + ((w - (getColumns() * SLOT_SIZE)) / 2), y + this.itemGridHeight() + 4, font, graphics, weight, ci);
            ci.cancel();
        }
    }

    @Inject(method = "getProgressBarFill", at = @At("HEAD"), cancellable = true)
    private static void getProgressBarFill(Fraction weight, CallbackInfoReturnable<Integer> cir) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            cir.setReturnValue(Math.clamp(Mth.mulAndTruncate(weight, 20 * 4 - 2), 0, 20 * 4 - 2));
        }
    }

    @Inject(method = "getProgressBarFillText", at = @At("HEAD"), cancellable = true)
    private static void getProgressBarFillText(final Fraction weight, CallbackInfoReturnable<Component> cir) {
        if (ImprovedInventoryConfig.bundleProgressBarFraction) {
            if (weight.equals(Fraction.ZERO)) {
                cir.setReturnValue(BUNDLE_EMPTY_TEXT);
            } else if (weight.equals(Fraction.ONE)) {
                cir.setReturnValue(BUNDLE_FULL_TEXT);
            } else {
                cir.setReturnValue(Component.literal(weight.getNumerator() * (64 / weight.getDenominator()) + "/64"));
            }
        }
    }

    @Inject(method = "extractProgressbar", at = @At("HEAD"), cancellable = true)
    private static void extractProgressbar(int x, int y, Font font, GuiGraphicsExtractor graphics, Fraction weight, CallbackInfo ci) {
        if (ImprovedInventoryConfig.expandedBundleTooltip) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, getProgressBarTexture(weight), x + 1, y, getProgressBarFill(weight), 13);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESSBAR_BORDER_SPRITE, x, y, 20 * 4, 13);
            Component progressBarFillText = getProgressBarFillText(weight);
            if (progressBarFillText != null) {
                graphics.centeredText(font, progressBarFillText, x + 20 * 4 / 2, y + 3, -1);
            }
            ci.cancel();
        }
    }
}
