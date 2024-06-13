package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {
    @Shadow
    public Slot focusedSlot;
    @Shadow
    @Final
    protected T handler;
    @Shadow
    protected int backgroundWidth = 176;
    @Inject(method = "drawMouseoverTooltip", at = @At("TAIL"))
    protected void drawMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (handler.getCursorStack().isEmpty() && focusedSlot != null ) {
            if (ImprovedInventoryConfig.shulkerBoxTooltip && focusedSlot.getStack().getTranslationKey().contains("shulker_box")) {
                DefaultedList<ItemStack> items = DefaultedList.of();
                for (ItemStack itemStack : focusedSlot.getStack().getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).iterateNonEmpty()) {
                    items.add(itemStack);
                }
                if (!items.isEmpty()) {
                    context.getMatrices().push();
                    context.getMatrices().translate(0.0F, 0.0F, 600.0F);
                    int startX = x + 8;
                    int startY = y - 16;
                    context.drawTexture(Identifier.of("textures/gui/container/generic_54.png"), startX, startY, 0, 0, this.backgroundWidth, 3 * 18 + 17);
                    context.drawTexture(Identifier.of("textures/gui/container/generic_54.png"), startX, startY + 3 * 18 + 17, 0, 215, this.backgroundWidth, 7);
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
            if (ImprovedInventoryConfig.mapTooltip && focusedSlot.getStack().getItem() instanceof FilledMapItem) {
                if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().world.getMapState(focusedSlot.getStack().get(DataComponentTypes.MAP_ID)) != null) {
                    context.getMatrices().push();
                    int startX = x - 78;
                    int startY = y - 16;
                    context.getMatrices().translate(0.0F, 0.0F, 599.0F);
                    context.drawTexture(
                        Identifier.of("textures/map/map_background_checkerboard.png"),
                        startX, startY,
                        0, 0,
                        70, 70,
                        70, 70
                    );
                    context.getMatrices().translate(startX + 3.0F, startY + 3.0F, 1.0F);
                    context.getMatrices().scale(0.5F, 0.5F, 1.0F);
                    MinecraftClient.getInstance().gameRenderer.getMapRenderer().draw(
                        context.getMatrices(),
                        context.getVertexConsumers(),
                        focusedSlot.getStack().get(DataComponentTypes.MAP_ID),
                        MinecraftClient.getInstance().world.getMapState(focusedSlot.getStack().get(DataComponentTypes.MAP_ID)),
                        true,
                        15728880
                    );
                    context.getMatrices().pop();
                }
            }
        }
    }

}
