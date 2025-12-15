package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.*;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledHeight;
    @Shadow private int renderHealthValue;
    @Shadow @Final private Random random;
    @Shadow private int ticks;
    @Shadow private int scaledWidth;

    @Inject(method = "renderStatusBars", at = @At("TAIL"))
    private void renderStatusBars(DrawContext context, CallbackInfo ci) {
        if (ImprovedInventoryConfig.armorBarColors && this.client.player != null) {
            this.client.getProfiler().push("armor");

            int i = MathHelper.ceil(this.client.player.getHealth());
            int j = this.renderHealthValue;
            this.random.setSeed(this.ticks * 312871L);
            int m = this.scaledWidth / 2 - 91;
            int o = this.scaledHeight - 39;
            float f = Math.max((float)this.client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
            int p = MathHelper.ceil(this.client.player.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);

            ColoredArmorBar.coloredArmorBarHandler(context, this.client.player, o, q, r, m);

            this.client.getProfiler().pop();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("TAIL"))
    private void renderStatusEffectOverlay(DrawContext context, CallbackInfo ci) {
        if (ImprovedInventoryConfig.statusEffectTimer) {
            StatusEffectTimerBar.statusEffectTimerHandler(context, this.client);
        }
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
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