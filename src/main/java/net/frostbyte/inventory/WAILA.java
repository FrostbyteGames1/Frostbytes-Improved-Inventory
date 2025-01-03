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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

@Environment(EnvType.CLIENT)
public class WAILA implements HudRenderCallback {
    Identifier box = Identifier.ofVanilla("textures/gui/sprites/toast/advancement.png");
    Identifier heart = Identifier.ofVanilla("textures/gui/sprites/hud/heart/full.png");
    Identifier armor = Identifier.ofVanilla("textures/gui/sprites/hud/armor_full.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int x, y;
        if (ImprovedInventoryConfig.wailaHorizontalAnchor) {
            x = ImprovedInventoryConfig.wailaOffsetX;
        } else {
            x = mc.getWindow().getScaledWidth() - 130 - ImprovedInventoryConfig.wailaOffsetX;
        }
        if (ImprovedInventoryConfig.wailaVerticalAnchor) {
            y = ImprovedInventoryConfig.wailaOffsetY;
        } else {
            y = mc.getWindow().getScaledHeight() - 32 - ImprovedInventoryConfig.wailaOffsetY;
        }
        if (mc.crosshairTarget != null) {
            if (mc.crosshairTarget.getType() != HitResult.Type.MISS) {
                drawContext.drawTexture(RenderLayer::getGuiTextured, box, x + 4, y, 34, 0, 126, 32, 160, 32);
                drawContext.drawTexture(RenderLayer::getGuiTextured, box, x, y, 0, 0, 4, 32, 160, 32);
            }
            if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                Block block = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock();
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
                drawContext.drawWrappedTextWithShadow(mc.textRenderer, StringVisitable.plain(block.getName().getString()), x + 30, y + 8, 100, Colors.WHITE);
            } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
                if (entity instanceof LivingEntity livingEntity) {
                    drawEntity(drawContext, x, y, x + 32, y + 32, (int) (26.835890 * Math.pow(Math.E, livingEntity.getHeight() / -1.6296130) + 2.0259996), 0.0625F, x + 64, y + 16, livingEntity);
                    drawContext.drawTextWithShadow(mc.textRenderer, entity.getType().getName().getString(), x + 30, y + 8, Colors.WHITE);
                    drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", livingEntity.getHealth() / 2), x + 30, y + 18, Colors.WHITE);
                    drawContext.drawTexture(RenderLayer::getGuiTextured, heart, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)), y + 18, 0, 0, 9, 9, 9, 9);
                    drawContext.drawTextWithShadow(mc.textRenderer, String.format("%.1f", (float) livingEntity.getArmor() / 2), x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16, y + 18, Colors.WHITE);
                    drawContext.drawTexture(RenderLayer::getGuiTextured, armor, x + 30 + mc.textRenderer.getWidth(String.format("%.1f", livingEntity.getHealth() / 2)) + 16 + mc.textRenderer.getWidth(String.format("%.1f", (float) livingEntity.getArmor() / 2)), y + 17, 0, 0, 9, 9, 9, 9);
                } else {
                    drawContext.drawItem(entity.getPickBlockStack(), x + 8, y + 8);
                    drawContext.drawWrappedTextWithShadow(mc.textRenderer, StringVisitable.plain(entity.getType().getName().getString()), x + 30, y + 8, 100, Colors.WHITE);
                }
            }
        }
    }
}
