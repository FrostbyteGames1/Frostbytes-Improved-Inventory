package net.frostbyte.inventory.mixin;

import net.frostbyte.inventory.duck.DebugScreenEntryListDuck;
import net.frostbyte.inventory.gui.components.debug.DebugScreenSideEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.debug.DebugOptionsScreen$OptionEntry")
public abstract class DebugOptionsScreen_OptionEntryMixin {
    @Unique
    private CycleButton<DebugScreenEntryStatus> statusButton;
    @Unique
    private CycleButton<DebugScreenSideEnum> sideButton;

    @Unique
    private static Component getStatusText(DebugScreenEntryStatus status) {
        return Component.translatable("debug.entry.status." + status.getSerializedName());
    }

    @Unique
    private static Component getSideText(DebugScreenSideEnum side) {
        return Component.translatable("debug.entry.side." + side.getSerializedName());
    }

    @Unique
    private void setNextStatus(DebugOptionsScreen.OptionEntry entry) {
        DebugScreenEntryStatus status = Minecraft.getInstance().debugEntries.getStatus(entry.location);
        switch (status.ordinal()) {
            case 0 -> entry.setValue(entry.location, DebugScreenEntryStatus.IN_OVERLAY);
            case 1 -> entry.setValue(entry.location, DebugScreenEntryStatus.NEVER);
            case 2 -> entry.setValue(entry.location, DebugScreenEntryStatus.ALWAYS_ON);
        }
    }

    @Unique
    private void setNextSide(DebugOptionsScreen.OptionEntry entry) {
        DebugScreenEntryListDuck entries = (DebugScreenEntryListDuck) Minecraft.getInstance().debugEntries;
        DebugScreenSideEnum side = entries.getSide(entry.location);
        switch (side.ordinal()) {
            case 0 -> this.setSide(entry.location, DebugScreenSideEnum.LEFT);
            case 1 -> this.setSide(entry.location, DebugScreenSideEnum.RIGHT);
            case 2 -> this.setSide(entry.location, DebugScreenSideEnum.AUTO);
        }
    }

    @Unique
    public final void setSide(Identifier location, DebugScreenSideEnum side) {
        DebugOptionsScreen.OptionEntry entry = (DebugOptionsScreen.OptionEntry) (Object) this;
        DebugScreenEntryList entries = Minecraft.getInstance().debugEntries;
        ((DebugScreenEntryListDuck) entries).setSide(location, side);
        entry.refreshEntry();
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/debug/DebugOptionsScreen$OptionEntry;refreshEntry()V"))
    void init(CallbackInfo ci) {
        DebugOptionsScreen.OptionEntry entry = (DebugOptionsScreen.OptionEntry) (Object) this;
        this.statusButton = CycleButton.builder(DebugOptionsScreen_OptionEntryMixin::getStatusText, DebugScreenEntryStatus.NEVER).withValues(DebugScreenEntryStatus.values()).create(10, 5, 90, 16, Component.translatable("debug.entry.status"), (button, value) -> this.setNextStatus(entry));
        this.sideButton = CycleButton.builder(DebugOptionsScreen_OptionEntryMixin::getSideText, DebugScreenSideEnum.AUTO).withValues(DebugScreenSideEnum.values()).create(10, 5, 90, 16, Component.translatable("debug.entry.side"), (button, value) -> this.setNextSide(entry));
        entry.children.clear();
        entry.children.add(this.statusButton);
        entry.children.add(this.sideButton);
    }

    @Inject(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CycleButton;setX(I)V"), cancellable = true)
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a, CallbackInfo ci) {
        DebugOptionsScreen.OptionEntry entry = (DebugOptionsScreen.OptionEntry) (Object) this;
        int buttonsStartX = entry.getContentX() + entry.getContentWidth() - this.statusButton.getWidth() - 2 - this.sideButton.getWidth();
        this.statusButton.setX(buttonsStartX);
        this.sideButton.setX(this.statusButton.getX() + this.statusButton.getWidth() + 2);
        this.sideButton.setY(entry.getContentY());
        this.statusButton.setY(entry.getContentY());
        this.statusButton.extractRenderState(graphics, mouseX, mouseY, a);
        this.sideButton.extractRenderState(graphics, mouseX, mouseY, a);
        ci.cancel();
    }

    @Inject(method = "refreshEntry", at = @At("TAIL"))
    void refreshEntry(CallbackInfo ci) {
        DebugOptionsScreen.OptionEntry entry = (DebugOptionsScreen.OptionEntry) (Object) this;
        DebugScreenEntryList entries = Minecraft.getInstance().debugEntries;
        DebugScreenSideEnum side = ((DebugScreenEntryListDuck) entries).getSide(entry.location);
        this.sideButton.setValue(side);
        DebugScreenEntryStatus statusValue = entries.getStatus(entry.location);
        this.statusButton.setValue(statusValue);
        if (DebugScreenEntries.getEntry(entry.location) instanceof DebugEntryNoop) {
            this.sideButton.active = false;
        }
    }
}
