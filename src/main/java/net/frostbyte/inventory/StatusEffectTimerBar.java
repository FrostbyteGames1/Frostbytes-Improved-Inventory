package net.frostbyte.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import net.frostbyte.inventory.duck.StatusEffectInstanceDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class StatusEffectTimerBar {
    private static final Identifier EFFECT_BACKGROUND_AMBIENT_TEXTURE = Identifier.ofVanilla("hud/effect_background_ambient");
    private static final Identifier EFFECT_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/effect_background");

    @SuppressWarnings("DataFlowIssue")
    public static void statusEffectTimerHandler(DrawContext context, MinecraftClient client) {
        Collection<StatusEffectInstance> collection = client.player.getStatusEffects();
        if (!collection.isEmpty() && (client.currentScreen == null || !client.currentScreen.showsStatusEffects())) {
            int i = 0;
            int j = 0;
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
                if (statusEffectInstance.shouldShowIcon()) {
                    int k = context.getScaledWindowWidth();
                    int l = 1;
                    if (client.isDemo()) {
                        l += 15;
                    }

                    if (registryEntry.value().isBeneficial()) {
                        ++i;
                        k -= 25 * i;
                    } else {
                        ++j;
                        k -= 25 * j;
                        l += 26;
                    }

                    float f = 1.0F;
                    if (statusEffectInstance.isAmbient()) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_AMBIENT_TEXTURE, k, l, 24, 24);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_TEXTURE, k, l, 24, 24);
                        if (statusEffectInstance.isDurationBelow(200)) {
                            int m = statusEffectInstance.getDuration();
                            int n = 10 - m / 20;
                            f = MathHelper.clamp((float) m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) m * 3.1415927F / 5.0F) * MathHelper.clamp((float) n / 10.0F * 0.25F, 0.0F, 0.25F);
                            f = MathHelper.clamp(f, 0.0F, 1.0F);
                        }
                        if (!statusEffectInstance.isInfinite()) {
                            float percentage = ((float) statusEffectInstance.getDuration()) / ((float) ((StatusEffectInstanceDuck) statusEffectInstance).getMaxDuration());
                            context.fill(k + 3, l + 21, k + 22, l + 22, Color.BLACK.getRGB());
                            context.fill(k + 3, l + 21, k + 3 + (int) (percentage * 18), l + 22, statusEffectInstance.getEffectType().value().getColor() | 0xff000000);
                        }
                    }

                    final float finalF = f;
                    final int finalK = k;
                    final int finalL = l;
                    list.add(() -> {
                        int kx = ColorHelper.getWhite(finalF);
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(registryEntry), finalK + 3, finalL + 3, 18, 18, kx);
                    });
                }
            }

            list.forEach(Runnable::run);
        }
    }
}
