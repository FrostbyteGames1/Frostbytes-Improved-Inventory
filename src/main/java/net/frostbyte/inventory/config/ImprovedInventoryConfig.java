package net.frostbyte.inventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImprovedInventoryConfig {

    public static final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("frostbyte");
    public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean duraDisplay = true;
    public static boolean slotCycle = true;
    public static boolean stackRefill = true;
    public static boolean toolSelect = true;
    public static boolean paperdoll = true;
    public static int zoomFOV = 30;
    public static int gamma = 500;
    public static int maxInteractions = 0;

    public static Screen createScreen(Screen parent) {
        read();
        return YetAnotherConfigLib.createBuilder()
            .title(Text.of("Frostbyte's Improved Inventory Config Menu"))
            
            .category(ConfigCategory.createBuilder()
                .name(Text.of("Inventory"))
                .tooltip(Text.of("Options that interact with the player's inventory"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Hotbar Stack Refilling"))
                    .description(OptionDescription.of(Text.of("Refills the hotbar with a new stack of the same item from the inventory")))
                    .binding(true, () -> stackRefill, newVal -> stackRefill = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Automatic Tool Selection"))
                    .description(OptionDescription.of(Text.of("Automatically swaps to the hotbar slot with the best tool when mining and the best weapon when attacking")))
                    .binding(true, () -> toolSelect, newVal -> toolSelect = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.of("Maximum Interactions Per Tick"))
                    .description(OptionDescription.of(Text.of("Limits the number of interactions created each tick when sorting a container (If set to 0, this setting is ignored)")))
                    .binding(0, () -> maxInteractions, newVal -> maxInteractions = newVal)
                    .controller(option -> integerSliderController(option, 0, 100, 10))
                    .build())
                .build())
        
            .category(ConfigCategory.createBuilder()
                .name(Text.of("HUD"))
                .tooltip(Text.of("Options that add additional information to the HUD (heads-up display"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Armor Durability Display"))
                    .description(OptionDescription.of(Text.of("Displays the currently equipped armor items on the screen")))
                    .binding(true, () -> duraDisplay, newVal -> duraDisplay = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Slot Cycling Preview"))
                    .description(OptionDescription.of(Text.of("Displays a preview of the item stacks that would be cycled to")))
                    .binding(true, () -> slotCycle, newVal -> slotCycle = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Bedrock Paperdoll"))
                    .description(OptionDescription.of(Text.of("Displays the player model on the screen like in Bedrock Edition")))
                    .binding(true, () -> paperdoll, newVal -> paperdoll = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
        
            .category(ConfigCategory.createBuilder()
                .name(Text.of("Screen"))
                .tooltip(Text.of("Options that modify the entire screen"))
                .option(Option.<Integer>createBuilder()
                    .name(Text.of("Target Zoom FOV"))
                    .description(OptionDescription.of(Text.of("The FOV (field of view) used when the zoom key is held")))
                    .binding(30, () -> zoomFOV, newVal -> zoomFOV = newVal)
                    .controller(option -> integerSliderController(option, 30, 110, 1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.of("Gamma Level"))
                    .description(OptionDescription.of(Text.of("The gamma (brightness) used when increased gamma is toggled on")))
                    .binding(500, () -> gamma, newVal -> gamma = newVal)
                    .controller(option -> integerSliderController(option, 0, 2000, 100))
                    .build())
                .build())
            
            .save(ImprovedInventoryConfig::write)
            .build()
            .generateScreen(parent);
    }

    private static IntegerSliderControllerBuilder integerSliderController(Option<Integer> option, int min, int max, int step) {
        return IntegerSliderControllerBuilder.create(option)
            .range(min, max)
            .step(step);
    }

    public static void write() {
        try {
            if (Files.notExists(configDir)) {
                Files.createDirectory(configDir);
            }
            Files.deleteIfExists(configFile);
            JsonObject json = new JsonObject();
            json.addProperty("duraDisplay", duraDisplay);
            json.addProperty("slotCycle", slotCycle);
            json.addProperty("stackRefill", stackRefill);
            json.addProperty("toolSelect", toolSelect);
            json.addProperty("paperdoll", paperdoll);
            json.addProperty("zoomFOV", zoomFOV);
            json.addProperty("gamma", gamma);
            json.addProperty("maxInteractions", maxInteractions);
        } catch (IOException ignored) {
        }
    }

    public static void read() {
        try {
            if (Files.notExists(configFile)) {
                write();
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("duraDisplay"))
                duraDisplay = json.getAsJsonPrimitive("duraDisplay").getAsBoolean();
            if (json.has("slotCycle"))
                slotCycle = json.getAsJsonPrimitive("slotCycle").getAsBoolean();
            if (json.has("stackRefill"))
                stackRefill = json.getAsJsonPrimitive("stackRefill").getAsBoolean();
            if (json.has("toolSelect"))
                toolSelect = json.getAsJsonPrimitive("toolSelect").getAsBoolean();
            if (json.has("paperdoll"))
                paperdoll = json.getAsJsonPrimitive("paperdoll").getAsBoolean();
            if (json.has("zoomFOV"))
                zoomFOV = json.getAsJsonPrimitive("zoomFOV").getAsInt();
            if (json.has("gamma"))
                gamma = json.getAsJsonPrimitive("gamma").getAsInt();
            if (json.has("maxInteractions"))
                maxInteractions = json.getAsJsonPrimitive("maxInteractions").getAsInt();
        } catch (IOException ignored) {
        }
    }

}
