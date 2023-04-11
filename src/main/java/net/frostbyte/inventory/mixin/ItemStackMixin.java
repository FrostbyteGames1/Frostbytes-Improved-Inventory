package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.StackRefiller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemStack.class, priority = 1001)
public abstract class ItemStackMixin {

    @Shadow public abstract int getCount();

    @Shadow public abstract ItemStack copy();
    int slot = -1;

    @Inject(method = "decrement", at = @At("HEAD"))
    public void decrementHead(int amount, CallbackInfo ci) {
        if (StackRefiller.stackRefill) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (getCount() - amount <= 0 && mc.player.getInventory().getSlotWithStack(this.copy()) < 9) {
                for (int i = 9; i < 36; i++) {
                    if (mc.player.getInventory().getStack(i).getItem().equals(mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).getItem())) {
                        slot = i;
                        break;
                    }
                }
            } else {
                slot = -1;
            }
        }
    }

    @Inject(method = "decrement", at = @At("TAIL"))
    public void decrementTail(int amount, CallbackInfo ci) {
        if (StackRefiller.stackRefill) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (slot != -1) {
                ItemStack temp = mc.player.getInventory().getStack(slot);
                mc.player.getInventory().setStack(slot, ItemStack.EMPTY);
                mc.player.getInventory().setStack(mc.player.getInventory().selectedSlot, temp);
            }
            mc.player.getInventory().markDirty();
        }
    }

}
