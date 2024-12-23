package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ImprovedInventory;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.of("hud/armor_empty");
    @Unique private static final Identifier IRON_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/iron_armor_full");
    @Unique private static final Identifier IRON_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/iron_armor_half_1");
    @Unique private static final Identifier IRON_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/iron_armor_half_2");
    @Unique private static final Identifier CHAIN_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/chain_armor_full");
    @Unique private static final Identifier CHAIN_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/chain_armor_half_1");
    @Unique private static final Identifier CHAIN_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/chain_armor_half_2");
    @Unique private static final Identifier TURTLE_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/turtle_armor_full");
    @Unique private static final Identifier TURTLE_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/turtle_armor_half_1");
    @Unique private static final Identifier TURTLE_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/turtle_armor_half_2");
    @Unique private static final Identifier LEATHER_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/leather_armor_full");
    @Unique private static final Identifier LEATHER_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/leather_armor_half_1");
    @Unique private static final Identifier LEATHER_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/leather_armor_half_2");
    @Unique private static final Identifier GOLD_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/gold_armor_full");
    @Unique private static final Identifier GOLD_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/gold_armor_half_1");
    @Unique private static final Identifier GOLD_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/gold_armor_half_2");
    @Unique private static final Identifier DIAMOND_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/diamond_armor_full");
    @Unique private static final Identifier DIAMOND_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/diamond_armor_half_1");
    @Unique private static final Identifier DIAMOND_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/diamond_armor_half_2");
    @Unique private static final Identifier NETHERITE_ARMOR_FULL_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/netherite_armor_full");
    @Unique private static final Identifier NETHERITE_ARMOR_HALF_1_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/netherite_armor_half_1");
    @Unique private static final Identifier NETHERITE_ARMOR_HALF_2_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "hud/netherite_armor_half_2");
    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
        if (ImprovedInventoryConfig.armorBarColors) {
            int totalArmor = player.getArmor();

            int footArmor = 0;
            ArmorMaterial footMaterial = ArmorMaterials.IRON;
            if (!player.getInventory().getArmorStack(0).isEmpty() && player.getInventory().getArmorStack(0).getItem() instanceof ArmorItem armorItem) {
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
            if (!player.getInventory().getArmorStack(1).isEmpty() && player.getInventory().getArmorStack(1).getItem() instanceof ArmorItem armorItem) {
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
            if (!player.getInventory().getArmorStack(2).isEmpty() && player.getInventory().getArmorStack(2).getItem() instanceof ArmorItem armorItem) {
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
            if (!player.getInventory().getArmorStack(3).isEmpty() && player.getInventory().getArmorStack(3).getItem() instanceof ArmorItem armorItem) {
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

                    context.drawGuiTexture(RenderLayer::getGuiTextured, ARMOR_EMPTY_TEXTURE, o, m, 9, 9);

                    if (n * 2 + 1 < footArmor) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(footMaterial, "full"), o, m, 9, 9);
                    } else if (n * 2 + 1 == footArmor) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(footMaterial, "half1"), o, m, 9, 9);
                        if (legArmor > 0) {
                            context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(legMaterial, "half2"), o, m, 9, 9);
                        } else if (chestArmor > 0) {
                            context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(chestMaterial, "half2"), o, m, 9, 9);
                        } else if (headArmor > 0) {
                            context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(headMaterial, "half2"), o, m, 9, 9);
                        }
                    }

                    else if (n * 2 + 1 < (footArmor + legArmor)) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(legMaterial, "full"), o, m, 9, 9);
                    } else if (n * 2 + 1 == (footArmor + legArmor)) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(legMaterial, "half1"), o, m, 9, 9);
                        if (chestArmor > 0) {
                            context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(chestMaterial, "half2"), o, m, 9, 9);
                        } else if (headArmor > 0) {
                            context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(headMaterial, "half2"), o, m, 9, 9);
                        }
                    }

                    else if (n * 2 + 1 < (footArmor + legArmor + chestArmor)) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(chestMaterial, "full"), o, m, 9, 9);
                    } else if (n * 2 + 1 == (footArmor + legArmor + chestArmor)) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(chestMaterial, "half1"), o, m, 9, 9);
                        if (headArmor > 0) {
                            context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(headMaterial, "half2"), o, m, 9, 9);
                        }
                    }

                    else if (n * 2 + 1 < totalArmor ) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(headMaterial, "full"), o, m, 9, 9);
                    } else if (n * 2 + 1 == totalArmor) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, getTexture(headMaterial, "half1"), o, m, 9, 9);
                    }

                }
            }
            ci.cancel();
        }
    }

    @Unique
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
        // iron & modded materials
        if (shape.equals("half1")) {
            return IRON_ARMOR_HALF_1_TEXTURE;
        } else if (shape.equals("half2")) {
            return IRON_ARMOR_HALF_2_TEXTURE;
        } else {
            return IRON_ARMOR_FULL_TEXTURE;
        }
    }
}
