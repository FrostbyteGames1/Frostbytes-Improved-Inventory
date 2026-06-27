package net.frostbyte.inventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.gui.components.debug.*;
import net.frostbyte.inventory.tags.ModTags;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovedInventory implements ModInitializer {
	public static final String MOD_ID = "inventory";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static KeyMapping.Category KEYBIND_CATEGORY;

	@Override
	public void onInitialize() {
		ModTags.registerModTags();

		ImprovedInventoryConfig.read();

		KEYBIND_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ImprovedInventory.MOD_ID, "improved_inventory"));

		SlotCycler slotCycler = new SlotCycler();
		slotCycler.setKeyMappings();
		HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath(MOD_ID, "slot_cycle"), slotCycler);

		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "stack_refill"), new StackRefiller());

		HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath(MOD_ID, "durability_display"), new DurabilityDisplayer());

		new InventorySorter().setKeyMappings();

		new Zoom().setKeyMappings();

		new Gamma().setKeyMappings();

		HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath(MOD_ID, "paperdoll"), new Paperdoll());

		new NearbyContainerViewer().setKeyMappings();

		DebugScreenEntries.register(DebugEntryCoordinates.GROUP, new DebugEntryCoordinates());
		DebugScreenEntries.register(DebugEntryDimension.GROUP, new DebugEntryDimension());
		DebugScreenEntries.register(DebugEntryFacing.GROUP, new DebugEntryFacing());
		DebugScreenEntries.register(DebugEntrySlimeChunk.GROUP, new DebugEntrySlimeChunk());
		DebugScreenEntries.register(DebugEntrySpeed.GROUP, new DebugEntrySpeed());
		DebugScreenEntries.register(DebugEntrySprinting.GROUP, new DebugEntrySprinting());
		DebugScreenEntries.register(DebugEntryGameTime.GROUP, new DebugEntryGameTime());
		DebugScreenEntries.register(DebugEntryRealTime.GROUP, new DebugEntryRealTime());

		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "waila"), new WAILA());
	}

}