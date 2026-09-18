package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItems.class)
public abstract class FirstPersonHandsAndItemsMixin {

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private ItemStack offHandItem;

    @Shadow
    private float mainHandHeight;

    @Shadow
    private float oMainHandHeight;

    @Shadow
    private float offHandHeight;

    @Shadow
    private float oOffHandHeight;

    @Shadow
    protected abstract boolean shouldInstantlyReplaceVisibleItem(final ItemStack currentlyVisibleItem, final ItemStack expectedItem, final LocalPlayer player);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (ImprovedInventoryConfig.heldItemsVisibleInBoat && minecraft.player != null) {
            this.oMainHandHeight = this.mainHandHeight;
            this.oOffHandHeight = this.offHandHeight;
            LocalPlayer player = minecraft.player;
            ItemStack nextMainHand = player.getMainHandItem();
            ItemStack nextOffHand = player.getOffhandItem();
            if (this.shouldInstantlyReplaceVisibleItem(this.mainHandItem, nextMainHand, minecraft.player)) {
                this.mainHandItem = nextMainHand;
            }

            if (this.shouldInstantlyReplaceVisibleItem(this.offHandItem, nextOffHand, minecraft.player)) {
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
