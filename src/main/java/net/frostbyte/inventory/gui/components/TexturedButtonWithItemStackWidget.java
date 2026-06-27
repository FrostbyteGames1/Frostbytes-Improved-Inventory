package net.frostbyte.inventory.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class TexturedButtonWithItemStackWidget extends ImageButton {
    protected final ItemStack itemStack;
    public TexturedButtonWithItemStackWidget(int x, int y, int width, int height, WidgetSprites textures, ItemStack itemStack, Button.OnPress pressAction) {
        super(x, y, width, height, textures, pressAction);
        this.itemStack = itemStack;
    }

    @Override
    public void extractContents(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        graphics.item(itemStack, this.getX() + 5, this.getY() + 8);
    }
}
