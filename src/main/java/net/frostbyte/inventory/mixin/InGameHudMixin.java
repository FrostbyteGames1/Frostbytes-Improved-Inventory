package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.*;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
        if (ImprovedInventoryConfig.armorBarColors) {
            ColoredArmorBar.coloredArmorBarHandler(context, player, i, j, k, x);
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ImprovedInventoryConfig.statusEffectTimer) {
            StatusEffectTimerBar.statusEffectTimerHandler(context, this.client);
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        // Slot Cycle
        if (!SlotCycler.cycleUpKey.isUnbound() && !SlotCycler.cycleDownKey.isUnbound()) {
            SlotCycler.slotCycleHandler(client);
        }

        // Tool Select
        if (ImprovedInventoryConfig.toolSelect) {
            ToolSelector.toolSelectHandler(client);
        }

        // Stack Refill
        if (ImprovedInventoryConfig.stackRefill) {
            StackRefiller.tryRefillStack(client);
        }

        // Zoom
        if (!Zoom.zoomKey.isUnbound()) {
            Zoom.zoomHandler(client);
        }

        // Gamma
        if (!Gamma.gammaKey.isUnbound()) {
            Gamma.gammaHandler(client);
        }

        // Nearby Container Viewer
        if (ImprovedInventoryConfig.containerTab) {
            NearbyContainerViewer.nearbyContainerViewerHandler(client);
        }
    }
}