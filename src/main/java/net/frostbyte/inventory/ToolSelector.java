package net.frostbyte.inventory;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ToolSelector {
    static int ticksSinceMiningStarted = 0;
    static Block currentlyMiningBlock = Blocks.AIR;
    
    public static ArrayList<Block> SHEARS_MINEABLE = Lists.newArrayList(
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
    );

    @SuppressWarnings("DataFlowIssue")
    static double getAttackDamageOfItemInSlot(int itemSlot) {
        String damageString = MinecraftClient.getInstance().player.getInventory().getStack(itemSlot)
            .getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(EntityAttributes.GENERIC_ATTACK_DAMAGE).toString()
            .replaceFirst(".*?amount=([0-9]+\\.[0-9]+).*", "$1");
        if(damageString.matches("[0-9]+\\.[0-9]+")){
            return 1.0F + Float.parseFloat(damageString);
        } else {
            return 1.0F;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    static double getAttackSpeedOfItemInSlot(int itemSlot) {
        String speedString = MinecraftClient.getInstance().player.getInventory().getStack(itemSlot)
            .getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(EntityAttributes.GENERIC_ATTACK_SPEED).toString()
            .replaceFirst(".*?amount=-([0-9]+\\.[0-9]+).*", "$1");
        if(speedString.matches("[0-9]+\\.[0-9]+")){
            return 4.0F - Float.parseFloat(speedString);
        } else {
            return 4.0F;
        }
    }

    public static boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        if (stack.isOf(Items.SHEARS) && SHEARS_MINEABLE.contains(state.getBlock())) {
            return true;
        }
        return stack.isSuitableFor(state);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void toolSelectHandler(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (client.options.attackKey.isPressed() && !ImprovedInventoryConfig.toolSelectBlacklist.contains(player.getMainHandStack().getItem().getDefaultStack().getItem()) && !player.isSpectator() && !player.isCreative() && ImprovedInventoryConfig.toolSelect) {
            HitResult target = client.crosshairTarget;
            assert target != null;
            if (target.getType() == HitResult.Type.ENTITY) {
                if (((EntityHitResult) target).getEntity() instanceof ItemFrameEntity) {
                    return;
                }
                if (((EntityHitResult) target).getEntity() instanceof AbstractMinecartEntity) {
                    float fastestBreak = player.getInventory().getStack(player.getInventory().selectedSlot).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(player.getInventory().selectedSlot), Blocks.STONE.getDefaultState());
                    int fastestBreakSlot = player.getInventory().selectedSlot;
                    for (int i = 0; i < 9; i++) {
                        if (player.getInventory().getStack(i).isIn(ItemTags.PICKAXES) && player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), Blocks.STONE.getDefaultState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), Blocks.STONE.getDefaultState());
                            fastestBreakSlot = i;
                        }
                    }
                    client.player.getInventory().selectedSlot = fastestBreakSlot;
                    player.getInventory().markDirty();
                    return;
                }
                if (((EntityHitResult) target).getEntity() instanceof FallingBlockEntity fallingBlock) {
                    float fastestBreak = player.getInventory().getStack(player.getInventory().selectedSlot).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(player.getInventory().selectedSlot), fallingBlock.getBlockState());
                    int fastestBreakSlot = player.getInventory().selectedSlot;
                    for (int i = 0; i < 9; i++) {
                        if (isCorrectForDrops(player.getInventory().getStack(i), fallingBlock.getBlockState()) && player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), fallingBlock.getBlockState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), fallingBlock.getBlockState());
                            fastestBreakSlot = i;
                        }
                    }
                    client.player.getInventory().selectedSlot = fastestBreakSlot;
                    player.getInventory().markDirty();
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
                    BlockState blockState = client.world.getBlockState(((BlockHitResult) target).getBlockPos());
                    if (!currentlyMiningBlock.equals(blockState.getBlock())) {
                        currentlyMiningBlock = blockState.getBlock();
                        ticksSinceMiningStarted = 0;
                    } else {
                        if (SHEARS_MINEABLE.contains(blockState.getBlock())) {
                            for (int i = 0; i < 9; i++) {
                                if (player.getInventory().getStack(i).isOf(Items.SHEARS)) {
                                    client.player.getInventory().selectedSlot = i;
                                    player.getInventory().markDirty();
                                    return;
                                }
                            }
                        }
                        float fastestBreak = player.getInventory().getStack(player.getInventory().selectedSlot).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(player.getInventory().selectedSlot), blockState);
                        int fastestBreakSlot = player.getInventory().selectedSlot;
                        for (int i = 0; i < 9; i++) {
                            if (isCorrectForDrops(player.getInventory().getStack(i), blockState) && player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), blockState) > fastestBreak) {
                                fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), blockState);
                                fastestBreakSlot = i;
                            }
                        }
                        client.player.getInventory().selectedSlot = fastestBreakSlot;
                        player.getInventory().markDirty();
                    }
                }
            }
        } else {
            ticksSinceMiningStarted = 0;
            currentlyMiningBlock = Blocks.AIR;
        }
    }

}
