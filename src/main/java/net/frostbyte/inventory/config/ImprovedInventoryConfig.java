package net.frostbyte.inventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImprovedInventoryConfig {

    public final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("frostbyte");
    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    boolean duraDisplay = true;
    boolean slotCycle = true;
    boolean stackRefill = true;
    boolean toolSelect = true;

    boolean inventorySort = true;

    public void write() {
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
            json.addProperty("inventorySort", inventorySort);
            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read() {
        try {
            if (Files.notExists(configFile)) {
                write();
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("duraDisplay"))
                duraDisplay = json.getAsJsonPrimitive("duraDisplay").getAsBoolean();
            if (json.has("slotCycle"))
                duraDisplay = json.getAsJsonPrimitive("slotCycle").getAsBoolean();
            if (json.has("stackRefill"))
                duraDisplay = json.getAsJsonPrimitive("stackRefill").getAsBoolean();
            if (json.has("toolSelect"))
                duraDisplay = json.getAsJsonPrimitive("toolSelect").getAsBoolean();
            if (json.has("inventorySort"))
                duraDisplay = json.getAsJsonPrimitive("inventorySort").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
