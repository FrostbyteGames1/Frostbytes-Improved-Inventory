package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class InventorySorter implements ClientTickEvents.EndTick {
    public static KeyBinding sortKey;
    private int interactions = 0;
    MinecraftClient mc;
    ClientPlayerInteractionManager interactionManager;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(sortKey = new KeyBinding("Sort Container", InputUtil.Type.MOUSE, 2, "Improved Inventory"));
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        mc = client;
        interactionManager = mc.interactionManager;

        if (mc.player == null) {
            return;
        }

        if (mc.currentScreen instanceof GenericContainerScreen || mc.currentScreen instanceof ShulkerBoxScreen || mc.currentScreen instanceof HopperScreen || mc.currentScreen instanceof Generic3x3ContainerScreen || mc.currentScreen instanceof InventoryScreen) {
            if (shouldSort()) {
                long windowCode = MinecraftClient.getInstance().getWindow().getHandle();
                int keyCode = InputUtil.fromTranslationKey(sortKey.getBoundKeyTranslationKey()).getCode();
                if (keyCode >= 32 && keyCode <= 348 && GLFW.glfwGetKey(windowCode, keyCode) == 1) {
                    interactions = 0;
                    sortStacks(((HandledScreen<?>) mc.currentScreen).getScreenHandler());
                }
                if (keyCode >= 0 && keyCode <= 7 && GLFW.glfwGetMouseButton(windowCode, keyCode) == 1) {
                    interactions = 0;
                    sortStacks(((HandledScreen<?>) mc.currentScreen).getScreenHandler());
                }
            }
        }
    }

    // Prioritizes pick stack over sort if player is in creative mode and both actions have the same keybind
    boolean shouldSort() {
        if (mc.player == null) {
            // False if player doesn't exist
            return false;
        }
        if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
            // False if cursor isn't empty
            return false;
        }
        if (!mc.player.isSpectator() && !mc.player.isCreative()) {
            // True if player is in survival mode
            return true;
        }
        if (mc.currentScreen instanceof HandledScreen<?> handledScreen && handledScreen.focusedSlot != null && !handledScreen.focusedSlot.getStack().isEmpty()) {
            // False if focused slot isn't empty
            return false;
        }
        // True otherwise
        return true;
    }

    // Returns the number of non-player inventory slots on screen
    int getNumSlots(ScreenHandler screenHandler) {
        int num = 0;
        for (int i = 0; i < screenHandler.slots.size(); i++) {
            if (!(screenHandler.getSlot(i).inventory instanceof PlayerInventory)) {
                num++;
            }
        }
        return num;
    }

    // Combines stacks of the same item
    void combineStacks(ScreenHandler screenHandler) {
        ItemStack stack;
        if (mc.currentScreen instanceof InventoryScreen) {
            for (int i = 9; i < 36; i++) {
                stack = screenHandler.getSlot(i).getStack();
                if (!stack.isEmpty() && stack.getMaxCount() > stack.getCount()) {
                    for (int j = i + 1; j < 36; j++) {
                        if (screenHandler.getSlot(j).getStack().getItem().equals(stack.getItem()) && screenHandler.getSlot(j).getStack().getComponents().equals(stack.getComponents())) {
                            interactionManager.clickSlot(screenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player);
                            interactionManager.clickSlot(screenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                            interactions += 2;
                            if (!screenHandler.getCursorStack().isEmpty()) {
                                interactionManager.clickSlot(screenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player);
                                interactions ++;
                            }
                        }
                        if (interactions >= ImprovedInventoryConfig.maxInteractions && ImprovedInventoryConfig.maxInteractions != 0) {
                            break;
                        }
                    }
                    if (interactions >= ImprovedInventoryConfig.maxInteractions && ImprovedInventoryConfig.maxInteractions != 0) {
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < getNumSlots(screenHandler); i++) {
                stack = screenHandler.getSlot(i).getStack();
                if (!stack.isEmpty() && stack.getMaxCount() > stack.getCount()) {
                    for (int j = i + 1; j < getNumSlots(screenHandler); j++) {
                        if (screenHandler.getSlot(j).getStack().getItem().equals(stack.getItem()) && screenHandler.getSlot(j).getStack().getComponents().equals(stack.getComponents())) {
                            interactionManager.clickSlot(screenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player);
                            interactionManager.clickSlot(screenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                            interactions += 2;
                            if (!screenHandler.getCursorStack().isEmpty()) {
                                interactionManager.clickSlot(screenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player);
                                interactions ++;
                            }
                        }
                        if (interactions >= ImprovedInventoryConfig.maxInteractions && ImprovedInventoryConfig.maxInteractions != 0) {
                            break;
                        }
                    }
                    if (interactions >= ImprovedInventoryConfig.maxInteractions && ImprovedInventoryConfig.maxInteractions != 0) {
                        break;
                    }
                }
            }
        }
    }

    // Compares non-empty stacks by localized item name
    int compareStacks(ItemStack a, ItemStack b) {
        if (a.equals(b)) {
            return 0;
        }
        if (a.isEmpty()) {
            return 1;
        }
        if (b.isEmpty()) {
            return -1;
        }
        int temp = a.getItem().getName().getString().compareTo(b.getItem().getName().getString());
        if (temp == 0) {
            return Integer.compare(b.getCount(), a.getCount());
        } else {
            return Integer.compare(temp, 0);
        }
    }

    // Collects combined stacks into an ArrayList and sorts the array
    ArrayList<ItemStack> getSortedStackArray(ScreenHandler screenHandler) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if (mc.currentScreen instanceof InventoryScreen) {
            for (int i = 9; i < 36; i++) {
                stacks.add(screenHandler.getSlot(i).getStack());
            }
        } else {
            for (int i = 0; i < getNumSlots(screenHandler); i++) {
                stacks.add(screenHandler.getSlot(i).getStack());
            }
        }
        stacks.sort(this::compareStacks);
        if (mc.currentScreen instanceof InventoryScreen) {
            for (int i = 9; i > 0; i--) {
                stacks.addFirst(screenHandler.getSlot(i - 1).getStack());
            }
        }
        return stacks;
    }

    // Sorts container
    void sortStacks(ScreenHandler screenHandler) {
        screenHandler.enableSyncing();
        combineStacks(screenHandler);
        ArrayList<ItemStack> sortedStacks = getSortedStackArray(screenHandler);
        for (int i = 0; i < sortedStacks.size(); i++) {
            if (!screenHandler.getSlot(i).getStack().equals(sortedStacks.get(i))) {
                int slot = screenHandler.getStacks().indexOf(sortedStacks.get(i));
                interactionManager.clickSlot(screenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
                interactionManager.clickSlot(screenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                interactions += 2;
                if (!screenHandler.getCursorStack().isEmpty()) {
                    if (!screenHandler.getCursorStack().isEmpty()) {
                        if (mc.currentScreen instanceof InventoryScreen) {
                            for (int j = 9; j < 36; j++) {
                                if (screenHandler.slots.get(j).getStack().isEmpty()) {
                                    interactionManager.clickSlot(screenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player);
                                    break;
                                }
                            }
                        } else {
                            interactionManager.clickSlot(screenHandler.syncId, screenHandler.getStacks().indexOf(ItemStack.EMPTY), 0, SlotActionType.PICKUP, mc.player);
                        }
                    }
                    interactions ++;
                }
            }
            if (interactions >= ImprovedInventoryConfig.maxInteractions && ImprovedInventoryConfig.maxInteractions != 0) {
                break;
            }
        }
        if (!screenHandler.getCursorStack().isEmpty()) {
            if (!screenHandler.getCursorStack().isEmpty()) {
                if (mc.currentScreen instanceof InventoryScreen) {
                    for (int j = 9; j < 36; j++) {
                        if (screenHandler.slots.get(j).getStack().isEmpty()) {
                            interactionManager.clickSlot(screenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player);
                            break;
                        }
                    }
                } else {
                    interactionManager.clickSlot(screenHandler.syncId, screenHandler.getStacks().indexOf(ItemStack.EMPTY), 0, SlotActionType.PICKUP, mc.player);
                }
            }
        }
        screenHandler.sendContentUpdates();
    }

}
