package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.*;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractArmor", at = @At("HEAD"), cancellable = true)
    private static void extractArmor(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft, CallbackInfo ci) {
        if (ImprovedInventoryConfig.armorBarColors) {
            ColoredArmorBar.coloredArmorBarHandler(graphics, player, yLineBase, numHealthRows, healthRowHeight, xLeft);
            ci.cancel();
        }
    }

    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void extractEffects(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (ImprovedInventoryConfig.statusEffectTimer) {
            StatusEffectTimerBar.statusEffectTimerHandler(graphics, minecraft);
            ci.cancel();
        }
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        // Slot Cycle
        SlotCycler.slotCycleHandler(minecraft);

        // Tool Select
        if (ImprovedInventoryConfig.toolSelect) {
            ToolSelector.toolSelectHandler(minecraft);
        }

        // Stack Refill
        if (ImprovedInventoryConfig.stackRefill) {
            StackRefiller.tryRefillStack(minecraft);
        }

        // Stack Refill Preview
        if (ImprovedInventoryConfig.stackRefillPreview) {
            StackRefiller.updateRefillPreview(minecraft);
        }

        // Zoom
        if (!Zoom.zoomKey.isUnbound()) {
            Zoom.zoomHandler(minecraft);
        }

        // Gamma
        if (!Gamma.gammaKey.isUnbound()) {
            Gamma.gammaHandler(minecraft);
        }

        // Nearby Container Viewer
        if (ImprovedInventoryConfig.containerTab) {
            NearbyContainerViewer.nearbyContainerViewerHandler(minecraft);
        }
    }
}