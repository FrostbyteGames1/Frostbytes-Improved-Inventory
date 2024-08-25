package net.frostbyte.inventory.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class TexturedButtonWithItemStackWidget extends TexturedButtonWidget {
    protected final ItemStack itemStack;
    public TexturedButtonWithItemStackWidget(int x, int y, int width, int height, int u, int v, Identifier texture, ItemStack itemStack, PressAction pressAction) {
        super(x, y, width, height, u, v, texture, pressAction);
        this.itemStack = itemStack;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawItem(itemStack, this.getX() + 5, this.getY() + 8);
    }
}
