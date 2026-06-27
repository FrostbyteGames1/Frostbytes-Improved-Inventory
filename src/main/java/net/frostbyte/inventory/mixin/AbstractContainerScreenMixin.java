package net.frostbyte.inventory.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.frostbyte.inventory.ContainerSearch;
import net.frostbyte.inventory.ExpandedTooltipInfo;
import net.frostbyte.inventory.ImprovedInventory;
import net.frostbyte.inventory.NearbyContainerViewer;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Shadow
    @Nullable
    public Slot hoveredSlot;
    @Shadow
    @Final
    public int imageWidth;
    @Shadow
    @Final
    protected T menu;
    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    @Unique
    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("textures/gui/sprites/container/slot_highlight_back.png");
    @Unique
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("textures/gui/sprites/container/slot_highlight_front.png");
    @Unique
    private static EditBox searchField;
    @Unique
    private static ImageButton searchButton;
    @Unique
    private static final Identifier BUTTON_WIDGET_TEXTURE = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "textures/gui/sprites/widget/button.png");
    @Unique
    private static final WidgetSprites SEARCH_BUTTON_TEXTURES = new WidgetSprites(
        Identifier.withDefaultNamespace("icon/search"),
        Identifier.withDefaultNamespace("icon/search")
    );
    @Unique
    private static ImageWidget searchInfoHoverableIcon;
    @Unique
    private static final Identifier SEARCH_INFO_HOVERABLE_ICON_TEXTURE = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "textures/gui/sprites/widget/info_button.png");
    @Unique
    private static boolean hasLoadedItemGroups = false;
    protected AbstractContainerScreenMixin(final Component title) {
        super(title);
    }
    @Unique
    private static List<MenuType<?>> searchableMenus = List.of(
        MenuType.GENERIC_9x1,
        MenuType.GENERIC_9x2,
        MenuType.GENERIC_9x3,
        MenuType.GENERIC_9x4,
        MenuType.GENERIC_9x5,
        MenuType.GENERIC_9x6,
        MenuType.GENERIC_3x3,
        MenuType.SHULKER_BOX
    );
    
    @Inject(method = "extractTooltip", at = @At("HEAD"), cancellable = true)
    protected void drawMouseoverTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (hoveredSlot != null && hoveredSlot.hasItem() ) {
            if (ImprovedInventoryConfig.shulkerBoxTooltip && hoveredSlot.getItem().getComponents().has(DataComponents.CONTAINER)) {
                int color = -1;
                if (ImprovedInventoryConfig.shulkerBoxTooltipColors) {
                    if (hoveredSlot.getItem().getItem() == Items.WHITE_SHULKER_BOX) {
                        color = DyeColor.WHITE.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.ORANGE_SHULKER_BOX) {
                        color = DyeColor.ORANGE.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.MAGENTA_SHULKER_BOX) {
                        color = DyeColor.MAGENTA.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.LIGHT_BLUE_SHULKER_BOX) {
                        color = DyeColor.LIGHT_BLUE.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.YELLOW_SHULKER_BOX) {
                        color = DyeColor.YELLOW.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.LIME_SHULKER_BOX) {
                        color = DyeColor.LIME.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.PINK_SHULKER_BOX) {
                        color = DyeColor.PINK.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.GRAY_SHULKER_BOX) {
                        color = DyeColor.GRAY.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.LIGHT_GRAY_SHULKER_BOX) {
                        color = DyeColor.LIGHT_GRAY.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.CYAN_SHULKER_BOX) {
                        color = DyeColor.CYAN.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.PURPLE_SHULKER_BOX) {
                        color = DyeColor.PURPLE.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.BLUE_SHULKER_BOX) {
                        color = DyeColor.BLUE.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.BROWN_SHULKER_BOX) {
                        color = DyeColor.BROWN.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.GREEN_SHULKER_BOX) {
                        color = DyeColor.GREEN.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.RED_SHULKER_BOX) {
                        color = DyeColor.RED.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.BLACK_SHULKER_BOX) {
                        color = DyeColor.BLACK.getTextureDiffuseColor();
                    } else if (hoveredSlot.getItem().getItem() == Items.SHULKER_BOX) {
                        color = new Color(148, 100, 148).getRGB();
                    }
                }
                ExpandedTooltipInfo.shulkerBoxTooltipHandler(graphics, mouseX, mouseY, hoveredSlot, imageWidth, color);
                ci.cancel();
            }
            if (ImprovedInventoryConfig.mapTooltip && hoveredSlot.getItem().getItem() instanceof MapItem) {
                if (Minecraft.getInstance().level != null && hoveredSlot.getItem().get(DataComponents.MAP_ID) != null) {
                    ExpandedTooltipInfo.mapTooltipHandler(graphics, mouseX, mouseY, hoveredSlot);
                }
            }
        }
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void addSearchField(CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && menu.menuType != null && searchableMenus.contains(menu.getType())) {
            // Send item group info to the client
            if (!hasLoadedItemGroups && minecraft.player != null && minecraft.level != null) {
                CreativeModeTabs.tryRebuildTabContents(FeatureFlags.DEFAULT_FLAGS, true, minecraft.level.registryAccess());
                hasLoadedItemGroups = true;
            }
            searchField = new EditBox(Minecraft.getInstance().font, this.leftPos + this.imageWidth - 169, this.topPos + 4, 162, 12, searchField, Component.literal(""));
            searchField.setHint(Component.translatable("itemGroup.search"));
            this.addRenderableWidget(searchField);
            searchField.setVisible(false);
            searchField.setValue("");
            searchInfoHoverableIcon = ImageWidget.texture(12, 12, SEARCH_INFO_HOVERABLE_ICON_TEXTURE, 12, 12);
            searchInfoHoverableIcon.setX(this.leftPos + this.imageWidth + 1);
            searchInfoHoverableIcon.setY(this.topPos + 4);
            searchInfoHoverableIcon.setTooltip(Tooltip.create(ContainerSearch.searchInfoTooltipText));
            searchInfoHoverableIcon.visible = false;
            searchInfoHoverableIcon.active = false;
            searchButton = new ImageButton(this.leftPos + this.imageWidth - 17, this.topPos + 6, 8, 8, SEARCH_BUTTON_TEXTURES, button -> {
                searchField.setVisible(true);
                searchInfoHoverableIcon.visible = true;
                searchInfoHoverableIcon.active = true;
            });
            this.addRenderableWidget(searchButton);
        }
    }
    
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && menu.menuType != null && searchableMenus.contains(menu.getType())) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BUTTON_WIDGET_TEXTURE, this.leftPos + this.imageWidth - 19, this.topPos + 4, 0, 0, 12, 12, 12, 12);
            searchButton.extractRenderState(graphics, mouseX, mouseY, a);
            searchField.extractRenderState(graphics, mouseX, mouseY, a);
            searchInfoHoverableIcon.extractRenderState(graphics, mouseX, mouseY, a);
        }
    }
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (super.keyPressed(event)) {
            cir.setReturnValue(true);
        } else if (ImprovedInventoryConfig.containerSearch && menu.menuType != null && searchableMenus.contains(menu.getType())) {
            if (searchField.isActive()) {
                searchField.keyPressed(event);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void mouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (ImprovedInventoryConfig.containerSearch && menu.menuType != null && searchableMenus.contains(menu.getType())) {
            if (!searchField.isHovered()) {
                searchField.setFocused(false);
                if ((searchField.getValue().isEmpty() || searchField.getValue().isBlank()) && !searchInfoHoverableIcon.isHovered()) {
                    searchField.setVisible(false);
                    searchInfoHoverableIcon.visible = false;
                    searchInfoHoverableIcon.active = false;
                }
            } else if (searchField.isHovered() && event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                searchField.setValue("");
            } else if (searchField.isHovered() && event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                searchField.setFocused(true);
            }
        }
    }

    @Inject(method = "extractContents", at = @At("TAIL"))
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerSearch && menu.menuType != null && searchableMenus.contains(menu.getType())) {
            for (Slot slot : this.menu.slots) {
                if (ContainerSearch.doesStackContainString(Minecraft.getInstance(), searchField.getValue(), slot.getItem())) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, this.leftPos + slot.x - 4, this.topPos + slot.y - 4, 0, 0, 24, 24, 24, 24);
                    graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, this.leftPos + slot.x - 4, this.topPos + slot.y - 4, 0, 0, 24, 24, 24, 24);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        // Nearby Container Viewer
        if (ImprovedInventoryConfig.containerTab) {
            NearbyContainerViewer.nearbyContainerViewerHandler(minecraft);
        }
    }

}
