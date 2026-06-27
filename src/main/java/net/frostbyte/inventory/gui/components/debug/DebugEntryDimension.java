package net.frostbyte.inventory.gui.components.debug;

import net.frostbyte.inventory.ImprovedInventory;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DebugEntryDimension implements DebugScreenEntry {
    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "dimension");

    public DebugEntryDimension() {
    }

    @Override
    public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();
        if (entity != null && minecraft.level != null) {
            BlockPos feetPos = entity.blockPosition();
            if (minecraft.level.isInsideBuildHeight(feetPos.getY())) {
                if (SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES && serverOrClientLevel instanceof ServerLevel) {
                    displayer.addToGroup(GROUP, List.of(
                        "Dimension: " + printDimension(minecraft.level.dimensionTypeRegistration()),
                        "Server Dimension: " + printDimension(serverOrClientLevel.dimensionTypeRegistration()))
                    );
                } else {
                    displayer.addLine("Dimension: " + printDimension(minecraft.level.dimensionTypeRegistration()));
                }
            }

        }
    }

    private static String printDimension(final Holder<DimensionType> dimension) {
        return dimension.unwrap().map((key) -> key.identifier().toString(), (l) -> "[unregistered " + l + "]");
    }
}
