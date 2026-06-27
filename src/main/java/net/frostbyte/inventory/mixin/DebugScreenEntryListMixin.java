package net.frostbyte.inventory.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.inventory.ImprovedInventory;
import net.frostbyte.inventory.duck.DebugScreenEntryListDuck;
import net.frostbyte.inventory.gui.components.debug.DebugScreenSideEnum;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StrictJsonParser;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(DebugScreenEntryList.class)
public abstract class DebugScreenEntryListMixin implements DebugScreenEntryListDuck {
    @Unique
    private final Map<Identifier, DebugScreenSideEnum> sides = new HashMap<>();
    @Unique
    private File debugSidesFile;
    @Unique
    private Codec<SerializedSides> sidesCodec;

    public DebugScreenEntryListMixin() {
        this.sidesCodec = SerializedSides.CODEC;
    }

    @Inject(method = "<init>", at = @At("CTOR_HEAD"))
    void init(File workingDirectory, DataFixer dataFixer, CallbackInfo ci) {
        this.sidesCodec = SerializedSides.CODEC;
        this.debugSidesFile = new File(workingDirectory, "improved-inventory-debug-menu-config.json");
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntryList;rebuildCurrentList()V"))
    void load(CallbackInfo ci) {
        DebugScreenEntryList list = (DebugScreenEntryList) (Object) this;

        try {
            if (!this.debugSidesFile.isFile()) {
                return;
            }

            Dynamic<JsonElement> data = new Dynamic<>(JsonOps.INSTANCE, StrictJsonParser.parse(FileUtils.readFileToString(this.debugSidesFile, StandardCharsets.UTF_8)));
            SerializedSides serializedOptions = this.sidesCodec.parse(data).getOrThrow((error) -> new IOException("Could not parse debug sides profile JSON: " + error));
            this.resetSides(serializedOptions.custom().orElse(Map.of()));
        } catch (IOException | JsonSyntaxException e) {
            ImprovedInventory.LOGGER.error("Couldn't read debug sides profile file {}, resetting to default", this.debugSidesFile, e);
            list.save();
        }

    }

    @Inject(method = "save", at = @At("TAIL"))
    void save(CallbackInfo ci) {
        SerializedSides serializedOptions = new SerializedSides(Optional.of(this.sides));

        try {
            FileUtils.writeStringToFile(this.debugSidesFile, this.sidesCodec.encodeStart(JsonOps.INSTANCE, serializedOptions).getOrThrow().toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            ImprovedInventory.LOGGER.error("Failed to save debug sides profile file {}", this.debugSidesFile, e);
        }

    }

    @Override
    public void setSide(Identifier location, DebugScreenSideEnum side) {
        DebugScreenEntryList list = (DebugScreenEntryList) (Object) this;
        this.sides.put(location, side);
        list.rebuildCurrentList();
        list.save();
    }

    @Override
    public DebugScreenSideEnum getSide(Identifier location) {
        return this.sides.getOrDefault(location, DebugScreenSideEnum.AUTO);
    }

    @Unique
    public final void resetSides(Map<Identifier, DebugScreenSideEnum> newEntries) {
        this.sides.clear();
        this.sides.putAll(newEntries);
    }

    @Environment(EnvType.CLIENT)
    record SerializedSides(Optional<Map<Identifier, DebugScreenSideEnum>> custom) {
        private static final Codec<Map<Identifier, DebugScreenSideEnum>> CUSTOM_ENTRIES_CODEC;
        public static final Codec<SerializedSides> CODEC;

        static {
            CUSTOM_ENTRIES_CODEC = Codec.unboundedMap(Identifier.CODEC, DebugScreenSideEnum.CODEC);
            CODEC = RecordCodecBuilder.create((i) -> i.group(CUSTOM_ENTRIES_CODEC.optionalFieldOf("data").forGetter(SerializedSides::custom)).apply(i, SerializedSides::new));
        }
    }

}
