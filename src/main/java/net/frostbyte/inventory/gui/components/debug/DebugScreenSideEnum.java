package net.frostbyte.inventory.gui.components.debug;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum DebugScreenSideEnum implements StringRepresentable {
    AUTO("auto"),
    LEFT("left"),
    RIGHT("right");

    public static final Codec<DebugScreenSideEnum> CODEC = StringRepresentable.fromEnum(DebugScreenSideEnum::values);
    private final String name;

    DebugScreenSideEnum(final String name) {
        this.name = name;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }
}
