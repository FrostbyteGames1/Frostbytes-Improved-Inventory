package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.PartialTickSupplier;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarRendererMixin implements ContextualBarRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private static Identifier LOCATOR_BAR_ARROW_DOWN;
    @Shadow
    @Final
    private static Identifier LOCATOR_BAR_ARROW_UP;
    @Unique @Final
    private static final Identifier EXPERIENCE_BAR_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/experience_bar_background");
    @Unique @Final
    private static final Identifier EXPERIENCE_BAR_PROGRESS_SPRITE = Identifier.withDefaultNamespace("hud/experience_bar_progress");

    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    public void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (ImprovedInventoryConfig.combineExpAndLocatorBars && this.minecraft.player != null) {
            LocalPlayer player = this.minecraft.player;
            int left = this.left(this.minecraft.getWindow());
            int top = this.top(this.minecraft.getWindow());
            int xpNeededForNextLevel = player.getXpNeededForNextLevel();
            if (xpNeededForNextLevel > 0) {
                int progress = (int)(player.experienceProgress * 183.0F);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_SPRITE, left, top, 182, 5);
                if (progress > 0) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, left, top, progress, 5);
                }
            }
            ci.cancel();
        }
    }

    @Unique
    public int getSizeForDistance(WaypointStyle waypointStyle, float distance) {
        if (distance < (float) waypointStyle.nearDistance()) {
            return 8;
        } else if (distance >= (float) waypointStyle.farDistance()) {
            return 3;
        } else {
            int i = (int) Mth.lerp((distance - (float) waypointStyle.nearDistance()) / (float)(waypointStyle.farDistance() - waypointStyle.nearDistance()), 1, 3);
            return switch (i) {
                case 1 -> 7;
                case 2 -> 5;
                case 3 -> 3;
                default -> 8;
            };
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (ImprovedInventoryConfig.playerHeadWaypoints) {
            int top = this.top(minecraft.getWindow());
            Entity cameraEntity = minecraft.getCameraEntity();
            if (cameraEntity != null) {
                Level level = cameraEntity.level();
                TickRateManager tickRateManager = level.tickRateManager();
                PartialTickSupplier partialTickSupplier = (entity) -> deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(entity));
                this.minecraft.player.connection.getWaypointManager().forEachWaypoint(cameraEntity, (waypoint) -> {
                    if (!(Boolean)waypoint.id().left().map((uuid) -> uuid.equals(cameraEntity.getUUID())).orElse(false)) {
                        double angle = waypoint.yawAngleToCamera(level, this.minecraft.gameRenderer.getMainCamera(), partialTickSupplier);
                        if (!(angle <= (double)-60.0F) && !(angle > (double)60.0F)) {
                            int screenMiddle = Mth.ceil((float)(graphics.guiWidth() - 9) / 2.0F);
                            WaypointStyle waypointStyle = minecraft.getWaypointStyles().get(waypoint.icon().style);
                            int dotPosition = Mth.floor(angle * (double)173.0F / (double)2.0F / (double)60.0F);
                            if (waypoint.id().left().isPresent() && minecraft.getConnection().getPlayerInfo(waypoint.id().left().get()) != null) {
                                int size = getSizeForDistance(waypointStyle, Mth.sqrt((float)waypoint.distanceSquared(cameraEntity)));
                                PlayerFaceExtractor.extractRenderState(graphics, minecraft.getConnection().getPlayerInfo(waypoint.id().left().get()).getSkin(), screenMiddle + dotPosition, top - 2 + (9 - size) / 2, size, Color.WHITE.getRGB());
                            } else {
                                Waypoint.Icon icon = waypoint.icon();
                                WaypointStyle style = this.minecraft.getWaypointStyles().get(icon.style);
                                float distance = Mth.sqrt((float)waypoint.distanceSquared(cameraEntity));
                                Identifier sprite = style.sprite(distance);
                                int color = icon.color.orElseGet(() -> waypoint.id().map((uuid) -> ARGB.setBrightness(ARGB.color(255, uuid.hashCode()), 0.9F), (name) -> ARGB.setBrightness(ARGB.color(255, name.hashCode()), 0.9F)));
                                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, screenMiddle + dotPosition, top - 2, 9, 9, color);
                            }
                            TrackedWaypoint.PitchDirection pitchDirection = waypoint.pitchDirectionToCamera(level, this.minecraft.gameRenderer, partialTickSupplier);
                            if (pitchDirection != TrackedWaypoint.PitchDirection.NONE) {
                                int arrowTop;
                                Identifier arrowSprite;
                                if (pitchDirection == TrackedWaypoint.PitchDirection.DOWN) {
                                    arrowTop = 6;
                                    arrowSprite = LOCATOR_BAR_ARROW_DOWN;
                                } else {
                                    arrowTop = -6;
                                    arrowSprite = LOCATOR_BAR_ARROW_UP;
                                }
                                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, arrowSprite, screenMiddle + dotPosition + 1, top + arrowTop, 7, 5);
                            }
                        }
                    }
                });
            }
            ci.cancel();
        }
    }

}
