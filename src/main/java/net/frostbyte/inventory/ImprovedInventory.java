package net.frostbyte.inventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;

public class ImprovedInventory implements ModInitializer {

	public static final String MOD_ID = "inventory";

	@Override
	public void onInitialize() {
		ImprovedInventoryConfig config = new ImprovedInventoryConfig();
		config.read();

		SlotCycler slotCycler = new SlotCycler();
		slotCycler.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(slotCycler);
		HudRenderCallback.EVENT.register(slotCycler);

		ToolSelector toolSelector = new ToolSelector();
		ClientTickEvents.END_CLIENT_TICK.register(toolSelector);

		StackRefiller stackRefiller = new StackRefiller();
		ClientTickEvents.END_CLIENT_TICK.register(stackRefiller);

		DurabilityDisplayer durabilityDisplayer = new DurabilityDisplayer();
		ClientTickEvents.END_CLIENT_TICK.register(durabilityDisplayer);
		HudRenderCallback.EVENT.register(durabilityDisplayer);

		InventorySorter inventorySorter = new InventorySorter();
		inventorySorter.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(inventorySorter);
	}

}