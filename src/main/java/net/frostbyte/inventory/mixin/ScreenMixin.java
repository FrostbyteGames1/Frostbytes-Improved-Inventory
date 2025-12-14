package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.InventorySorter;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.gui.widget.TexturedButtonWithItemStackWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.frostbyte.inventory.NearbyContainerViewer.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow protected abstract <T extends Element & Drawable> T addDrawableChild(T drawableElement);
    @Shadow protected MinecraftClient client;
    @Shadow protected abstract void clearChildren();
    @Shadow protected abstract void init();
    @Unique
    private static final ButtonTextures TEXTURES_LEFT = new ButtonTextures(
        Identifier.of("container/creative_inventory/tab_top_unselected_1"),
        Identifier.of("container/creative_inventory/tab_top_unselected_1")
    );
    @Unique
    private static final ButtonTextures TEXTURES_LEFT_SELECTED = new ButtonTextures(
        Identifier.of("container/creative_inventory/tab_top_selected_1"),
        Identifier.of("container/creative_inventory/tab_top_selected_1")
    );
    @Unique
    private static final ButtonTextures TEXTURES_MID = new ButtonTextures(
        Identifier.of("container/creative_inventory/tab_top_unselected_2"),
        Identifier.of("container/creative_inventory/tab_top_unselected_2")
    );
    @Unique
    private static final ButtonTextures TEXTURES_MID_SELECTED = new ButtonTextures(
        Identifier.of("container/creative_inventory/tab_top_selected_2"),
        Identifier.of("container/creative_inventory/tab_top_selected_2")
    );
    @Unique
    private static final ButtonTextures TEXTURES_RIGHT = new ButtonTextures(
        Identifier.of("container/creative_inventory/tab_top_unselected_7"),
        Identifier.of("container/creative_inventory/tab_top_unselected_7")
    );
    @Unique
    private static final ButtonTextures TEXTURES_RIGHT_SELECTED = new ButtonTextures(
        Identifier.of("container/creative_inventory/tab_top_selected_7"),
        Identifier.of("container/creative_inventory/tab_top_selected_7")
    );
    @Unique
    private static final ButtonTextures TEXTURES_FORWARD = new ButtonTextures(
        Identifier.of("transferable_list/select"),
        Identifier.of("transferable_list/select_highlighted")
    );
    @Unique
    private static final ButtonTextures TEXTURES_BACK = new ButtonTextures(
        Identifier.of("transferable_list/unselect"),
        Identifier.of("transferable_list/unselect_highlighted")
    );
    @Unique
    int screenWidth;
    @Unique
    int screenHeight;

    @Inject(method = "init", at = @At("HEAD"))
    public void init(int width, int height, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab && !ImprovedInventoryConfig.containerTabKeybindOnly && !containers.isEmpty() && client.world != null && client.player != null && client.interactionManager != null && client.currentScreen instanceof HandledScreen<?> screen && !(client.currentScreen instanceof CreativeInventoryScreen) && !(client.currentScreen instanceof MerchantScreen)) {
            screenWidth = screen.backgroundWidth;
            screenHeight = screen.backgroundHeight;
            if (screen instanceof HopperScreen) {
                screenHeight += 2;
            } else if (screen instanceof ShulkerBoxScreen) {
                screenHeight += 1;
            }
            ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
            playerHead.set(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(client.player.getGameProfile()));
            TexturedButtonWithItemStackWidget tab;
            if (client.currentScreen instanceof InventoryScreen) {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURES_LEFT_SELECTED, playerHead, button -> {
                    if (client.interactionManager.hasRidingInventory()) {
                        client.player.openRidingInventory();
                    } else {
                        client.currentScreen.close();
                        client.getTutorialManager().onInventoryOpened();
                        client.setScreen(new InventoryScreen(client.player));
                    }
                });
            } else {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURES_LEFT, playerHead, button -> {
                    if (client.interactionManager.hasRidingInventory()) {
                        client.player.openRidingInventory();
                    } else {
                        client.currentScreen.close();
                        client.getTutorialManager().onInventoryOpened();
                        client.setScreen(new InventoryScreen(client.player));
                    }
                });
            }
            tab.setTooltip(Tooltip.of(client.player.getDisplayName()));
            addDrawableChild(tab);

            int maxTabs = screenWidth / 26;
            int numTabs = Math.min(maxTabs, containers.size());
            for (int i = 0; i < numTabs; i++) {
                int container = i;
                Text displayName = getDisplayName(containers.get(container));
                ItemStack displayStack = getDisplayStack(containers.get(container));
                if (i == maxTabs - 1) {
                    if (!(client.currentScreen instanceof InventoryScreen) && current == container) {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURES_RIGHT_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURES_RIGHT, displayStack, button -> openContainer(container));
                    }
                } else {
                    if (!(client.currentScreen instanceof InventoryScreen) && current == container) {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURES_MID_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURES_MID, displayStack, button -> openContainer(container));
                    }
                }
                tab.setTooltip(Tooltip.of(displayName));
                addDrawableChild(tab);
            }

            if (containers.size() > maxTabs - 1) {
                TexturedButtonWidget backButton = new TexturedButtonWidget(width / 2 + screenWidth / 2 - 2, height / 2 - screenHeight / 2 - 24, 24, 24, TEXTURES_FORWARD, button -> {
                    containers.addLast(containers.removeFirst());
                    current--;
                    if (current < 0) {
                        current = containers.size() - 1;
                    }
                    this.clearChildren();
                    this.init(width, height, ci);
                    this.init();
                });
                TexturedButtonWidget forwardButton = new TexturedButtonWidget(width / 2 - screenWidth / 2 - 17, height / 2 - screenHeight / 2 - 24, 24, 24, TEXTURES_BACK, button -> {
                    containers.addFirst(containers.removeLast());
                    current++;
                    if (current == containers.size()) {
                        current = 0;
                    }
                    this.clearChildren();
                    this.init(width, height, ci);
                    this.init();
                });
                addDrawableChild(backButton);
                addDrawableChild(forwardButton);
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
            InventorySorter.inventorySortHandler(client);
        }
    }

}
