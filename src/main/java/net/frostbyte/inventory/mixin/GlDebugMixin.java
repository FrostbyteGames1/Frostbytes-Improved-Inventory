package net.frostbyte.inventory.mixin;

import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlDebug.DebugMessage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(GlDebug.class)
public abstract class GlDebugMixin {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private static Queue<DebugMessage> DEBUG_MESSAGES;
    @Shadow
    @Nullable
    private static volatile DebugMessage lastDebugMessage;
    @Unique
    private static boolean reportedError1282 = false;

    @SuppressWarnings("SynchronizeOnNonFinalField")
    @Inject(method = "info", at = @At(value = "HEAD"), cancellable = true)
    private static void info(int source, int type, int id, int severity, int length, long message, long l, CallbackInfo ci) {
        if (id == 1282) {
            if (reportedError1282) {
                ci.cancel();
            } else {
                String string = GLDebugMessageCallback.getMessage(length, message);
                DebugMessage debugMessage;
                synchronized(DEBUG_MESSAGES) {
                    debugMessage = lastDebugMessage;
                    if (debugMessage != null && debugMessage.equals(source, type, id, severity, string)) {
                        ++debugMessage.count;
                    } else {
                        debugMessage = new DebugMessage(source, type, id, severity, string);
                        DEBUG_MESSAGES.add(debugMessage);
                        lastDebugMessage = debugMessage;
                    }
                }
                LOGGER.info("OpenGL debug message: {}", debugMessage);
                LOGGER.info("OpenGL error 1282 has been suppressed by Frostbyte's Improved Inventory. This error will not be shown again until Minecraft is restarted.");
                reportedError1282 = true;
                ci.cancel();
            }
        }

    }
}
