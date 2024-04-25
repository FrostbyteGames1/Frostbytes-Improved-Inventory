package net.frostbyte.inventory.mixin;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(SimpleOption.class)
public abstract class SimpleOptionMixin<T> {

    @Shadow @Final Text text;
    @Shadow T value;
    @Shadow @Final Codec<T> codec;
    @Shadow @Final SimpleOption.Callbacks<T> callbacks;
    @Shadow @Final static Logger LOGGER = LogUtils.getLogger();
    @Shadow @Final T defaultValue;
    @Shadow @Final Consumer<T> changeCallback;

    @SuppressWarnings({"unchecked", "unused"})
    public Codec<T> getCodec() {
        if (text.getString().equals(I18n.translate("options.gamma"))) {
            return (Codec<T>) Codec.DOUBLE;
        }
        return this.codec;
    }

    @SuppressWarnings("unused")
    public void setValue(T value) {
        if (text.getString().equals(I18n.translate("options.gamma"))) {
            this.value = value;
            return;
        }
        T object = this.callbacks.validate(value).orElseGet(() -> {
            Logger var10000 = LOGGER;
            String var10001 = String.valueOf(value);
            var10000.error("Illegal option value " + var10001 + " for " + this.text);
            return this.defaultValue;
        });
        if (!MinecraftClient.getInstance().isRunning()) {
            this.value = object;
        } else {
            if (!Objects.equals(this.value, object)) {
                this.value = object;
                this.changeCallback.accept(this.value);
            }
        }
    }
}
