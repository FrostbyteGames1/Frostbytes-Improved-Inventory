package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.tags.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class ToolSelector {
    static int ticksSinceMiningStarted = 0;
    static Block currentlyMiningBlock = Blocks.AIR;

    static double getAttackDamageOfItemInSlot(int itemSlot) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return 0;
        }
        ItemAttributeModifiers component = client.player.getInventory().getItem(itemSlot).getComponents().get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (component != null) {
            return component.compute(Attributes.ATTACK_DAMAGE, client.player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), EquipmentSlot.MAINHAND);
        }
        return client.player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
    }

    static double getAttackSpeedOfItemInSlot(int itemSlot) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return 0;
        }
        ItemAttributeModifiers component = client.player.getInventory().getItem(itemSlot).getComponents().get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (component != null) {
            return component.compute(Attributes.ATTACK_SPEED, client.player.getAttributeBaseValue(Attributes.ATTACK_SPEED), EquipmentSlot.MAINHAND);
        }
        return client.player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
    }

    public static boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        Tool tool = stack.get(DataComponents.TOOL);
        return tool != null && tool.isCorrectForDrops(state);
    }

    public static void toolSelectHandler(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.getCameraEntity() == null || client.level == null) {
            return;
        }
        if (client.options.keyAttack.isDown() && !ImprovedInventoryConfig.toolSelectBlacklist.contains(player.getInventory().getSelectedItem().getItem().getDefaultInstance().getItem()) && !player.isSpectator() && !player.isCreative() && ImprovedInventoryConfig.toolSelect) {
            BlockHitResult target = (BlockHitResult) client.getCameraEntity().pick(client.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, false);
            if (client.crosshairPickEntity != null) {
                if (client.crosshairPickEntity instanceof ItemFrame || player.getInventory().getSelectedItem().is(Items.MACE)) {
                    return;
                }
                if (client.crosshairPickEntity instanceof AbstractMinecart) {
                    float fastestBreak = player.getInventory().getItem(player.getInventory().getSelectedSlot()).getItem().getDestroySpeed(player.getInventory().getItem(player.getInventory().getSelectedSlot()), Blocks.STONE.defaultBlockState());
                    int fastestBreakSlot = player.getInventory().getSelectedSlot();
                    for (int i = 0; i < 9; i++) {
                        if (player.getInventory().getItem(i).is(ItemTags.PICKAXES) && player.getInventory().getItem(i).getItem().getDestroySpeed(player.getInventory().getItem(i), Blocks.STONE.defaultBlockState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getItem(i).getItem().getDestroySpeed(player.getInventory().getItem(i), Blocks.STONE.defaultBlockState());
                            fastestBreakSlot = i;
                        }
                    }
                    client.player.getInventory().setSelectedSlot(fastestBreakSlot);
            client.player.getInventory().setChanged();
            client.player.inventoryMenu.broadcastChanges();
                    return;
                }
                if (client.crosshairPickEntity instanceof FallingBlockEntity fallingBlock) {
                    float fastestBreak = player.getInventory().getItem(player.getInventory().getSelectedSlot()).getItem().getDestroySpeed(player.getInventory().getItem(player.getInventory().getSelectedSlot()), fallingBlock.getBlockState());
                    int fastestBreakSlot = player.getInventory().getSelectedSlot();
                    for (int i = 0; i < 9; i++) {
                        if (isCorrectForDrops(player.getInventory().getItem(i), fallingBlock.getBlockState()) && player.getInventory().getItem(i).getItem().getDestroySpeed(player.getInventory().getItem(i), fallingBlock.getBlockState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getItem(i).getItem().getDestroySpeed(player.getInventory().getItem(i), fallingBlock.getBlockState());
                            fastestBreakSlot = i;
                        }
                    }
                    client.player.getInventory().setSelectedSlot(fastestBreakSlot);
            client.player.getInventory().setChanged();
            client.player.inventoryMenu.broadcastChanges();
                    return;
                }
                double maxDPS = getAttackDamageOfItemInSlot(player.getInventory().getSelectedSlot());
                if (ImprovedInventoryConfig.weaponSelectPreference) {
                    maxDPS *= getAttackSpeedOfItemInSlot(player.getInventory().getSelectedSlot());
                }
                int maxDamageSlot = player.getInventory().getSelectedSlot();
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
                player.getInventory().setSelectedSlot(maxDamageSlot);
            } else if (target.getType() == HitResult.Type.BLOCK) {
                if (ticksSinceMiningStarted < 2) {
                    ticksSinceMiningStarted++;
                } else {
                    BlockState blockState = client.level.getBlockState(target.getBlockPos());
                    if (!currentlyMiningBlock.equals(blockState.getBlock())) {
                        currentlyMiningBlock = blockState.getBlock();
                        ticksSinceMiningStarted = 0;
                    } else {
                        if (blockState.is(ModTags.SHEARS_MINEABLE)) {
                            for (int i = 0; i < 9; i++) {
                                if (player.getInventory().getItem(i).is(Items.SHEARS)) {
                                    client.player.getInventory().setSelectedSlot(i);
                                    client.player.getInventory().setChanged();
                                    client.player.inventoryMenu.broadcastChanges();
                                    return;
                                }
                            }
                        }
                        float fastestBreak = player.getInventory().getItem(player.getInventory().getSelectedSlot()).getItem().getDestroySpeed(player.getInventory().getItem(player.getInventory().getSelectedSlot()), blockState);
                        int fastestBreakSlot = player.getInventory().getSelectedSlot();
                        for (int i = 0; i < 9; i++) {
                            if (isCorrectForDrops(player.getInventory().getItem(i), blockState) && player.getInventory().getItem(i).getItem().getDestroySpeed(player.getInventory().getItem(i), blockState) > fastestBreak) {
                                fastestBreak = player.getInventory().getItem(i).getItem().getDestroySpeed(player.getInventory().getItem(i), blockState);
                                fastestBreakSlot = i;
                            }
                        }
                        client.player.getInventory().setSelectedSlot(fastestBreakSlot);
                        client.player.getInventory().setChanged();
                        client.player.inventoryMenu.broadcastChanges();
                    }
                }
            }
        } else {
            ticksSinceMiningStarted = 0;
            currentlyMiningBlock = Blocks.AIR;
        }
    }

}
