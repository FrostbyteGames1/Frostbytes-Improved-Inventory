package net.frostbyte.inventory.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class TexturedButtonWithItemStackWidget extends TexturedButtonWidget {
    protected final ItemStack itemStack;
    public TexturedButtonWithItemStackWidget(int x, int y, int width, int height, ButtonTextures textures, ItemStack itemStack, PressAction pressAction) {
        super(x, y, width, height, textures, pressAction);
        this.itemStack = itemStack;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier identifier = this.textures.get(this.isNarratable(), this.isSelected());
        context.drawGuiTexture(RenderLayer::getGuiTextured, identifier, this.getX(), this.getY(), this.width, this.height);
        context.drawItem(itemStack, this.getX() + 5, this.getY() + 8);
    }
}
