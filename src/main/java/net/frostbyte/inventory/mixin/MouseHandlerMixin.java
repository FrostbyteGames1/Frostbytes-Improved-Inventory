package net.frostbyte.inventory.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUIAbstractInput;
import net.fabricmc.loader.api.FabricLoader;
import net.frostbyte.inventory.SlotCycler;
import net.frostbyte.inventory.Zoom;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow private double xpos;
    @Shadow private double ypos;
    @Shadow private boolean mouseGrabbed;
    @Shadow private boolean ignoreFirstMove;

    @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
    public void lockCursor(CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("itemswapper") || !(minecraft.screen instanceof ItemSwapperUIAbstractInput)) {
            if (ImprovedInventoryConfig.containerTab && ImprovedInventoryConfig.containerTabFreeCursor) {
                if (minecraft.isWindowActive()) {
                    if (!this.mouseGrabbed) {
                        if (InputQuirks.RESTORE_KEY_STATE_AFTER_MOUSE_GRAB) {
                            KeyMapping.setAll();
                        }
                        this.mouseGrabbed = true;
                        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow(), 212995, this.xpos, this.ypos);
                        minecraft.setScreen(null);
                        minecraft.missTime = 10000;
                        this.ignoreFirstMove = true;
                    }
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "releaseMouse", at = @At("HEAD"), cancellable = true)
    public void unlockCursor(CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("itemswapper") || !(minecraft.screen instanceof ItemSwapperUIAbstractInput)) {
            if (ImprovedInventoryConfig.containerTab && ImprovedInventoryConfig.containerTabFreeCursor) {
                if (this.mouseGrabbed) {
                    this.mouseGrabbed = false;
                    InputConstants.grabOrReleaseMouse(this.minecraft.getWindow(), 212993, this.xpos, this.ypos);
                }
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        if (handle == minecraft.getWindow().handle()) {
            if (Zoom.zoomKey.isDown()) {
                if (ImprovedInventoryConfig.zoomScrollRequiresControl) {
                    if (InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_LCONTROL) || InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_RCONTROL)) {
                        Zoom.scrollAmount = Mth.clamp(Zoom.scrollAmount + Mth.sign(yoffset), 0, ImprovedInventoryConfig.zoomFOV - 1);
                        ci.cancel();
                    }
                } else {
                    Zoom.scrollAmount = Mth.clamp(Zoom.scrollAmount + Mth.sign(yoffset), 0, ImprovedInventoryConfig.zoomFOV - 1);
                    ci.cancel();
                }
            }
            if (ImprovedInventoryConfig.slotCycleAltScroll) {
                if (InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_LALT) || InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_RALT)) {
                    if (Mth.sign(yoffset) > 0) {
                        SlotCycler.cycleUp(minecraft, minecraft.player);
                        ci.cancel();
                    } else if (Mth.sign(yoffset) < 0) {
                        SlotCycler.cycleDown(minecraft, minecraft.player);
                        ci.cancel();
                    }
                }
            }
        }
    }

}
