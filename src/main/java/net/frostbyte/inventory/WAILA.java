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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

@Environment(EnvType.CLIENT)
public class WAILA implements HudRenderCallback {
    int x, y;
    Identifier BOX = Identifier.ofVanilla("textures/gui/sprites/toast/advancement.png");
    Identifier HEART = Identifier.ofVanilla("textures/gui/sprites/hud/heart/full.png");
    Identifier ARMOR = Identifier.ofVanilla("textures/gui/sprites/hud/armor_full.png");
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
        Items.GOLDEN_SWORD,
        Items.IRON_SWORD,
        Items.DIAMOND_SWORD,
        Items.NETHERITE_SWORD
    ));

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (ImprovedInventoryConfig.waila && !mc.player.isSpectator() && !mc.options.hudHidden && mc.currentScreen == null && !mc.inGameHud.getDebugHud().shouldShowDebugHud()) {
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
                        drawContext.drawItem(stack, (int) (1.25 * (x + 16 + i * 12)), (int) (1.25 * (y + 16)), 0, 150);
                        drawContext.getMatrices().pop();
                    }
                    if (block instanceof EndPortalBlock || block instanceof EndGatewayBlock) {
                        ItemStack stack = Items.BARRIER.getDefaultStack();
                        stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(ImprovedInventory.MOD_ID, "end_portal"));
                        drawContext.drawItem(stack, x + 8, y + 8);
                    } else if (block instanceof NetherPortalBlock) {
                        ItemStack stack = Items.BARRIER.getDefaultStack();
                        stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(ImprovedInventory.MOD_ID, "nether_portal"));
                        drawContext.drawItem(stack, x + 8, y + 8);
                    } else {
                        drawContext.drawItem(new ItemStack(block.asItem()), x + 8, y + 8);
                    }
                    drawContext.drawTextWithShadow(mc.textRenderer, name, x + 30, y + 8, Colors.WHITE);
                } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
                    String name = entity.getType().getName().getString();
                    int textWidth = mc.textRenderer.getWidth(name);
                    if (entity instanceof LivingEntity livingEntity) {
                        textWidth = Math.max(textWidth, mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)) + 9);
                    }
                    drawBox(drawContext, textWidth);
                    if (entity instanceof LivingEntity livingEntity) {
                        drawEntity(drawContext, x, y, x + 32, y + 32, getScaleFromHeight(livingEntity.getHeight()), 0.0625F, x + 64, y + 16, livingEntity);
                        drawContext.drawTextWithShadow(mc.textRenderer, entity.getType().getName().getString(), x + 30, y + 8, Colors.WHITE);
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", livingEntity.getHealth() / 2), x + 30, y + 18, Colors.WHITE);
                        drawContext.drawTexture(RenderLayer::getGuiTextured, HEART, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)), y + 17, 0, 0, 9, 9, 9, 9);
                        drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", (float) livingEntity.getArmor() / 2), x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16, y + 18, Colors.WHITE);
                        drawContext.drawTexture(RenderLayer::getGuiTextured, ARMOR, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)), y + 17, 0, 0, 9, 9, 9, 9);
                    } else {
                        drawContext.drawItem(entity.getPickBlockStack(), x + 8, y + 8);
                        drawContext.drawTextWithShadow(mc.textRenderer, name, x + 30, y + 8, Colors.WHITE);
                    }
                }
            }
        }
    }

    int getScaleFromHeight(float height) {
        return (int) (26.835890 * Math.pow(Math.E, height / -1.6296130) + 2.0259996);
    }

    void drawBox(DrawContext drawContext, int textWidth) {
        if (!ImprovedInventoryConfig.wailaHorizontalAnchor) {
            x = MinecraftClient.getInstance().getWindow().getScaledWidth() - textWidth - 38 - ImprovedInventoryConfig.wailaOffsetX;
        }
        if (textWidth <= 130) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, BOX, x + 4, y, 4, 0, 26 + textWidth, 32, 160, 32);
        } else {
            int temp = textWidth;
            int i = 0;
            while (temp > 130) {
                drawContext.drawTexture(RenderLayer::getGuiTextured, BOX, x + 4 + i * 130, y, 4, 0, 152, 32, 160, 32);
                temp -= 130;
                i++;
            }
            drawContext.drawTexture(RenderLayer::getGuiTextured, BOX, x + 26 + i * 130, y, 4, 0, temp + 4, 32, 160, 32);
        }
        drawContext.drawTexture(RenderLayer::getGuiTextured, BOX, x + 30 + textWidth, y, 152, 0, 8, 32, 160, 32);
        drawContext.drawTexture(RenderLayer::getGuiTextured, BOX, x, y, 0, 0, 4, 32, 160, 32);
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
