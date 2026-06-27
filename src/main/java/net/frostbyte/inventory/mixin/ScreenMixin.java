package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.InventorySorter;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.gui.components.TexturedButtonWithItemStackWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.frostbyte.inventory.NearbyContainerViewer.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    protected abstract <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget);

    @Shadow
    @Final
    private List<GuiEventListener> children;

    @Shadow
    protected abstract void init();

    @Shadow
    protected abstract void clearWidgets();

    @Unique
    private static final WidgetSprites TEXTURES_LEFT = new WidgetSprites(
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_1"),
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_1")
    );
    @Unique
    private static final WidgetSprites TEXTURES_LEFT_SELECTED = new WidgetSprites(
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_1"),
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_1")
    );
    @Unique
    private static final WidgetSprites TEXTURES_MID = new WidgetSprites(
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2"),
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2")
    );
    @Unique
    private static final WidgetSprites TEXTURES_MID_SELECTED = new WidgetSprites(
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_2"),
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_2")
    );
    @Unique
    private static final WidgetSprites TEXTURES_RIGHT = new WidgetSprites(
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_7"),
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_7")
    );
    @Unique
    private static final WidgetSprites TEXTURES_RIGHT_SELECTED = new WidgetSprites(
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_7"),
        Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_7")
    );
    @Unique
    private static final WidgetSprites TEXTURES_BACK = new WidgetSprites(
        Identifier.withDefaultNamespace("transferable_list/select"),
        Identifier.withDefaultNamespace("transferable_list/select_highlighted")
    );
    @Unique
    private static final WidgetSprites TEXTURES_FORWARD = new WidgetSprites(
        Identifier.withDefaultNamespace("transferable_list/unselect"),
        Identifier.withDefaultNamespace("transferable_list/unselect_highlighted")
    );
    @Unique
    int screenWidth;
    @Unique
    int screenHeight;
    
    @Inject(method = "init(II)V", at = @At("HEAD"))
    public void init(int width, int height, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab && !ImprovedInventoryConfig.containerTabKeybindOnly && !containers.isEmpty() && minecraft.level != null && minecraft.player != null && minecraft.screen instanceof AbstractContainerScreen<?> containerScreen && !(minecraft.screen instanceof CreativeModeInventoryScreen) && !(minecraft.screen instanceof MerchantScreen)) {
            screenWidth = containerScreen.imageWidth;
            screenHeight = containerScreen.imageHeight;
            ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
            playerHead.set(DataComponents.PROFILE, ResolvableProfile.createResolved(minecraft.player.getGameProfile()));
            TexturedButtonWithItemStackWidget tab;
            if (minecraft.screen instanceof InventoryScreen) {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURES_LEFT_SELECTED, playerHead, button -> {
                    if (minecraft.player.getVehicle() != null && minecraft.player.getVehicle() instanceof AbstractHorse horse) {
                        minecraft.player.openHorseInventory(horse, minecraft.player.getInventory());
                    } else if (minecraft.player.getVehicle() != null && minecraft.player.getVehicle() instanceof AbstractChestBoat boat) {
                        boat.interactWithContainerVehicle(minecraft.player);
                    } else {
                        minecraft.screen.onClose();
                        minecraft.getTutorial().onOpenInventory();
                        minecraft.setScreen(new InventoryScreen(minecraft.player));
                    }
                });
            } else {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURES_LEFT, playerHead, button -> {
                    if (minecraft.player.getVehicle() != null && minecraft.player.getVehicle() instanceof AbstractHorse horse) {
                        minecraft.player.openHorseInventory(horse, minecraft.player.getInventory());
                    } else if (minecraft.player.getVehicle() != null && minecraft.player.getVehicle() instanceof AbstractChestBoat boat) {
                        boat.interactWithContainerVehicle(minecraft.player);
                    } else {
                        minecraft.screen.onClose();
                        minecraft.getTutorial().onOpenInventory();
                        minecraft.setScreen(new InventoryScreen(minecraft.player));
                    }
                });
            }
            tab.setTooltip(Tooltip.create(minecraft.player.getDisplayName()));
            addRenderableWidget(tab);

            int maxTabs = screenWidth / 26;
            int numTabs = Math.min(maxTabs, containers.size());
            for (int i = 0; i < numTabs; i++) {
                int container = i;
                Component displayName = getDisplayName(containers.get(container));
                ItemStack displayStack = getDisplayStack(containers.get(container));
                if (i == maxTabs - 1) {
                    if (!(minecraft.screen instanceof InventoryScreen) && current == container) {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURES_RIGHT_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURES_RIGHT, displayStack, button -> openContainer(container));
                    }
                } else {
                    if (!(minecraft.screen instanceof InventoryScreen) && current == container) {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURES_MID_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURES_MID, displayStack, button -> openContainer(container));
                    }
                }
                tab.setTooltip(Tooltip.create(displayName));
                addRenderableWidget(tab);
            }

            if (containers.size() > maxTabs - 1) {
                ImageButton backButton = new ImageButton(width / 2 + screenWidth / 2 - 2, height / 2 - screenHeight / 2 - 24, 24, 24, TEXTURES_BACK, button -> {
                    containers.addLast(containers.removeFirst());
                    current--;
                    if (current < 0) {
                        current = containers.size() - 1;
                    }
                    this.clearWidgets();
                    this.children.clear();
                    this.init(width, height, ci);
                    this.init();
                });
                ImageButton forwardButton = new ImageButton(width / 2 - screenWidth / 2 - 17, height / 2 - screenHeight / 2 - 24, 24, 24, TEXTURES_FORWARD, button -> {
                    containers.addFirst(containers.removeLast());
                    current++;
                    if (current == containers.size()) {
                        current = 0;
                    }
                    this.clearWidgets();
                    this.children.clear();
                    this.init(width, height, ci);
                    this.init();
                });
                addRenderableWidget(backButton);
                addRenderableWidget(forwardButton);
            }
        }
    }

    @Inject(method = "resize", at = @At("TAIL"))
    public void resize(int width, int height, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab && !ImprovedInventoryConfig.containerTabKeybindOnly) {
            this.init(width, height, ci);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (!InventorySorter.sortKey.isUnbound()) {
            InventorySorter.inventorySortHandler(minecraft);
        }
    }

}
