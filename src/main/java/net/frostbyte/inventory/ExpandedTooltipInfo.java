package net.frostbyte.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.CompassItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ExpandedTooltipInfo {

    public static List<ItemStack> getShulkerInventory(ItemStack stack) {
        List<ItemStack> shulkerInventory = new ArrayList<>(27);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            if (nbt.contains("BlockEntityTag")) {
                NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
                if (blockEntityTag.contains("Items")) {
                    NbtList items = blockEntityTag.getList("Items", 10);
                    for (NbtElement item : items) {
                        shulkerInventory.add(((NbtCompound) item).getByte("Slot"), ItemStack.fromNbt((NbtCompound) item));
                    }
                }
            }
        }
        return shulkerInventory;
    }

    public static void shulkerBoxTooltipHandler(DrawContext context, int x, int y, Slot focusedSlot, int backgroundWidth) {
        DefaultedList<ItemStack> items = DefaultedList.of();
        List<ItemStack> inventory = getShulkerInventory(focusedSlot.getStack());
        for (int i = 0; i < inventory.size(); i++) {
            items.add(i, inventory.get(i));
        }
        if (!items.isEmpty()) {
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 1000);
            int startX = x + 8;
            int startY = y - 16;
            context.drawTexture(
                Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/generic_54.png"),
                startX, startY,
                0, 0,
                backgroundWidth, 3 * 18 + 17,
                256, 256
            );
            context.drawTexture(
                Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/generic_54.png"),
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
                    context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, items.get(i), startX + 8 + i * 18, startY + 18);
                } else if (i < 18) {
                    context.drawItem(items.get(i), startX + 8 + (i - 9) * 18, startY + 18 + 18, 0);
                    context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, items.get(i), startX + 8 + (i - 9) * 18, startY + 18 + 18);
                } else {
                    context.drawItem(items.get(i), startX + 8 + (i - 18) * 18, startY + 18 + 36, 0);
                    context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, items.get(i), startX + 8 + (i - 18) * 18, startY + 18 + 36);
                }
            }
            context.getMatrices().pop();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void mapTooltipHandler(DrawContext context, int x, int y, Slot focusedSlot) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 1000);
        context.drawTexture(
            Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/map/map_background_checkerboard.png"),
            x - 78, y - 16,
            0, 0,
            70, 70,
            70, 70
        );
        context.getMatrices().translate(x - 78 + 3, y - 16 + 3, 1);
        context.getMatrices().scale(0.5F, 0.5F, 1.0F);
        int mapId = FilledMapItem.getMapId(focusedSlot.getStack());
        MapState mapState = FilledMapItem.getMapState(mapId, MinecraftClient.getInstance().world);
        MinecraftClient.getInstance().gameRenderer.getMapRenderer().draw(
            context.getMatrices(),
            context.getVertexConsumers(),
            mapId,
            mapState,
            true,
            15728880
        );
        context.getMatrices().pop();
    }

    @SuppressWarnings("DataFlowIssue")
    public static void compassTooltipHandler(ItemStack stack, List<Text> tooltip) {
        MutableText text;
        if (stack.getNbt() != null && CompassItem.hasLodestone(stack)) {
            Optional<RegistryKey<World>> dimension = World.CODEC.parse(NbtOps.INSTANCE, stack.getNbt().get("LodestoneDimension")).result();
            if (dimension.isPresent() && MinecraftClient.getInstance().world.getRegistryKey() == dimension.get()) {
                text = MutableText.of(Text.of(NbtHelper.toBlockPos(stack.getNbt().getCompound("LodestonePos")).toShortString()).getContent());
            } else {
                text = MutableText.of(Text.translatable("compass.target.none").getContent());
            }
        } else if (MinecraftClient.getInstance().world.getRegistryKey() == World.OVERWORLD) {
            text = MutableText.of(Text.of(MinecraftClient.getInstance().world.getSpawnPos().toShortString()).getContent());
        } else {
            text = MutableText.of(Text.translatable("compass.target.none").getContent());
        }
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        tooltip.add(text);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void recoveryCompassTooltipHandler(List<Text> tooltip) {
        MutableText text;
        Optional<GlobalPos> lastDeathPos = MinecraftClient.getInstance().player.getLastDeathPos();
        if (lastDeathPos.isPresent() && lastDeathPos.get().getDimension() == MinecraftClient.getInstance().world.getRegistryKey()) {
            text = MutableText.of(Text.of(lastDeathPos.get().getPos().toShortString()).getContent());
        } else {
            text = MutableText.of(Text.translatable("compass.target.none").getContent());
        }
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        tooltip.add(text);
    }

    public static void clockTooltipHandler(List<Text> tooltip) {
        MutableText text = getTimeOfDayText();
        Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
        tooltip.add(text);
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

    public static void foodTooltipHandler(ItemStack stack, List<Text> tooltip) {
        MutableText text;
        FoodComponent food = stack.getItem().getFoodComponent();
        if (food != null) {
            text = MutableText.of(Text.of(Text.translatable("food.tooltip_nutrition").getString() + food.getHunger()).getContent());
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(text);

            text = MutableText.of(Text.of(Text.translatable("food.tooltip_saturation").getString() + String.format("%.1f", food.getSaturationModifier())).getContent());
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(text);
        }
    }
}
