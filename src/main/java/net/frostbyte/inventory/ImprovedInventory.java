package net.frostbyte.inventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.tags.ModTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovedInventory implements ModInitializer {
	public static final String MOD_ID = "inventory";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@SuppressWarnings("deprecation")
	@Override
	public void onInitialize() {
		ModTags.registerModTags();

		ImprovedInventoryConfig.read();

		SlotCycler slotCycler = new SlotCycler();
		slotCycler.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(slotCycler);
		HudRenderCallback.EVENT.register(slotCycler);

		ToolSelector toolSelector = new ToolSelector();
		ClientTickEvents.END_CLIENT_TICK.register(toolSelector);

		StackRefiller stackRefiller = new StackRefiller();
		ClientTickEvents.END_CLIENT_TICK.register(stackRefiller);
		HudRenderCallback.EVENT.register(stackRefiller);

		DurabilityDisplayer durabilityDisplayer = new DurabilityDisplayer();
		HudRenderCallback.EVENT.register(durabilityDisplayer);

		InventorySorter inventorySorter = new InventorySorter();
		inventorySorter.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(inventorySorter);

		Zoom zoom = new Zoom();
		zoom.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(zoom);

		Gamma gamma = new Gamma();
		gamma.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(gamma);

		Paperdoll paperdoll = new Paperdoll();
		HudRenderCallback.EVENT.register(paperdoll);

		NearbyContainerViewer nearbyContainerViewer = new NearbyContainerViewer();
		ClientTickEvents.END_CLIENT_TICK.register(nearbyContainerViewer);
		nearbyContainerViewer.setKeybindings();

		TextDisplayer textDisplayer = new TextDisplayer();
		HudRenderCallback.EVENT.register(textDisplayer);

		WAILA waila = new WAILA();
		HudRenderCallback.EVENT.register(waila);
	}

}