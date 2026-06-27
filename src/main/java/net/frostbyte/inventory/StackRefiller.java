package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.*;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class StackRefiller implements HudElement {
    static Item mainHandItem = Items.AIR;
    static DataComponentMap mainHandComponents = ItemStack.EMPTY.getComponents();
    static int mainHandSlot = -1;
    static int mainHandStackSize = 0;

    static Item offHandItem = Items.AIR;
    static DataComponentMap offHandComponents = ItemStack.EMPTY.getComponents();
    static int offHandSlot = 40;
    static int offHandStackSize = 0;

    public static final ArrayList<Item> FOOD_REFILL_BLACKLIST = new ArrayList<>(Arrays.asList(
        Items.GOLDEN_APPLE,
        Items.ENCHANTED_GOLDEN_APPLE,
        Items.SUSPICIOUS_STEW,
        Items.CHORUS_FRUIT
    ));

    static void updateCurrentStack(Minecraft client) {
        if (client.player == null) {
            return;
        }
        mainHandItem = client.player.getInventory().getSelectedItem().getItem();
        mainHandComponents = client.player.getInventory().getSelectedItem().getComponents();
        mainHandSlot = client.player.getInventory().getSelectedSlot();

        offHandItem = client.player.getInventory().getItem(offHandSlot).getItem();
        offHandComponents = client.player.getInventory().getItem(offHandSlot).getComponents();
    }
    
    @SuppressWarnings("DataFlowIssue")
    public static int slotToRefill(Minecraft client) {
        if (
            client.player.getInventory().getSelectedItem().isEmpty() && mainHandItem != Items.AIR ||
            (client.player.getInventory().getSelectedItem().is(Items.GLASS_BOTTLE) && ((mainHandItem == Items.HONEY_BOTTLE) || mainHandItem == Items.POTION)) ||
            (client.player.getInventory().getSelectedItem().is(Items.BUCKET) && (mainHandItem == Items.MILK_BUCKET || mainHandItem instanceof BucketItem)) ||
            (client.player.getInventory().getSelectedItem().is(Items.BOWL) && (mainHandItem == Items.SUSPICIOUS_STEW || mainHandItem == Items.MUSHROOM_STEW || mainHandItem == Items.RABBIT_STEW || mainHandItem == Items.BEETROOT_SOUP))
        ) {
            return mainHandSlot;
        } else if (
            client.player.getInventory().getItem(offHandSlot).isEmpty() && offHandItem != Items.AIR ||
            (client.player.getInventory().getItem(offHandSlot).is(Items.GLASS_BOTTLE) && ((offHandItem == Items.HONEY_BOTTLE) || offHandItem == Items.POTION)) ||
            (client.player.getInventory().getItem(offHandSlot).is(Items.BUCKET) && (offHandItem == Items.MILK_BUCKET || offHandItem instanceof BucketItem)) ||
            (client.player.getInventory().getItem(offHandSlot).is(Items.BOWL) && (offHandItem == Items.SUSPICIOUS_STEW || offHandItem == Items.MUSHROOM_STEW || offHandItem == Items.RABBIT_STEW || offHandItem == Items.BEETROOT_SOUP))
        ) {
            return offHandSlot;
        }
        return -1;
    }
    
    public static void refillStack(Minecraft client, int slot, Item item, DataComponentMap components) {
        if (client.player == null || client.gameMode == null) {
            return;
        }
        // Refill potion or suspicious stew only if the target item has the same effects as the depleted item
        if (item instanceof PotionItem || item == Items.SUSPICIOUS_STEW) {
            for (int i = 35; i > 8; i--) {
                if ((
                    components.has(DataComponents.POTION_CONTENTS) &&
                    client.player.getInventory().getItem(i).has(DataComponents.POTION_CONTENTS) &&
                    Objects.equals(components.get(DataComponents.POTION_CONTENTS), client.player.getInventory().getItem(i).get(DataComponents.POTION_CONTENTS))
                ) || (
                    components.has(DataComponents.SUSPICIOUS_STEW_EFFECTS) &&
                    client.player.getInventory().getItem(i).has(DataComponents.SUSPICIOUS_STEW_EFFECTS) &&
                    Objects.equals(components.get(DataComponents.SUSPICIOUS_STEW_EFFECTS), client.player.getInventory().getItem(i).get(DataComponents.SUSPICIOUS_STEW_EFFECTS))
                )) {
                    if (!Objects.equals(components.get(DataComponents.ITEM_MODEL), client.player.getInventory().getItem(i).get(DataComponents.ITEM_MODEL))
                            || !Objects.equals(components.get(DataComponents.ITEM_NAME), client.player.getInventory().getItem(i).get(DataComponents.ITEM_NAME))) continue;
                    client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, i, slot, ContainerInput.SWAP, client.player);
                    return;
                }
            }
            return;
        }

        // Refill all other items
        for (int i = 35; i > 8; i--) {
            if (item == client.player.getInventory().getItem(i).getItem()) {
                if (!Objects.equals(components.get(DataComponents.ITEM_MODEL), client.player.getInventory().getItem(i).get(DataComponents.ITEM_MODEL))
                        || !Objects.equals(components.get(DataComponents.ITEM_NAME), client.player.getInventory().getItem(i).get(DataComponents.ITEM_NAME))) continue;
                client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, i, slot, ContainerInput.SWAP, client.player);
                return;
            }
        }

        // If the refill target is a food item and no match is found, refill with a different food item
        if (item.components().has(DataComponents.FOOD)) {
            for (int i = 35; i > 8; i--) {
                if (client.player.getInventory().getItem(i).getItem().components().has(DataComponents.FOOD)) {
                    for (Item blacklist : FOOD_REFILL_BLACKLIST) {
                        if (client.player.getInventory().getItem(i).is(blacklist)) {
                            return;
                        }
                    }
                    client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, i, slot, ContainerInput.SWAP, client.player);
                    return;
                }
            }
        }
    }

    public static void tryRefillStack(Minecraft client) {
        if (client.player == null) {
            return;
        }
        if (client.screen == null) {
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

    public static void updateRefillPreview(Minecraft client) {
        if (client.player == null) {
            return;
        }
        updateCurrentStack(client);
        mainHandStackSize = 0;
        for (int i = 35; i > 8; i--) {
            if (!Objects.equals(client.player.getInventory().getSelectedItem().getComponents().get(DataComponents.ITEM_MODEL), client.player.getInventory().getItem(i).get(DataComponents.ITEM_MODEL))
                    || !Objects.equals(client.player.getInventory().getSelectedItem().getComponents().get(DataComponents.ITEM_NAME), client.player.getInventory().getItem(i).get(DataComponents.ITEM_NAME))) continue;
            if (mainHandItem instanceof PotionItem || mainHandItem == Items.SUSPICIOUS_STEW) {
                if (client.player.getInventory().getSelectedItem().getComponents().equals(client.player.getInventory().getItem(i).getComponents())) {
                    mainHandStackSize += client.player.getInventory().getItem(i).getCount();
                }
            } else if (mainHandItem == client.player.getInventory().getItem(i).getItem()) {
                mainHandStackSize += client.player.getInventory().getItem(i).getCount();
            }
        }
        offHandStackSize = 0;
        for (int i = 35; i > 8; i--) {
            if (!Objects.equals(client.player.getInventory().getSelectedItem().getComponents().get(DataComponents.ITEM_MODEL), client.player.getInventory().getItem(i).get(DataComponents.ITEM_MODEL))
                    || !Objects.equals(client.player.getInventory().getSelectedItem().getComponents().get(DataComponents.ITEM_NAME), client.player.getInventory().getItem(i).get(DataComponents.ITEM_NAME))) continue;
            if (offHandItem instanceof PotionItem || offHandItem == Items.SUSPICIOUS_STEW) {
                if (client.player.getInventory().getSelectedItem().getComponents().equals(client.player.getInventory().getItem(i).getComponents())) {
                    offHandStackSize += client.player.getInventory().getItem(i).getCount();
                }
            } else if (offHandItem == client.player.getInventory().getItem(i).getItem()) {
                offHandStackSize += client.player.getInventory().getItem(i).getCount();
            }
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        if (ImprovedInventoryConfig.stackRefillPreview && !client.player.isSpectator() && !client.options.hideGui && client.screen == null) {
            graphics.pose().pushMatrix();
            graphics.pose().scale(0.5F, 0.5F);
            if (mainHandStackSize > 0) {
                graphics.text(client.font, Component.literal("+ " + mainHandStackSize), 2 * (client.getWindow().getGuiScaledWidth() / 2 - 90 + mainHandSlot * 20 + 2), 2 * (client.getWindow().getGuiScaledHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
            }
            if (offHandStackSize > 0) {
                if (client.player.getMainArm() == HumanoidArm.RIGHT) {
                    graphics.text(client.font, Component.literal("+ " + offHandStackSize), 2 * (client.getWindow().getGuiScaledWidth() / 2 - 90 - 29 + 2), 2 * (client.getWindow().getGuiScaledHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
                } else {
                    graphics.text(client.font, Component.literal("+ " + offHandStackSize), 2 * (client.getWindow().getGuiScaledWidth() / 2 + 90 + 9 + 2), 2 * (client.getWindow().getGuiScaledHeight() - 20), ImprovedInventoryConfig.stackRefillPreviewColor.getRGB(), true);
                }
            }
            graphics.pose().popMatrix();
        }
    }
}


