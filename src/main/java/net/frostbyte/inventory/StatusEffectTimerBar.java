package net.frostbyte.inventory;

import com.google.common.collect.Ordering;
import net.frostbyte.inventory.duck.StatusEffectInstanceDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.awt.*;
import java.util.Collection;

public class StatusEffectTimerBar {

    @SuppressWarnings("DataFlowIssue")
    public static void statusEffectTimerHandler(DrawContext context, MinecraftClient client) {
        Collection<StatusEffectInstance> collection = client.player.getStatusEffects();
        if (!collection.isEmpty() && client.currentScreen != null) {
            if (client.currentScreen instanceof AbstractInventoryScreen<?> abstractInventoryScreen && abstractInventoryScreen.hideStatusEffectHud()) {
                return;
            }
            int i = 0;
            int j = 0;

            for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                StatusEffect registryEntry = statusEffectInstance.getEffectType();
                if (statusEffectInstance.shouldShowIcon()) {
                    int k = context.getScaledWindowWidth();
                    int l = 1;
                    if (client.isDemo()) {
                        l += 15;
                    }

                    if (registryEntry.isBeneficial()) {
                        ++i;
                        k -= 25 * i;
                    } else {
                        ++j;
                        k -= 25 * j;
                        l += 26;
                    }

                    if (!statusEffectInstance.isAmbient() && !statusEffectInstance.isInfinite()) {
                        float percentage = ((float) statusEffectInstance.getDuration()) / ((float) ((StatusEffectInstanceDuck) statusEffectInstance).getMaxDuration());
                        context.fill(k + 3, l + 21, k + 22, l + 22, Color.BLACK.getRGB());
                        context.fill(k + 3, l + 21, k + 3 + (int) (percentage * 18), l + 22, statusEffectInstance.getEffectType().getColor() | 0xff000000);
                    }
                }
            }
        }
    }
}
