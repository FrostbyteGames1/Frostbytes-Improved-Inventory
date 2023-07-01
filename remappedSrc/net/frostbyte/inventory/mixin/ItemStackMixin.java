package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.StackRefiller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(value = ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract int getCount();

    @Shadow public abstract Item getItem();

    @Shadow public abstract ItemStack copy();

    int targetSlot;

    @Inject(method = "decrement", at = @At("HEAD"))
    public void decrementHead(int amount, CallbackInfo ci) {
        if (StackRefiller.stackRefill) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (amount >= getCount() && mc.player.getInventory().getSlotWithStack(this.copy()) == mc.player.getInventory().selectedSlot && mc.currentScreen == null) {
                for (int i = 36; i > 8; i--) {
                    if (mc.player.getInventory().getStack(i).getItem().equals(this.getItem())) {
                        targetSlot = i;
                        break;
                    }
                }
            } else {
                targetSlot = -1;
            }
        }
    }

    @Inject(method = "decrement", at = @At("TAIL"))
    public void decrementTail(int amount, CallbackInfo ci) {
        if (StackRefiller.stackRefill) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (targetSlot != -1) {
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, targetSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
                mc.player.getInventory().markDirty();
            }
        }
    }
}
