package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ImprovedInventory;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {
    @Shadow
    public Slot focusedSlot;
    @Shadow
    @Final
    protected T handler;
    @Shadow
    protected int backgroundWidth;
    @Shadow
    protected int x;
    @Shadow
    protected int y;
    @Unique
    private static TextFieldWidget searchField;
    @Unique
    private static TexturedButtonWidget searchButton;
    @Unique
    private static final ButtonTextures SEARCH_BUTTON_TEXTURES = new ButtonTextures(
        Identifier.of("icon/search"),
        Identifier.of("icon/search")
    );

    protected HandledScreenMixin(Text title) {
        super(title);
    }

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

    @Inject(method = "init()V", at = @At("RETURN"))
    private void addSearchField(CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            searchField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.x + this.backgroundWidth - 87, this.y + 4, 80, 12, searchField, Text.of(""));
            searchField.setPlaceholder(Text.translatable("itemGroup.search"));
            this.addSelectableChild(searchField);
            searchField.setVisible(false);
            searchField.setText("");
            searchButton = new TexturedButtonWidget(this.x + this.backgroundWidth - 17, this.y + 6, 8, 8, SEARCH_BUTTON_TEXTURES, button -> searchField.setVisible(true));
            this.addSelectableChild(searchButton);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            context.drawTexture(Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/widget/button.png"), this.x + this.backgroundWidth - 19, this.y + 4, 0, 0, 12, 12, 12, 12);
            searchButton.render(context, mouseX, mouseY, delta);
            searchField.render(context, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        } else if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            if (searchField.isActive()) {
                searchField.keyPressed(keyCode, scanCode, modifiers);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            if (!searchField.isHovered()) {
                searchField.setFocused(false);
                if (searchField.getText().isEmpty() || searchField.getText().isBlank()) {
                    searchField.setVisible(false);
                }
            } else if (searchField.isHovered() && button == GLFW.GLFW_MOUSE_BUTTON_2) {
                searchField.setText("");
            } else if (searchField.isHovered() && button == GLFW.GLFW_MOUSE_BUTTON_1) {
                searchField.setFocused(true);
            }
        }
    }

    @Inject(method = "drawSlot", at = @At("TAIL"))
    protected void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            if (doesStackContainString(searchField.getText(), slot.getStack())) {
                HandledScreen.drawSlotHighlight(context, slot.x, slot.y, 600);
            }
        }
    }

    @Unique
    private boolean doesStackContainString(String search, ItemStack stack) {
        if (search.isEmpty() || search.isBlank() || stack.isEmpty()) {
            return false;
        }
        return stack.getName().getString().toLowerCase().replaceAll(" ", "").contains(search.toLowerCase().replaceAll(" ", "")) || stack.getItem().getDefaultStack().getName().getString().toLowerCase().replaceAll(" ", "").contains(search.toLowerCase().replaceAll(" ", ""));
    }

}
