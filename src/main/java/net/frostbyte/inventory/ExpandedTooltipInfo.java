package net.frostbyte.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
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
            context.getMatrices().pushMatrix();
            int startX = x + 8;
            int startY = y - 16;
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                Identifier.of("textures/gui/container/generic_54.png"),
                startX, startY,
                0, 0,
                backgroundWidth, 3 * 18 + 17,
                256, 256
            );
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
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
            context.getMatrices().popMatrix();
        }
    }

    public static void mapTooltipHandler(DrawContext context, int x, int y, Slot focusedSlot) {
        context.getMatrices().pushMatrix();
        int startX = x - 78;
        int startY = y - 16;
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            Identifier.of("textures/map/map_background_checkerboard.png"),
            startX, startY,
            0, 0,
            70, 70,
            70, 70
        );
        startX += 3;
        startY += 3;
        MapIdComponent mapIdComponent = focusedSlot.getStack().get(DataComponentTypes.MAP_ID);
        MapState mapState = FilledMapItem.getMapState(mapIdComponent, MinecraftClient.getInstance().world);
        if (mapState != null) {
            MinecraftClient.getInstance().getMapRenderer().update(mapIdComponent, mapState, new MapRenderState());
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                MinecraftClient.getInstance().getMapRenderer().textureManager.getTextureId(mapIdComponent, mapState),
                startX, startY,
                0, 0,
                64, 64,
                64, 64
            );

            for (MapDecoration decoration : mapState.getDecorations()) {
                Identifier sprite = decoration.getAssetId();
                if (sprite != null) {
                    sprite = sprite.withPrefixedPath("textures/map/decorations/");
                    sprite = sprite.withSuffixedPath(".png");
                    if (sprite.toString().endsWith("player.png")) {
                        String rotation = switch (decoration.rotation()) {
                            case 0 -> "_south";
                            case 1, 2, 3 -> "_southwest";
                            case 4 -> "_west";
                            case 5, 6, 7 -> "_northwest";
                            default -> "_north";
                            case 9, 10, 11 -> "_northeast";
                            case 12 -> "_east";
                            case 13, 14, 15 -> "_southeast";
                        };
                        sprite = Identifier.of(ImprovedInventory.MOD_ID, "textures/map/decorations/player" + rotation + ".png");
                    }
                    float decorationX = decoration.x() / 4F + 32F;
                    float decorationY = decoration.z() / 4F + 32F;
                    context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        sprite,
                        (int) (startX + decorationX), (int) (startY + decorationY),
                        0, 0,
                        4, 4,
                        4, 4
                    );
                }
            }
        }
        context.getMatrices().popMatrix();
    }

    public static void compassTooltipHandler(ItemStack stack, Consumer<Text> textConsumer) {
        MutableText text;
        if (stack.getComponents().contains(DataComponentTypes.LODESTONE_TRACKER)) {
            Optional<GlobalPos> optional = Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER)).target();
            if (optional.isPresent() && Objects.requireNonNull(MinecraftClient.getInstance().world).getDimensionEntry().getIdAsString().contains(optional.get().dimension().getValue().toShortTranslationKey())) {
                text = MutableText.of(Text.of(optional.get().pos().toShortString()).getContent());
            } else {
                text = MutableText.of(Text.translatable("compass.target.none").getContent());
            }
        } else if (Objects.requireNonNull(MinecraftClient.getInstance().world).getDimensionEntry().getIdAsString().contains("overworld")) {
            text = MutableText.of(Text.of(MinecraftClient.getInstance().world.getSpawnPos().toShortString()).getContent());
        } else {
            text = MutableText.of(Text.translatable("compass.target.none").getContent());
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
            text = MutableText.of(Text.translatable("compass.target.none").getContent());
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
                case 1000 -> MutableText.of(Text.translatable("clock.time.day").getContent());
                case 6000 -> MutableText.of(Text.translatable("clock.time.noon").getContent());
                case 13000 -> MutableText.of(Text.translatable("clock.time.night").getContent());
                case 18000 -> MutableText.of(Text.translatable("clock.time.midnight").getContent());
                default -> MutableText.of(Text.of(String.valueOf(time)).getContent());
            };
        } else {
            text = MutableText.of(Text.translatable("clock.time.none").getContent());
        }
        return text;
    }

    public static void foodTooltipHandler(ItemStack stack, Consumer<Text> textConsumer) {
        MutableText text;
        FoodComponent food = stack.getComponents().get(DataComponentTypes.FOOD);

        text = MutableText.of(Text.of(Text.translatable("food.tooltip_nutrition").getString() + Objects.requireNonNull(food).nutrition()).getContent());
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        textConsumer.accept(text);

        text = MutableText.of(Text.of(Text.translatable("food.tooltip_saturation").getString() + String.format("%.1f", food.saturation())).getContent());
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        textConsumer.accept(text);
    }
}
