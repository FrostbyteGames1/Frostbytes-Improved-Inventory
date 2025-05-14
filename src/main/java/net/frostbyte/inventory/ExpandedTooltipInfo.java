package net.frostbyte.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.GlobalPos;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class ExpandedTooltipInfo {

    public static void shulkerBoxTooltipHandler(DrawContext context, int x, int y, Slot focusedSlot, int backgroundWidth) {
        DefaultedList<ItemStack> items = DefaultedList.of();
        List<ItemStack> inventory = focusedSlot.getStack().getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).stream().toList();
        for (int i = 0; i < inventory.size(); i++) {
            items.add(i, inventory.get(i));
        }
        if (!items.isEmpty()) {
            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 600.0F);
            int startX = x + 8;
            int startY = y - 16;
            context.drawTexture(
                RenderLayer::getGuiTextured,
                Identifier.of("textures/gui/container/generic_54.png"),
                startX, startY,
                0, 0,
                backgroundWidth, 3 * 18 + 17,
                256, 256
            );
            context.drawTexture(
                RenderLayer::getGuiTextured,
                Identifier.of("textures/gui/container/generic_54.png"),
                startX, startY + 3 * 18 + 17,
                0, 215,
                backgroundWidth, 7,
                256, 256
            );
            int nameColor = new Color(63, 63, 63).getRGB();
            if (focusedSlot.getStack().getItem().getName().getStyle().getColor() != null) {
                nameColor = focusedSlot.getStack().getItem().getName().getStyle().getColor().getRgb();
            }
            context.drawText(MinecraftClient.getInstance().textRenderer, focusedSlot.getStack().getName(), startX + 8, startY + 6, nameColor, false);
            for (int i = 0; i < items.size(); i++) {
                if (i < 9) {
                    context.drawItem(items.get(i), startX + 8 + i * 18, startY + 18, 0);
                    context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, items.get(i), startX + 8 + i * 18, startY + 18);
                } else if (i < 18) {
                    context.drawItem(items.get(i), startX + 8 + (i - 9) * 18, startY + 18 + 18, 0);
                    context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, items.get(i), startX + 8 + (i - 9) * 18, startY + 18 + 18);
                } else {
                    context.drawItem(items.get(i), startX + 8 + (i - 18) * 18, startY + 18 + 36, 0);
                    context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, items.get(i), startX + 8 + (i - 18) * 18, startY + 18 + 36);
                }
            }
            context.getMatrices().pop();
        }
    }

    public static void mapTooltipHandler(DrawContext context, int x, int y, Slot focusedSlot) {
        context.getMatrices().push();
        int startX = x - 78;
        int startY = y - 16;
        context.getMatrices().translate(0.0F, 0.0F, 599.0F);
        context.drawTexture(
            RenderLayer::getGuiTextured,
            Identifier.of("textures/map/map_background_checkerboard.png"),
            startX, startY,
            0, 0,
            70, 70,
            70, 70
        );
        context.getMatrices().translate(startX + 3.0F, startY + 3.0F, 1.0F);
        context.getMatrices().scale(0.5F, 0.5F, 1.0F);
        MapRenderState mapRenderState = new MapRenderState();
        mapRenderState.texture = MinecraftClient.getInstance().getMapRenderer().textureManager.getTextureId(focusedSlot.getStack().get(DataComponentTypes.MAP_ID), FilledMapItem.getMapState(focusedSlot.getStack(), MinecraftClient.getInstance().world));
        MinecraftClient.getInstance().getMapRenderer().draw(
            mapRenderState,
            context.getMatrices(),
            context.vertexConsumers,
            true,
            15728880
        );
        context.getMatrices().pop();
    }

    public static void compassTooltipHandler(ItemStack stack, Consumer<Text> textConsumer) {
        MutableText text;
        if (stack.getComponents().contains(DataComponentTypes.LODESTONE_TRACKER)) {
            Optional<GlobalPos> optional = Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER)).target();
            if (optional.isPresent() && Objects.requireNonNull(MinecraftClient.getInstance().world).getDimensionEntry().getIdAsString().contains(optional.get().dimension().getValue().toShortTranslationKey())) {
                text = MutableText.of(Text.of(optional.get().pos().toShortString()).getContent());
            } else {
                text = MutableText.of(Text.of("No Target").getContent());
            }
        } else if (Objects.requireNonNull(MinecraftClient.getInstance().world).getDimensionEntry().getIdAsString().contains("overworld")) {
            text = MutableText.of(Text.of(MinecraftClient.getInstance().world.getSpawnPos().toShortString()).getContent());
        } else {
            text = MutableText.of(Text.of("No Target").getContent());
        }
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        textConsumer.accept(text);
    }

    public static void recoveryCompassTooltipHandler(Consumer<Text> textConsumer) {
        MutableText text;
        Optional<GlobalPos> optional = Objects.requireNonNull(MinecraftClient.getInstance().player).getLastDeathPos();
        if (optional.isPresent() && Objects.requireNonNull(MinecraftClient.getInstance().world).getDimensionEntry().getIdAsString().contains(optional.get().dimension().getValue().toShortTranslationKey())) {
            text = MutableText.of(Text.of(optional.get().pos().toShortString()).getContent());
        } else {
            text = MutableText.of(Text.of("No Target").getContent());
        }
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        textConsumer.accept(text);
    }

    public static void clockTooltipHandler(Consumer<Text> textConsumer) {
        MutableText text = getTimeOfDayText();
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        textConsumer.accept(text);
    }

    private static MutableText getTimeOfDayText() {
        MutableText text;
        if (!Objects.requireNonNull(MinecraftClient.getInstance().world).getDimension().hasFixedTime()) {
            long time = MinecraftClient.getInstance().world.getTimeOfDay() % 24000L;
            text = switch ((int) time) {
                case 1000 -> MutableText.of(Text.of("Day").getContent());
                case 6000 -> MutableText.of(Text.of("Noon").getContent());
                case 13000 -> MutableText.of(Text.of("Night").getContent());
                case 18000 -> MutableText.of(Text.of("Midnight").getContent());
                default -> MutableText.of(Text.of(String.valueOf(time)).getContent());
            };
        } else {
            text = MutableText.of(Text.of("Time Unknown").getContent());
        }
        return text;
    }

    public static void foodTooltipHandler(ItemStack stack, Consumer<Text> textConsumer) {
        MutableText text;
        FoodComponent food = stack.getComponents().get(DataComponentTypes.FOOD);

        text = MutableText.of(Text.of("Nutrition: " + Objects.requireNonNull(food).nutrition()).getContent());
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        textConsumer.accept(text);

        text = MutableText.of(Text.of(String.format("Saturation: %.1f", food.saturation())).getContent());
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        textConsumer.accept(text);
    }
}
