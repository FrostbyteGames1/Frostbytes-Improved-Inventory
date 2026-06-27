package net.frostbyte.inventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.impl.controller.ColorControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.IntegerFieldControllerBuilderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ImprovedInventoryConfig {

    public static final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("frostbyte");
    public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/improved-inventory.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean duraDisplay = true;
    public static boolean duraDisplayHorizontalAnchor = false;
    public static boolean duraDisplayVerticalAnchor = false;
    public static int duraDisplayOffsetX = 0;
    public static int duraDisplayOffsetY = 0;
    public static boolean slotCycle = true;
    public static boolean slotCycleAltScroll = true;
    public static boolean slotCycleAltNum = true;
    public static int slotCycleOffsetX = 0;
    public static int slotCycleOffsetY = 0;
    public static boolean stackRefill = true;
    public static boolean stackRefillPreview = true;
    public static Color stackRefillPreviewColor = Color.WHITE;
    public static ArrayList<Item> stackRefillBlacklist = new ArrayList<>();
    public static boolean toolSelect = true;
    public static boolean weaponSelectPreference = true;
    public static ArrayList<Item> toolSelectBlacklist = new ArrayList<>();
    public static boolean paperdoll = true;
    public static boolean paperdollHorizontalAnchor = true;
    public static boolean paperdollVerticalAnchor = true;
    public static int paperdollOffsetX = 0;
    public static int paperdollOffsetY = 0;
    public static boolean waila = true;
    public static boolean wailaHorizontalAnchor = true;
    public static boolean wailaVerticalAnchor = false;
    public static int wailaOffsetX = 0;
    public static int wailaOffsetY = 0;
    public static int zoomFOV = 30;
    public static boolean zoomScrollRequiresControl = true;
    public static boolean zoomSound = true;
    public static int gamma = 500;
    public static int maxInteractions = 0;
    public static boolean containerSearch = true;
    public static boolean containerTab = true;
    public static boolean containerTabFreeCursor = true;
    public static boolean containerTabKeybindOnly = false;
    public static ArrayList<Item> containerTabBlacklist = new ArrayList<>();
    public static boolean shulkerBoxTooltip = true;
    public static boolean shulkerBoxTooltipColors = true;
    public static boolean mapTooltip = true;
    public static boolean heldItemsVisibleInBoat = true;
    public static boolean armorBarColors = true;
    public static boolean expandedBundleTooltip = true;
    public static boolean bundleProgressBarFraction = true;
    public static boolean compassTooltip = true;
    public static boolean clockTooltip = true;
    public static boolean foodTooltip = true;
    public static boolean statusEffectTimer = true;
    public static boolean combineExpAndLocatorBars = true;
    public static boolean playerHeadWaypoints = true;

    public static Screen createScreen(Screen parent) {
        read();
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Frostbyte's Improved Inventory Config Menu"))
            
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Inventory"))
                .tooltip(Component.literal("Options that interact with the player's inventory"))

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Hotbar Stack Refilling"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Hotbar Stack Refilling"))
                        .description(OptionDescription.of(Component.literal("Refills the hotbar with a new stack of the same item from the inventory")))
                        .binding(true, () -> stackRefill, newVal -> stackRefill = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Hotbar Stack Refilling Preview"))
                        .description(OptionDescription.of(Component.literal("Adds a preview of the number of matching items in the inventory to the selected hotbar stack")))
                        .binding(true, () -> stackRefillPreview, newVal -> stackRefillPreview = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Color>createBuilder()
                        .name(Component.literal("Hotbar Stack Refilling Preview Color"))
                        .description(OptionDescription.of(Component.literal("Sets the font color of the hotbar stack refilling preview")))
                        .binding(Color.WHITE, () -> stackRefillPreviewColor, newVal -> stackRefillPreviewColor = newVal)
                        .controller(ColorControllerBuilderImpl::new)
                        .build())
                    .build())
                .group(ListOption.<Item>createBuilder()
                    .name(Component.literal("Hotbar Stack Refilling Blacklist"))
                    .collapsed(true)
                    .description(OptionDescription.of(Component.literal("Defines a list of items that will not be refilled")))
                    .binding(new ArrayList<>(), () -> stackRefillBlacklist, newVal -> stackRefillBlacklist = new ArrayList<>(newVal))
                    .controller(ItemControllerBuilder::create)
                    .initial(ItemStack.EMPTY.getItem())
                .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Automatic Tool Selection"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Automatic Tool Selection"))
                        .description(OptionDescription.of(Component.literal("Automatically swaps to the hotbar slot with the best tool when mining and the best weapon when attacking")))
                        .binding(true, () -> toolSelect, newVal -> toolSelect = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Weapon Selection Damage Preference"))
                        .description(OptionDescription.of(Component.literal("Determines whether damage per second or damage per hit is prioritized when swapping to a weapon")))
                        .binding(true, () -> weaponSelectPreference, newVal -> weaponSelectPreference = newVal)
                        .controller(ImprovedInventoryConfig::DpsDphController)
                        .build())
                    .build())
                .group(ListOption.<Item>createBuilder()
                    .name(Component.literal("Automatic Tool Selection Blacklist"))
                    .collapsed(true)
                    .description(OptionDescription.of(Component.literal("Defines a list of items that will not be swapped away from")))
                    .binding(new ArrayList<>(), () -> toolSelectBlacklist, newVal -> toolSelectBlacklist = new ArrayList<>(newVal))
                    .controller(ItemControllerBuilder::create)
                    .initial(ItemStack.EMPTY.getItem())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Inventory Sorting"))
                    .collapsed(true)
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Maximum Interactions Per Tick"))
                        .description(OptionDescription.of(Component.literal("Limits the number of interactions created each tick when sorting a container (If set to 0, this setting is ignored)")))
                        .binding(0, () -> maxInteractions, newVal -> maxInteractions = newVal)
                        .controller(option -> integerSliderController(option, 0, 100, 1))
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Inventory Search Bar"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Inventory Search Bar"))
                        .description(OptionDescription.of(Component.literal("Adds a search bar to all container blocks.")))
                        .binding(true, () -> containerSearch, newVal -> containerSearch = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Tab To Nearby Containers"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Tab To Nearby Containers"))
                        .description(OptionDescription.of(Component.literal("Allows the player to access all containers within reach using either a keybind or the tab created in the inventory screen")))
                        .binding(true, () -> containerTab, newVal -> containerTab = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Unlocked Cursor"))
                        .description(OptionDescription.of(Component.literal("Stops the cursor from snapping to the center of the screen when accessing a nearby container using a tab (If Tab to Nearby Containers is disabled, this setting is ignored)")))
                        .binding(true, () -> containerTabFreeCursor, newVal -> containerTabFreeCursor = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Keybind Only"))
                        .description(OptionDescription.of(Component.literal("Hides the inventory tabs but maintains the keybind functionality (Useful when using mods with expanded container screens)")))
                        .binding(false, () -> containerTabKeybindOnly, newVal -> containerTabKeybindOnly = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .group(ListOption.<Item>createBuilder()
                    .name(Component.literal("Tab To Nearby Containers Mod Blacklist"))
                    .collapsed(true)
                    .description(OptionDescription.of(Component.literal("Defines a list of mods whose containers will be ignored")))
                    .binding(new ArrayList<>(), () -> containerTabBlacklist, newVal -> containerTabBlacklist = new ArrayList<>(newVal))
                    .controller(ItemControllerBuilder::create)
                    .initial(ItemStack.EMPTY.getItem())
                    .build())
                .build())
        
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("HUD"))
                .tooltip(Component.literal("Options that add additional information to the heads-up display"))

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Armor Durability Display"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Armor Durability Display"))
                        .description(OptionDescription.of(Component.literal("Displays the currently equipped armor items on the screen")))
                        .binding(true, () -> duraDisplay, newVal -> duraDisplay = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Durability Display Horizontal Anchor"))
                        .description(OptionDescription.of(Component.literal("The side of the screen to display the armor durability on")))
                        .binding(false, () -> duraDisplayHorizontalAnchor, newVal -> duraDisplayHorizontalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::leftRightController)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Durability Display Vertical Anchor"))
                        .description(OptionDescription.of(Component.literal("The side of the screen to display the armor durability on")))
                        .binding(false, () -> duraDisplayVerticalAnchor, newVal -> duraDisplayVerticalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::topBottomController)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Durability Display X Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the x position of the durability display")))
                        .binding(0, () -> duraDisplayOffsetX, newVal -> duraDisplayOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Durability Display Y Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the y position of the durability display")))
                        .binding(0, () -> duraDisplayOffsetY, newVal -> duraDisplayOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Slot Cycling"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Use Scroll Wheel for Slot Cycling"))
                        .description(OptionDescription.of(Component.literal("Holding Alt while scrolling activates slot cycling")))
                        .binding(true, () -> slotCycleAltScroll, newVal -> slotCycleAltScroll = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Use Number Keys for Slot Cycling"))
                        .description(OptionDescription.of(Component.literal("Holding Alt while pressing 1, 2, or 3 swaps the current stack with the stack in the first, second, or third row (from the bottom of the screen)")))
                        .binding(true, () -> slotCycleAltNum, newVal -> slotCycleAltNum = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Slot Cycling Preview"))
                        .description(OptionDescription.of(Component.literal("Displays a preview of the item stacks that would be cycled to")))
                        .binding(true, () -> slotCycle, newVal -> slotCycle = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Slot Cycling Preview X Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the x position of the slot cycle preview")))
                        .binding(0, () -> slotCycleOffsetX, newVal -> slotCycleOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Slot Cycling Preview Y Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the y position of the slot cycle preview")))
                        .binding(0, () -> slotCycleOffsetY, newVal -> slotCycleOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Bedrock Paperdoll"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Bedrock Paperdoll"))
                        .description(OptionDescription.of(Component.literal("Displays the player model on the screen like in Bedrock Edition")))
                        .binding(true, () -> paperdoll, newVal -> paperdoll = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Bedrock Paperdoll Horizontal Anchor"))
                        .description(OptionDescription.of(Component.literal("The side of the screen to display the player model on")))
                        .binding(true, () -> paperdollHorizontalAnchor, newVal -> paperdollHorizontalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::leftRightController)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Bedrock Paperdoll Vertical Anchor"))
                        .description(OptionDescription.of(Component.literal("The side of the screen to display the player model on")))
                        .binding(true, () -> paperdollVerticalAnchor, newVal -> paperdollVerticalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::topBottomController)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Bedrock Paperdoll X Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the x position of the paperdoll")))
                        .binding(0, () -> paperdollOffsetX, newVal -> paperdollOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Bedrock Paperdoll Y Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the y position of the paperdoll")))
                        .binding(0, () -> paperdollOffsetY, newVal -> paperdollOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("WAILA (What Am I Looking At?)"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("WAILA (What Am I Looking At?)"))
                        .description(OptionDescription.of(Component.literal("Displays the targeted block or entity")))
                        .binding(true, () -> waila, newVal -> waila = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("WAILA Horizontal Anchor"))
                        .description(OptionDescription.of(Component.literal("The side of the screen to display WAILA on")))
                        .binding(true, () -> wailaHorizontalAnchor, newVal -> wailaHorizontalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::leftRightController)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("WAILA Vertical Anchor"))
                        .description(OptionDescription.of(Component.literal("The side of the screen to display WAILA on")))
                        .binding(true, () -> wailaVerticalAnchor, newVal -> wailaVerticalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::topBottomController)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("WAILA X Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the x position of WAILA")))
                        .binding(0, () -> wailaOffsetX, newVal -> wailaOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("WAILA Y Offset"))
                        .description(OptionDescription.of(Component.literal("Adjusts the y position of WAILA")))
                        .binding(0, () -> wailaOffsetY, newVal -> wailaOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Held Items Visible In Boats"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Held Items Visible In Boats"))
                        .description(OptionDescription.of(Component.literal("Stops held items being hidden while rowing a boat")))
                        .binding(true, () -> heldItemsVisibleInBoat, newVal -> heldItemsVisibleInBoat = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Colored Armor Bar"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Colored Armor Bar"))
                        .description(OptionDescription.of(Component.literal("Colors the armor bar icons to match the materials of the equipped armor")))
                        .binding(true, () -> armorBarColors, newVal -> armorBarColors = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Status Effect Timer"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Status Effect Timer"))
                        .description(OptionDescription.of(Component.literal("Adds bars to the status effect overlay that show the remaining durations of active effects")))
                        .binding(true, () -> statusEffectTimer, newVal -> statusEffectTimer = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Locator Bar"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Don't Hide Experience Bar"))
                        .description(OptionDescription.of(Component.literal("Renders the locator bar on top of the experience bar, instead of replacing it.")))
                        .binding(true, () -> combineExpAndLocatorBars, newVal -> combineExpAndLocatorBars = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Players' Waypoints Use Their Skins"))
                        .description(OptionDescription.of(Component.literal("Waypoints that point to a player use that player's head as the sprite")))
                        .binding(true, () -> playerHeadWaypoints, newVal -> playerHeadWaypoints = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())

            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Tooltips"))
                .tooltip(Component.literal("Options that add additional information to item tooltips"))

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Compass Target Tooltip"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Compass Target Tooltip"))
                        .description(OptionDescription.of(Component.literal("Adds a compass's target position to its tooltip")))
                        .binding(true, () -> compassTooltip, newVal -> compassTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Clock Daytime Tooltip"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Clock Daytime Tooltip"))
                        .description(OptionDescription.of(Component.literal("Adds the time of day to the clock's tooltip")))
                        .binding(true, () -> clockTooltip, newVal -> clockTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Food Nutrition Tooltip"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Food Nutrition Tooltip"))
                        .description(OptionDescription.of(Component.literal("Adds the nutrition and saturation of a food item to its tooltip")))
                        .binding(true, () -> foodTooltip, newVal -> foodTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Shulker Box Preview"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Shulker Box Preview"))
                        .description(OptionDescription.of(Component.literal("Displays a Shulker Box's inventory in its tooltip")))
                        .binding(true, () -> shulkerBoxTooltip, newVal -> shulkerBoxTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Colored Shulker Box Preview"))
                        .description(OptionDescription.of(Component.literal("Tints the inventory preview to the color of the Shulker Box")))
                        .binding(true, () -> shulkerBoxTooltipColors, newVal -> shulkerBoxTooltipColors = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Map Preview"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Map Preview"))
                        .description(OptionDescription.of(Component.literal("Displays a map's contents in its tooltip")))
                        .binding(true, () -> mapTooltip, newVal -> mapTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Bundles"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Expanded Bundle Tooltip"))
                        .description(OptionDescription.of(Component.literal("Displays the entire contents of a bundle in its tooltip")))
                        .binding(true, () -> expandedBundleTooltip, newVal -> expandedBundleTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Bundle Progress Bar Fraction"))
                        .description(OptionDescription.of(Component.literal("Displays a bundle's fullness as a fraction of 64 on top of its progress bar")))
                        .binding(true, () -> bundleProgressBarFraction, newVal -> bundleProgressBarFraction = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())
        
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Screen Effects"))
                .tooltip(Component.literal("Options that modify the entire screen"))

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Zoom"))
                    .collapsed(true)
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Target Zoom FOV"))
                        .description(OptionDescription.of(Component.literal("The field of view used when the zoom key is held")))
                        .binding(30, () -> zoomFOV, newVal -> zoomFOV = newVal)
                        .controller(option -> integerSliderController(option, 30, 110, 1))
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Scrolling to Adjust Zoom Requires Control"))
                        .description(OptionDescription.of(Component.literal("Requires the CTRL key to be pressed in order for the scroll wheel to adjust zoom")))
                        .binding(true, () -> zoomScrollRequiresControl, newVal -> zoomScrollRequiresControl = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Play Spyglass Sound on Zoom"))
                        .description(OptionDescription.of(Component.literal("Plays the spyglass sound when the zoom key is pressed")))
                        .binding(true, () -> zoomSound, newVal -> zoomSound = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Gamma"))
                    .collapsed(true)
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Gamma Level"))
                        .description(OptionDescription.of(Component.literal("The gamma (brightness) used when increased gamma is toggled on")))
                        .binding(500, () -> gamma, newVal -> gamma = newVal)
                        .controller(option -> integerSliderController(option, 0, 2000, 100))
                        .build())
                    .build())
                .build())
            
            .save(ImprovedInventoryConfig::write)
            .build()
            .generateScreen(parent);
    }

    private static IntegerSliderControllerBuilder integerSliderController(Option<Integer> option, int min, int max, int step) {
        return IntegerSliderControllerBuilder.create(option)
            .range(min, max)
            .step(step);
    }

    private static BooleanControllerBuilder leftRightController(Option<Boolean> option) {
        return BooleanControllerBuilder.create(option)
            .formatValue(value -> value ? Component.literal("LEFT") : Component.literal("RIGHT"));
    }

    private static BooleanControllerBuilder topBottomController(Option<Boolean> option) {
        return BooleanControllerBuilder.create(option)
            .formatValue(value -> value ? Component.literal("TOP") : Component.literal("BOTTOM"));
    }

    private static BooleanControllerBuilder DpsDphController(Option<Boolean> option) {
        return BooleanControllerBuilder.create(option)
            .formatValue(value -> value ? Component.literal("Damage/Second") : Component.literal("Damage/Hit"));
    }

   private static ArrayList<Item> stringArrayToItemArrayList(String[] stringArray) {
       ArrayList<Item> itemArrayList = new ArrayList<>();
       for (String string : stringArray) {
           if (!string.isEmpty()) {
               try {
                   itemArrayList.add(ItemRegistryHelper.getItemFromName(string));
               } catch (Exception ignored) {}
           }
        }
        return itemArrayList;
   }

    private static ArrayList<String> stringArrayToStringArrayList(String[] stringArray) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (String string : stringArray) {
            try {
                stringArrayList.add(string);
            } catch (Exception ignored) {}
        }
        return stringArrayList;
    }

    public static void write() {
        try {
            if (Files.notExists(configDir)) {
                Files.createDirectory(configDir);
            }
            Files.deleteIfExists(configFile);
            JsonObject json = new JsonObject();
            json.addProperty("duraDisplay", duraDisplay);
            json.addProperty("duraDisplayHorizontalAnchor", duraDisplayHorizontalAnchor ? "LEFT" : "RIGHT");
            json.addProperty("duraDisplayVerticalAnchor", duraDisplayVerticalAnchor ? "TOP" : "BOTTOM");
            json.addProperty("duraDisplayOffsetX", duraDisplayOffsetX);
            json.addProperty("duraDisplayOffsetY", duraDisplayOffsetY);
            json.addProperty("slotCycle", slotCycle);
            json.addProperty("slotCycleAltScroll", slotCycleAltScroll);
            json.addProperty("slotCycleAltNum", slotCycleAltNum);
            json.addProperty("slotCycleOffsetX", slotCycleOffsetX);
            json.addProperty("slotCycleOffsetY", slotCycleOffsetY);
            json.addProperty("stackRefill", stackRefill);
            json.addProperty("stackRefillPreview", stackRefillPreview);
            json.addProperty("stackRefillPreviewColor", stackRefillPreviewColor.getRGB());
            json.addProperty("stackRefillBlacklist", String.valueOf(stackRefillBlacklist));
            json.addProperty("toolSelect", toolSelect);
            json.addProperty("weaponSelectPreference", weaponSelectPreference ? "Damage/Second" : "Damage/Hit");
            json.addProperty("toolSelectBlacklist", String.valueOf(toolSelectBlacklist));
            json.addProperty("paperdoll", paperdoll);
            json.addProperty("paperdollHorizontalAnchor", paperdollHorizontalAnchor ? "LEFT" : "RIGHT");
            json.addProperty("paperdollVerticalAnchor", paperdollVerticalAnchor ? "TOP" : "BOTTOM");
            json.addProperty("paperdollOffsetX", paperdollOffsetX);
            json.addProperty("paperdollOffsetY", paperdollOffsetY);
            json.addProperty("waila", waila);
            json.addProperty("wailaHorizontalAnchor", wailaHorizontalAnchor ? "LEFT" : "RIGHT");
            json.addProperty("wailaVerticalAnchor", wailaVerticalAnchor ? "TOP" : "BOTTOM");
            json.addProperty("wailaOffsetX", wailaOffsetX);
            json.addProperty("wailaOffsetY", wailaOffsetY);
            json.addProperty("zoomFOV", zoomFOV);
            json.addProperty("zoomScrollRequiresControl", zoomScrollRequiresControl);
            json.addProperty("zoomSound", zoomSound);
            json.addProperty("gamma", gamma);
            json.addProperty("maxInteractions", maxInteractions);
            json.addProperty("containerSearch", containerSearch);
            json.addProperty("containerTab", containerTab);
            json.addProperty("containerTabFreeCursor", containerTabFreeCursor);
            json.addProperty("containerTabKeybindOnly", containerTabKeybindOnly);
            json.addProperty("containerTabBlacklist", String.valueOf(containerTabBlacklist));
            json.addProperty("compassTooltip", compassTooltip);
            json.addProperty("clockTooltip", clockTooltip);
            json.addProperty("foodTooltip", foodTooltip);
            json.addProperty("shulkerBoxTooltip", shulkerBoxTooltip);
            json.addProperty("shulkerBoxTooltipColors", shulkerBoxTooltipColors);
            json.addProperty("mapTooltip", mapTooltip);
            json.addProperty("expandedBundleTooltip", expandedBundleTooltip);
            json.addProperty("bundleProgressBarFraction", bundleProgressBarFraction);
            json.addProperty("heldItemsVisibleInBoat", heldItemsVisibleInBoat);
            json.addProperty("armorBarColors", armorBarColors);
            json.addProperty("statusEffectTimer", statusEffectTimer);
            json.addProperty("combineExpAndLocatorBars", combineExpAndLocatorBars);
            json.addProperty("playerHeadWaypoints", playerHeadWaypoints);
            Files.writeString(configFile, gson.toJson(json));
        } catch (Exception ignored) {}
    }

    public static void read() {
        try {
            if (Files.notExists(configFile)) {
                write();
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("duraDisplay")) {
                duraDisplay = json.getAsJsonPrimitive("duraDisplay").getAsBoolean();
            }
            if (json.has("duraDisplayHorizontalAnchor")) {
                duraDisplayHorizontalAnchor = json.getAsJsonPrimitive("duraDisplayHorizontalAnchor").getAsString().equalsIgnoreCase("LEFT");
            }
            if (json.has("duraDisplayVerticalAnchor")) {
                duraDisplayVerticalAnchor = json.getAsJsonPrimitive("duraDisplayVerticalAnchor").getAsString().equalsIgnoreCase("TOP");
            }
            if (json.has("duraDisplayOffsetX")) {
                duraDisplayOffsetX = json.getAsJsonPrimitive("duraDisplayOffsetX").getAsInt();
            }
            if (json.has("duraDisplayOffsetY")) {
                duraDisplayOffsetY = json.getAsJsonPrimitive("duraDisplayOffsetY").getAsInt();
            }
            if (json.has("slotCycle")) {
                slotCycle = json.getAsJsonPrimitive("slotCycle").getAsBoolean();
            }
            if (json.has("slotCycleAltScroll")) {
                slotCycleAltScroll = json.getAsJsonPrimitive("slotCycleAltScroll").getAsBoolean();
            }
            if (json.has("slotCycleAltNum")) {
                slotCycleAltNum = json.getAsJsonPrimitive("slotCycleAltNum").getAsBoolean();
            }
            if (json.has("slotCycleOffsetX")) {
                slotCycleOffsetX = json.getAsJsonPrimitive("slotCycleOffsetX").getAsInt();
            }
            if (json.has("slotCycleOffsetY")) {
                slotCycleOffsetY = json.getAsJsonPrimitive("slotCycleOffsetY").getAsInt();
            }
            if (json.has("stackRefill")) {
                stackRefill = json.getAsJsonPrimitive("stackRefill").getAsBoolean();
            }
            if (json.has("stackRefillPreview")) {
                stackRefillPreview = json.getAsJsonPrimitive("stackRefillPreview").getAsBoolean();
            }
            if (json.has("stackRefillPreviewColor")) {
                stackRefillPreviewColor = new Color(json.getAsJsonPrimitive("stackRefillPreviewColor").getAsInt());
            }
            if (json.has("stackRefillBlacklist")) {
                stackRefillBlacklist = stringArrayToItemArrayList(json.getAsJsonPrimitive("stackRefillBlacklist").getAsString().replace("[", "").replace("]", "").split(", "));
            }
            if (json.has("toolSelect")) {
                toolSelect = json.getAsJsonPrimitive("toolSelect").getAsBoolean();
            }
            if (json.has("weaponSelectPreference")) {
                weaponSelectPreference = json.getAsJsonPrimitive("weaponSelectPreference").getAsString().equalsIgnoreCase("Damage/Second");
            }
            if (json.has("toolSelectBlacklist")) {
                toolSelectBlacklist = stringArrayToItemArrayList(json.getAsJsonPrimitive("toolSelectBlacklist").getAsString().replace("[", "").replace("]", "").split(", "));
            }
            if (json.has("paperdoll")) {
                paperdoll = json.getAsJsonPrimitive("paperdoll").getAsBoolean();
            }
            if (json.has("paperdollHorizontalAnchor")) {
                paperdollHorizontalAnchor = json.getAsJsonPrimitive("paperdollHorizontalAnchor").getAsString().equalsIgnoreCase("LEFT");
            }
            if (json.has("paperdollVerticalAnchor")) {
                paperdollVerticalAnchor = json.getAsJsonPrimitive("paperdollVerticalAnchor").getAsString().equalsIgnoreCase("TOP");
            }
            if (json.has("paperdollOffsetX")) {
                paperdollOffsetX = json.getAsJsonPrimitive("paperdollOffsetX").getAsInt();
            }
            if (json.has("paperdollOffsetY")) {
                paperdollOffsetY = json.getAsJsonPrimitive("paperdollOffsetY").getAsInt();
            }
            if (json.has("waila")) {
                waila = json.getAsJsonPrimitive("waila").getAsBoolean();
            }
            if (json.has("wailaHorizontalAnchor")) {
                wailaHorizontalAnchor = json.getAsJsonPrimitive("wailaHorizontalAnchor").getAsString().equalsIgnoreCase("LEFT");
            }
            if (json.has("wailaVerticalAnchor")) {
                wailaVerticalAnchor = json.getAsJsonPrimitive("wailaVerticalAnchor").getAsString().equalsIgnoreCase("TOP");
            }
            if (json.has("wailaOffsetX")) {
                wailaOffsetX = json.getAsJsonPrimitive("wailaOffsetX").getAsInt();
            }
            if (json.has("wailaOffsetY")) {
                wailaOffsetY = json.getAsJsonPrimitive("wailaOffsetY").getAsInt();
            }
            if (json.has("zoomFOV")) {
                zoomFOV = json.getAsJsonPrimitive("zoomFOV").getAsInt();
            }
            if (json.has("zoomScrollRequiresControl")) {
                zoomScrollRequiresControl = json.getAsJsonPrimitive("zoomScrollRequiresControl").getAsBoolean();
            }
            if (json.has("zoomSound")) {
                zoomSound = json.getAsJsonPrimitive("zoomSound").getAsBoolean();
            }
            if (json.has("gamma")) {
                gamma = json.getAsJsonPrimitive("gamma").getAsInt();
            }
            if (json.has("maxInteractions")) {
                maxInteractions = json.getAsJsonPrimitive("maxInteractions").getAsInt();
            }
            if (json.has("containerSearch")) {
                containerSearch = json.getAsJsonPrimitive("containerSearch").getAsBoolean();
            }
            if (json.has("containerTab")) {
                containerTab = json.getAsJsonPrimitive("containerTab").getAsBoolean();
            }
            if (json.has("containerTabFreeCursor")) {
                containerTabFreeCursor = json.getAsJsonPrimitive("containerTabFreeCursor").getAsBoolean();
            }
            if (json.has("containerTabKeybindOnly")) {
                containerTabKeybindOnly = json.getAsJsonPrimitive("containerTabKeybindOnly").getAsBoolean();
            }
            if (json.has("containerTabBlacklist")) {
                containerTabBlacklist = stringArrayToItemArrayList(json.getAsJsonPrimitive("containerTabBlacklist").getAsString().replace("[", "").replace("]", "").split(", "));
            }
            if (json.has("compassTooltip")) {
                compassTooltip = json.getAsJsonPrimitive("compassTooltip").getAsBoolean();
            }
            if (json.has("clockTooltip")) {
                clockTooltip = json.getAsJsonPrimitive("clockTooltip").getAsBoolean();
            }
            if (json.has("foodTooltip")) {
                foodTooltip = json.getAsJsonPrimitive("foodTooltip").getAsBoolean();
            }
            if (json.has("shulkerBoxTooltip")) {
                shulkerBoxTooltip = json.getAsJsonPrimitive("shulkerBoxTooltip").getAsBoolean();
            }
            if (json.has("shulkerBoxTooltipColors")) {
                shulkerBoxTooltipColors = json.getAsJsonPrimitive("shulkerBoxTooltipColors").getAsBoolean();
            }
            if (json.has("mapTooltip")) {
                mapTooltip = json.getAsJsonPrimitive("mapTooltip").getAsBoolean();
            }
            if (json.has("expandedBundleTooltip")) {
                expandedBundleTooltip = json.getAsJsonPrimitive("expandedBundleTooltip").getAsBoolean();
            }
            if (json.has("bundleProgressBarFraction")) {
                bundleProgressBarFraction = json.getAsJsonPrimitive("bundleProgressBarFraction").getAsBoolean();
            }
            if (json.has("heldItemsVisibleInBoat")) {
                heldItemsVisibleInBoat = json.getAsJsonPrimitive("heldItemsVisibleInBoat").getAsBoolean();
            }
            if (json.has("armorBarColors")) {
                armorBarColors = json.getAsJsonPrimitive("armorBarColors").getAsBoolean();
            }
            if (json.has("statusEffectTimer")) {
                statusEffectTimer = json.getAsJsonPrimitive("statusEffectTimer").getAsBoolean();
            }
            if (json.has("combineExpAndLocatorBars")) {
                combineExpAndLocatorBars = json.getAsJsonPrimitive("combineExpAndLocatorBars").getAsBoolean();
            }
            if (json.has("playerHeadWaypoints")) {
                playerHeadWaypoints = json.getAsJsonPrimitive("playerHeadWaypoints").getAsBoolean();
            }
        } catch (Exception ignored) {}
    }

}
