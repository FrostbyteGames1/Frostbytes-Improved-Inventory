package net.frostbyte.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class InventorySorter implements ClientTickEvents.EndTick {

    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    boolean inventorySort = true;
    public KeyBinding sortKey;

    MinecraftClient mc;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(sortKey = new KeyBinding("Sort Container", InputUtil.Type.MOUSE, 2, "Improved Inventory"));
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        mc = client;

        if (mc.player == null) {
            return;
        }

        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("inventorySort"))
                inventorySort = json.getAsJsonPrimitive("inventorySort").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mc.currentScreen instanceof GenericContainerScreen || mc.currentScreen instanceof Generic3x3ContainerScreen || mc.currentScreen instanceof HopperScreen || mc.currentScreen instanceof ShulkerBoxScreen) {
            if (inventorySort) {
                // If key is bound to keyboard
                if (GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.fromTranslationKey(sortKey.getBoundKeyTranslationKey()).getCode()) == 1) {
                    sortContainer(mc.player.currentScreenHandler.getStacks().subList(0, mc.player.currentScreenHandler.getStacks().size() - mc.player.getInventory().size() + 5));
                }
                // If key is bound to mouse button
                if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.fromTranslationKey(sortKey.getBoundKeyTranslationKey()).getCode()) == 1) {
                    sortContainer(mc.player.currentScreenHandler.getStacks().subList(0, mc.player.currentScreenHandler.getStacks().size() - mc.player.getInventory().size() + 5));
                }
            }
        }
    }

    List<ItemStack> sortStacks(List<ItemStack> stacks) {
        // Combine stacks
        for (int i = 0; i < stacks.size(); i++) {
            for (int j = 0; j < stacks.size(); j++) {
                if (i != j && ItemStack.canCombine(stacks.get(j), stacks.get(i))) {
                    stacks.set(i, stacks.get(i).copyWithCount(stacks.get(i).getCount() + stacks.get(j).getCount()));
                    if (stacks.get(i).getCount() > stacks.get(i).getMaxCount()) {
                        int overflow = stacks.get(i).getCount() - stacks.get(i).getMaxCount();
                        stacks.set(i, stacks.get(i).copyWithCount(stacks.get(i).getCount() - overflow));
                        stacks.set(j, stacks.get(j).copyWithCount(overflow));
                    } else {
                        stacks.set(j, ItemStack.EMPTY);
                    }
                }
            }
        }
        // Remove empty stacks
        int emptySlots = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                emptySlots++;
            }
        }
        stacks.removeIf(Predicate.isEqual(ItemStack.EMPTY));
        // Sort remaining stacks
        stacks.sort(new NameComparator());
        // Add empty stacks to ends
        for (int i = 0; i < emptySlots; i++) {
            stacks.add(ItemStack.EMPTY);
        }
        return stacks;
    }

    void sortContainer(List<ItemStack> stacks) {
        // Combine stacks
        List<ItemStack> sortedStacks = sortStacks(stacks);
        int targetNumSlots = 0;
        for (ItemStack stack : sortedStacks) {
            if (!stack.isEmpty()) {
                targetNumSlots++;
            }
        }
        int currentNumSlots = 0;
        assert mc.player != null;
        for (ItemStack stack : mc.player.currentScreenHandler.getStacks().subList(0, mc.player.currentScreenHandler.getStacks().size() - mc.player.getInventory().size() + 5)) {
            if (!stack.isEmpty()) {
                currentNumSlots++;
            }
        }
        while (targetNumSlots != currentNumSlots) {
            for (int i = 0; i < mc.player.currentScreenHandler.getStacks().size() - mc.player.getInventory().size() + 4; i++) {
                if (!mc.player.currentScreenHandler.getSlot(i).getStack().isEmpty()) {
                    assert mc.interactionManager != null;
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i + 1, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                    if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i + 1, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                    }
                    if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler screenHandler) {
                        screenHandler.getInventory().markDirty();
                    } else if (mc.player.currentScreenHandler instanceof Generic3x3ContainerScreenHandler screenHandler) {
                        screenHandler.sendContentUpdates();
                    } else if (mc.player.currentScreenHandler instanceof HopperScreenHandler screenHandler) {
                        screenHandler.sendContentUpdates();
                    } else if (mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler screenHandler) {
                        screenHandler.sendContentUpdates();
                    }
                }
                currentNumSlots = 0;
                for (ItemStack stack : mc.player.currentScreenHandler.getStacks().subList(0, mc.player.currentScreenHandler.getStacks().size() - mc.player.getInventory().size() + 5)) {
                    if (!stack.isEmpty()) {
                        currentNumSlots++;
                    }
                }
            }
        }
        // Sort combined stacks
        for (int i = 0; i < sortedStacks.size(); i++) {
            for (int j = 0; j < sortedStacks.size(); j++) {
                if (i != j) {
                    if (mc.player.currentScreenHandler.getSlot(i).getStack().getItem().getDefaultStack().getName().toString().compareToIgnoreCase(mc.player.currentScreenHandler.getSlot(j).getStack().getItem().getDefaultStack().getName().toString()) < 0) {
                        assert mc.interactionManager != null;
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                        if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                        }
                    }
                }
            }
        }
        // Order stacks of same name by size
        for (int i = 0; i < sortedStacks.size(); i++) {
            for (int j = 0; j < sortedStacks.size(); j++) {
                if (i != j) {
                    if (mc.player.currentScreenHandler.getSlot(i).getStack().getItem().getDefaultStack().getName().equals(mc.player.currentScreenHandler.getSlot(j).getStack().getName())) {
                        if (mc.player.currentScreenHandler.getSlot(i).getStack().getCount() > mc.player.currentScreenHandler.getSlot(j).getStack().getCount()) {
                            assert mc.interactionManager != null;
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                            if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < sortedStacks.size(); i++) {
            if (!mc.player.currentScreenHandler.getSlot(i).getStack().isEmpty()) {
                for (int j = 0; j < i; j++) {
                    if (mc.player.currentScreenHandler.getSlot(j).getStack().isEmpty()) {
                        assert mc.interactionManager != null;
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, j, 0, SlotActionType.PICKUP, mc.player.getInventory().player);
                        break;
                    }
                }
            }
        }
    }

    static class NameComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack a, ItemStack b) {
            return a.getName().getString().compareToIgnoreCase(b.getName().getString());
        }
    }
}
