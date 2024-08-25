package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.ImprovedInventory;
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
    private static final Identifier TEXTURES_LEFT = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Unique
    private static final Identifier TEXTURES_LEFT_SELECTED = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Unique
    private static final Identifier TEXTURES_MID = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Unique
    private static final Identifier TEXTURES_MID_SELECTED = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Unique
    private static final Identifier TEXTURES_RIGHT = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Unique
    private static final Identifier TEXTURES_RIGHT_SELECTED = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Unique
    private static final Identifier TEXTURES_FORWARD = new Identifier("textures/gui/server_selection.png");
    @Unique
    private static final Identifier TEXTURES_BACK = new Identifier("textures/gui/server_selection.png");
    @Unique
    int screenWidth = 176;
    @Unique
    int screenHeight = 166;

    @Inject(method = "init*", at = @At("HEAD"))
    public void init(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (ImprovedInventoryConfig.containerTab && !containers.isEmpty() && client.world != null && client.player != null && client.interactionManager != null && client.currentScreen instanceof HandledScreen<?> screen && !(client.currentScreen instanceof CreativeInventoryScreen) && !(client.currentScreen instanceof MerchantScreen)) {
            
            if (screen instanceof GenericContainerScreen containerScreen) {
                screenHeight = 114 + containerScreen.getScreenHandler().getRows() * 18;
            } else if (screen instanceof HopperScreen) {
                screenHeight = 135;
            } else if (screen instanceof ShulkerBoxScreen) {
                screenHeight = 169;
            }

            ItemStack playerHead = Items.PLAYER_HEAD.getDefaultStack();
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), client.player.getGameProfile()));
            playerHead.setNbt(nbtCompound);

            TexturedButtonWithItemStackWidget tab;
            if (client.currentScreen instanceof AbstractInventoryScreen<?>) {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 28, 26, 32, 0, 0, TEXTURES_LEFT_SELECTED, playerHead, button -> {
                    if (client.interactionManager.hasRidingInventory()) {
                        client.player.openRidingInventory();
                    } else {
                        client.setScreen(new InventoryScreen(client.player));
                    }
                });
            } else {
                tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2, height / 2 - screenHeight / 2 - 32, 26, 32, 0, 0, TEXTURES_LEFT, playerHead, button -> {
                    if (client.interactionManager.hasRidingInventory()) {
                        client.player.openRidingInventory();
                    } else {
                        client.setScreen(new InventoryScreen(client.player));
                    }
                });
            }
            tab.setTooltip(Tooltip.of(client.player.getDisplayName()));
            addDrawableChild(tab);

            int numTabs = Math.min(6, containers.size());
            for (int i = 0; i < numTabs; i++) {
                int container = i;
                Text displayName = getDisplayName(containers.get(container));
                ItemStack displayStack = getDisplayStack(containers.get(container));
                if (i == 5) {
                    if (!(client.currentScreen instanceof AbstractInventoryScreen<?>) && current == container) {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 28, 26, 32, 156, 0, TEXTURES_RIGHT_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 + screenWidth / 2 - 26, height / 2 - screenHeight / 2 - 32, 26, 32, 156, 0, TEXTURES_RIGHT, displayStack, button -> openContainer(container));
                    }
                } else {
                    if (!(client.currentScreen instanceof AbstractInventoryScreen<?>) && current == container) {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 28, 26, 32, 26, 0, TEXTURES_MID_SELECTED, displayStack, button -> openContainer(container));
                    } else {
                        tab = new TexturedButtonWithItemStackWidget(width / 2 - screenWidth / 2 + (i + 1) * 25, height / 2 - screenHeight / 2 - 32, 26, 32, 26, 0, TEXTURES_MID, displayStack, button -> openContainer(container));
                    }
                }
                tab.setTooltip(Tooltip.of(displayName));
                addDrawableChild(tab);
            }

            if (containers.size() > 6) {
                TexturedButtonWidget backButton = new TexturedButtonWidget(width / 2 + screenWidth / 2 + 3, height / 2 - screenHeight / 2 - 24, 16, 32, 16, 5, TEXTURES_FORWARD, button -> {
                    Vec3i temp = containers.get(current);
                    containers.add(containers.remove(0));
                    current = containers.indexOf(temp);
                    this.clearChildren();
                    this.init(client, width, height, ci);
                    this.init();
                });
                TexturedButtonWidget forwardButton = new TexturedButtonWidget(width / 2 - screenWidth / 2 - 17, height / 2 - screenHeight / 2 - 24, 16, 32, 34, 5, TEXTURES_BACK, button -> {
                    Vec3i temp = containers.get(current);
                    containers.add(0, containers.remove(containers.size() - 1));
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
        this.init(client, width, height, ci);
    }

}
