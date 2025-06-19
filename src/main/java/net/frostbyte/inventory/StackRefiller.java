package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class StackRefiller implements HudRenderCallback {
    static Item item = Items.AIR;
    static ComponentMap components = ItemStack.EMPTY.getComponents();
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
                (mc.player.getInventory().getSelectedStack().isEmpty() && item != Items.AIR) ||
                (mc.player.getInventory().getSelectedStack().isOf(Items.GLASS_BOTTLE) && ((item == Items.HONEY_BOTTLE) || item == Items.POTION)) ||
                (mc.player.getInventory().getSelectedStack().isOf(Items.BUCKET) && (item == Items.MILK_BUCKET || item instanceof BucketItem)) ||
                (mc.player.getInventory().getSelectedStack().isOf(Items.BOWL) && (item == Items.SUSPICIOUS_STEW || item == Items.MUSHROOM_STEW || item == Items.RABBIT_STEW || item == Items.BEETROOT_SOUP))
            ) && !ImprovedInventoryConfig.stackRefillBlacklist.contains(item) && slot == mc.player.getInventory().getSelectedSlot()) {
                // Refill potion or suspicious stew only if the target item has the same effects as the depleted item
                if (item instanceof PotionItem || item == Items.SUSPICIOUS_STEW) {
                    for (int i = 35; i > 8; i--) {
                        if ((
                            components.contains(DataComponentTypes.POTION_CONTENTS) &&
                            mc.player.getInventory().getStack(i).contains(DataComponentTypes.POTION_CONTENTS) &&
                            Objects.equals(components.get(DataComponentTypes.POTION_CONTENTS), mc.player.getInventory().getStack(i).get(DataComponentTypes.POTION_CONTENTS))
                            ) || (
                            components.contains(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) &&
                            mc.player.getInventory().getStack(i).contains(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) &&
                            Objects.equals(components.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS), mc.player.getInventory().getStack(i).get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS))
                        )) {
                            assert mc.interactionManager != null;
                            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i, mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, mc.player.getInventory().player);
                            mc.player.getInventory().markDirty();
                            mc.player.playerScreenHandler.sendContentUpdates();
                            mc.player.playerScreenHandler.updateToClient();
                            return;
                        }
                    }
                    return;
                }

                // Refill all other items
                for (int i = 35; i > 8; i--) {
                    if (item == mc.player.getInventory().getStack(i).getItem()) {
                        assert mc.interactionManager != null;
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i, mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, mc.player.getInventory().player);
                        mc.player.getInventory().markDirty();
                        mc.player.playerScreenHandler.sendContentUpdates();
                        mc.player.playerScreenHandler.updateToClient();
                        return;
                    }
                }

                // If the refill target is a food item and no match is found, refill with a different food item
                if (item.getComponents().contains(DataComponentTypes.FOOD)) {
                    for (int i = 35; i > 8; i--) {
                        if (mc.player.getInventory().getStack(i).getItem().getComponents().contains(DataComponentTypes.FOOD)) {
                            for (Item blacklist : FOOD_REFILL_BLACKLIST) {
                                if (mc.player.getInventory().getStack(i).isOf(blacklist)) {
                                    return;
                                }
                            }
                            assert mc.interactionManager != null;
                            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i, mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, mc.player.getInventory().player);
                            mc.player.getInventory().markDirty();
                            mc.player.playerScreenHandler.sendContentUpdates();
                            mc.player.playerScreenHandler.updateToClient();
                            return;
                        }
                    }
                }
            }

            item = mc.player.getInventory().getSelectedStack().getItem();
            components = mc.player.getInventory().getSelectedStack().getComponents();
            slot = mc.player.getInventory().getSelectedSlot();

            if (ImprovedInventoryConfig.stackRefillPreview) {
                numItems = 0;
                for (int i = 35; i > 8; i--) {
                    if (item instanceof PotionItem || item == Items.SUSPICIOUS_STEW) {
                        if (mc.player.getInventory().getSelectedStack().getComponents().equals(mc.player.getInventory().getStack(i).getComponents())) {
                            numItems += mc.player.getInventory().getStack(i).getCount();
                        }
                    } else if (item == mc.player.getInventory().getStack(i).getItem()) {
                        numItems += mc.player.getInventory().getStack(i).getCount();
                    }
                }
            }
        } else {
            item = Items.AIR;
            components = ItemStack.EMPTY.getComponents();
            slot = -1;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (ImprovedInventoryConfig.stackRefillPreview && !client.player.isSpectator() && !client.options.hudHidden && client.currentScreen == null && numItems > 0) {
            drawContext.getMatrices().pushMatrix();
            drawContext.getMatrices().scale(0.5F, 0.5F);
            drawContext.drawText(client.textRenderer, Text.of("+ " + numItems), 2 * (drawContext.getScaledWindowWidth() / 2 - 90 + slot * 20 + 2), 2 * (drawContext.getScaledWindowHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
            drawContext.getMatrices().popMatrix();
        }
    }
}


