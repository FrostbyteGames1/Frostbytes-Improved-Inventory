package net.frostbyte.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.util.Identifier;

public class ColoredArmorBar {
    
    public static void coloredArmorBarHandler(DrawContext context, PlayerEntity player, int i, int j, int k, int x) {
        int totalArmor = player.getArmor();

        int footArmor = 0;
        String footMaterial = ArmorMaterials.IRON.getName();
        if (!player.getInventory().getStack(36).isEmpty() && player.getInventory().getStack(36).getItem() instanceof ArmorItem armorItem) {
            footArmor = armorItem.getProtection();
            footMaterial = armorItem.getMaterial().getName();
        }

        int legArmor = 0;
        String legMaterial = ArmorMaterials.IRON.getName();
        if (!player.getInventory().getStack(37).isEmpty() && player.getInventory().getStack(37).getItem() instanceof ArmorItem armorItem) {
            legArmor = armorItem.getProtection();
            legMaterial = armorItem.getMaterial().getName();
        }

        int chestArmor = 0;
        String chestMaterial = ArmorMaterials.IRON.getName();
        if (!player.getInventory().getStack(38).isEmpty() && player.getInventory().getStack(38).getItem() instanceof ArmorItem armorItem) {
            chestArmor = armorItem.getProtection();
            chestMaterial = armorItem.getMaterial().getName();
        }

        int headArmor = 0;
        String headMaterial = ArmorMaterials.IRON.getName();
        if (!player.getInventory().getStack(39).isEmpty() && player.getInventory().getStack(39).getItem() instanceof ArmorItem armorItem) {
            headArmor = armorItem.getProtection();
            headMaterial = armorItem.getMaterial().getName();
        }

        if (totalArmor > 0) {
            int m = i - (j - 1) * k - 10;
            for(int n = 0; n < 10; ++n) {
                int o = x + n * 8;

                if (n * 2 + 1 < footArmor) {
                    context.drawTexture(getTexture(footMaterial, "full"), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == footArmor) {
                    context.drawTexture(getTexture(footMaterial, "half1"), o, m, 0, 0, 9, 9, 9, 9);
                    if (legArmor > 0) {
                        context.drawTexture(getTexture(legMaterial, "half2"), o, m, 0, 0, 9, 9, 9, 9);
                    } else if (chestArmor > 0) {
                        context.drawTexture(getTexture(chestMaterial, "half2"), o, m, 0, 0, 9, 9, 9, 9);
                    } else if (headArmor > 0) {
                        context.drawTexture(getTexture(headMaterial, "half2"), o, m, 0, 0, 9, 9, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor)) {
                    context.drawTexture(getTexture(legMaterial, "full"), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor)) {
                    context.drawTexture(getTexture(legMaterial, "half1"), o, m, 0, 0, 9, 9, 9, 9);
                    if (chestArmor > 0) {
                        context.drawTexture(getTexture(chestMaterial, "half2"), o, m, 0, 0, 9, 9, 9, 9);
                    } else if (headArmor > 0) {
                        context.drawTexture(getTexture(headMaterial, "half2"), o, m, 0, 0, 9, 9, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor + chestArmor)) {
                    context.drawTexture(getTexture(chestMaterial, "full"), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor + chestArmor)) {
                    context.drawTexture(getTexture(chestMaterial, "half1"), o, m, 0, 0, 9, 9, 9, 9);
                    if (headArmor > 0) {
                        context.drawTexture(getTexture(headMaterial, "half2"), o, m, 0, 0, 9, 9, 9, 9);
                    }
                }

                else if (n * 2 + 1 < totalArmor ) {
                    context.drawTexture(getTexture(headMaterial, "full"), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == totalArmor) {
                    context.drawTexture(getTexture(headMaterial, "half1"), o, m, 0, 0, 9, 9, 9, 9);
                }

            }
        }
    }

    private static Identifier getTexture(String material, String shape) {
        Identifier texture = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/hud/" + material + "_armor_" + shape + ".png");
        if (MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent()) {
            return texture;
        }
        return Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/hud/iron_armor_" + shape + ".png");
    }
}
