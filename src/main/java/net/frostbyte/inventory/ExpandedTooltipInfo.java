package net.frostbyte.inventory;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class ExpandedTooltipInfo {

    @SuppressWarnings("DataFlowIssue")
    public static void shulkerBoxTooltipHandler(GuiGraphicsExtractor graphics, int x, int y, Slot focusedSlot, int backgroundWidth, int color) {
        List<ItemStack> items = new ArrayList<>();
        List<ItemStack> inventory = focusedSlot.getItem().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).allItemsCopyStream().toList();
        for (int i = 0; i < inventory.size(); i++) {
            items.add(i, inventory.get(i));
        }
        if (!items.isEmpty()) {
            int startX = x + 8;
            int startY = y - 16;
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                Identifier.withDefaultNamespace("textures/gui/container/generic_54.png"),
                startX, startY,
                0, 0,
                backgroundWidth, 3 * 18 + 17,
                256, 256,
                color
            );
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                Identifier.withDefaultNamespace("textures/gui/container/generic_54.png"),
                startX, startY + 3 * 18 + 17,
                0, 215,
                backgroundWidth, 7,
                256, 256,
                color
            );
            int nameColor = new Color(63, 63, 63).getRGB();
            if (focusedSlot.getItem().has(DataComponents.CUSTOM_NAME)) {
                nameColor = focusedSlot.getItem().getOrDefault(DataComponents.CUSTOM_NAME, CommonComponents.EMPTY).getStyle().getColor().getValue();
            }
            nameColor = adjustTextColor(color, nameColor);
            graphics.text(Minecraft.getInstance().font, focusedSlot.getItem().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY), startX + 8, startY + 6, nameColor, false);
            for (int i = 0; i < items.size(); i++) {
                if (i < 9) {
                    graphics.item(items.get(i), startX + 8 + i * 18, startY + 18, 0);
                    graphics.itemDecorations(Minecraft.getInstance().font, items.get(i), startX + 8 + i * 18, startY + 18);
                } else if (i < 18) {
                    graphics.item(items.get(i), startX + 8 + (i - 9) * 18, startY + 18 + 18, 0);
                    graphics.itemDecorations(Minecraft.getInstance().font, items.get(i), startX + 8 + (i - 9) * 18, startY + 18 + 18);
                } else {
                    graphics.item(items.get(i), startX + 8 + (i - 18) * 18, startY + 18 + 36, 0);
                    graphics.itemDecorations(Minecraft.getInstance().font, items.get(i), startX + 8 + (i - 18) * 18, startY + 18 + 36);
                }
            }
        }
    }

    public static int adjustTextColor(int background, int text) {
        final int CONTRAST = 125;

        // Preserve alpha
        int alpha = text & 0xFF000000;

        // Background RGB
        int br = (background >> 16) & 0xFF;
        int bg = (background >> 8) & 0xFF;
        int bb = background & 0xFF;

        // Text RGB
        int tr = (text >> 16) & 0xFF;
        int tg = (text >> 8) & 0xFF;
        int tb = text & 0xFF;

        // Perceived luminance
        int bgLum = (int)(0.299 * br + 0.587 * bg + 0.114 * bb);
        int textLum = (int)(0.299 * tr + 0.587 * tg + 0.114 * tb);

        // Target luminance based on background brightness
        int targetLum = (bgLum < 128)
                ? Math.min(255, bgLum + CONTRAST)
                : Math.max(0, bgLum - CONTRAST);

        // Shift the text by the required amount
        int delta = targetLum - textLum;

        tr = Math.clamp(tr + delta, 0, 255);
        tg = Math.clamp(tg + delta, 0, 255);
        tb = Math.clamp(tb + delta, 0, 255);

        return alpha | (tr << 16) | (tg << 8) | tb;
    }

    public static void mapTooltipHandler(GuiGraphicsExtractor graphics, int x, int y, Slot focusedSlot) {
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            Identifier.withDefaultNamespace("textures/map/map_background_checkerboard.png"),
            x - 78, y - 16,
            0, 0,
            70, 70,
            70, 70
        );

        MapRenderState mapRenderState = new MapRenderState();
        MapId mapId = focusedSlot.getItem().get(DataComponents.MAP_ID);
        MapItemSavedData mapData = null;
        if (mapId != null && Minecraft.getInstance().level != null) {
            mapData = MapItem.getSavedData(mapId, Minecraft.getInstance().level);
        }
        if (mapData != null) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(x - 78 + 3, y - 16 + 3);
            graphics.pose().scale(0.5f);
            Minecraft.getInstance().getMapRenderer().extractRenderState(mapId, mapData, mapRenderState);
            graphics.map(mapRenderState);
            graphics.pose().popMatrix();

            for (MapDecoration decoration : mapData.getDecorations()) {
                if (decoration.type() == MapDecorationTypes.PLAYER) {
                    graphics.pose().pushMatrix();
                    graphics.pose().translate(x - 78 + 3 + decoration.x() / 4F + 32F, y - 16 + 3 + decoration.y() / 4F + 32F);
                    graphics.pose().scale(0.5f);
                    graphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        Identifier.withDefaultNamespace("textures/map/decorations/player_off_map.png"),
                        0, 0,
                        0, 0,
                        8, 8,
                        8, 8
                    );
                    graphics.pose().popMatrix();
                }
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void compassTooltipHandler(ItemStack stack, Consumer<Component> textConsumer) {
        Component text;
        if (stack.getComponents().has(DataComponents.LODESTONE_TRACKER)) {
            Optional<GlobalPos> optional = stack.getComponents().get(DataComponents.LODESTONE_TRACKER).target();
            if (optional.isPresent() && Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension().equals(optional.get().dimension())) {
                text = Component.literal(optional.get().pos().toShortString());
            } else {
                text = Component.translatable("compass.target.none");
            }
        } else if (Objects.requireNonNull(Minecraft.getInstance().level).dimension().identifier().toShortString().contains("overworld")) {
            text = Component.literal(Minecraft.getInstance().level.getRespawnData().pos().toShortString());
        } else {
            text = Component.translatable("compass.target.none");
        }
        text.getStyle().applyFormat(ChatFormatting.GRAY);
        textConsumer.accept(text);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void recoveryCompassTooltipHandler(Consumer<Component> textConsumer) {
        Component text;
        Optional<GlobalPos> optional = Minecraft.getInstance().player.getLastDeathLocation();
        if (optional.isPresent() && Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension().equals(optional.get().dimension())) {
            text = Component.literal(optional.get().pos().toShortString());
        } else {
            text = Component.translatable("compass.target.none");
        }
        text.getStyle().applyFormat(ChatFormatting.GRAY);
        textConsumer.accept(text);
    }

    public static void clockTooltipHandler(Consumer<Component> textConsumer) {
        Component text = getTimeOfDayText();
        text.getStyle().applyFormat(ChatFormatting.GRAY);
        textConsumer.accept(text);
    }

    private static Component getTimeOfDayText() {
        Component text;
        if (!Objects.requireNonNull(Minecraft.getInstance().level).dimensionType().hasFixedTime()) {
            long time = Minecraft.getInstance().level.getOverworldClockTime() % 24000L;
            text = switch ((int) time) {
                case 1000 -> Component.translatable("clock.time.day");
                case 6000 -> Component.translatable("clock.time.noon");
                case 13000 -> Component.translatable("clock.time.night");
                case 18000 -> Component.translatable("clock.time.midnight");
                default -> Component.literal(String.valueOf(time));
            };
        } else {
            text = Component.translatable("clock.time.none");
        }
        return text;
    }

    @SuppressWarnings("DataFlowIssue")
    public static void foodTooltipHandler(ItemStack stack, Consumer<Component> textConsumer) {
        if (stack.getComponents().has(DataComponents.FOOD)) {
            Component text;
            FoodProperties food = stack.getComponents().get(DataComponents.FOOD);

            text = Component.translatable("food.tooltip_nutrition").append(String.valueOf(food.nutrition()));
            text.getStyle().applyFormat(ChatFormatting.GRAY);
            textConsumer.accept(text);

            text = Component.translatable("food.tooltip_saturation").append(String.format("%.1f", food.saturation()));
            text.getStyle().applyFormat(ChatFormatting.GRAY);
            textConsumer.accept(text);
        }
    }
}
