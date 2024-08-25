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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class ToolSelector implements ClientTickEvents.EndTick{
    MinecraftClient mc;
    int ticksSinceMiningStarted = 0;
    Block currentlyMiningBlock = Blocks.AIR;

    public static final ArrayList<Block> SHEARS_MINEABLE = new ArrayList<>(Arrays.asList(
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
            Blocks.GRASS,
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
            Blocks.WHITE_CARPET
    ));

    public float getAttackDamageOfItemInSlot(int itemSlot) {
        assert mc.player != null;
        String damageString = mc.player.getInventory().getStack(itemSlot)
            .getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(EntityAttributes.GENERIC_ATTACK_DAMAGE).toString()
            .replaceFirst(".*?amount=([0-9]+\\.[0-9]+).*", "$1");
        if(damageString.matches("[0-9]+\\.[0-9]+")){
            return 1.0F + Float.parseFloat(damageString);
        } else {
            return 1.0F;
        }
    }

    public float getAttackSpeedOfItemInSlot(int itemSlot) {
        assert mc.player != null;
        String speedString = mc.player.getInventory().getStack(itemSlot)
            .getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(EntityAttributes.GENERIC_ATTACK_SPEED).toString()
            .replaceFirst(".*?amount=-([0-9]+\\.[0-9]+).*", "$1");
        if(speedString.matches("[0-9]+\\.[0-9]+")){
            return 4.0F - Float.parseFloat(speedString);
        } else {
            return 4.0F;
        }
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;

        if (player == null) {
            return;
        }

        if (mc.options.attackKey.isPressed() && !ImprovedInventoryConfig.toolSelectBlacklist.contains(player.getMainHandStack().getItem().getDefaultStack().getItem()) && !player.isSpectator() && !player.isCreative() && ImprovedInventoryConfig.toolSelect) {
            HitResult target = mc.crosshairTarget;
            assert target != null;
            if (target.getType() == HitResult.Type.ENTITY) {
                double maxDPS = getAttackDamageOfItemInSlot(player.getInventory().selectedSlot) * getAttackSpeedOfItemInSlot(player.getInventory().selectedSlot);
                int maxDamageSlot = player.getInventory().selectedSlot;
                for (int i = 0; i < 9; i++) {
                    if (maxDPS < getAttackDamageOfItemInSlot(i) * getAttackSpeedOfItemInSlot(i)) {
                        maxDPS = getAttackDamageOfItemInSlot(i) * getAttackSpeedOfItemInSlot(i);
                        maxDamageSlot = i;
                    }
                }
                player.getInventory().selectedSlot = maxDamageSlot;
            } else if (target.getType() == HitResult.Type.BLOCK) {
                if (ticksSinceMiningStarted < 2) {
                    ticksSinceMiningStarted++;
                } else {
                    BlockPos blockPos = ((BlockHitResult) target).getBlockPos();
                    assert mc.world != null;
                    BlockState blockState = mc.world.getBlockState(((BlockHitResult) target).getBlockPos());
                    if (!currentlyMiningBlock.equals(blockState.getBlock())) {
                        currentlyMiningBlock = blockState.getBlock();
                        ticksSinceMiningStarted = 0;
                    } else {
                        for (Block block : SHEARS_MINEABLE) {
                            if (blockState.getBlock().getDefaultState().equals(block.getDefaultState())) {
                                for (int i = 0; i < 9; i++) {
                                    if (player.getInventory().getStack(i).isOf(Items.SHEARS)) {
                                        assert mc.player != null;
                                        mc.player.getInventory().selectedSlot = i;
                                        return;
                                    }
                                }
                            }
                        }
                        int slot = player.getInventory().selectedSlot;
                        float fastestBreak = player.getInventory().getStack(slot).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(slot), blockState);
                        int fastestBreakSlot = player.getInventory().selectedSlot;
                        for (int i = 0; i < 9; i++) {
                            assert mc.player != null;
                            if (player.getInventory().getStack(i).getItem().canMine(blockState, mc.world, blockPos, player) && player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), blockState) > fastestBreak) {
                                fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), blockState);
                                fastestBreakSlot = i;
                            }
                        }
                        if (!player.getInventory().getStack(fastestBreakSlot).getItem().canMine(blockState, mc.world, blockPos, player) && player.getInventory().getStack(fastestBreakSlot).getItem().isDamageable()) {
                            if (!player.getInventory().getMainHandStack().getItem().isDamageable()) {
                                fastestBreakSlot = player.getInventory().selectedSlot;
                            } else {
                                for (int i = 0; i < 9; i++) {
                                    if (!player.getInventory().getStack(fastestBreakSlot).getItem().isDamageable()) {
                                        fastestBreakSlot = i;
                                    }
                                }
                            }
                        }
                        assert mc.player != null;
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
