package net.frostbyte.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.tags.ModTags;
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
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@Environment(EnvType.CLIENT)
public class ToolSelector implements ClientTickEvents.EndTick{
    MinecraftClient mc;
    int ticksSinceMiningStarted = 0;
    Block currentlyMiningBlock = Blocks.AIR;

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

        if (mc.options.attackKey.isPressed() && !ImprovedInventoryConfig.toolSelectBlacklist.contains(player.getMainHandStack().getItem().getDefaultStack().getItem()) && !player.isSpectator() && !player.isCreative() && ImprovedInventoryConfig.toolSelect) {
            HitResult target = mc.crosshairTarget;
            assert target != null;
            if (target.getType() == HitResult.Type.ENTITY) {
                if (((EntityHitResult) target).getEntity() instanceof ItemFrameEntity || player.getMainHandStack().isOf(Items.MACE)) {
                    return;
                }
                if (((EntityHitResult) target).getEntity() instanceof AbstractMinecartEntity) {
                    float fastestBreak = player.getInventory().getStack(player.getInventory().getSelectedSlot()).getItem().getMiningSpeed(player.getInventory().getStack(player.getInventory().getSelectedSlot()), Blocks.STONE.getDefaultState());
                    int fastestBreakSlot = player.getInventory().getSelectedSlot();
                    for (int i = 0; i < 9; i++) {
                        if (player.getInventory().getStack(i).isIn(ItemTags.PICKAXES) && player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), Blocks.STONE.getDefaultState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), Blocks.STONE.getDefaultState());
                            fastestBreakSlot = i;
                        }
                    }
                    mc.player.getInventory().setSelectedSlot(fastestBreakSlot);
                    return;
                }
                if (((EntityHitResult) target).getEntity() instanceof FallingBlockEntity fallingBlock) {
                    float fastestBreak = player.getInventory().getStack(player.getInventory().getSelectedSlot()).getItem().getMiningSpeed(player.getInventory().getStack(player.getInventory().getSelectedSlot()), fallingBlock.getBlockState());
                    int fastestBreakSlot = player.getInventory().getSelectedSlot();
                    for (int i = 0; i < 9; i++) {
                        if (isCorrectForDrops(player.getInventory().getStack(i), fallingBlock.getBlockState()) && player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), fallingBlock.getBlockState()) > fastestBreak) {
                            fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), fallingBlock.getBlockState());
                            fastestBreakSlot = i;
                        }
                    }
                    mc.player.getInventory().setSelectedSlot(fastestBreakSlot);
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
                    BlockState blockState = mc.world.getBlockState(((BlockHitResult) target).getBlockPos());
                    if (!currentlyMiningBlock.equals(blockState.getBlock())) {
                        currentlyMiningBlock = blockState.getBlock();
                        ticksSinceMiningStarted = 0;
                    } else {
                        if (blockState.isIn(ModTags.SHEARS_MINEABLE)) {
                            for (int i = 0; i < 9; i++) {
                                if (player.getInventory().getStack(i).isOf(Items.SHEARS)) {
                                    mc.player.getInventory().setSelectedSlot(i);
                                    return;
                                }
                            }
                        }
                        float fastestBreak = player.getInventory().getStack(player.getInventory().getSelectedSlot()).getItem().getMiningSpeed(player.getInventory().getStack(player.getInventory().getSelectedSlot()), blockState);
                        int fastestBreakSlot = player.getInventory().getSelectedSlot();
                        for (int i = 0; i < 9; i++) {
                            if (isCorrectForDrops(player.getInventory().getStack(i), blockState) && player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), blockState) > fastestBreak) {
                                fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeed(player.getInventory().getStack(i), blockState);
                                fastestBreakSlot = i;
                            }
                        }
                        mc.player.getInventory().setSelectedSlot(fastestBreakSlot);
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
