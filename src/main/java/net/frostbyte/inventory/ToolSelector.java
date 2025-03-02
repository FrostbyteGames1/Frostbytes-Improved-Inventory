package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class ToolSelector implements ClientTickEvents.EndTick{
    MinecraftClient mc;
    int ticksSinceMiningStarted = 0;
    Block currentlyMiningBlock = Blocks.AIR;

    static final ArrayList<Block> SHEARS_MINEABLE = new ArrayList<>(Arrays.asList(
        Blocks.COBWEB,
        Blocks.DEAD_BUSH,
        Blocks.FERN,
        Blocks.GLOW_LICHEN,
        Blocks.HANGING_ROOTS,
        Blocks.LARGE_FERN,
        Blocks.ACACIA_LEAVES,
        Blocks.BIRCH_LEAVES,
        Blocks.CHERRY_LEAVES,
        Blocks.AZALEA_LEAVES,
        Blocks.JUNGLE_LEAVES,
        Blocks.DARK_OAK_LEAVES,
        Blocks.FLOWERING_AZALEA_LEAVES,
        Blocks.MANGROVE_LEAVES,
        Blocks.OAK_LEAVES,
        Blocks.SPRUCE_LEAVES,
        Blocks.NETHER_SPROUTS,
        Blocks.SEAGRASS,
        Blocks.SHORT_GRASS,
        Blocks.TALL_GRASS,
        Blocks.TALL_SEAGRASS,
        Blocks.TRIPWIRE,
        Blocks.TWISTING_VINES,
        Blocks.TWISTING_VINES_PLANT,
        Blocks.VINE,
        Blocks.WEEPING_VINES,
        Blocks.WEEPING_VINES_PLANT,
        Blocks.RED_WOOL,
        Blocks.ORANGE_WOOL,
        Blocks.YELLOW_WOOL,
        Blocks.LIME_WOOL,
        Blocks.GREEN_WOOL,
        Blocks.CYAN_WOOL,
        Blocks.LIGHT_BLUE_WOOL,
        Blocks.BLUE_WOOL,
        Blocks.PURPLE_WOOL,
        Blocks.MAGENTA_WOOL,
        Blocks.PINK_WOOL,
        Blocks.BROWN_WOOL,
        Blocks.BLACK_WOOL,
        Blocks.GRAY_WOOL,
        Blocks.LIGHT_GRAY_WOOL,
        Blocks.WHITE_WOOL,
        Blocks.RED_CARPET,
        Blocks.ORANGE_CARPET,
        Blocks.YELLOW_CARPET,
        Blocks.LIME_CARPET,
        Blocks.GREEN_CARPET,
        Blocks.CYAN_CARPET,
        Blocks.LIGHT_BLUE_CARPET,
        Blocks.BLUE_CARPET,
        Blocks.PURPLE_CARPET,
        Blocks.MAGENTA_CARPET,
        Blocks.PINK_CARPET,
        Blocks.BROWN_CARPET,
        Blocks.BLACK_CARPET,
        Blocks.GRAY_CARPET,
        Blocks.LIGHT_GRAY_CARPET,
        Blocks.WHITE_CARPET,
        Blocks.PALE_HANGING_MOSS,
        Blocks.PALE_OAK_LEAVES
    ));

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    double getAttackDamageOfItemInSlot(int itemSlot) {
        assert mc.player != null;
        AttributeModifiersComponent component = mc.player.getInventory().getStack(itemSlot).getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        double modifier = 0.0F;
        if (component != null && component.modifiers() != null && !component.modifiers().isEmpty()) {
            for (AttributeModifiersComponent.Entry entry : component.modifiers()) {
                if (entry.attribute().getKey().get().getValue().toString().equals("minecraft:attack_damage")) {
                    modifier = entry.modifier().value();
                    break;
                }
            }
        }
        return mc.player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE) + modifier;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    double getAttackSpeedOfItemInSlot(int itemSlot) {
        assert mc.player != null;
        AttributeModifiersComponent component = mc.player.getInventory().getStack(itemSlot).getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        double modifier = 0.0F;
        if (component != null && component.modifiers() != null && !component.modifiers().isEmpty()) {
            for (AttributeModifiersComponent.Entry entry : component.modifiers()) {
                if (entry.attribute().getKey().get().getValue().toString().equals("minecraft:attack_speed")) {
                    modifier = entry.modifier().value();
                    break;
                }
            }
        }
        return mc.player.getAttributeBaseValue(EntityAttributes.ATTACK_SPEED) + modifier;
    }

    public static boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
        return toolComponent != null && toolComponent.isCorrectForDrops(state);
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;
        assert mc.world != null;
        assert mc.player != null;

        if (player == null) {
            return;
        }

        if (mc.options.attackKey.isPressed() && (player.getMainHandStack().isEmpty() || !ImprovedInventoryConfig.toolSelectBlacklist.contains(player.getMainHandStack().getItem().getDefaultStack().getItem())) && !player.isSpectator() && !player.isCreative() && ImprovedInventoryConfig.toolSelect) {
            HitResult target = mc.crosshairTarget;
            assert target != null;
            if (target.getType() == HitResult.Type.ENTITY) {
                if (((EntityHitResult) target).getEntity() instanceof ItemFrameEntity || player.getMainHandStack().isOf(Items.MACE)) {
                    return;
                }
                if (((EntityHitResult) target).getEntity() instanceof MinecartEntity) {
                    float fastestBreak = player.getInventory().getStack(player.getInventory().selectedSlot).getItem().getMiningSpeed(player.getInventory().getStack(player.getInventory().selectedSlot), Blocks.STONE.getDefaultState());
                    int fastestBreakSlot = player.getInventory().selectedSlot;
                    for (int i = 0; i < 9; i++) {
                        if (player.getInventory().getStack(i).getItem() instanceof PickaxeItem && player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), Blocks.STONE.getDefaultState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), Blocks.STONE.getDefaultState());
                            fastestBreakSlot = i;
                        }
                    }
                    mc.player.getInventory().selectedSlot = fastestBreakSlot;
                    return;
                }
                if (((EntityHitResult) target).getEntity() instanceof FallingBlockEntity fallingBlock) {
                    float fastestBreak = player.getInventory().getStack(player.getInventory().selectedSlot).getItem().getMiningSpeed(player.getInventory().getStack(player.getInventory().selectedSlot), fallingBlock.getBlockState());
                    int fastestBreakSlot = player.getInventory().selectedSlot;
                    for (int i = 0; i < 9; i++) {
                        if (isCorrectForDrops(player.getInventory().getStack(i), fallingBlock.getBlockState()) && player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), fallingBlock.getBlockState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), fallingBlock.getBlockState());
                            fastestBreakSlot = i;
                        }
                    }
                    mc.player.getInventory().selectedSlot = fastestBreakSlot;
                    return;
                }
                double maxDPS = getAttackDamageOfItemInSlot(player.getInventory().selectedSlot);
                if (ImprovedInventoryConfig.weaponSelectPreference) {
                    maxDPS *= getAttackSpeedOfItemInSlot(player.getInventory().selectedSlot);
                }
                int maxDamageSlot = player.getInventory().selectedSlot;
                for (int i = 0; i < 9; i++) {
                    if (ImprovedInventoryConfig.weaponSelectPreference) {
                        if (maxDPS < getAttackDamageOfItemInSlot(i) * getAttackSpeedOfItemInSlot(i)) {
                            maxDPS = getAttackDamageOfItemInSlot(i) * getAttackSpeedOfItemInSlot(i);
                            maxDamageSlot = i;
                        }
                    } else {
                        if (maxDPS < getAttackDamageOfItemInSlot(i)) {
                            maxDPS = getAttackDamageOfItemInSlot(i);
                            maxDamageSlot = i;
                        }
                    }
                }
                player.getInventory().selectedSlot = maxDamageSlot;
            } else if (target.getType() == HitResult.Type.BLOCK) {
                if (ticksSinceMiningStarted < 2) {
                    ticksSinceMiningStarted++;
                } else {
                    BlockState blockState = mc.world.getBlockState(((BlockHitResult) target).getBlockPos());
                    if (!currentlyMiningBlock.equals(blockState.getBlock())) {
                        currentlyMiningBlock = blockState.getBlock();
                        ticksSinceMiningStarted = 0;
                    } else {
                        for (Block block : SHEARS_MINEABLE) {
                            if (blockState.isOf(block)) {
                                for (int i = 0; i < 9; i++) {
                                    if (player.getInventory().getStack(i).isOf(Items.SHEARS)) {
                                        mc.player.getInventory().selectedSlot = i;
                                        return;
                                    }
                                }
                            }
                        }
                        float fastestBreak = player.getInventory().getStack(player.getInventory().selectedSlot).getItem().getMiningSpeed(player.getInventory().getStack(player.getInventory().selectedSlot), blockState);
                        int fastestBreakSlot = player.getInventory().selectedSlot;
                        for (int i = 0; i < 9; i++) {
                            if (isCorrectForDrops(player.getInventory().getStack(i), blockState) && player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), blockState) > fastestBreak) {
                                fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), blockState);
                                fastestBreakSlot = i;
                            }
                        }
                        mc.player.getInventory().selectedSlot = fastestBreakSlot;
                    }
                }
            }
        } else {
            ticksSinceMiningStarted = 0;
            currentlyMiningBlock = Blocks.AIR;
        }

        player.getInventory().markDirty();
    }

}
