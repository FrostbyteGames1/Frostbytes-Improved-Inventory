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
import net.minecraft.util.Arm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class StackRefiller implements HudRenderCallback {
    static Item mainHandItem = Items.AIR;
    static ComponentMap mainHandComponents = ItemStack.EMPTY.getComponents();
    static int mainHandSlot = -1;
    static int mainHandStackSize = 0;

    static Item offHandItem = Items.AIR;
    static ComponentMap offHandComponents = ItemStack.EMPTY.getComponents();
    static int offHandSlot = 40;
    static int offHandStackSize = 0;

    public static final ArrayList<Item> FOOD_REFILL_BLACKLIST = new ArrayList<>(Arrays.asList(
        Items.GOLDEN_APPLE,
        Items.ENCHANTED_GOLDEN_APPLE,
        Items.SUSPICIOUS_STEW,
        Items.CHORUS_FRUIT
    ));

    static void updateCurrentStack(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        mainHandItem = client.player.getInventory().getSelectedStack().getItem();
        mainHandComponents = client.player.getInventory().getSelectedStack().getComponents();
        mainHandSlot = client.player.getInventory().getSelectedSlot();

        offHandItem = client.player.getInventory().getStack(offHandSlot).getItem();
        offHandComponents = client.player.getInventory().getStack(offHandSlot).getComponents();
    }
    
    @SuppressWarnings("DataFlowIssue")
    public static int slotToRefill(MinecraftClient client) {
        if (
            client.player.getInventory().getSelectedStack().isEmpty() && mainHandItem != Items.AIR ||
            (client.player.getInventory().getSelectedStack().isOf(Items.GLASS_BOTTLE) && ((mainHandItem == Items.HONEY_BOTTLE) || mainHandItem == Items.POTION)) ||
            (client.player.getInventory().getSelectedStack().isOf(Items.BUCKET) && (mainHandItem == Items.MILK_BUCKET || mainHandItem instanceof BucketItem)) ||
            (client.player.getInventory().getSelectedStack().isOf(Items.BOWL) && (mainHandItem == Items.SUSPICIOUS_STEW || mainHandItem == Items.MUSHROOM_STEW || mainHandItem == Items.RABBIT_STEW || mainHandItem == Items.BEETROOT_SOUP))
        ) {
            return mainHandSlot;
        } else if (
            client.player.getInventory().getStack(offHandSlot).isEmpty() && offHandItem != Items.AIR ||
            (client.player.getInventory().getStack(offHandSlot).isOf(Items.GLASS_BOTTLE) && ((offHandItem == Items.HONEY_BOTTLE) || offHandItem == Items.POTION)) ||
            (client.player.getInventory().getStack(offHandSlot).isOf(Items.BUCKET) && (offHandItem == Items.MILK_BUCKET || offHandItem instanceof BucketItem)) ||
            (client.player.getInventory().getStack(offHandSlot).isOf(Items.BOWL) && (offHandItem == Items.SUSPICIOUS_STEW || offHandItem == Items.MUSHROOM_STEW || offHandItem == Items.RABBIT_STEW || offHandItem == Items.BEETROOT_SOUP))
        ) {
            return offHandSlot;
        }
        return -1;
    }
    
    @SuppressWarnings("DataFlowIssue")
    public static void refillStack(MinecraftClient client, int slot, Item item, ComponentMap components) {
        // Refill potion or suspicious stew only if the target item has the same effects as the depleted item
        if (item instanceof PotionItem || item == Items.SUSPICIOUS_STEW) {
            for (int i = 35; i > 8; i--) {
                if ((
                    components.contains(DataComponentTypes.POTION_CONTENTS) &&
                    client.player.getInventory().getStack(i).contains(DataComponentTypes.POTION_CONTENTS) &&
                    Objects.equals(components.get(DataComponentTypes.POTION_CONTENTS), client.player.getInventory().getStack(i).get(DataComponentTypes.POTION_CONTENTS))
                ) || (
                    components.contains(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) &&
                    client.player.getInventory().getStack(i).contains(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) &&
                    Objects.equals(components.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS), client.player.getInventory().getStack(i).get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS))
                )) {
                    assert client.interactionManager != null;
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, slot, SlotActionType.SWAP, client.player.getInventory().player);
                    client.player.getInventory().markDirty();
                    client.player.playerScreenHandler.sendContentUpdates();
                    client.player.playerScreenHandler.updateToClient();
                    return;
                }
            }
            return;
        }

        // Refill all other items
        for (int i = 35; i > 8; i--) {
            if (item == client.player.getInventory().getStack(i).getItem()) {
                client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, slot, SlotActionType.SWAP, client.player.getInventory().player);
                client.player.getInventory().markDirty();
                client.player.playerScreenHandler.sendContentUpdates();
                client.player.playerScreenHandler.updateToClient();
                return;
            }
        }

        // If the refill target is a food item and no match is found, refill with a different food item
        if (item.getComponents().contains(DataComponentTypes.FOOD)) {
            for (int i = 35; i > 8; i--) {
                if (client.player.getInventory().getStack(i).getItem().getComponents().contains(DataComponentTypes.FOOD)) {
                    for (Item blacklist : FOOD_REFILL_BLACKLIST) {
                        if (client.player.getInventory().getStack(i).isOf(blacklist)) {
                            return;
                        }
                    }
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, slot, SlotActionType.SWAP, client.player.getInventory().player);
                    client.player.getInventory().markDirty();
                    client.player.playerScreenHandler.sendContentUpdates();
                    client.player.playerScreenHandler.updateToClient();
                    return;
                }
            }
        }
    }

    public static void tryRefillStack(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        if (client.currentScreen == null) {
            int targetSlot = slotToRefill(client);
            if (targetSlot == client.player.getInventory().getSelectedSlot()) {
                refillStack(client, targetSlot, mainHandItem, mainHandComponents);
            } else if (targetSlot == offHandSlot) {
                refillStack(client, targetSlot, offHandItem, offHandComponents);
            }
            updateCurrentStack(client);
        } else {
            mainHandItem = Items.AIR;
            mainHandComponents = ItemStack.EMPTY.getComponents();
            mainHandSlot = -1;

            offHandItem = Items.AIR;
            offHandComponents = ItemStack.EMPTY.getComponents();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static void updateRefillPreview(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        updateCurrentStack(client);
        mainHandStackSize = 0;
        for (int i = 35; i > 8; i--) {
            if (mainHandItem instanceof PotionItem || mainHandItem == Items.SUSPICIOUS_STEW) {
                if (client.player.getInventory().getSelectedStack().getComponents().equals(client.player.getInventory().getStack(i).getComponents())) {
                    mainHandStackSize += client.player.getInventory().getStack(i).getCount();
                }
            } else if (mainHandItem == client.player.getInventory().getStack(i).getItem()) {
                mainHandStackSize += client.player.getInventory().getStack(i).getCount();
            }
        }
        offHandStackSize = 0;
        for (int i = 35; i > 8; i--) {
            if (offHandItem instanceof PotionItem || offHandItem == Items.SUSPICIOUS_STEW) {
                if (client.player.getInventory().getSelectedStack().getComponents().equals(client.player.getInventory().getStack(i).getComponents())) {
                    offHandStackSize += client.player.getInventory().getStack(i).getCount();
                }
            } else if (offHandItem == client.player.getInventory().getStack(i).getItem()) {
                offHandStackSize += client.player.getInventory().getStack(i).getCount();
            }
        }
    }

    @SuppressWarnings({"DataFlowIssue", "NullableProblems"})
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (ImprovedInventoryConfig.stackRefillPreview && !client.player.isSpectator() && !client.options.hudHidden && client.currentScreen == null) {
            drawContext.getMatrices().pushMatrix();
            drawContext.getMatrices().scale(0.5F, 0.5F);
            if (mainHandStackSize > 0) {
                drawContext.drawText(client.textRenderer, Text.of("+ " + mainHandStackSize), 2 * (drawContext.getScaledWindowWidth() / 2 - 90 + mainHandSlot * 20 + 2), 2 * (drawContext.getScaledWindowHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
            }
            if (offHandStackSize > 0) {
                if (client.player.getMainArm() == Arm.RIGHT) {
                    drawContext.drawText(client.textRenderer, Text.of("+ " + offHandStackSize), 2 * (drawContext.getScaledWindowWidth() / 2 - 90 - 29 + 2), 2 * (drawContext.getScaledWindowHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
                } else {
                    drawContext.drawText(client.textRenderer, Text.of("+ " + offHandStackSize), 2 * (drawContext.getScaledWindowWidth() / 2 + 90 + 9 + 2), 2 * (drawContext.getScaledWindowHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
                }
            }
            drawContext.getMatrices().popMatrix();
        }
    }
}


