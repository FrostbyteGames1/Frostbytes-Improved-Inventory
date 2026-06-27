package net.frostbyte.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.frostbyte.inventory.tags.ModTags;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class NearbyContainerViewer {
    public static KeyMapping containerKey;
    public static ArrayList<Vec3i> containers = new ArrayList<>();
    public static int current = 0;
    static int tabButtonCooldown;
    public void setKeyMappings() {
        KeyMappingHelper.registerKeyMapping(containerKey = new KeyMapping("key.next_container", InputConstants.Type.KEYSYM, InputConstants.KEY_TAB, ImprovedInventory.KEYBIND_CATEGORY));
    }


    public static void nearbyContainerViewerHandler(Minecraft client) {
        if (client.player == null) {
            return;
        }
        if (tabButtonCooldown > 0) {
            tabButtonCooldown--;
        }
        if (client.screen instanceof AbstractContainerScreen<?> && !(client.screen instanceof CreativeModeInventoryScreen)) {
            if (containers.isEmpty()) {
                return;
            }
            int keyCode = KeyMappingHelper.getBoundKeyOf(containerKey).getValue();
            if (InputConstants.isKeyDown(client.getWindow(), keyCode)) {
                if (InputConstants.isKeyDown(client.getWindow(), KeyEvent.VK_SHIFT)) {
                    current--;
                    if (current < 0) {
                        current = containers.size() - 1;
                    }
                } else {
                    current++;
                    if (current > containers.size() - 1) {
                        current = 0;
                    }
                }
                openContainer(current);
                tabButtonCooldown = 6;
            }
        } else {
            updateContainerList();
            current = 0;
            if (!containers.isEmpty() && client.options.keyUse.isDown() && client.getCameraEntity() != null && client.getCameraEntity().pick(client.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, false).getType() == HitResult.Type.BLOCK) {
                BlockHitResult target = (BlockHitResult) client.getCameraEntity().pick(client.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, false);
                double d = target.distanceTo(client.player);
                int closest = 0;
                for (int i = 0; i < containers.size(); i++) {
                    if (target.getBlockPos().distChessboard(new BlockPos(containers.get(i))) < d) {
                        d = target.getBlockPos().distChessboard(new BlockPos(containers.get(i)));
                        closest = i;
                    }
                }
                current = closest;
            }
        }
    }

    private static <T> List<T> getAttachedBlocks(Level world, BlockPos pos, BiFunction<Level, BlockPos, T> mapper) {
        List<T> outList = new ArrayList<>();
        for (Direction direction : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            BlockPos attachedPos = pos.offset(direction.getUnitVec3i());
            BlockState attachedState = world.getBlockState(attachedPos);
            if (attachedState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && attachedState.getValue(BlockStateProperties.HORIZONTAL_FACING) == direction) {
                T mappedValue = mapper.apply(world, attachedPos);
                if (mappedValue != null) outList.add(mappedValue);
            }
        }
        return outList;
    }

    public static Component getDisplayName(Vec3i blockPos) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) {
            return Component.literal("");
        }

        Component name;

        // Get name of block
        if (client.level.getBlockEntity(new BlockPos(blockPos)) instanceof MenuProvider menuProvider) {
            name = menuProvider.getDisplayName();
        } else {
            name = client.level.getBlockState(new BlockPos(blockPos)).getBlock().getName();
        }

        // Get text from sign
        List<SignBlockEntity> signs = getAttachedBlocks(client.level, new BlockPos(blockPos), (w, p) -> w.getBlockEntity(p) instanceof SignBlockEntity sbe ? sbe : null);
        if (!signs.isEmpty()) {
            name = Arrays.stream(signs.getFirst().getFrontText().getMessages(false)).map(Component::getString).filter(s -> !s.isBlank()).collect(Collectors.joining("\n")).isBlank() ? name : Component.literal(Arrays.stream(signs.getFirst().getFrontText().getMessages(false)).map(Component::getString).filter(s -> !s.isBlank()).collect(Collectors.joining("\n")));
        }

        // Get name of item in item frame
        List<ItemFrame> itemFrames = client.level.getEntitiesOfClass(ItemFrame.class, new AABB(new BlockPos(blockPos).getCenter(), new BlockPos(blockPos).getCenter()).expandTowards(0.55, 0.55, 0.55));
        if (!itemFrames.isEmpty()) {
            name = itemFrames.getFirst().getItem().getHoverName();
        }

        return name;
    }

    public static ItemStack getDisplayStack(Vec3i blockPos) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack;

        // Get container stack
        stack = new ItemStack(client.level.getBlockState(new BlockPos(blockPos)).getBlock());

        // Get item in item frame
        List<ItemFrame> itemFrames = client.level.getEntitiesOfClass(ItemFrame.class, new AABB(new BlockPos(blockPos).getCenter(), new BlockPos(blockPos).getCenter()).expandTowards(0.55, 0.55, 0.55));
        if (!itemFrames.isEmpty()) {
            stack = itemFrames.getFirst().getItem();
        }

        return stack;
    }

    public static void updateContainerList() {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null) {
            containers.clear();
            double reach = client.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
            AABB box = new AABB(client.player.getOnPos()).inflate(reach);
            for (BlockPos blockPos : BlockPos.betweenClosed((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ)) {
                boolean blacklisted = ImprovedInventoryConfig.containerTabBlacklist.contains(client.level.getBlockState(blockPos).getBlock().asItem());
                if (!blacklisted && client.level.getBlockEntity(blockPos) instanceof BaseContainerBlockEntity container && container.canOpen(client.player) && !containers.contains(blockPos)) {
                    findContainers(client, blockPos);
                }
                if (!blacklisted && client.level.getBlockState(blockPos).is(ModTags.HAS_GUI)) {
                    findContainers(client, blockPos);
                }
            }
            containers.sort(Comparator.comparingDouble(a -> a.distSqr(new Vec3i((int) client.player.getX(), (int) client.player.getY(), (int) client.player.getZ()))));
        }
    }

    private static void findContainers(Minecraft client, BlockPos blockPos) {
        if (client.level != null && !containers.contains(blockPos)) {
            if (client.level.getBlockState(blockPos).hasProperty(BlockStateProperties.CHEST_TYPE)) {
                if (client.level.getBlockState(blockPos).getValue(BlockStateProperties.CHEST_TYPE).equals(ChestType.LEFT)) {
                    if (!containers.contains(blockPos.offset(client.level.getBlockState(blockPos).getValue(ChestBlock.FACING).getClockWise(Direction.Axis.Y).getUnitVec3i()))) {
                        containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    }
                } else if (client.level.getBlockState(blockPos).getValue(BlockStateProperties.CHEST_TYPE).equals(ChestType.RIGHT)) {
                    if (!containers.contains(blockPos.offset(client.level.getBlockState(blockPos).getValue(ChestBlock.FACING).getCounterClockWise(Direction.Axis.Y).getUnitVec3i()))) {
                        containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    }
                } else {
                    containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                }
            } else {
                containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            }
        }
    }

    public static void openContainer(int container) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.gameMode == null) {
            return;
        }
        if (container > containers.size() - 1) {
            container = 0;
        }
        if (container < 0) {
            container = containers.size() - 1;
        }
        current = container;
        Vec3i targetPos = containers.get(current);
        client.player.lookAt(EntityAnchorArgument.Anchor.EYES, new BlockPos(targetPos).getCenter());
        client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(targetPos), Direction.EAST, new BlockPos(targetPos), false));
    }

}
