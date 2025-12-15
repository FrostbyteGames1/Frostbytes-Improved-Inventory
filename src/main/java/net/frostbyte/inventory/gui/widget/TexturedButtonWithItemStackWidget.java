package net.frostbyte.inventory.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class TexturedButtonWithItemStackWidget extends TexturedButtonWidget {
    protected final ItemStack itemStack;
    public TexturedButtonWithItemStackWidget(int x, int y, int width, int height, Identifier texture, ItemStack itemStack, PressAction pressAction) {
        super(x, y, width, height, 0, 0, 0, texture, width, height, pressAction);
        this.itemStack = itemStack;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(this.texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        context.drawItem(itemStack, this.getX() + 5, this.getY() + 8);
    }
}
