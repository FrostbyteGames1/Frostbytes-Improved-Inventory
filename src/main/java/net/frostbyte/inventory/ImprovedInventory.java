package net.frostbyte.inventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ImprovedInventory implements ModInitializer {

	public static final String MOD_ID = "inventory";

	@Override
	public void onInitialize() {
		SlotCycler slotCycler = new SlotCycler();
		slotCycler.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(slotCycler);
		HudRenderCallback.EVENT.register(slotCycler);

		ToolSelector toolSelector = new ToolSelector();
		toolSelector.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(toolSelector);

		StackRefiller stackRefiller = new StackRefiller();
		stackRefiller.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(stackRefiller);

		DurabilityDisplayer durabilityDisplayer = new DurabilityDisplayer();
		durabilityDisplayer.setKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(durabilityDisplayer);
		HudRenderCallback.EVENT.register(durabilityDisplayer);
	}

}