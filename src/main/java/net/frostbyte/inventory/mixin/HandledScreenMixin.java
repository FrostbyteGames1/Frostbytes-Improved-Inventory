package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ContainerSearch;
import net.frostbyte.inventory.ExpandedTooltipInfo;
import net.frostbyte.inventory.ImprovedInventory;
import net.frostbyte.inventory.NearbyContainerViewer;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.gui.widget.HoverableIconWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    private static final Identifier BUTTON_WIDGET_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/widget/button.png");
    @Unique
    private static final ButtonTextures SEARCH_BUTTON_TEXTURES = new ButtonTextures(
        Identifier.of("icon/search"),
        Identifier.of("icon/search")
    );
    @Unique
    private static HoverableIconWidget searchInfoHoverableIcon;
    @Unique
    private static final Identifier SEARCH_INFO_HOVERABLE_ICON_TEXTURE = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/widget/info_button.png");
    @Unique
    private static boolean hasLoadedItemGroups = false;
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("TAIL"))
    protected void drawMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (handler.getCursorStack().isEmpty() && focusedSlot != null ) {
            if (ImprovedInventoryConfig.shulkerBoxTooltip && focusedSlot.getStack().getComponents().contains(DataComponentTypes.CONTAINER)) {
                ExpandedTooltipInfo.shulkerBoxTooltipHandler(context, x, y, focusedSlot, this.backgroundWidth);
            }
            if (ImprovedInventoryConfig.mapTooltip && focusedSlot.getStack().getItem() instanceof FilledMapItem) {
                if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().world.getMapState(focusedSlot.getStack().get(DataComponentTypes.MAP_ID)) != null) {
                    ExpandedTooltipInfo.mapTooltipHandler(context, x, y, focusedSlot);
                }
            }
        }
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void addSearchField(CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            // Send item group info to the client
            if (!hasLoadedItemGroups && client != null && client.player != null && client.world != null) {
                ItemGroups.updateDisplayContext(FeatureFlags.FEATURE_MANAGER.getFeatureSet(), true, client.world.getRegistryManager());
                hasLoadedItemGroups = true;
            }
            searchField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.x + this.backgroundWidth - 169, this.y + 4, 162, 12, searchField, Text.of(""));
            searchField.setPlaceholder(Text.translatable("itemGroup.search"));
            this.addSelectableChild(searchField);
            searchField.setVisible(false);
            searchField.setText("");
            searchInfoHoverableIcon = HoverableIconWidget.create(12, 12, SEARCH_INFO_HOVERABLE_ICON_TEXTURE, 12, 12);
            searchInfoHoverableIcon.setPosition(this.x + this.backgroundWidth + 1, this.y + 4);
            searchInfoHoverableIcon.setTooltip(Tooltip.of(ContainerSearch.searchInfoTooltipText));
            searchInfoHoverableIcon.visible = false;
            searchInfoHoverableIcon.active = false;
            searchButton = new TexturedButtonWidget(this.x + this.backgroundWidth - 17, this.y + 6, 8, 8, SEARCH_BUTTON_TEXTURES, button -> {
                searchField.setVisible(true);
                searchInfoHoverableIcon.visible = true;
                searchInfoHoverableIcon.active = true;
            });
            this.addSelectableChild(searchButton);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, BUTTON_WIDGET_TEXTURE, this.x + this.backgroundWidth - 19, this.y + 4, 0, 0, 12, 12, 12, 12);
            searchButton.render(context, mouseX, mouseY, delta);
            searchField.render(context, mouseX, mouseY, delta);
            searchInfoHoverableIcon.render(context, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (super.keyPressed(input)) {
            cir.setReturnValue(true);
        } else if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            if (searchField.isActive()) {
                searchField.keyPressed(input);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            if (!searchField.isHovered()) {
                searchField.setFocused(false);
                if ((searchField.getText().isEmpty() || searchField.getText().isBlank()) && !searchInfoHoverableIcon.isHovered()) {
                    searchField.setVisible(false);
                    searchInfoHoverableIcon.visible = false;
                    searchInfoHoverableIcon.active = false;
                }
            } else if (searchField.isHovered() && click.button() == GLFW.GLFW_MOUSE_BUTTON_2) {
                searchField.setText("");
            } else if (searchField.isHovered() && click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
                searchField.setFocused(true);
            }
        }
    }

    @Inject(method = "drawSlot", at = @At("TAIL"))
    protected void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler)) {
            if (ContainerSearch.doesStackContainString(MinecraftClient.getInstance(), searchField.getText(), slot.getStack())) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HandledScreen.SLOT_HIGHLIGHT_BACK_TEXTURE, slot.x - 4, slot.y - 4, 24, 24);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HandledScreen.SLOT_HIGHLIGHT_FRONT_TEXTURE, slot.x - 4, slot.y - 4, 24, 24);
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        // Nearby Container Viewer
        if (ImprovedInventoryConfig.containerTab) {
            NearbyContainerViewer.nearbyContainerViewerHandler(client);
        }
    }

}
