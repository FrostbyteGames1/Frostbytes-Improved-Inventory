package net.frostbyte.inventory.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class HoverableIconWidget implements Drawable, Element, Widget {
    protected int width;
    protected int height;
    private int x;
    private int y;
    protected boolean hovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0F;
    private int navigationOrder;
    private boolean focused;
    private final TooltipState tooltip = new TooltipState();

    public HoverableIconWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static HoverableIconWidget create(int width, int height, Identifier texture, int textureWidth, int textureHeight) {
        return new HoverableIconWidget.Texture(0, 0, width, height, texture, textureWidth, textureHeight);
    }

    public static HoverableIconWidget create(int width, int height, Identifier texture) {
        return new HoverableIconWidget.Simple(0, 0, width, height, texture);
    }

    public int getHeight() {
        return this.height;
    }

    public final void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.visible) {
            this.hovered = context.scissorContains(mouseX, mouseY) && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            this.renderWidget(context, mouseX, mouseY, deltaTicks);
            this.tooltip.render(context, mouseX, mouseY, this.isHovered(), this.isFocused(), this.getNavigationFocus());
        }
    }

    public void setTooltip(@Nullable Tooltip tooltip) {
        this.tooltip.setTooltip(tooltip);
    }

    @Nullable
    public Tooltip getTooltip() {
        return this.tooltip.getTooltip();
    }

    public void setTooltipDelay(Duration tooltipDelay) {
        this.tooltip.setDelay(tooltipDelay);
    }

    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

    }

    protected static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int startX, int startY, int endX, int endY, int color) {
        drawScrollableText(context, textRenderer, text, (startX + endX) / 2, startX, startY, endX, endY, color);
    }

    protected static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color) {
        int i = textRenderer.getWidth(text);
        int var10000 = startY + endY;
        Objects.requireNonNull(textRenderer);
        int j = (var10000 - 9) / 2 + 1;
        int k = endX - startX;
        int l;
        if (i > k) {
            l = i - k;
            double d = (double)Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.max((double)l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
            double g = MathHelper.lerp(f, 0.0, l);
            context.enableScissor(startX, startY, endX, endY);
            context.drawTextWithShadow(textRenderer, text, startX - (int)g, j, color);
            context.disableScissor();
        } else {
            l = MathHelper.clamp(centerX, startX + i / 2, endX - i / 2);
            context.drawCenteredTextWithShadow(textRenderer, text, l, j, color);
        }

    }

    public void onClick(double mouseX, double mouseY) {
    }

    public void onRelease(double mouseX, double mouseY) {
    }

    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean bl = this.isMouseOver(mouseX, mouseY);
                if (bl) {
                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isValidClickButton(button)) {
            this.onDrag(mouseX, mouseY, deltaX, deltaY);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (this.active && this.visible) {
            return !this.isFocused() ? GuiNavigationPath.of(this) : null;
        } else {
            return null;
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)this.getRight() && mouseY < (double)this.getBottom();
    }

    public void playDownSound(SoundManager soundManager) {
        playClickSound(soundManager);
    }

    public static void playClickSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        } else {
            return this.hovered ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
        }
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }

    public void forEachChild(Consumer<ClickableWidget> consumer) {
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ScreenRect getNavigationFocus() {
        return Element.super.getNavigationFocus();
    }

    public void setDimensionsAndPosition(int width, int height, int x, int y) {
        this.setDimensions(width, height);
        this.setPosition(x, y);
    }

    public int getNavigationOrder() {
        return this.navigationOrder;
    }

    public void setNavigationOrder(int navigationOrder) {
        this.navigationOrder = navigationOrder;
    }

    @Environment(EnvType.CLIENT)
    static class Texture extends HoverableIconWidget {
        private final Identifier texture;
        private final int textureWidth;
        private final int textureHeight;

        public Texture(int x, int y, int width, int height, Identifier texture, int textureWidth, int textureHeight) {
            super(x, y, width, height);
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
        }
    }

    @Environment(EnvType.CLIENT)
    static class Simple extends HoverableIconWidget {
        private final Identifier texture;

        public Simple(int x, int y, int width, int height, Identifier texture) {
            super(x, y, width, height);
            this.texture = texture;
        }

        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
    }
}
