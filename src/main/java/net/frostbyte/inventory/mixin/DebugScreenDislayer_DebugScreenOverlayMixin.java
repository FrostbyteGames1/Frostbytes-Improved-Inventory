package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.duck.DebugScreenOverlayDuck;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.components.DebugScreenOverlay$1")
public abstract class DebugScreenDislayer_DebugScreenOverlayMixin implements DebugScreenOverlayDuck {
    @Unique
    Identifier next = Identifier.withDefaultNamespace("unknown");

    @Inject(method = "addLine", at = @At("HEAD"), cancellable = true)
    void addLine(String line, CallbackInfo ci) {
        ((DebugScreenDisplayer) this).addToGroup(this.next, line);
        ci.cancel();
    }

    @Inject(method = "addPriorityLine", at = @At("HEAD"), cancellable = true)
    void addPriorityLine(String line, CallbackInfo ci) {
        ((DebugScreenDisplayer) this).addToGroup(this.next, line);
        ci.cancel();
    }

    @ModifyVariable(method = "addToGroup(Lnet/minecraft/resources/Identifier;Ljava/lang/String;)V", at = @At("HEAD"), name = "group", argsOnly = true)
    private Identifier modifyAddToGroupStringConstructor(Identifier group) {
        return this.next;
    }

    @ModifyVariable(method = "addToGroup(Lnet/minecraft/resources/Identifier;Ljava/util/Collection;)V", at = @At("HEAD"), name = "group", argsOnly = true)
    private Identifier modifyAddToGroupCollectionConstructor(Identifier group) {
        return this.next;
    }

    @Override
    public void setNext(Identifier identifier) {
        this.next = identifier;
    }
}
