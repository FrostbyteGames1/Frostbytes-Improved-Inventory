package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if (ImprovedInventoryConfig.compassTooltip && stack.isOf(Items.COMPASS)) {
            MutableText text;
            if (stack.getComponents().contains(DataComponentTypes.LODESTONE_TRACKER)) {
                Optional<GlobalPos> optional = stack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER).target();
                if (optional.isPresent() && MinecraftClient.getInstance().world.getDimensionEntry().getIdAsString().contains(optional.get().dimension().getValue().toShortTranslationKey())) {
                    text = MutableText.of(Text.of(optional.get().pos().toShortString()).getContent());
                } else {
                    text = MutableText.of(Text.of("No Target").getContent());
                }
            } else if (MinecraftClient.getInstance().world.getDimensionEntry().getIdAsString().contains("overworld")) {
                text = MutableText.of(Text.of(MinecraftClient.getInstance().world.getSpawnPos().toShortString()).getContent());
            } else {
                text = MutableText.of(Text.of("No Target").getContent());
            }
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(text);
        } else if (ImprovedInventoryConfig.compassTooltip && stack.isOf(Items.RECOVERY_COMPASS)) {
            MutableText text;
            Optional<GlobalPos> optional = MinecraftClient.getInstance().player.getLastDeathPos();
            if (optional.isPresent() && MinecraftClient.getInstance().world.getDimensionEntry().getIdAsString().contains(optional.get().dimension().getValue().toShortTranslationKey())) {
                text = MutableText.of(Text.of(optional.get().pos().toShortString()).getContent());
            } else {
                text = MutableText.of(Text.of("No Target").getContent());
            }
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(text);
        } else if (ImprovedInventoryConfig.clockTooltip && stack.isOf(Items.CLOCK)) {
            MutableText text;
            if (!MinecraftClient.getInstance().world.getDimension().hasFixedTime()) {
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
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(text);
        }
        if (ImprovedInventoryConfig.foodTooltip && stack.getComponents().contains(DataComponentTypes.FOOD)) {
            MutableText text;
            FoodComponent food = stack.getComponents().get(DataComponentTypes.FOOD);

            text = MutableText.of(Text.of("Nutrition: " + food.nutrition()).getContent());
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(text);

            text = MutableText.of(Text.of(String.format("Saturation: %.1f", food.saturation())).getContent());
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(text);
        }
    }
}
