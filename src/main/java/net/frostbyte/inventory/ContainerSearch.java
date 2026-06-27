package net.frostbyte.inventory;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContainerSearch {

    public static Component searchInfoTooltipText = Component.translatable("container.search.tooltip_header").append("\n\n")
        .append(Component.translatable("container.search.tooltip_@")).append("\n")
        .append(Component.translatable("container.search.tooltip_#")).append("\n")
        .append(Component.translatable("container.search.tooltip_$")).append("\n")
        .append(Component.translatable("container.search.tooltip_%")).append("\n")
        .append(Component.translatable("container.search.tooltip_&")).append("\n")
        .append(Component.translatable("container.search.tooltip_-"));

    public static boolean doesStackContainString(Minecraft client, String search, ItemStack stack) {
        // Empty strings & stacks are ignored
        if (search.isBlank() || stack.isEmpty()) {
            return false;
        }

        // Split search string by space unless double quotes are used (https://stackoverflow.com/a/7804472)
        Map<String, Boolean> terms = new HashMap<>();
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(search);
        while (matcher.find()) {
            terms.put(matcher.group(1).replace("\"", ""), false);
        }

        // Iterate through the search terms and update the map
        terms.replaceAll((term, value) -> handleSearchTermsWithOperators(client, term, stack));

        // Return true only if all map values are true
        for (boolean value : terms.values()) {
            if (!value) {
                return false;
            }
        }
        return true;
    }

    private static boolean handleSearchTermsWithOperators(Minecraft client, String term, ItemStack stack) {
        // Empty strings & stacks are ignored
        if (term.isBlank() || stack.isEmpty()) {
            return false;
        }

        // If the term starts with "-", remove the first character and negate the result when returned
        boolean negate = false;
        if (term.startsWith("-")) {
            negate = true;
            term = term.substring(1).toLowerCase();
            if (term.isBlank()) {
                return false;
            }
        }

        // If the first character is an operator (@, #, &, $, or %):
        if (term.startsWith("@") || term.startsWith("#") || term.startsWith("&") || term.startsWith("$") || term.startsWith("%")) {
            // Get the term without the operator
            String searchTerm = term.substring(1).toLowerCase();
            if (searchTerm.isBlank()) {
                return false;
            }

            // If the operator is "@", search by mod ID
            if (term.startsWith("@")) {
                for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                    if (mod.getMetadata().getId().contains(searchTerm) && stack.getItem().getCreatorNamespace(stack).equalsIgnoreCase(mod.getMetadata().getId())) {
                        return !negate;
                    }
                }
                return negate;
            }

            // If the operator is "#", search by tooltip
            else if (term.startsWith("#")) {
                return negate != stack.getTooltipLines(Item.TooltipContext.EMPTY, client == null ? null : client.player, TooltipFlag.ADVANCED).stream().anyMatch(text -> text.getString().toLowerCase().contains(searchTerm));
            }

            // If the operator is "&", search by item ID
            else if (term.startsWith("&")) {
                return negate != stack.getItem().getDescriptionId().toLowerCase().contains(searchTerm);
            }

            // If the operator is "$", search by tag
            else if (term.startsWith("$")) {
                // Search block tags
                if (stack.getItem() instanceof BlockItem blockItem) {
                    for (TagKey<Block> tag : blockItem.getBlock().defaultBlockState().tags().toList()) {
                        if (tag.toString().contains(searchTerm)) {
                            return !negate;
                        }
                    }
                }

                // Search item tags
                for (TagKey<Item> tag : stack.tags().toList()) {
                    if (tag.toString().contains(searchTerm)) {
                        return !negate;
                    }
                }

                return negate;
            }

            // If the operator is "%", search by creative tab
            else if (term.startsWith("%")) {
                for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
                    if (group.getDisplayName().getString().toLowerCase().contains(searchTerm) && group.contains(new ItemStack(stack.getItem()))) {
                        return !negate;
                    }
                }
                return negate;
            }
        }

        // If the search term has no operator, search by item name and by stack name
        return negate != (stack.getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY).getString().toLowerCase().contains(term.toLowerCase()) || stack.getItem().getDefaultInstance().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY).getString().toLowerCase().contains(term.toLowerCase()));
    }

}
