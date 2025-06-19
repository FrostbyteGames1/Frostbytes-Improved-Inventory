package net.frostbyte.inventory;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.util.Identifier;

public class ColoredArmorBar {
    private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.of("hud/armor_empty");
    private static final Identifier IRON_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/iron_armor_full");
    private static final Identifier IRON_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/iron_armor_half_1");
    private static final Identifier IRON_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/iron_armor_half_2");
    private static final Identifier CHAIN_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/chain_armor_full");
    private static final Identifier CHAIN_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/chain_armor_half_1");
    private static final Identifier CHAIN_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/chain_armor_half_2");
    private static final Identifier TURTLE_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/turtle_armor_full");
    private static final Identifier TURTLE_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/turtle_armor_half_1");
    private static final Identifier TURTLE_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/turtle_armor_half_2");
    private static final Identifier LEATHER_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/leather_armor_full");
    private static final Identifier LEATHER_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/leather_armor_half_1");
    private static final Identifier LEATHER_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/leather_armor_half_2");
    private static final Identifier GOLD_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/gold_armor_full");
    private static final Identifier GOLD_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/gold_armor_half_1");
    private static final Identifier GOLD_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/gold_armor_half_2");
    private static final Identifier DIAMOND_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/diamond_armor_full");
    private static final Identifier DIAMOND_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/diamond_armor_half_1");
    private static final Identifier DIAMOND_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/diamond_armor_half_2");
    private static final Identifier NETHERITE_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/netherite_armor_full");
    private static final Identifier NETHERITE_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/netherite_armor_half_1");
    private static final Identifier NETHERITE_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/netherite_armor_half_2");
    
