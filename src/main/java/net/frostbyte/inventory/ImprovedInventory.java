package net.frostbyte.inventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.util.Identifier;

public class ImprovedInventory implements ModInitializer {
	public static final String MOD_ID = "inventory";
	public static Identifier KEYBIND_CATEGORY;

	@Override
	public void onInitialize() {
		ImprovedInventoryConfig.read();

		KEYBIND_CATEGORY = Identifier.of(ImprovedInventory.MOD_ID, "key.categories.improved_inventory");

		SlotCycler slotCycler = new SlotCycler();
		slotCycler.setKeyBindings();
		HudRenderCallback.EVENT.register(slotCycler);

		HudRenderCallback.EVENT.register(new StackRefiller());

		HudRenderCallback.EVENT.register(new DurabilityDisplayer());

		new InventorySorter().setKeyBindings();

		new Zoom().setKeyBindings();

		new Gamma().setKeyBindings();

		HudRenderCallback.EVENT.register(new Paperdoll());

		new NearbyContainerViewer().setKeybindings();

		HudRenderCallback.EVENT.register(new TextDisplayer());

		HudRenderCallback.EVENT.register(new WAILA());
	}

}