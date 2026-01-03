package net.frostbyte.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ColoredArmorBar {
    private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.of("hud/armor_empty");
    
    public static void coloredArmorBarHandler(DrawContext context, PlayerEntity player, int i, int j, int k, int x) {
        int totalArmor = player.getArmor();

        int footArmor = 0;
        Identifier footMaterial = ArmorMaterials.IRON.assetId().getValue();
        if (!player.getInventory().getStack(36).isEmpty()) {
            Item armorItem = player.getInventory().getStack(36).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    footArmor = (int) entry.modifier().value();
                    break;
                }
            }
            EquippableComponent equippableComponent = armorItem.getComponents().getOrDefault(DataComponentTypes.EQUIPPABLE, null);
            Optional<RegistryKey<EquipmentAsset>> equipmentAssetRegistryKey = equippableComponent != null ? equippableComponent.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                footMaterial =  equipmentAssetRegistryKey.get().getValue();
            }
        }

        int legArmor = 0;
        Identifier legMaterial = ArmorMaterials.IRON.assetId().getValue();
        if (!player.getInventory().getStack(37).isEmpty()) {
            Item armorItem = player.getInventory().getStack(37).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    legArmor = (int) entry.modifier().value();
                    break;
                }
            }
            EquippableComponent equippableComponent = armorItem.getComponents().getOrDefault(DataComponentTypes.EQUIPPABLE, null);
            Optional<RegistryKey<EquipmentAsset>> equipmentAssetRegistryKey = equippableComponent != null ? equippableComponent.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                legMaterial =  equipmentAssetRegistryKey.get().getValue();
            }
        }

        int chestArmor = 0;
        Identifier chestMaterial = ArmorMaterials.IRON.assetId().getValue();
        if (!player.getInventory().getStack(38).isEmpty()) {
            Item armorItem = player.getInventory().getStack(38).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    chestArmor = (int) entry.modifier().value();
                    break;
                }
            }
            EquippableComponent equippableComponent = armorItem.getComponents().getOrDefault(DataComponentTypes.EQUIPPABLE, null);
            Optional<RegistryKey<EquipmentAsset>> equipmentAssetRegistryKey = equippableComponent != null ? equippableComponent.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                chestMaterial =  equipmentAssetRegistryKey.get().getValue();
            }
        }

        int headArmor = 0;
        Identifier headMaterial = ArmorMaterials.IRON.assetId().getValue();
        if (!player.getInventory().getStack(39).isEmpty()) {
            Item armorItem = player.getInventory().getStack(39).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    headArmor = (int) entry.modifier().value();
                    break;
                }
            }
            EquippableComponent equippableComponent = armorItem.getComponents().getOrDefault(DataComponentTypes.EQUIPPABLE, null);
            Optional<RegistryKey<EquipmentAsset>> equipmentAssetRegistryKey = equippableComponent != null ? equippableComponent.assetId() : Optional.empty();
            if (equipmentAssetRegistryKey.isPresent()) {
                headMaterial =  equipmentAssetRegistryKey.get().getValue();
            }
        }

        if (totalArmor > 0) {
            int m = i - (j - 1) * k - 10;
            for(int n = 0; n < 10; ++n) {
                int o = x + n * 8;

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_TEXTURE, o, m, 9, 9);

                if (n * 2 + 1 < footArmor) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(footMaterial, ICON_SHAPE.FULL), o, m, 9, 9);
                } else if (n * 2 + 1 == footArmor) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(footMaterial, ICON_SHAPE.HALF1), o, m, 9, 9);
                    if (legArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, ICON_SHAPE.HALF2), o, m, 9, 9);
                    } else if (chestArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.HALF2), o, m, 9, 9);
                    } else if (headArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF2), o, m, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, ICON_SHAPE.FULL), o, m, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, ICON_SHAPE.HALF1), o, m, 9, 9);
                    if (chestArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.HALF2), o, m, 9, 9);
                    } else if (headArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF2), o, m, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor + chestArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.FULL), o, m, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor + chestArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, ICON_SHAPE.HALF1), o, m, 9, 9);
                    if (headArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF2), o, m, 9, 9);
                    }
                }

                else if (n * 2 + 1 < totalArmor ) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.FULL), o, m, 9, 9);
                } else if (n * 2 + 1 == totalArmor) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, ICON_SHAPE.HALF1), o, m, 9, 9);
                }

            }
        }
    }

    private static Identifier getTexture(Identifier material, ICON_SHAPE shape) {
        Identifier texture = Identifier.of(ImprovedInventory.MOD_ID, "hud/" + material.getPath() + "_armor_" + shape.name().toLowerCase());
        if (MinecraftClient.getInstance().getResourceManager().getResource(Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/" + texture.getPath() + ".png")).isPresent()) {
            return texture;
        }
        return Identifier.of(ImprovedInventory.MOD_ID, "hud/iron_armor_" + shape);
    }
    
    enum ICON_SHAPE {
        FULL,
        HALF1,
        HALF2
    }
}
