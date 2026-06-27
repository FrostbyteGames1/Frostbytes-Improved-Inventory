package net.frostbyte.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import net.frostbyte.inventory.duck.MobEffectInstanceDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class StatusEffectTimerBar {

    public static final Identifier EFFECT_BACKGROUND_AMBIENT_SPRITE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/effect_background_ambient.png");
    public static final Identifier EFFECT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/effect_background.png");

    @SuppressWarnings("DataFlowIssue")
    public static void statusEffectTimerHandler(GuiGraphicsExtractor graphics, Minecraft client) {
        Collection<MobEffectInstance> collection = client.player.getActiveEffects();
        if (!collection.isEmpty() && (client.screen == null || !client.screen.showsActiveEffects())) {
            int i = 0;
            int j = 0;
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            for (MobEffectInstance MobEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                Holder<MobEffect> effectHolder = MobEffectInstance.getEffect();
                if (MobEffectInstance.showIcon()) {
                    int k = client.getWindow().getGuiScaledWidth();
                    int l = 1;
                    if (client.isDemo()) {
                        l += 15;
                    }

                    if (effectHolder.value().isBeneficial()) {
                        ++i;
                        k -= 25 * i;
                    } else {
                        ++j;
                        k -= 25 * j;
                        l += 26;
                    }

                    float f = 1.0F;
                    if (MobEffectInstance.isAmbient()) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_AMBIENT_SPRITE, k, l, 0, 0, 24, 24, 24, 24);
                    } else {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SPRITE, k, l, 0, 0, 24, 24, 24, 24);
                        if (MobEffectInstance.endsWithin(200)) {
                            int m = MobEffectInstance.getDuration();
                            int n = 10 - m / 20;
                            f = Mth.clamp((float) m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float) m * 3.1415927F / 5.0F) * Mth.clamp((float) n / 10.0F * 0.25F, 0.0F, 0.25F);
                            f = Mth.clamp(f, 0.0F, 1.0F);
                        }
                        if (!MobEffectInstance.isInfiniteDuration()) {
                            float percentage = ((float) MobEffectInstance.getDuration()) / ((float) ((MobEffectInstanceDuck) MobEffectInstance).getMaxDuration());
                            graphics.fill(k + 3, l + 21, k + 22, l + 22, Color.BLACK.getRGB());
                            graphics.fill(k + 3, l + 21, k + 3 + (int) (percentage * 18), l + 22, MobEffectInstance.getEffect().value().getColor() | 0xff000000);
                        }
                    }

                    final int finalK = k;
                    final int finalL = l;
                    float finalF = f;
                    list.add(() -> graphics.blit(RenderPipelines.GUI_TEXTURED, getMobEffectSprite(effectHolder), finalK + 3, finalL + 3, 0, 0, 18, 18, 18, 18, ARGB.white(finalF)));
                }
            }

            list.forEach(Runnable::run);
        }
    }

    public static Identifier getMobEffectSprite(final Holder<MobEffect> effect) {
        return effect.unwrapKey().map(ResourceKey::identifier).map((id) -> id.withPrefix("textures/mob_effect/").withSuffix(".png")).orElseGet(MissingTextureAtlasSprite::getLocation);
    }
}
