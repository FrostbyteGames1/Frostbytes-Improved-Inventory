package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin {
    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options, CallbackInfo ci) {
        if (ImprovedInventoryConfig.shulkerBoxTooltip) {
            DefaultedList<ItemStack> items = DefaultedList.of();
            NbtCompound nbt = stack.getNbt();
            if (nbt != null) {
                if (nbt.contains("BlockEntityTag")) {
                    NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
                    if (blockEntityTag.contains("Items")) {
                        NbtList nbtList = blockEntityTag.getList("Items", 10);
                        for (NbtElement item : nbtList) {
                            items.add(ItemStack.fromNbt((NbtCompound) item));
                        }
                    }
                }
            }
            if (!items.isEmpty()) {
                ci.cancel();
            }
        }
    }
}
