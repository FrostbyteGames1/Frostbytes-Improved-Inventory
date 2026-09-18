package net.frostbyte.inventory.util;

import com.mojang.blaze3d.platform.InputConstants;

import static org.lwjgl.sdl.SDLMouse.SDL_GetMouseState;

public final class InputUtil {
    public static boolean isKeyDown(InputConstants.Key key) {
        if (key.getType() == InputConstants.Type.MOUSE) {
            return (SDL_GetMouseState(null, null) & key.getValue()) != 0;
        }
        return InputConstants.isKeyDown(key.getValue());
    }
}
