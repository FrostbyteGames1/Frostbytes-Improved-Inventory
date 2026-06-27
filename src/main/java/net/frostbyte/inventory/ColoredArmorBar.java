package net.frostbyte.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Optional;

public class ColoredArmorBar {
    private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/armor_empty.png");
    
    public static void coloredArmorBarHandler(GuiGraphicsExtractor graphics, Player player, int i, int j, int k, int x) {
        int totalArmor = player.getArmorValue();

        int footArmor = 0;
        Identifier footMaterial = ArmorMaterials.IRON.assetId().identifier();
        if (!player.getInventory().getItem(36).isEmpty()) {
            Item armorItem = player.getInventory().getItem(36).getItem();
            for (ItemAttributeModifiers.Entry entry : armorItem.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
                if (entry.attribute() == Attributes.ARMOR) {
                    footArmor = (int) entry.modifier().amount();
                    break;
                }
            }
            Equippable equippable = armorItem.components().get(DataComponents.EQUIPPABLE);
            Optional<ResourceKey<EquipmentAsset>> equipmentAssetRegistryKey = equippable != null ? equippable.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                footMaterial =  equipmentAssetRegistryKey.get().identifier();
            }
        }

        int legArmor = 0;
        Identifier legMaterial = ArmorMaterials.IRON.assetId().identifier();
        if (!player.getInventory().getItem(37).isEmpty()) {
            Item armorItem = player.getInventory().getItem(37).getItem();
            for (ItemAttributeModifiers.Entry entry : armorItem.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
                if (entry.attribute() == Attributes.ARMOR) {
                    legArmor = (int) entry.modifier().amount();
                    break;
                }
            }
            Equippable equippable = armorItem.components().get(DataComponents.EQUIPPABLE);
            Optional<ResourceKey<EquipmentAsset>> equipmentAssetRegistryKey = equippable != null ? equippable.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                legMaterial =  equipmentAssetRegistryKey.get().identifier();
            }
        }

        int chestArmor = 0;
        Identifier chestMaterial = ArmorMaterials.IRON.assetId().identifier();
        if (!player.getInventory().getItem(38).isEmpty()) {
            Item armorItem = player.getInventory().getItem(38).getItem();
            for (ItemAttributeModifiers.Entry entry : armorItem.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
                if (entry.attribute() == Attributes.ARMOR) {
                    chestArmor = (int) entry.modifier().amount();
                    break;
                }
            }
            Equippable equippable = armorItem.components().get(DataComponents.EQUIPPABLE);
            Optional<ResourceKey<EquipmentAsset>> equipmentAssetRegistryKey = equippable != null ? equippable.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                chestMaterial =  equipmentAssetRegistryKey.get().identifier();
            }
        }

        int headArmor = 0;
        Identifier headMaterial = ArmorMaterials.IRON.assetId().identifier();
        if (!player.getInventory().getItem(39).isEmpty()) {
            Item armorItem = player.getInventory().getItem(39).getItem();
            for (ItemAttributeModifiers.Entry entry : armorItem.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
                if (entry.attribute() == Attributes.ARMOR) {
                    headArmor = (int) entry.modifier().amount();
                    break;
                }
            }
            Equippable equippable = armorItem.components().get(DataComponents.EQUIPPABLE);
            Optional<ResourceKey<EquipmentAsset>> equipmentAssetRegistryKey = equippable != null ? equippable.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                headMaterial =  equipmentAssetRegistryKey.get().identifier();
            }
        }

        if (totalArmor > 0) {
            int m = i - (j - 1) * k - 10;
            for(int n = 0; n < 10; ++n) {
                int o = x + n * 8;

                graphics.blit(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_TEXTURE, o, m, 0, 0, 9, 9, 9, 9);

                if (n * 2 + 1 < footArmor) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(footMaterial, ICON_SHAPE.FULL), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == footArmor) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(footMaterial, ICON_SHAPE.HALF1), o, m, 0, 0, 9, 9, 9, 9);
                    if (legArmor > 0) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, ICON_SHAPE.HALF2), o, m, 0, 0, 9, 9, 9, 9);
                    } else if (chestArmor > 0) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.HALF2), o, m, 0, 0, 9, 9, 9, 9);
                    } else if (headArmor > 0) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF2), o, m, 0, 0, 9, 9, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, ICON_SHAPE.FULL), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, ICON_SHAPE.HALF1), o, m, 0, 0, 9, 9, 9, 9);
                    if (chestArmor > 0) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.HALF2), o, m, 0, 0, 9, 9, 9, 9);
                    } else if (headArmor > 0) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF2), o, m, 0, 0, 9, 9, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor + chestArmor)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.FULL), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor + chestArmor)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.HALF1), o, m, 0, 0, 9, 9, 9, 9);
                    if (headArmor > 0) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF2), o, m, 0, 0, 9, 9, 9, 9);
                    }
                }

                else if (n * 2 + 1 < totalArmor ) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.FULL), o, m, 0, 0, 9, 9, 9, 9);
                } else if (n * 2 + 1 == totalArmor) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF1), o, m, 0, 0, 9, 9, 9, 9);
                }

            }
        }
    }

    private static Identifier getTexture(Identifier material, ICON_SHAPE shape) {
        Identifier texture = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "textures/gui/sprites/hud/" + material.getPath() + "_armor_" + shape.name().toLowerCase() + ".png");
        if (Minecraft.getInstance().getResourceManager().getResource(texture).isPresent()) {
            return texture;
        }
        return Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "textures/gui/sprites/hud/iron_armor_" + shape.name().toLowerCase() + ".png");
    }
    
    enum ICON_SHAPE {
        FULL,
        HALF1,
        HALF2
    }
}
