package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class StackRefiller implements HudRenderCallback {
    static Item item = Items.AIR;
    static int slot = -1;
    static int numItems = 0;
    public static final ArrayList<Item> FOOD_REFILL_BLACKLIST = new ArrayList<>(Arrays.asList(
        Items.GOLDEN_APPLE,
        Items.ENCHANTED_GOLDEN_APPLE,
        Items.SUSPICIOUS_STEW,
        Items.CHORUS_FRUIT
    ));

    public static void tryRefillStack(MinecraftClient mc) {
        if (mc.player == null) {
            return;
        }
        if (mc.player.currentScreenHandler.getStacks().size() == 46 && mc.currentScreen == null) {
            if ((
                (mc.player.getInventory().getMainHandStack().isEmpty() && item != Items.AIR) ||
                (mc.player.getInventory().getMainHandStack().isOf(Items.GLASS_BOTTLE) && ((item == Items.HONEY_BOTTLE) || item == Items.POTION)) ||
                (mc.player.getInventory().getMainHandStack().isOf(Items.BUCKET) && (item == Items.MILK_BUCKET || item instanceof BucketItem)) ||
                (mc.player.getInventory().getMainHandStack().isOf(Items.BOWL) && (item == Items.SUSPICIOUS_STEW || item == Items.MUSHROOM_STEW || item == Items.RABBIT_STEW || item == Items.BEETROOT_SOUP))
            ) && !ImprovedInventoryConfig.stackRefillBlacklist.contains(item) && slot == mc.player.getInventory().selectedSlot) {
                for (int i = 35; i > 8; i--) {
                    if (item == mc.player.getInventory().getStack(i).getItem()) {
                        assert mc.interactionManager != null;
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player.getInventory().player);
                        mc.player.getInventory().markDirty();
                        mc.player.playerScreenHandler.sendContentUpdates();
                        mc.player.playerScreenHandler.updateToClient();
                        return;
                    }
                }

                // If the refill target is a food item and no match is found, refill with a different food item
                if (item.isFood()) {
                    for (int i = 35; i > 8; i--) {
                        if (mc.player.getInventory().getStack(i).getItem().isFood() && !FOOD_REFILL_BLACKLIST.contains(mc.player.getInventory().getStack(i).getItem())) {
                            assert mc.interactionManager != null;
                            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player.getInventory().player);
                            mc.player.getInventory().markDirty();
                            mc.player.playerScreenHandler.sendContentUpdates();
                            mc.player.playerScreenHandler.updateToClient();
                            return;
                        }
                    }
                }
            }

            item = mc.player.getInventory().getMainHandStack().getItem();
            slot = mc.player.getInventory().selectedSlot;

            if (ImprovedInventoryConfig.stackRefillPreview) {
                numItems = 0;
                for (int i = 35; i > 8; i--) {
                    if (item == mc.player.getInventory().getStack(i).getItem()) {
                        numItems += mc.player.getInventory().getStack(i).getCount();
                    }
                }
            }
        } else {
            item = Items.AIR;
            slot = -1;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onHudRender(DrawContext drawContext, float tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (ImprovedInventoryConfig.stackRefillPreview && !client.player.isSpectator() && !client.options.hudHidden && client.currentScreen == null && numItems > 0) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().scale(0.5F, 0.5F, 1);
            drawContext.drawText(client.textRenderer, Text.of("+ " + numItems), 2 * (drawContext.getScaledWindowWidth() / 2 - 90 + slot * 20 + 2), 2 * (drawContext.getScaledWindowHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
            drawContext.getMatrices().pop();
        }
    }
}


