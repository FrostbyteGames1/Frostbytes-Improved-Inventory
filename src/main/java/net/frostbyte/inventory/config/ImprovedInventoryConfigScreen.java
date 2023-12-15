package net.frostbyte.inventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImprovedInventoryConfigScreen extends Screen {
    final Screen parent;
    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ImprovedInventoryConfigScreen(final Screen parent) {
        super(Text.of("Improved Inventory Options"));
        this.parent = parent;
    }

    public void init() {
        boolean duraDisplay = true;
        boolean slotCycle = true;
        boolean stackRefill = true;
        boolean toolSelect = true;
        boolean inventorySort = true;

        try {
            if (Files.notExists(configFile)) {
                return;
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
            if (json.has("inventorySort"))
                toolSelect = json.getAsJsonPrimitive("inventorySort").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (MinecraftClient.getInstance() == null) {
            return;
        }

        final CheckboxWidget duraButton =
                CheckboxWidget.builder(Text.of("Durability Display"), this.textRenderer)
                        .pos(this.width / 2 - 130, this.height / 4 - 24)
                        .checked(duraDisplay)
                        .build();
        this.addDrawableChild(duraButton);

        final CheckboxWidget cycleButton =
                CheckboxWidget.builder(Text.of("Slot Cycle"), this.textRenderer)
                        .pos(this.width / 2 - 130, this.height / 4)
                        .checked(slotCycle)
                        .build();
        this.addDrawableChild(cycleButton);

        final CheckboxWidget refillButton =
                CheckboxWidget.builder(Text.of("Stack Refill"), this.textRenderer)
                        .pos(this.width / 2 - 130, this.height / 4 + 24)
                        .checked(stackRefill)
                        .build();
        this.addDrawableChild(refillButton);

        final CheckboxWidget selectButton =
                CheckboxWidget.builder(Text.of("Tool Select"), this.textRenderer)
                        .pos(this.width / 2 - 130, this.height / 4 + 48)
                        .checked(toolSelect)
                        .build();
        this.addDrawableChild(selectButton);

        final CheckboxWidget sortButton =
                CheckboxWidget.builder(Text.of("Sort Inventory"), this.textRenderer)
                        .pos(this.width / 2 - 130, this.height / 4 + 72)
                        .checked(toolSelect)
                        .build();
        this.addDrawableChild(sortButton);

        final ButtonWidget doneButton =
                ButtonWidget.builder(Text.of("Done"), button -> save(duraButton.isChecked(), cycleButton.isChecked(), refillButton.isChecked(), selectButton.isChecked()))
                        .dimensions(this.width / 2 - 130, this.height - 28, 260, 20).build();
        this.addDrawableChild(doneButton);
    }

    void save(boolean dura, boolean cycle, boolean refill, boolean select) {
        try {
            Files.deleteIfExists(configFile);
            JsonObject json = new JsonObject();
            json.addProperty("duraDisplay", dura);
            json.addProperty("slotCycle", cycle);
            json.addProperty("stackRefill", refill);
            json.addProperty("toolSelect", select);
            json.addProperty("inventorySort", select);
            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.setScreen(this.parent);
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }
}
