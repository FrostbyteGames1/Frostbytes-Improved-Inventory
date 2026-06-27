package net.frostbyte.inventory.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.frostbyte.inventory.duck.DebugScreenOverlayDuck;
import net.frostbyte.inventory.duck.DebugScreenEntryListDuck;
import net.frostbyte.inventory.gui.components.debug.DebugScreenSideEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntry;display(Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/chunk/LevelChunk;)V"))
    void setNext(GuiGraphicsExtractor graphics, CallbackInfo ci, @Local(name = "id") Identifier id, @Local(name = "displayer") DebugScreenDisplayer displayer) {
        ((DebugScreenOverlayDuck) displayer).setNext(id);
    }

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 3))
    boolean addGroups(List<String> instance, @Local(name = "groups") Map<Identifier, Collection<String>> groups, @Local(name = "leftLines") List<String> leftLines, @Local(name = "rightLines") List<String> rightLines) {
        for(Map.Entry<Identifier, Collection<String>> group : groups.entrySet()) {
            Collection<String> lines = group.getValue();
            if (!lines.isEmpty()) {
                DebugScreenEntryList entries = Minecraft.getInstance().debugEntries;
                DebugScreenSideEnum side = ((DebugScreenEntryListDuck) entries).getSide(group.getKey());
                if (side == DebugScreenSideEnum.AUTO) {
                    if (leftLines.size() > rightLines.size()) {
                        side = DebugScreenSideEnum.RIGHT;
                    } else {
                        side = DebugScreenSideEnum.LEFT;
                    }
                }
                if (side == DebugScreenSideEnum.LEFT) {
                    leftLines.addAll(lines);
                    leftLines.add("");
                } else {
                    rightLines.addAll(lines);
                    rightLines.add("");
                }
            }
        }
        return true;
    }
}
