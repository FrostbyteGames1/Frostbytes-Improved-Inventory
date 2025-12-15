package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ImprovedInventory;
import net.frostbyte.inventory.InventorySorter;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.gui.widget.TexturedButtonWithItemStackWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
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
    @Shadow protected abstract void clearChildren();
    @Shadow
    protected abstract void init();
    @Shadow protected MinecraftClient client;
    @Unique
    private static final Identifier TEXTURE_LEFT = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/container/creative_inventory/tab_top_unselected_1.png");
    @Unique
    private static final Identifier TEXTURE_LEFT_SELECTED = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/container/creative_inventory/tab_top_selected_1.png");
    @Unique
    private static final Identifier TEXTURE_MID = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/container/creative_inventory/tab_top_unselected_2.png");
    @Unique
    private static final Identifier TEXTURE_MID_SELECTED = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/container/creative_inventory/tab_top_selected_2.png");
    @Unique
    private static final Identifier TEXTURE_RIGHT = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/container/creative_inventory/tab_top_unselected_7.png");
    @Unique
    private static final Identifier TEXTURE_RIGHT_SELECTED = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/container/creative_inventory/tab_top_selected_7.png");
    @Unique
    private static final Identifier TEXTURE_FORWARD = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/transferable_list/select.png");
    @Unique
    private static final Identifier TEXTURE_BACK = Identifier.of(ImprovedInventory.MOD_ID, "textures/gui/sprites/transferable_list/unselect.png");
    @Unique
    int screenWidth = 176;
    @Unique
    int screenHeight = 166;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    public void init(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab && !ImprovedInventoryConfig.containerTabKeybindOnly && !containers.isEmpty() && client.world != null && client.player != null && client.interactionManager != null && client.currentScreen instanceof HandledScreen<?> screen && !(client.currentScreen instanceof CreativeInventoryScreen) && !(client.currentScreen instanceof MerchantScreen)) {
            screenWidth = screen.backgroundWidth;
            screenHeight = screen.backgroundHeight;
            if (screen instanceof HopperScreen) {
                screenHeight += 2;
            } else if (screen instanceof ShulkerBoxScreen) {
                screenHeight += 1;
            }
            ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), client.player.getGameProfile()));
            TexturedButtonWithItemStackWidget tab;
            if (client.currentScreen instanceof InventoryScreen) {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURE_LEFT_SELECTED, playerHead, button -> {
                    if (client.interactionManager.hasRidingInventory()) {
                        client.player.openRidingInventory();
                    } else {
                        client.currentScreen.close();
                        client.getTutorialManager().onInventoryOpened();
                        client.setScreen(new InventoryScreen(client.player));
                    }
                });
            } else {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURE_LEFT, playerHead, button -> {
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
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURE_RIGHT_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURE_RIGHT, displayStack, button -> openContainer(container));
                    }
                } else {
                    if (!(client.currentScreen instanceof InventoryScreen) && current == container) {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 28, 26, 32, TEXTURE_MID_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 28, 26, 28, TEXTURE_MID, displayStack, button -> openContainer(container));
                    }
                }
                tab.setTooltip(Tooltip.of(displayName));
                addDrawableChild(tab);
            }

            if (containers.size() > maxTabs) {
                TexturedButtonWidget backButton = new TexturedButtonWidget(width / 2 + screenWidth / 2 - 2, height / 2 - screenHeight / 2 - 24, 24, 24, 0, 0, 24, TEXTURE_FORWARD, 24, 48, button -> {
                    Vec3i temp = containers.get(current);
                    containers.addLast(containers.removeFirst());
                    current = containers.indexOf(temp);
                    this.clearChildren();
                    this.init(client, width, height, ci);
                    this.init();
                });
                TexturedButtonWidget forwardButton = new TexturedButtonWidget(width / 2 - screenWidth / 2 - 17, height / 2 - screenHeight / 2 - 24, 24, 24, 0, 0, 24, TEXTURE_BACK, 24, 48, button -> {
                    Vec3i temp = containers.get(current);
                    containers.addFirst(containers.removeLast());
                    current = containers.indexOf(temp);
                    this.clearChildren();
                    this.init(client, width, height, ci);
                    this.init();
                });
                addDrawableChild(backButton);
                addDrawableChild(forwardButton);
            }
        }
    }

    @Inject(method = "resize", at = @At("TAIL"))
    public void resize(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab && !ImprovedInventoryConfig.containerTabKeybindOnly) {
            this.init(client, width, height, ci);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (!InventorySorter.sortKey.isUnbound()) {
            InventorySorter.inventorySortHandler(client);
        }
    }

}
