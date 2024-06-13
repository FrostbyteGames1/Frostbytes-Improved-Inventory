package net.frostbyte.inventory.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.frostbyte.inventory.ImprovedInventory;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.of("hud/armor_empty");
    @Shadow private static final Identifier ARMOR_FULL_TEXTURE = Identifier.of("hud/armor_full");
    @Unique private static final Identifier ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/armor_half_1");
    @Unique private static final Identifier ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/armor_half_2");
    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
        if (ImprovedInventoryConfig.armorBarColors) {
            int totalArmor = player.getArmor();

            int footArmor = 0;
            ArmorMaterial footMaterial = ArmorMaterials.IRON.value();
            if (!player.getInventory().getArmorStack(0).isEmpty() && player.getInventory().getArmorStack(0).getItem() instanceof ArmorItem armorItem) {
                footArmor = armorItem.getProtection();
                footMaterial = armorItem.getMaterial().value();
            }

            int legArmor = 0;
            ArmorMaterial legMaterial = ArmorMaterials.IRON.value();
            if (!player.getInventory().getArmorStack(1).isEmpty() && player.getInventory().getArmorStack(1).getItem() instanceof ArmorItem armorItem) {
                legArmor = armorItem.getProtection();
                legMaterial = armorItem.getMaterial().value();
            }

            int chestArmor = 0;
            ArmorMaterial chestMaterial = ArmorMaterials.IRON.value();
            if (!player.getInventory().getArmorStack(2).isEmpty() && player.getInventory().getArmorStack(2).getItem() instanceof ArmorItem armorItem) {
                chestArmor = armorItem.getProtection();
                chestMaterial = armorItem.getMaterial().value();
            }

            int headArmor = 0;
            ArmorMaterial headMaterial = ArmorMaterials.IRON.value();
            if (!player.getInventory().getArmorStack(3).isEmpty() && player.getInventory().getArmorStack(3).getItem() instanceof ArmorItem armorItem) {
                headArmor = armorItem.getProtection();
                headMaterial = armorItem.getMaterial().value();
            }

            if (totalArmor > 0) {
                RenderSystem.enableBlend();
                int m = i - (j - 1) * k - 10;
                for(int n = 0; n < 10; ++n) {
                    int o = x + n * 8;

                    RenderSystem.setShaderColor(1,1,1,1);
                    context.drawGuiTexture(ARMOR_EMPTY_TEXTURE, o, m, 9, 9);

                    if (n * 2 + 1 < footArmor) {
                        setShaderColorFromArmorMaterial(footMaterial);
                        context.drawGuiTexture(ARMOR_FULL_TEXTURE, o, m, 9, 9);
                    } else if (n * 2 + 1 == footArmor) {
                        setShaderColorFromArmorMaterial(footMaterial);
                        context.drawGuiTexture(ARMOR_HALF_1_TEXTURE, o, m, 9, 9);
                        if (legArmor > 0) {
                            setShaderColorFromArmorMaterial(legMaterial);
                            context.drawGuiTexture(ARMOR_HALF_2_TEXTURE, o, m, 9, 9);
                        } else if (chestArmor > 0) {
                            setShaderColorFromArmorMaterial(chestMaterial);
                            context.drawGuiTexture(ARMOR_HALF_2_TEXTURE, o, m, 9, 9);
                        } else if (headArmor > 0) {
                            setShaderColorFromArmorMaterial(headMaterial);
                            context.drawGuiTexture(ARMOR_HALF_2_TEXTURE, o, m, 9, 9);
                        }
                    }

                    else if (n * 2 + 1 < (footArmor + legArmor)) {
                        setShaderColorFromArmorMaterial(legMaterial);
                        context.drawGuiTexture(ARMOR_FULL_TEXTURE, o, m, 9, 9);
                    } else if (n * 2 + 1 == (footArmor + legArmor)) {
                        setShaderColorFromArmorMaterial(legMaterial);
                        context.drawGuiTexture(ARMOR_HALF_1_TEXTURE, o, m, 9, 9);
                        if (chestArmor > 0) {
                            setShaderColorFromArmorMaterial(chestMaterial);
                            context.drawGuiTexture(ARMOR_HALF_2_TEXTURE, o, m, 9, 9);
                        } else if (headArmor > 0) {
                            setShaderColorFromArmorMaterial(headMaterial);
                            context.drawGuiTexture(ARMOR_HALF_2_TEXTURE, o, m, 9, 9);
                        }
                    }

                    else if (n * 2 + 1 < (footArmor + legArmor + chestArmor)) {
                        setShaderColorFromArmorMaterial(chestMaterial);
                        context.drawGuiTexture(ARMOR_FULL_TEXTURE, o, m, 9, 9);
                    } else if (n * 2 + 1 == (footArmor + legArmor + chestArmor)) {
                        setShaderColorFromArmorMaterial(chestMaterial);
                        context.drawGuiTexture(ARMOR_HALF_1_TEXTURE, o, m, 9, 9);
                        if (headArmor > 0) {
                            setShaderColorFromArmorMaterial(headMaterial);
                            context.drawGuiTexture(ARMOR_HALF_2_TEXTURE, o, m, 9, 9);
                        }
                    }

                    else if (n * 2 + 1 < totalArmor ) {
                        setShaderColorFromArmorMaterial(headMaterial);
                        context.drawGuiTexture(ARMOR_FULL_TEXTURE, o, m, 9, 9);
                    } else if (n * 2 + 1 == totalArmor) {
                        setShaderColorFromArmorMaterial(headMaterial);
                        context.drawGuiTexture(ARMOR_HALF_1_TEXTURE, o, m, 9, 9);
                    }

                }
                RenderSystem.setShaderColor(1,1,1,1);
                RenderSystem.disableBlend();
            }
            ci.cancel();
        }
    }

    @Unique
    private static void setShaderColorFromArmorMaterial(ArmorMaterial material) {
        // chain, iron, and modded materials
        Color color = new Color(255, 255, 255);

        // vanilla materials
        if (material == ArmorMaterials.LEATHER.value()) {
            color = new Color(141, 85, 55);
        }
        if (material == ArmorMaterials.GOLD.value()) {
            color = new Color(255, 210, 0);
        }
        if (material == ArmorMaterials.TURTLE.value()) {
            color = new Color(78, 227, 82);
        }
        if (material == ArmorMaterials.DIAMOND.value()) {
            color = new Color(0, 255, 246);
        }
        if (material == ArmorMaterials.NETHERITE.value()) {
            color = new Color(168, 148, 154);
        }

        // update shader
        RenderSystem.setShaderColor((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 1);
    }

}
