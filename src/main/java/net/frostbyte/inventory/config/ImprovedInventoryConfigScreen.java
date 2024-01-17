package net.frostbyte.inventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

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
        int zoomFOV = 30;

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
            if (json.has("zoomFOV"))
                zoomFOV = json.getAsJsonPrimitive("zoomFOV").getAsInt();
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

        final TextFieldWidget zoomField =
                new TextFieldWidget(textRenderer, this.width / 2 - 130, this.height / 4 + 72, 60, 20, Text.of("Zoom FOV"));
        zoomField.setText(String.valueOf(zoomFOV));
        zoomField.setTooltip(Tooltip.of(Text.of("Set the target FOV for zoom.\n(Defaults to 30)")));
        zoomField.setPlaceholder(Text.of("30"));
        this.addDrawableChild(zoomField);

        final ButtonWidget doneButton =
                ButtonWidget.builder(Text.of("Done"), button -> save(duraButton.isChecked(), cycleButton.isChecked(), refillButton.isChecked(), selectButton.isChecked(), zoomField.getText()))
                        .dimensions(this.width / 2 - 130, this.height - 28, 260, 20).build();
        this.addDrawableChild(doneButton);
    }

    void save(boolean dura, boolean cycle, boolean refill, boolean select, String zoom) {
        if (zoom.matches("[0-9]+\\.[0-9]+")) {
            zoom = zoom.split("\\.")[0];
        } else if (!zoom.matches("[0-9]+")) {
            zoom = "30";
        }
        if (Integer.parseInt(zoom) < 30) {
            zoom = "30";
        }
        if (Integer.parseInt(zoom) > MinecraftClient.getInstance().options.getFov().getValue()) {
            zoom = String.valueOf(MinecraftClient.getInstance().options.getFov().getValue());
        }

        try {
            Files.deleteIfExists(configFile);
            JsonObject json = new JsonObject();
            json.addProperty("duraDisplay", dura);
            json.addProperty("slotCycle", cycle);
            json.addProperty("stackRefill", refill);
            json.addProperty("toolSelect", select);
            json.addProperty("zoomFOV", zoom);
            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert client != null;
        client.setScreen(this.parent);
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        context.drawText(this.textRenderer, "Zoom FOV", this.width / 2 - 60, this.height / 4 + 78, Colors.WHITE, false);
        super.render(context, mouseX, mouseY, delta);
    }
}
