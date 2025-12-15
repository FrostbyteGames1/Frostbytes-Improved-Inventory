package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.block.Block;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WAILA implements HudRenderCallback {
    int x, y;
    Identifier BOX = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/toast/advancement.png");
    Identifier HEART = Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/mob_effect/regeneration.png");
    Identifier ARMOR = Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/mob_effect/resistance.png");
    Identifier LLAMA_STRENGTH = Identifier.of(ImprovedInventory.MOD_ID, "textures/chest_icon.png");
    Identifier HORSE_SPEED = Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/mob_effect/speed.png");
    Identifier HORSE_JUMP = Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/mob_effect/jump_boost.png");
    ArrayList<Item> AXES = new ArrayList<>(List.of(
        Items.WOODEN_AXE,
        Items.STONE_AXE,
        Items.GOLDEN_AXE,
        Items.IRON_AXE,
        Items.DIAMOND_AXE,
        Items.NETHERITE_AXE
    ));
    ArrayList<Item> HOES = new ArrayList<>(List.of(
        Items.WOODEN_HOE,
        Items.STONE_HOE,
        Items.GOLDEN_HOE,
        Items.IRON_HOE,
        Items.DIAMOND_HOE,
        Items.NETHERITE_HOE
    ));
    ArrayList<Item> PICKAXES = new ArrayList<>(List.of(
        Items.WOODEN_PICKAXE,
        Items.STONE_PICKAXE,
        Items.GOLDEN_PICKAXE,
        Items.IRON_PICKAXE,
        Items.DIAMOND_PICKAXE,
        Items.NETHERITE_PICKAXE
    ));
    ArrayList<Item> SHOVELS = new ArrayList<>(List.of(
        Items.WOODEN_SHOVEL,
        Items.STONE_SHOVEL,
        Items.GOLDEN_SHOVEL,
        Items.IRON_SHOVEL,
        Items.DIAMOND_SHOVEL,
        Items.NETHERITE_SHOVEL
    ));
    ArrayList<Item> SWORDS = new ArrayList<>(List.of(
        Items.WOODEN_SWORD,
        Items.STONE_SWORD,
        Items.STONE_SWORD,
        Items.GOLDEN_SWORD,
        Items.IRON_SWORD,
        Items.DIAMOND_SWORD,
        Items.NETHERITE_SWORD
    ));

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onHudRender(DrawContext drawContext, float tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (ImprovedInventoryConfig.waila && !mc.player.isSpectator() && !mc.options.hudHidden && mc.currentScreen == null && !mc.options.debugEnabled) {
            x = ImprovedInventoryConfig.wailaHorizontalAnchor ? ImprovedInventoryConfig.wailaOffsetX : mc.getWindow().getScaledWidth() - 130 - ImprovedInventoryConfig.wailaOffsetX;
            y = ImprovedInventoryConfig.wailaVerticalAnchor ? ImprovedInventoryConfig.wailaOffsetY : mc.getWindow().getScaledHeight() - 32 - ImprovedInventoryConfig.wailaOffsetY;
            if (mc.crosshairTarget != null) {
                if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                    Block block = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock();
                    String name = block.getName().getString();
                    drawBox(drawContext, Math.max(mc.textRenderer.getWidth(name), (getToolsForBlock(block).size() - 1) * 16));
                    int i = 0;
                    for (ItemStack stack : getToolsForBlock(block)) {
                        i++;
                        drawContext.getMatrices().push();
                        drawContext.getMatrices().scale(0.8f, 0.8f, 1);
                        drawContext.drawItem(mc.player, stack, (int) (1.25 * (x + 16 + i * 12)), (int) (1.25 * (y + 16)), 0);
                        drawContext.getMatrices().pop();
                    }
                    if (block instanceof EndPortalBlock || block instanceof EndGatewayBlock) {
                        drawContext.drawTexture(new Identifier(ImprovedInventory.MOD_ID, "textures/block/end_portal.png"), x + 8, y + 8, 0, 0, 16, 16, 16, 340);
                    } else if (block instanceof NetherPortalBlock) {
                        drawContext.drawTexture(new Identifier(Identifier.DEFAULT_NAMESPACE, "textures/block/nether_portal.png"), x + 8, y + 8, 0, 0, 16, 16, 16, 512);
                    } else if (block.asItem() != null) {
                        drawContext.drawItem(new ItemStack(block.asItem()), x + 8, y + 8);
                    }
                    drawContext.drawTextWithShadow(mc.textRenderer, name, x + 30, y + 8, Colors.WHITE);
                } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
                    String name = entity.getType().getName().getString();
                    int textWidth = mc.textRenderer.getWidth(name);
                    if (entity instanceof LivingEntity livingEntity) {
                        if (entity instanceof TameableEntity tameableEntity && tameableEntity.isTamed()) {
                            if (tameableEntity.getOwner() != null) {
                                name = tameableEntity.getOwner().getName().getString() + "'s " + name;
                            }
                            textWidth = mc.textRenderer.getWidth(name);
                        }
                        if (entity instanceof LlamaEntity llamaEntity) {
                            textWidth = Math.max(textWidth, mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16 + mc.textRenderer.getWidth(String.valueOf(3 * llamaEntity.getStrength())) + 9);
                        } else if (entity instanceof AbstractHorseEntity horseEntity) {
                            textWidth = Math.max(textWidth, mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED))) + 16 + mc.textRenderer.getWidth(String.format("%.1f", 7.375 * horseEntity.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) - 2.125)) + 9);
                        } else {
                            textWidth = Math.max(textWidth, mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 9);
                        }
                    } else if (entity instanceof FallingBlockEntity fallingBlockEntity) {
                        name = "Falling " + fallingBlockEntity.getBlockState().getBlock().getName().getString();
                        textWidth = mc.textRenderer.getWidth(name);
                    }
                    drawBox(drawContext, textWidth);
                    if (entity instanceof LivingEntity livingEntity) {
                        if (entity instanceof PlayerEntity player) {
                            PlayerSkinDrawer.draw(drawContext, mc.getNetworkHandler().getPlayerListEntry(player.getUuid()).getSkinTexture(), x + 8, y + 8, 16, true, false);
                        } else {
                            drawContext.drawItem(livingEntity.getPickBlockStack() != null ? livingEntity.getPickBlockStack() : ItemStack.EMPTY, x + 8, y + 8);
                        }
                        drawContext.drawTextWithShadow(mc.textRenderer, name, x + 30, y + 8, Colors.WHITE);
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", livingEntity.getHealth() / 2), x + 30, y + 18, Colors.WHITE);
                        drawContext.drawTexture(HEART, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)), y + 17, 0, 0, 9, 9, 9, 9);
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", (float) livingEntity.getArmor() / 2), x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16, y + 18, Colors.WHITE);
                        drawContext.drawTexture(ARMOR, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)), y + 17, 0, 0, 9, 9, 9, 9);
                        if (livingEntity instanceof LlamaEntity llamaEntity) {
                            drawContext.drawTextWithShadow(mc.textRenderer, String.valueOf(3 * llamaEntity.getStrength()), x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16, y + 18, Colors.WHITE);
                            drawContext.drawTexture(LLAMA_STRENGTH, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16 + mc.textRenderer.getWidth(String.valueOf(3 * llamaEntity.getStrength())), y + 17, 0, 0, 9, 9, 9, 9);
                        } else if (livingEntity instanceof AbstractHorseEntity horseEntity) {
                            drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", 42.16 * horseEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)), x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16, y + 18, Colors.WHITE);
                            drawContext.drawTexture(HORSE_SPEED, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED))), y + 17, 0, 0, 9, 9, 9, 9);
                            drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", 7.375 * horseEntity.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) - 2.125), x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED))) + 16, y + 18, Colors.WHITE);
                            drawContext.drawTexture(HORSE_JUMP, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", 42.16 * horseEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED))) + 16 + mc.textRenderer.getWidth(String.format("%.1f", 7.375 * horseEntity.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) - 2.125)), y + 17, 0, 0, 9, 9, 9, 9);
                        }

                    } else if (entity instanceof FallingBlockEntity fallingBlockEntity) {
                        drawContext.drawItem(fallingBlockEntity.getBlockState().getBlock().asItem().getDefaultStack(), x + 8, y + 8);
                        drawContext.drawTextWithShadow(mc.textRenderer, name, x + 30, y + 8, Colors.WHITE);
                    } else {
                        if (entity.getPickBlockStack() != null) {
                            drawContext.drawItem(entity.getPickBlockStack(), x + 8, y + 8);
                        }
                        drawContext.drawTextWithShadow(mc.textRenderer, name, x + 30, y + 8, Colors.WHITE);
                    }
                }
            }
        }
    }

    void drawBox(DrawContext drawContext, int textWidth) {
        if (!ImprovedInventoryConfig.wailaHorizontalAnchor) {
            x = MinecraftClient.getInstance().getWindow().getScaledWidth() - textWidth - 38 - ImprovedInventoryConfig.wailaOffsetX;
        }
        if (textWidth <= 130) {
            drawContext.drawTexture(BOX, x + 4, y, 4, 0, 26 + textWidth, 32, 160, 32);
        } else {
            int temp = textWidth;
            int i = 0;
            while (temp > 130) {
                drawContext.drawTexture(BOX, x + 4 + i * 130, y, 4, 0, 152, 32, 160, 32);
                temp -= 130;
                i++;
            }
            drawContext.drawTexture(BOX, x + 26 + i * 130, y, 4, 0, temp + 4, 32, 160, 32);
        }
        drawContext.drawTexture(BOX, x + 30 + textWidth, y, 152, 0, 8, 32, 160, 32);
        drawContext.drawTexture(BOX, x, y, 0, 0, 4, 32, 160, 32);
    }

    ArrayList<ItemStack> getToolsForBlock(Block block) {
        ArrayList<ItemStack> tools = new ArrayList<>();

        for (Item item : AXES) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultStack(), block.getDefaultState())) {
                tools.add(item.getDefaultStack());
                break;
            }
        }
        for (Item item : HOES) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultStack(), block.getDefaultState())) {
                tools.add(item.getDefaultStack());
                break;
            }
        }
        for (Item item : PICKAXES) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultStack(), block.getDefaultState())) {
                tools.add(item.getDefaultStack());
                break;
            }
        }
        for (Item item : SHOVELS) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultStack(), block.getDefaultState())) {
                tools.add(item.getDefaultStack());
                break;
            }
        }
        for (Item item : SWORDS) {
            if (ToolSelector.isCorrectForDrops(item.getDefaultStack(), block.getDefaultState())) {
                tools.add(item.getDefaultStack());
                break;
            }
        }
        if (ToolSelector.SHEARS_MINEABLE.contains(block)) {
            tools.add(Items.SHEARS.getDefaultStack());
        }

        return tools;
    }

}
