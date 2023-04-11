package net.frostbyte.inventory;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.mixin.item.ItemStackMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;

public class StackRefiller implements ClientTickEvents.EndTick {

    public KeyBinding toggleStackRefill;
    public static boolean stackRefill = true;
    public static boolean firstRun = true;
    MinecraftClient mc;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(toggleStackRefill = new KeyBinding("Toggle Stack Refill", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_N, "Improved Inventory"));
    }

    public void processKeyBinds() {
        if (toggleStackRefill.wasPressed()) {
            stackRefill = !stackRefill;
            message();
        }
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;

        if (player == null) {
            return;
        }

        if (firstRun) {
            message();
            firstRun = false;
        }

        processKeyBinds();

        player.getInventory().markDirty();
    }

    private void message() {
        String m = "[" + Formatting.GOLD + "Improved Inventory" + Formatting.WHITE + "] " + "Stack Refill: ";
        if (stackRefill) {
            m = m + Formatting.GREEN + "Active";
        }else {
            m = m + Formatting.RED + "Inactive";
        }
        mc.player.sendMessage(Text.literal(m), false);
    }
}