    public static void coloredArmorBarHandler(DrawContext context, PlayerEntity player, int i, int j, int k, int x) {
        int totalArmor = player.getArmor();

        int footArmor = 0;
        ArmorMaterial footMaterial = ArmorMaterials.IRON;
        if (!player.getInventory().getStack(36).isEmpty()) {
            Item armorItem = player.getInventory().getStack(36).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    footArmor = (int) entry.modifier().value();
                    break;
                }
            }
            footMaterial = switch (armorItem.getTranslationKey()) {
                case "item.minecraft.netherite_boots" -> ArmorMaterials.NETHERITE;
                case "item.minecraft.diamond_boots" -> ArmorMaterials.DIAMOND;
                case "item.minecraft.golden_boots" -> ArmorMaterials.GOLD;
                case "item.minecraft.leather_boots" -> ArmorMaterials.LEATHER;
                case "item.minecraft.chainmail_boots" -> ArmorMaterials.CHAIN;
                default -> footMaterial;
            };
        }

        int legArmor = 0;
        ArmorMaterial legMaterial = ArmorMaterials.IRON;
        if (!player.getInventory().getStack(37).isEmpty()) {
            Item armorItem = player.getInventory().getStack(37).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    legArmor = (int) entry.modifier().value();
                    break;
                }
            }
            legMaterial = switch (armorItem.getTranslationKey()) {
                case "item.minecraft.netherite_leggings" -> ArmorMaterials.NETHERITE;
                case "item.minecraft.diamond_leggings" -> ArmorMaterials.DIAMOND;
                case "item.minecraft.golden_leggings" -> ArmorMaterials.GOLD;
                case "item.minecraft.leather_leggings" -> ArmorMaterials.LEATHER;
                case "item.minecraft.chainmail_leggings" -> ArmorMaterials.CHAIN;
                default -> legMaterial;
            };
        }

        int chestArmor = 0;
        ArmorMaterial chestMaterial = ArmorMaterials.IRON;
        if (!player.getInventory().getStack(38).isEmpty()) {
            Item armorItem = player.getInventory().getStack(38).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    chestArmor = (int) entry.modifier().value();
                    break;
                }
            }
            chestMaterial = switch (armorItem.getTranslationKey()) {
                case "item.minecraft.netherite_chestplate" -> ArmorMaterials.NETHERITE;
                case "item.minecraft.diamond_chestplate" -> ArmorMaterials.DIAMOND;
                case "item.minecraft.golden_chestplate" -> ArmorMaterials.GOLD;
                case "item.minecraft.leather_chestplate" -> ArmorMaterials.LEATHER;
                case "item.minecraft.chainmail_chestplate" -> ArmorMaterials.CHAIN;
                default -> chestMaterial;
            };
        }

        int headArmor = 0;
        ArmorMaterial headMaterial = ArmorMaterials.IRON;
        if (!player.getInventory().getStack(39).isEmpty()) {
            Item armorItem = player.getInventory().getStack(39).getItem();
            for (AttributeModifiersComponent.Entry entry : armorItem.getComponents().getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers()) {
                if (entry.attribute() == EntityAttributes.ARMOR) {
                    headArmor = (int) entry.modifier().value();
                    break;
                }
            }
            headMaterial = switch (armorItem.getTranslationKey()) {
                case "item.minecraft.netherite_helmet" -> ArmorMaterials.NETHERITE;
                case "item.minecraft.diamond_helmet" -> ArmorMaterials.DIAMOND;
                case "item.minecraft.golden_helmet" -> ArmorMaterials.GOLD;
                case "item.minecraft.leather_helmet" -> ArmorMaterials.LEATHER;
                case "item.minecraft.turtle_helmet" -> ArmorMaterials.TURTLE_SCUTE;
                case "item.minecraft.chainmail_helmet" -> ArmorMaterials.CHAIN;
                default -> headMaterial;
            };
        }

        if (totalArmor > 0) {
            int m = i - (j - 1) * k - 10;
            for(int n = 0; n < 10; ++n) {
                int o = x + n * 8;

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_TEXTURE, o, m, 9, 9);

                if (n * 2 + 1 < footArmor) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(footMaterial, "full"), o, m, 9, 9);
                } else if (n * 2 + 1 == footArmor) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(footMaterial, "half1"), o, m, 9, 9);
                    if (legArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, "half2"), o, m, 9, 9);
                    } else if (chestArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, "half2"), o, m, 9, 9);
                    } else if (headArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, "half2"), o, m, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, "full"), o, m, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(legMaterial, "half1"), o, m, 9, 9);
                    if (chestArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, "half2"), o, m, 9, 9);
                    } else if (headArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, "half2"), o, m, 9, 9);
                    }
                }

                else if (n * 2 + 1 < (footArmor + legArmor + chestArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, "full"), o, m, 9, 9);
                } else if (n * 2 + 1 == (footArmor + legArmor + chestArmor)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(chestMaterial, "half1"), o, m, 9, 9);
                    if (headArmor > 0) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, "half2"), o, m, 9, 9);
                    }
                }

                else if (n * 2 + 1 < totalArmor ) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, "full"), o, m, 9, 9);
                } else if (n * 2 + 1 == totalArmor) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, getTexture(headMaterial, "half1"), o, m, 9, 9);
                }

            }
        }
    }

    private static Identifier getTexture(ArmorMaterial material, String shape) {
        if (material == ArmorMaterials.LEATHER) {
            if (shape.equals("half1")) {
                return LEATHER_ARMOR_HALF_1_TEXTURE;
            } else if (shape.equals("half2")) {
                return LEATHER_ARMOR_HALF_2_TEXTURE;
            } else {
                return LEATHER_ARMOR_FULL_TEXTURE;
            }
        }
        if (material == ArmorMaterials.CHAIN) {
            if (shape.equals("half1")) {
                return CHAIN_ARMOR_HALF_1_TEXTURE;
            } else if (shape.equals("half2")) {
                return CHAIN_ARMOR_HALF_2_TEXTURE;
            } else {
                return CHAIN_ARMOR_FULL_TEXTURE;
            }
        }
        if (material == ArmorMaterials.GOLD) {
            if (shape.equals("half1")) {
                return GOLD_ARMOR_HALF_1_TEXTURE;
            } else if (shape.equals("half2")) {
                return GOLD_ARMOR_HALF_2_TEXTURE;
            } else {
                return GOLD_ARMOR_FULL_TEXTURE;
            }
        }
        if (material == ArmorMaterials.TURTLE_SCUTE) {
            if (shape.equals("half1")) {
                return TURTLE_ARMOR_HALF_1_TEXTURE;
            } else if (shape.equals("half2")) {
                return TURTLE_ARMOR_HALF_2_TEXTURE;
            } else {
                return TURTLE_ARMOR_FULL_TEXTURE;
            }
        }
        if (material == ArmorMaterials.DIAMOND) {
            if (shape.equals("half1")) {
                return DIAMOND_ARMOR_HALF_1_TEXTURE;
            } else if (shape.equals("half2")) {
                return DIAMOND_ARMOR_HALF_2_TEXTURE;
            } else {
                return DIAMOND_ARMOR_FULL_TEXTURE;
            }
        }
        if (material == ArmorMaterials.NETHERITE) {
            if (shape.equals("half1")) {
                return NETHERITE_ARMOR_HALF_1_TEXTURE;
            } else if (shape.equals("half2")) {
                return NETHERITE_ARMOR_HALF_2_TEXTURE;
            } else {
                return NETHERITE_ARMOR_FULL_TEXTURE;
            }
        }
        // iron & non-vanilla armor materials
        if (shape.equals("half1")) {
            return IRON_ARMOR_HALF_1_TEXTURE;
        } else if (shape.equals("half2")) {
            return IRON_ARMOR_HALF_2_TEXTURE;
        } else {
            return IRON_ARMOR_FULL_TEXTURE;
        }
    }
}
