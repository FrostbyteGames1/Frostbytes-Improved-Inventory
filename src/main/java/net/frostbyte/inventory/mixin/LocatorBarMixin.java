package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.gui.hud.bar.LocatorBar;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickManager;
import net.minecraft.world.waypoint.EntityTickProgress;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBar.class)
public abstract class LocatorBarMixin implements Bar {
    @Unique @Final
    private static final Identifier EXP_BACKGROUND = Identifier.ofVanilla("hud/experience_bar_background");
    @Unique @Final
    private static final Identifier EXP_PROGRESS = Identifier.ofVanilla("hud/experience_bar_progress");
    @Shadow
    private static final Identifier ARROW_UP = Identifier.ofVanilla("hud/locator_bar_arrow_up");
    @Shadow
    private static final Identifier ARROW_DOWN = Identifier.ofVanilla("hud/locator_bar_arrow_down");
    @Shadow @Final
    private MinecraftClient client;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    public void renderBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ImprovedInventoryConfig.combineExpAndLocatorBars && this.client.player.getGameMode().isSurvivalLike()) {
            ClientPlayerEntity clientPlayerEntity = this.client.player;
            int i = this.getCenterX(this.client.getWindow());
            int j = this.getCenterY(this.client.getWindow());
            int k = clientPlayerEntity.getNextLevelExperience();
            if (k > 0) {
                int l = (int)(clientPlayerEntity.experienceProgress * 183.0F);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EXP_BACKGROUND, i, j, 182, 5);
                if (l > 0) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EXP_PROGRESS, 182, 5, 0, 0, i, j, l, 5);
                }
            }
            if (this.client.player.experienceLevel > 0) {
                Bar.drawExperienceLevel(context, this.client.textRenderer, this.client.player.experienceLevel);
            }
            ci.cancel();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "renderAddons", at = @At("HEAD"), cancellable = true)
    public void renderAddons(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        int i = this.getCenterY(this.client.getWindow());
        Entity entity = this.client.getCameraEntity();
        if (ImprovedInventoryConfig.playerHeadWaypoints) {
            if (entity != null) {
                World world = entity.getEntityWorld();
                TickManager tickManager = world.getTickManager();
                EntityTickProgress entityTickProgress = (entityx) -> tickCounter.getTickProgress(!tickManager.shouldSkipTick(entityx));
                this.client.player.networkHandler.getWaypointHandler().forEachWaypoint(entity, (waypoint) -> {
                    if (!(Boolean) waypoint.getSource().left().map((uuid) -> uuid.equals(entity.getUuid())).orElse(false)) {
                        double d = waypoint.getRelativeYaw(world, this.client.gameRenderer.getCamera(), entityTickProgress);
                        if (!(d <= -60.0) && !(d > 60.0)) {
                            int j = MathHelper.ceil((float) (context.getScaledWindowWidth() - 9) / 2.0F);
                            int l = MathHelper.floor(d * 173.0 / 2.0 / 60.0);
                            if (waypoint.getSource().left().isPresent() && this.client.getNetworkHandler().getPlayerListEntry(waypoint.getSource().left().get()) != null) {
                                PlayerSkinDrawer.draw(context, this.client.getNetworkHandler().getPlayerListEntry(waypoint.getSource().left().get()).getSkinTextures().body().texturePath(), j + l, i - 2, 8, true, false, -1);
                            } else {
                                Waypoint.Config config = waypoint.getConfig();
                                WaypointStyleAsset waypointStyleAsset = this.client.getWaypointStyleAssetManager().get(config.style);
                                float f = MathHelper.sqrt((float)waypoint.squaredDistanceTo(entity));
                                Identifier identifier = waypointStyleAsset.getSpriteForDistance(f);
                                int k = config.color.orElseGet(() -> waypoint.getSource().map((uuid) -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, uuid.hashCode()), 0.9F), (name) -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, name.hashCode()), 0.9F)));
                                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, j + l, i - 2, 9, 9, k);
                            }
                            TrackedWaypoint.Pitch pitch = waypoint.getPitch(world, this.client.gameRenderer, entityTickProgress);
                            if (pitch != TrackedWaypoint.Pitch.NONE) {
                                byte m;
                                Identifier identifier2;
                                if (pitch == TrackedWaypoint.Pitch.DOWN) {
                                    m = 6;
                                    identifier2 = ARROW_DOWN;
                                } else {
                                    m = -6;
                                    identifier2 = ARROW_UP;
                                }
                                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, j + l + 1, i + m, 7, 5);
                            }

                        }
                    }
                });
            }
            ci.cancel();
        }
    }

}
