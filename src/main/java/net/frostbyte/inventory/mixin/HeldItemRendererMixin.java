package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow private float equipProgressMainHand;
    @Shadow private float lastEquipProgressMainHand;
    @Shadow private float equipProgressOffHand;
    @Shadow private float lastEquipProgressOffHand;
    @Shadow @Final private MinecraftClient client;
    @Shadow private ItemStack mainHand;
    @Shadow private ItemStack offHand;
    @Shadow abstract boolean shouldSkipHandAnimationOnSwap(ItemStack from, ItemStack to);

    @Inject(method = "updateHeldItems", at = @At("HEAD"), cancellable = true)
    public void updateHeldItems(CallbackInfo ci) {
        if (ImprovedInventoryConfig.heldItemsVisibleInBoat && this.client.player != null) {
            this.lastEquipProgressMainHand = this.equipProgressMainHand;
            this.lastEquipProgressOffHand = this.equipProgressOffHand;
            ClientPlayerEntity clientPlayerEntity = this.client.player;
            ItemStack itemStack = clientPlayerEntity.getMainHandStack();
            ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
            if (this.shouldSkipHandAnimationOnSwap(this.mainHand, itemStack)) {
                this.mainHand = itemStack;
            }

            if (this.shouldSkipHandAnimationOnSwap(this.offHand, itemStack2)) {
                this.offHand = itemStack2;
            }

            float f = clientPlayerEntity.getAttackCooldownProgress(1.0F);
            float g = this.mainHand != itemStack ? 0.0F : f * f * f;
            float h = this.offHand != itemStack2 ? 0.0F : 1.0F;
            this.equipProgressMainHand += MathHelper.clamp(g - this.equipProgressMainHand, -0.4F, 0.4F);
            this.equipProgressOffHand += MathHelper.clamp(h - this.equipProgressOffHand, -0.4F, 0.4F);

            if (this.equipProgressMainHand < 0.1F) {
                this.mainHand = itemStack;
            }

            if (this.equipProgressOffHand < 0.1F) {
                this.offHand = itemStack2;
            }
            ci.cancel();
        }
    }

}
