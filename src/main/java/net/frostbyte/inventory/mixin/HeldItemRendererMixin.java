package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private float oMainHandHeight;

    @Shadow
    private float mainHandHeight;

    @Shadow
    private float oOffHandHeight;

    @Shadow
    private float offHandHeight;

    @Shadow
    protected abstract boolean shouldInstantlyReplaceVisibleItem(ItemStack currentlyVisibleItem, ItemStack expectedItem);

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private ItemStack offHandItem;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (ImprovedInventoryConfig.heldItemsVisibleInBoat && minecraft.player != null) {
            this.oMainHandHeight = this.mainHandHeight;
            this.oOffHandHeight = this.offHandHeight;
            LocalPlayer player = this.minecraft.player;
            ItemStack nextMainHand = player.getMainHandItem();
            ItemStack nextOffHand = player.getOffhandItem();
            if (this.shouldInstantlyReplaceVisibleItem(this.mainHandItem, nextMainHand)) {
                this.mainHandItem = nextMainHand;
            }

            if (this.shouldInstantlyReplaceVisibleItem(this.offHandItem, nextOffHand)) {
                this.offHandItem = nextOffHand;
            }

            float attackAnim = player.getItemSwapScale(1.0F);
            float mainHandTargetHeight = this.mainHandItem != nextMainHand ? 0.0F : attackAnim * attackAnim * attackAnim;
            float offHandTargetHeight = this.offHandItem != nextOffHand ? 0.0F : 1.0F;
            this.mainHandHeight += Mth.clamp(mainHandTargetHeight - this.mainHandHeight, -0.4F, 0.4F);
            this.offHandHeight += Mth.clamp(offHandTargetHeight - this.offHandHeight, -0.4F, 0.4F);

            if (this.mainHandHeight < 0.1F) {
                this.mainHandItem = nextMainHand;
            }

            if (this.offHandHeight < 0.1F) {
                this.offHandItem = nextOffHand;
            }
            ci.cancel();
        }
    }

}
