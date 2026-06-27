package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.tags.ModTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WAILA implements HudElement {
    int x, y;
    Identifier BOX = Identifier.withDefaultNamespace("textures/gui/sprites/toast/advancement.png");
    Identifier HEART = Identifier.withDefaultNamespace("textures/mob_effect/regeneration.png");
    Identifier ARMOR = Identifier.withDefaultNamespace("textures/mob_effect/resistance.png");
    Identifier LLAMA_STRENGTH = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "textures/chest_icon.png");
    Identifier HORSE_SPEED = Identifier.withDefaultNamespace("textures/mob_effect/speed.png");
    Identifier HORSE_JUMP = Identifier.withDefaultNamespace("textures/mob_effect/jump_boost.png");
    ArrayList<Item> AXES = new ArrayList<>(List.of(
        Items.WOODEN_AXE,
        Items.STONE_AXE,
        Items.COPPER_AXE,
        Items.GOLDEN_AXE,
        Items.IRON_AXE,
        Items.DIAMOND_AXE,
        Items.NETHERITE_AXE
    ));
    ArrayList<Item> HOES = new ArrayList<>(List.of(
        Items.WOODEN_HOE,
        Items.STONE_HOE,
        Items.COPPER_HOE,
        Items.GOLDEN_HOE,
        Items.IRON_HOE,
        Items.DIAMOND_HOE,
        Items.NETHERITE_HOE
    ));
    ArrayList<Item> PICKAXES = new ArrayList<>(List.of(
        Items.WOODEN_PICKAXE,
        Items.STONE_PICKAXE,
        Items.COPPER_PICKAXE,
        Items.GOLDEN_PICKAXE,
        Items.IRON_PICKAXE,
        Items.DIAMOND_PICKAXE,
        Items.NETHERITE_PICKAXE
    ));
    ArrayList<Item> SHOVELS = new ArrayList<>(List.of(
        Items.WOODEN_SHOVEL,
        Items.STONE_SHOVEL,
        Items.COPPER_SHOVEL,
        Items.GOLDEN_SHOVEL,
        Items.IRON_SHOVEL,
        Items.DIAMOND_SHOVEL,
        Items.NETHERITE_SHOVEL
    ));
    ArrayList<Item> SWORDS = new ArrayList<>(List.of(
        Items.WOODEN_SWORD,
        Items.STONE_SWORD,
        Items.COPPER_SWORD,
        Items.GOLDEN_SWORD,
        Items.IRON_SWORD,
        Items.DIAMOND_SWORD,
        Items.NETHERITE_SWORD
    ));

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (ImprovedInventoryConfig.waila && client.player != null && !client.player.isSpectator() && !client.options.hideGui && client.screen == null) {
            x = ImprovedInventoryConfig.wailaHorizontalAnchor ? ImprovedInventoryConfig.wailaOffsetX : client.getWindow().getGuiScaledWidth() - 130 - ImprovedInventoryConfig.wailaOffsetX;
            y = ImprovedInventoryConfig.wailaVerticalAnchor ? ImprovedInventoryConfig.wailaOffsetY : client.getWindow().getGuiScaledHeight() - 32 - ImprovedInventoryConfig.wailaOffsetY;
            if (client.getCameraEntity() != null) {
                if (client.crosshairPickEntity != null) {
                    Entity entity = client.crosshairPickEntity;
                    String name = entity.getName().getString();
                    int textWidth = client.font.width(name);
                    if (entity instanceof LivingEntity livingEntity) {
                        if (entity instanceof OwnableEntity tameableEntity) {
                            if (tameableEntity.getOwner() != null) {
                                name = tameableEntity.getOwner().getName().getString() + "'s " + name;
                            }
                            textWidth = client.font.width(name);
                        }
                        if (entity instanceof Llama llamaEntity) {
                            textWidth = Math.max(textWidth, client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16 + client.font.width(String.valueOf(3 * llamaEntity.getStrength())) + 9);
                        } else if (entity instanceof AbstractHorse horseEntity) {
                            textWidth = Math.max(textWidth, client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16 + client.font.width(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(Attributes.MOVEMENT_SPEED))) + 16 + client.font.width(String.format("%.1f", 7.375 * horseEntity.getAttributeValue(Attributes.JUMP_STRENGTH) - 2.125)) + 9);
                        } else {
                            textWidth = Math.max(textWidth, client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 9);
                        }
                    } else if (entity instanceof FallingBlockEntity fallingBlockEntity) {
                        name = "Falling " + fallingBlockEntity.getBlockState().getBlock().getName().getString();
                        textWidth = client.font.width(name);
                    }
                    drawBox(graphics, textWidth);
                    if (entity instanceof LivingEntity livingEntity) {
                        if (entity instanceof Player player && client.getConnection() != null) {
                            PlayerFaceExtractor.extractRenderState(graphics, client.getConnection().getPlayerInfo(player.getUUID()).getSkin(), x + 8, x + 8, 8, Color.WHITE.getRGB());
                        } else {
                            graphics.item(livingEntity.isPickable() && livingEntity.getPickResult() != null ? livingEntity.getPickResult() : ItemStack.EMPTY, x + 8, y + 8);
                        }
                        graphics.text(client.font, name, x + 30, y + 8, Color.WHITE.getRGB());
                        graphics.text(client.font, String.format("%.1f", livingEntity.getHealth() / 2), x + 30, y + 18, Color.WHITE.getRGB());
                        graphics.blit(RenderPipelines.GUI_TEXTURED, HEART, x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)), y + 17, 0, 0, 9, 9, 9, 9);
                        graphics.text(client.font, String.format("%.1f", (float) livingEntity.getArmorValue() / 2), x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16, y + 18, Color.WHITE.getRGB());
                        graphics.blit(RenderPipelines.GUI_TEXTURED, ARMOR, x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)), y + 17, 0, 0, 9, 9, 9, 9);
                        if (livingEntity instanceof Llama llamaEntity) {
                            graphics.text(client.font, String.valueOf(3 * llamaEntity.getStrength()), x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16, y + 18, Color.WHITE.getRGB());
                            graphics.blit(RenderPipelines.GUI_TEXTURED, LLAMA_STRENGTH, x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16 + client.font.width(String.valueOf(3 * llamaEntity.getStrength())), y + 17, 0, 0, 9, 9, 9, 9);
                        } else if (livingEntity instanceof AbstractHorse horseEntity) {
                            graphics.text(client.font, String.format("%.1f", 42.16 * horseEntity.getAttributeValue(Attributes.MOVEMENT_SPEED)), x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16, y + 18, Color.WHITE.getRGB());
                            graphics.blit(RenderPipelines.GUI_TEXTURED, HORSE_SPEED, x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16 + client.font.width(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(Attributes.MOVEMENT_SPEED))), y + 17, 0, 0, 9, 9, 9, 9);
                            graphics.text(client.font, String.format("%.1f", 7.375 * horseEntity.getAttributeValue(Attributes.JUMP_STRENGTH) - 2.125), x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16 + client.font.width(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(Attributes.MOVEMENT_SPEED))) + 16, y + 18, Color.WHITE.getRGB());
                            graphics.blit(RenderPipelines.GUI_TEXTURED, HORSE_JUMP, x + 30 + client.font.width(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + client.font.width(String.format("%.1f", (float) livingEntity.getArmorValue() / 2)) + 16 + client.font.width(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(Attributes.MOVEMENT_SPEED))) + 16 + client.font.width(String.format("%.1f", 7.375 * horseEntity.getAttributeValue(Attributes.JUMP_STRENGTH) - 2.125)), y + 17, 0, 0, 9, 9, 9, 9);
                        }

                    } else if (entity instanceof FallingBlockEntity fallingBlockEntity) {
                        graphics.item(fallingBlockEntity.getBlockState().getBlock().asItem().getDefaultInstance(), x + 8, y + 8);
                        graphics.text(client.font, name, x + 30, y + 8, Color.WHITE.getRGB());
                    } else {
                        if (entity.isPickable() && entity.getPickResult() != null) {
                            graphics.item(entity.getPickResult(), x + 8, y + 8);
                        }
                        graphics.text(client.font, name, x + 30, y + 8, Color.WHITE.getRGB());
                    }
                } else if (client.getCameraEntity() != null && client.level != null && client.getCameraEntity().pick(client.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, false).getType() == HitResult.Type.BLOCK) {
                    Block block = client.level.getBlockState(((BlockHitResult) client.getCameraEntity().pick(client.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, false)).getBlockPos()).getBlock();
                    String name = block.getName().getString();
                    drawBox(graphics, Math.max(client.font.width(name), (getToolsForBlock(block).size() - 1) * 16));
                    int i = 0;
                    for (ItemStack stack : getToolsForBlock(block)) {
                        i++;
                        graphics.pose().pushMatrix();
                        graphics.pose().scale(0.8f, 0.8f);
                        graphics.item(client.player, stack, (int) (1.25 * (x + 16 + i * 12)), (int) (1.25 * (y + 16)), 0);
                        graphics.pose().popMatrix();
                    }
                    if (block instanceof EndPortalBlock || block instanceof EndGatewayBlock) {
                        ItemStack stack = Items.BARRIER.getDefaultInstance();
                        stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "end_portal"));
                        graphics.item(stack, x + 8, y + 8);
                    } else if (block instanceof NetherPortalBlock) {
                        ItemStack stack = Items.BARRIER.getDefaultInstance();
                        stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "nether_portal"));
                        graphics.item(stack, x + 8, y + 8);
                    } else {
                        graphics.item(new ItemStack(block.asItem()), x + 8, y + 8);
                    }
                    graphics.text(client.font, name, x + 30, y + 8, Color.WHITE.getRGB());
                }
            }
        }
    }

    void drawBox(GuiGraphicsExtractor graphics, int textWidth) {
        if (!ImprovedInventoryConfig.wailaHorizontalAnchor) {
            x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - textWidth - 38 - ImprovedInventoryConfig.wailaOffsetX;
        }
        if (textWidth <= 130) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BOX, x + 4, y, 4, 0, 26 + textWidth, 32, 160, 32);
        } else {
            int temp = textWidth;
            int i = 0;
            while (temp > 130) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BOX, x + 4 + i * 130, y, 4, 0, 152, 32, 160, 32);
                temp -= 130;
                i++;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, BOX, x + 26 + i * 130, y, 4, 0, temp + 4, 32, 160, 32);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOX, x + 30 + textWidth, y, 152, 0, 8, 32, 160, 32);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOX, x, y, 0, 0, 4, 32, 160, 32);
    }

    ArrayList<ItemStack> getToolsForBlock(Block block) {
        ArrayList<ItemStack> tools = new ArrayList<>();

        for (Item item : AXES) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultInstance(), block.defaultBlockState())) {
                tools.add(item.getDefaultInstance());
                break;
            }
        }
        for (Item item : HOES) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultInstance(), block.defaultBlockState())) {
                tools.add(item.getDefaultInstance());
                break;
            }
        }
        for (Item item : PICKAXES) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultInstance(), block.defaultBlockState())) {
                tools.add(item.getDefaultInstance());
                break;
            }
        }
        for (Item item : SHOVELS) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultInstance(), block.defaultBlockState())) {
                tools.add(item.getDefaultInstance());
                break;
            }
        }
        for (Item item : SWORDS) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultInstance(), block.defaultBlockState())) {
                tools.add(item.getDefaultInstance());
                break;
            }
        }
        if (block.defaultBlockState().is(ModTags.SHEARS_MINEABLE)) {
            tools.add(Items.SHEARS.getDefaultInstance());
        }

        return tools;
    }

}
