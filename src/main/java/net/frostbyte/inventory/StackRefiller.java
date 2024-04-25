package net.frostbyte.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class StackRefiller implements ClientTickEvents.EndTick {
    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean stackRefill = true;
    MinecraftClient mc;
    Item item = Items.AIR;
    int slot = -1;

    public static final ArrayList<Item> FOOD_REFILL_BLACKLIST = new ArrayList<>(Arrays.asList(
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.SUSPICIOUS_STEW,
            Items.CHORUS_FRUIT
    ));

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
            if (json.has("stackRefill"))
                stackRefill = json.getAsJsonPrimitive("stackRefill").getAsBoolean();
        } catch (IOException e) {
            ImprovedInventory.LOGGER.error(e.getMessage());
        }

        if (stackRefill) {
            tryRefillStack();
        }
    }

    void tryRefillStack() {
        assert mc.player != null;
        if (mc.player.currentScreenHandler.getStacks().size() == 46 && mc.currentScreen == null) {
            if (mc.player.getInventory().getMainHandStack().isEmpty() && item != Items.AIR && slot == mc.player.getInventory().selectedSlot) {
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
                for (int i = 35; i > 8; i--) {
                    if (item.getComponents().contains(DataComponentTypes.FOOD) && mc.player.getInventory().getStack(i).getItem().getComponents().contains(DataComponentTypes.FOOD)) {
                        for (Item unwanted : FOOD_REFILL_BLACKLIST) {
                            if (unwanted == mc.player.getInventory().getStack(i).getItem()) {
                                return;
                            }
                        }
                        assert mc.interactionManager != null;
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player.getInventory().player);
                        mc.player.getInventory().markDirty();
                        mc.player.playerScreenHandler.sendContentUpdates();
                        mc.player.playerScreenHandler.updateToClient();
                        return;
                    }
                }
            }
            item = mc.player.getInventory().getMainHandStack().getItem();
            slot = mc.player.getInventory().selectedSlot;
        } else {
            item = Items.AIR;
            slot = -1;
        }
    }
}


