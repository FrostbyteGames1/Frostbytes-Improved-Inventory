package net.frostbyte.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class InventorySorter {
    public static KeyMapping sortKey;
    public static int interactions = 0;

    public void setKeyMappings() {
        KeyMappingHelper.registerKeyMapping(sortKey = new KeyMapping("key.sort_container", InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_MIDDLE, ImprovedInventory.KEYBIND_CATEGORY));
    }

    public static void inventorySortHandler(Minecraft client) {
        if (client.screen instanceof AbstractContainerScreen<?> containerScreen) {
            int keyCode = KeyMappingHelper.getBoundKeyOf(sortKey).getValue();
            if ((keyCode >= 0 && keyCode <= 8 && GLFW.glfwGetMouseButton(client.getWindow().handle(), keyCode) == 1) || GLFW.glfwGetKey(client.getWindow().handle(), keyCode) == 1) {
                if (shouldSort(client, containerScreen)) {
                    interactions = 0;
                    sortStacks(client, containerScreen.getMenu());
                }
            }

        }
    }

    // Prioritizes pick stack over sort if player is in creative mode and both actions have the same keybind
    static boolean shouldSort(Minecraft client, AbstractContainerScreen<?> screen) {
        if (client.options.keyPickItem.same(sortKey) && client.player != null && client.player.isCreative()) {
            return screen.hoveredSlot == null || screen.hoveredSlot.getItem().isEmpty();
        }
        return true;
    }

    // Returns the number of non-player inventory slots on screen
    static int getNumSlots(AbstractContainerMenu menu) {
        return menu.slots.size() - 36;
    }

    // Combines stacks of the same item
    @SuppressWarnings("DataFlowIssue")
    static void combineStacks(Minecraft client, AbstractContainerMenu menu) {
        ItemStack stack1;
        ItemStack stack2;
        for (int i = 0; i < getNumSlots(menu) - 1; i++) {
            if (menu.getSlot(i).hasItem()) {
                stack1 = menu.getSlot(i).getItem();
                for (int j = i + 1; j < getNumSlots(menu); j++) {
                    if (menu.getSlot(j).hasItem()) {
                        stack2 = menu.getSlot(j).getItem();
                        if (ItemStack.isSameItemSameComponents(stack1, stack2) && stack1.getCount() < stack1.getMaxStackSize()) {
                            client.gameMode.handleContainerInput(menu.containerId, j, 0, ContainerInput.PICKUP, client.player);
                            client.gameMode.handleContainerInput(menu.containerId, i, 0, ContainerInput.PICKUP, client.player);
                            interactions += 2;
                            if (!menu.getCarried().isEmpty()) {
                                client.gameMode.handleContainerInput(menu.containerId, j, 0, ContainerInput.PICKUP, client.player);
                                interactions += 1;
                            }
                            if (ImprovedInventoryConfig.maxInteractions > 0 && interactions > ImprovedInventoryConfig.maxInteractions) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    // Compares non-empty stacks by localized item name
    static int compareStacks(ItemStack a, ItemStack b) {
        if (a.equals(b)) {
            return 0;
        }
        if (a.isEmpty()) {
            return 1;
        }
        if (b.isEmpty()) {
            return -1;
        }
        int temp = a.getItem().getName(a).getString().compareTo(b.getItem().getName(b).getString());
        if (temp == 0) {
            return Integer.compare(b.getCount(), a.getCount());
        } else {
            return Integer.compare(temp, 0);
        }
    }

    // Collects combined stacks into an ArrayList and sorts the array
    static ArrayList<ItemStack> getSortedStackArray(AbstractContainerMenu menu) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < getNumSlots(menu); i++) {
            if (menu.getSlot(i).hasItem()) {
                stacks.add(menu.getSlot(i).getItem());
            }
        }
        stacks.sort(InventorySorter::compareStacks);
        return stacks;
    }

    // Sorts container
    @SuppressWarnings("DataFlowIssue")
    public static void sortStacks(Minecraft client, AbstractContainerMenu menu) {
        combineStacks(client, menu);
        ArrayList<ItemStack> sortedStacks = getSortedStackArray(menu);
        for (int i = 0; i < sortedStacks.size(); i++) {
            if (i != menu.getItems().indexOf(sortedStacks.get(i))) {
                client.gameMode.handleContainerInput(menu.containerId, menu.getItems().indexOf(sortedStacks.get(i)), 0, ContainerInput.PICKUP, client.player);
                client.gameMode.handleContainerInput(menu.containerId, i, 0, ContainerInput.PICKUP, client.player);
                interactions += 2;
                if (ImprovedInventoryConfig.maxInteractions > 0 && interactions > ImprovedInventoryConfig.maxInteractions) {
                    return;
                }
            }
        }
    }

}
