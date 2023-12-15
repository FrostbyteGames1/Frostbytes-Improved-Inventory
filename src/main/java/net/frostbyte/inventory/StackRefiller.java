package net.frostbyte.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StackRefiller implements ClientTickEvents.EndTick {
    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean stackRefill = true;
    MinecraftClient mc;
    Item item = Items.AIR;
    int slot = -1;

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
            e.printStackTrace();
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
                    if (mc.player.getInventory().getStack(i).getItem() == item) {
                        assert mc.interactionManager != null;
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player.getInventory().player);
                        mc.player.getInventory().markDirty();
                        mc.player.playerScreenHandler.sendContentUpdates();
                        mc.player.playerScreenHandler.updateToClient();
                        return;
                    }
                    if (item.isFood() && item != Items.GOLDEN_APPLE && item != Items.ENCHANTED_GOLDEN_APPLE && item != Items.POPPED_CHORUS_FRUIT) {
                        if (mc.player.getInventory().getStack(i).getItem().isFood() && mc.player.getInventory().getStack(i).getItem() != Items.GOLDEN_APPLE && mc.player.getInventory().getStack(i).getItem() != Items.ENCHANTED_GOLDEN_APPLE && mc.player.getInventory().getStack(i).getItem() != Items.POPPED_CHORUS_FRUIT) {
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
        } else {
            item = Items.AIR;
            slot = -1;
        }
    }
}


