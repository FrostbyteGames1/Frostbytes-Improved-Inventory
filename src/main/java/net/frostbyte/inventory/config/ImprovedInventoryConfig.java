package net.frostbyte.inventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.impl.controller.ColorControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.IntegerFieldControllerBuilderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
    public static boolean textDisplay = true;
    public static ArrayList<String> textDisplayLeft = new ArrayList<>();
    public static ArrayList<String> textDisplayRight = new ArrayList<>();
    public static int textDisplayOffsetX = 0;
    public static int textDisplayOffsetY = 0;
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
    public static ArrayList<String> containerTabModBlacklist = new ArrayList<>();
    public static boolean shulkerBoxTooltip = true;
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
            .title(Text.of("Frostbyte's Improved Inventory Config Menu"))
            
            .category(ConfigCategory.createBuilder()
                .name(Text.of("Inventory"))
                .tooltip(Text.of("Options that interact with the player's inventory"))

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Hotbar Stack Refilling"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Hotbar Stack Refilling"))
                        .description(OptionDescription.of(Text.of("Refills the hotbar with a new stack of the same item from the inventory")))
                        .binding(true, () -> stackRefill, newVal -> stackRefill = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Hotbar Stack Refilling Preview"))
                        .description(OptionDescription.of(Text.of("Adds a preview of the number of matching items in the inventory to the selected hotbar stack")))
                        .binding(true, () -> stackRefillPreview, newVal -> stackRefillPreview = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Color>createBuilder()
                        .name(Text.of("Hotbar Stack Refilling Preview Color"))
                        .description(OptionDescription.of(Text.of("Sets the font color of the hotbar stack refilling preview")))
                        .binding(Color.WHITE, () -> stackRefillPreviewColor, newVal -> stackRefillPreviewColor = newVal)
                        .controller(ColorControllerBuilderImpl::new)
                        .build())
                    .build())
                .group(ListOption.<Item>createBuilder()
                    .name(Text.of("Hotbar Stack Refilling Blacklist"))
                    .collapsed(true)
                    .description(OptionDescription.of(Text.of("Defines a list of items that will not be refilled")))
                    .binding(new ArrayList<>(), () -> stackRefillBlacklist, newVal -> stackRefillBlacklist = new ArrayList<>(newVal))
                    .controller(ItemControllerBuilder::create)
                    .initial(ItemStack.EMPTY.getItem())
                .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Automatic Tool Selection"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Automatic Tool Selection"))
                        .description(OptionDescription.of(Text.of("Automatically swaps to the hotbar slot with the best tool when mining and the best weapon when attacking")))
                        .binding(true, () -> toolSelect, newVal -> toolSelect = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Weapon Selection Damage Preference"))
                        .description(OptionDescription.of(Text.of("Determines whether damage per second or damage per hit is prioritized when swapping to a weapon")))
                        .binding(true, () -> weaponSelectPreference, newVal -> weaponSelectPreference = newVal)
                        .controller(ImprovedInventoryConfig::DpsDphController)
                        .build())
                    .build())
                .group(ListOption.<Item>createBuilder()
                    .name(Text.of("Automatic Tool Selection Blacklist"))
                    .collapsed(true)
                    .description(OptionDescription.of(Text.of("Defines a list of items that will not be swapped away from")))
                    .binding(new ArrayList<>(), () -> toolSelectBlacklist, newVal -> toolSelectBlacklist = new ArrayList<>(newVal))
                    .controller(ItemControllerBuilder::create)
                    .initial(ItemStack.EMPTY.getItem())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Inventory Sorting"))
                    .collapsed(true)
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Maximum Interactions Per Tick"))
                        .description(OptionDescription.of(Text.of("Limits the number of interactions created each tick when sorting a container (If set to 0, this setting is ignored)")))
                        .binding(0, () -> maxInteractions, newVal -> maxInteractions = newVal)
                        .controller(option -> integerSliderController(option, 0, 100, 1))
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Inventory Search Bar"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Inventory Search Bar"))
                        .description(OptionDescription.of(Text.of("Adds a search bar to all container blocks.")))
                        .binding(true, () -> containerSearch, newVal -> containerSearch = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Tab To Nearby Containers"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Tab To Nearby Containers"))
                        .description(OptionDescription.of(Text.of("Allows the player to access all containers within reach using either a keybind or the tab created in the inventory screen")))
                        .binding(true, () -> containerTab, newVal -> containerTab = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Unlocked Cursor"))
                        .description(OptionDescription.of(Text.of("Stops the cursor from snapping to the center of the screen when accessing a nearby container using a tab (If Tab to Nearby Containers is disabled, this setting is ignored)")))
                        .binding(true, () -> containerTabFreeCursor, newVal -> containerTabFreeCursor = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Keybind Only"))
                        .description(OptionDescription.of(Text.of("Hides the inventory tabs but maintains the keybind functionality (Useful when using mods with expanded container screens)")))
                        .binding(false, () -> containerTabKeybindOnly, newVal -> containerTabKeybindOnly = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .group(ListOption.<String>createBuilder()
                    .name(Text.of("Tab To Nearby Containers Mod Blacklist"))
                    .collapsed(true)
                    .description(OptionDescription.of(Text.of("Defines a list of mods whose containers will be ignored")))
                    .binding(new ArrayList<>(), () -> containerTabModBlacklist, newVal -> containerTabModBlacklist = new ArrayList<>(newVal))
                    .controller(ImprovedInventoryConfig::loadedModsDropdownStringController)
                    .initial("")
                    .build())
                .build())
        
            .category(ConfigCategory.createBuilder()
                .name(Text.of("HUD"))
                .tooltip(Text.of("Options that add additional information to the heads-up display"))

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Armor Durability Display"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Armor Durability Display"))
                        .description(OptionDescription.of(Text.of("Displays the currently equipped armor items on the screen")))
                        .binding(true, () -> duraDisplay, newVal -> duraDisplay = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Durability Display Horizontal Anchor"))
                        .description(OptionDescription.of(Text.of("The side of the screen to display the armor durability on")))
                        .binding(false, () -> duraDisplayHorizontalAnchor, newVal -> duraDisplayHorizontalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::leftRightController)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Durability Display Vertical Anchor"))
                        .description(OptionDescription.of(Text.of("The side of the screen to display the armor durability on")))
                        .binding(false, () -> duraDisplayVerticalAnchor, newVal -> duraDisplayVerticalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::topBottomController)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Durability Display X Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the x position of the durability display")))
                        .binding(0, () -> duraDisplayOffsetX, newVal -> duraDisplayOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Durability Display Y Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the y position of the durability display")))
                        .binding(0, () -> duraDisplayOffsetY, newVal -> duraDisplayOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Slot Cycling"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Use Scroll Wheel for Slot Cycling"))
                        .description(OptionDescription.of(Text.of("Holding Alt while scrolling activates slot cycling")))
                        .binding(true, () -> slotCycleAltScroll, newVal -> slotCycleAltScroll = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Slot Cycling Preview"))
                        .description(OptionDescription.of(Text.of("Displays a preview of the item stacks that would be cycled to")))
                        .binding(true, () -> slotCycle, newVal -> slotCycle = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Slot Cycling Preview X Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the x position of the slot cycle preview")))
                        .binding(0, () -> slotCycleOffsetX, newVal -> slotCycleOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Slot Cycling Preview Y Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the y position of the slot cycle preview")))
                        .binding(0, () -> slotCycleOffsetY, newVal -> slotCycleOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Bedrock Paperdoll"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Bedrock Paperdoll"))
                        .description(OptionDescription.of(Text.of("Displays the player model on the screen like in Bedrock Edition")))
                        .binding(true, () -> paperdoll, newVal -> paperdoll = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Bedrock Paperdoll Horizontal Anchor"))
                        .description(OptionDescription.of(Text.of("The side of the screen to display the player model on")))
                        .binding(true, () -> paperdollHorizontalAnchor, newVal -> paperdollHorizontalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::leftRightController)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Bedrock Paperdoll Vertical Anchor"))
                        .description(OptionDescription.of(Text.of("The side of the screen to display the player model on")))
                        .binding(true, () -> paperdollVerticalAnchor, newVal -> paperdollVerticalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::topBottomController)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Bedrock Paperdoll X Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the x position of the paperdoll")))
                        .binding(0, () -> paperdollOffsetX, newVal -> paperdollOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Bedrock Paperdoll Y Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the y position of the paperdoll")))
                        .binding(0, () -> paperdollOffsetY, newVal -> paperdollOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Text Display"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Text Display"))
                        .description(OptionDescription.of(Text.of("Displays a list of informational text strings")))
                        .binding(true, () -> textDisplay, newVal -> textDisplay = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("text Display X Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the x position of the text display")))
                        .binding(0, () -> textDisplayOffsetX, newVal -> textDisplayOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Text Display Y Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the y position of the text display")))
                        .binding(0, () -> textDisplayOffsetY, newVal -> textDisplayOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())
                .group(ListOption.<String>createBuilder()
                    .name(Text.of("Text Display Strings (Left)"))
                    .collapsed(true)
                    .description(OptionDescription.of(Text.of("Defines the list of text strings that will be displayed on the left side of the screen")))
                    .binding(new ArrayList<>(), () -> textDisplayLeft, newVal -> textDisplayLeft = new ArrayList<>(newVal))
                    .controller(ImprovedInventoryConfig::textDisplayDropdownStringController)
                    .initial("")
                    .build())
                .group(ListOption.<String>createBuilder()
                    .name(Text.of("Text Display Strings (Right)"))
                    .collapsed(true)
                    .description(OptionDescription.of(Text.of("Defines the list of text strings that will be displayed on the right side of the screen")))
                    .binding(new ArrayList<>(), () -> textDisplayRight, newVal -> textDisplayRight = new ArrayList<>(newVal))
                    .controller(ImprovedInventoryConfig::textDisplayDropdownStringController)
                    .initial("")
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("WAILA (What Am I Looking At?)"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("WAILA (What Am I Looking At?)"))
                        .description(OptionDescription.of(Text.of("Displays the targeted block or entity")))
                        .binding(true, () -> waila, newVal -> waila = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("WAILA Horizontal Anchor"))
                        .description(OptionDescription.of(Text.of("The side of the screen to display WAILA on")))
                        .binding(true, () -> wailaHorizontalAnchor, newVal -> wailaHorizontalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::leftRightController)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("WAILA Vertical Anchor"))
                        .description(OptionDescription.of(Text.of("The side of the screen to display WAILA on")))
                        .binding(true, () -> wailaVerticalAnchor, newVal -> wailaVerticalAnchor = newVal)
                        .controller(ImprovedInventoryConfig::topBottomController)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("WAILA X Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the x position of WAILA")))
                        .binding(0, () -> wailaOffsetX, newVal -> wailaOffsetX = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("WAILA Y Offset"))
                        .description(OptionDescription.of(Text.of("Adjusts the y position of WAILA")))
                        .binding(0, () -> wailaOffsetY, newVal -> wailaOffsetY = newVal)
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Held Items Visible In Boats"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Held Items Visible In Boats"))
                        .description(OptionDescription.of(Text.of("Stops held items being hidden while rowing a boat")))
                        .binding(true, () -> heldItemsVisibleInBoat, newVal -> heldItemsVisibleInBoat = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Colored Armor Bar"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Colored Armor Bar"))
                        .description(OptionDescription.of(Text.of("Colors the armor bar icons to match the materials of the equipped armor")))
                        .binding(true, () -> armorBarColors, newVal -> armorBarColors = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Status Effect Timer"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Status Effect Timer"))
                        .description(OptionDescription.of(Text.of("Adds bars to the status effect overlay that show the remaining durations of active effects")))
                        .binding(true, () -> statusEffectTimer, newVal -> statusEffectTimer = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Locator Bar"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Don't Hide Experience Bar"))
                        .description(OptionDescription.of(Text.of("Renders the locator bar on top of the experience bar, instead of replacing it.")))
                        .binding(true, () -> combineExpAndLocatorBars, newVal -> combineExpAndLocatorBars = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Players' Waypoints Use Their Skins"))
                        .description(OptionDescription.of(Text.of("Waypoints that point to a player use that player's head as the sprite")))
                        .binding(true, () -> playerHeadWaypoints, newVal -> playerHeadWaypoints = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())

            .category(ConfigCategory.createBuilder()
                .name(Text.of("Tooltips"))
                .tooltip(Text.of("Options that add additional information to item tooltips"))

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Compass Target Tooltip"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Compass Target Tooltip"))
                        .description(OptionDescription.of(Text.of("Adds a compass's target position to its tooltip")))
                        .binding(true, () -> compassTooltip, newVal -> compassTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Clock Daytime Tooltip"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Clock Daytime Tooltip"))
                        .description(OptionDescription.of(Text.of("Adds the time of day to the clock's tooltip")))
                        .binding(true, () -> clockTooltip, newVal -> clockTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Food Nutrition Tooltip"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Food Nutrition Tooltip"))
                        .description(OptionDescription.of(Text.of("Adds the nutrition and saturation of a food item to its tooltip")))
                        .binding(true, () -> foodTooltip, newVal -> foodTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Shulker Box Preview"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Shulker Box Preview"))
                        .description(OptionDescription.of(Text.of("Displays a shulker box's inventory in its tooltip")))
                        .binding(true, () -> shulkerBoxTooltip, newVal -> shulkerBoxTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Map Preview"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Map Preview"))
                        .description(OptionDescription.of(Text.of("Displays a map's contents in its tooltip")))
                        .binding(true, () -> mapTooltip, newVal -> mapTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Expanded Bundle Tooltip"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Expanded Bundle Tooltip"))
                        .description(OptionDescription.of(Text.of("Displays the entire contents of a bundle in its tooltip")))
                        .binding(true, () -> expandedBundleTooltip, newVal -> expandedBundleTooltip = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Bundle Progress Bar Fraction"))
                    .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Bundle Progress Bar Fraction"))
                        .description(OptionDescription.of(Text.of("Displays a bundle's fullness as a fraction of 64 on top of its progress bar")))
                        .binding(true, () -> bundleProgressBarFraction, newVal -> bundleProgressBarFraction = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())
        
            .category(ConfigCategory.createBuilder()
                .name(Text.of("Screen Effects"))
                .tooltip(Text.of("Options that modify the entire screen"))

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Zoom"))
                    .collapsed(true)
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Target Zoom FOV"))
                        .description(OptionDescription.of(Text.of("The field of view used when the zoom key is held")))
                        .binding(30, () -> zoomFOV, newVal -> zoomFOV = newVal)
                        .controller(option -> integerSliderController(option, 30, 110, 1))
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Scrolling to Adjust Zoom Requires Control"))
                        .description(OptionDescription.of(Text.of("Requires the CTRL key to be pressed in order for the scroll wheel to adjust zoom")))
                        .binding(true, () -> zoomScrollRequiresControl, newVal -> zoomScrollRequiresControl = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Play Spyglass Sound on Zoom"))
                        .description(OptionDescription.of(Text.of("Plays the spyglass sound when the zoom key is pressed")))
                        .binding(true, () -> zoomSound, newVal -> zoomSound = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Gamma"))
                    .collapsed(true)
                    .option(Option.<Integer>createBuilder()
                        .name(Text.of("Gamma Level"))
                        .description(OptionDescription.of(Text.of("The gamma (brightness) used when increased gamma is toggled on")))
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
            .formatValue(value -> value ? Text.of("LEFT") : Text.of("RIGHT"));
    }

    private static BooleanControllerBuilder topBottomController(Option<Boolean> option) {
        return BooleanControllerBuilder.create(option)
            .formatValue(value -> value ? Text.of("TOP") : Text.of("BOTTOM"));
    }

    private static BooleanControllerBuilder DpsDphController(Option<Boolean> option) {
        return BooleanControllerBuilder.create(option)
            .formatValue(value -> value ? Text.of("Damage/Second") : Text.of("Damage/Hit"));
    }

   private static DropdownStringControllerBuilder textDisplayDropdownStringController(Option<String> option) {
        return DropdownStringControllerBuilder.create(option)
            .values(
                "Biome",
                "Blank Line",
                "Block Light",
                "Coordinates",
                "Day",
                "Dimension",
                "Entity Count",
                "Facing Direction",
                "FPS",
                "Local Difficulty",
                "Memory Usage",
                "Slime Chunk",
                "Speed",
                "Sprint Indicator",
                "Targeted Block/Entity",
                "Time (Game)",
                "Time (Real)"
            ).allowEmptyValue(true).allowAnyValue(false);
   }

   private static DropdownStringControllerBuilder loadedModsDropdownStringController(Option<String> option) {
        ArrayList<String> loadedMods = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            loadedMods.add(mod.getMetadata().getId());
        }
        return DropdownStringControllerBuilder.create(option).values(loadedMods).allowEmptyValue(true).allowAnyValue(false);
   }

   private static ArrayList<Item> stringArrayToItemArrayList(String[] stringArray) {
       ArrayList<Item> itemArrayList = new ArrayList<>();
       for (String string : stringArray) {
           if (!string.isEmpty()) {
               try {
                   itemArrayList.add(Registries.ITEM.get(Identifier.of(string)));
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
            json.addProperty("textDisplayLeft", String.valueOf(textDisplayLeft));
            json.addProperty("textDisplayRight", String.valueOf(textDisplayRight));
            json.addProperty("textDisplay", textDisplay);
            json.addProperty("textDisplayOffsetX", textDisplayOffsetX);
            json.addProperty("textDisplayOffsetY", textDisplayOffsetY);
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
            json.addProperty("containerTabModBlacklist", String.valueOf(containerTabModBlacklist));
            json.addProperty("compassTooltip", compassTooltip);
            json.addProperty("clockTooltip", clockTooltip);
            json.addProperty("foodTooltip", foodTooltip);
            json.addProperty("shulkerBoxTooltip", shulkerBoxTooltip);
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
            if (json.has("textDisplayLeft")) {
                textDisplayLeft = stringArrayToStringArrayList(json.getAsJsonPrimitive("textDisplayLeft").getAsString().replace("[", "").replace("]", "").split(", "));
            }
            if (json.has("textDisplayRight")) {
                textDisplayRight = stringArrayToStringArrayList(json.getAsJsonPrimitive("textDisplayRight").getAsString().replace("[", "").replace("]", "").split(", "));
            }
            if (json.has("textDisplay")) {
                textDisplay = json.getAsJsonPrimitive("textDisplay").getAsBoolean();
            }
            if (json.has("textDisplayOffsetX")) {
                textDisplayOffsetX = json.getAsJsonPrimitive("textDisplayOffsetX").getAsInt();
            }
            if (json.has("textDisplayOffsetY")) {
                textDisplayOffsetY = json.getAsJsonPrimitive("textDisplayOffsetY").getAsInt();
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
            if (json.has("containerTabModBlacklist")) {
                containerTabModBlacklist = stringArrayToStringArrayList(json.getAsJsonPrimitive("containerTabModBlacklist").getAsString().replace("[", "").replace("]", "").split(", "));
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
