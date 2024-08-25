package net.frostbyte.inventory;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.frostbyte.inventory.config.ImprovedInventoryConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class NearbyContainerViewer implements ClientTickEvents.EndTick {
    public static KeyBinding containerKey;
    public static ArrayList<Vec3i> containers = new ArrayList<>();
    public static int current = 0;
    public static boolean shouldCenterCursor = true;
    int tabButtonCooldown;
    public void setKeybindings() {
        KeyBindingHelper.registerKeyBinding(containerKey = new KeyBinding("Next Container (SHIFT for Previous)", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_TAB, "Improved Inventory"));
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        if (!ImprovedInventoryConfig.containerTab || client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (tabButtonCooldown > 0) {
            tabButtonCooldown--;
        }
        if (client.currentScreen instanceof HandledScreen<?> && !(client.currentScreen instanceof CreativeInventoryScreen)) {
            if (containers.isEmpty()) {
                return;
            }
            int keyCode = KeyBindingHelper.getBoundKeyOf(containerKey).getCode();
            if (((keyCode > 31 && GLFW.glfwGetKey(client.getWindow().getHandle(), keyCode) == 1) || (keyCode < 8 && GLFW.glfwGetMouseButton(client.getWindow().getHandle(), keyCode) == 1)) && tabButtonCooldown == 0) {
                if (Screen.hasShiftDown()) {
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
            if (!containers.isEmpty() && client.options.useKey.isPressed() && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                double d = client.crosshairTarget.getPos().distanceTo(new BlockPos(containers.get(0)).toCenterPos());
                int closest = 0;
                for (int i = 0; i < containers.size(); i++) {
                    if (client.crosshairTarget.getPos().distanceTo(new BlockPos(containers.get(i)).toCenterPos()) < d) {
                        d = client.crosshairTarget.getPos().distanceTo(new BlockPos(containers.get(i)).toCenterPos());
                        closest = i;
                    }
                }
                current = closest;
            }
            if (!shouldCenterCursor) {
                shouldCenterCursor = true;
                client.mouse.lockCursor();
            }
        }
    }

    private static <T> List<T> getAttachedBlocks(World world, BlockPos pos, BiFunction<World, BlockPos, T> mapper) {
        List<T> outList = new ArrayList<>();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!direction.getAxis().isHorizontal()) continue;
            BlockPos attachedPos = pos.offset(direction, 1);
            BlockState attachedState = world.getBlockState(attachedPos);
            if (attachedState.contains(Properties.HORIZONTAL_FACING) && attachedState.get(Properties.HORIZONTAL_FACING) == direction) {
                T mappedValue = mapper.apply(world, attachedPos);
                if (mappedValue != null) outList.add(mappedValue);
            }
        }
        return outList;
    }

    public static Text getDisplayName(Vec3i blockPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return Text.of("");
        }

        Text name;
        // Get name of block
        if (client.world.getBlockEntity(new BlockPos(blockPos)) instanceof NamedScreenHandlerFactory namedScreenHandlerFactory) {
            name = namedScreenHandlerFactory.getDisplayName();
        } else {
            name = client.world.getBlockState(new BlockPos(blockPos)).getBlock().getName();
        }

        // Get text from sign
        List<SignBlockEntity> signs = getAttachedBlocks(client.world, new BlockPos(blockPos), (w, p) -> w.getBlockEntity(p) instanceof SignBlockEntity sbe ? sbe : null);
        if (!signs.isEmpty()) {
            name = Arrays.stream(signs.get(0).getFrontText().getMessages(false)).map(Text::getString).filter(s -> !s.isBlank()).collect(Collectors.joining("\n")).isBlank() ? name : Text.of(Arrays.stream(signs.get(0).getFrontText().getMessages(false)).map(Text::getString).filter(s -> !s.isBlank()).collect(Collectors.joining("\n")));
        }

        // Get name of item in item frame
        List<ItemFrameEntity> itemFrames = client.world.getNonSpectatingEntities(ItemFrameEntity.class, new Box(new BlockPos(blockPos).toCenterPos(), new BlockPos(blockPos).toCenterPos()).expand(0.55, 0.55, 0.55));
        if (!itemFrames.isEmpty() && itemFrames.get(0).getHeldItemStack() != null) {
            if (itemFrames.get(0).getHeldItemStack().getComponents().contains(DataComponentTypes.CUSTOM_NAME)) {
                name = itemFrames.get(0).getHeldItemStack().getName();
            }
        }

        return name;
    }

    public static ItemStack getDisplayStack(Vec3i blockPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack;
        // Get container stack
        stack = new ItemStack(client.world.getBlockState(new BlockPos(blockPos)).getBlock());

        // Get item in item frame
        List<ItemFrameEntity> itemFrames = client.world.getNonSpectatingEntities(ItemFrameEntity.class, new Box(new BlockPos(blockPos).toCenterPos(), new BlockPos(blockPos).toCenterPos()).expand(0.55, 0.55, 0.55));
        if (!itemFrames.isEmpty() && itemFrames.get(0).getHeldItemStack() != null) {
            if (!itemFrames.get(0).getHeldItemStack().isEmpty()) {
                stack = itemFrames.get(0).getHeldItemStack();
            }
        }

        return stack;
    }

    public static void updateContainerList() {
        MinecraftClient client = MinecraftClient.getInstance();
        containers.clear();
        assert client.player != null;
        double reach = client.player.getBlockInteractionRange();
        List<Vec3d> blockOffsetVectors = List.of(
            new Vec3d(0.5D, 0.5D, 0.5D),
            new Vec3d(0.2D, 0.2D, 0.2D),
            new Vec3d(0.8D, 0.2D, 0.2D),
            new Vec3d(0.2D, 0.8D, 0.2D),
            new Vec3d(0.2D, 0.2D, 0.8D),
            new Vec3d(0.8D, 0.8D, 0.2D),
            new Vec3d(0.2D, 0.8D, 0.8D),
            new Vec3d(0.8D, 0.2D, 0.8D),
            new Vec3d(0.8D, 0.8D, 0.8D)
        );
        List<Block> blocksWithGUIs = List.of(
            Blocks.ANVIL,
            Blocks.BEACON,
            Blocks.CARTOGRAPHY_TABLE,
            Blocks.CHIPPED_ANVIL,
            Blocks.CRAFTING_TABLE,
            Blocks.DAMAGED_ANVIL,
            Blocks.ENDER_CHEST,
            Blocks.GRINDSTONE,
            Blocks.SMITHING_TABLE
        );
        for (BlockPos blockPos : BlockPos.iterate((int) (client.player.getX() - reach), (int) (client.player.getY() - reach), (int) (client.player.getZ() - reach), (int) (client.player.getX() + reach), (int) (client.player.getY() + reach), (int) (client.player.getZ() + reach))) {
            assert client.world != null;
            if (client.world.getBlockEntity(blockPos) instanceof LockableContainerBlockEntity lockableContainerBlockEntity && lockableContainerBlockEntity.canPlayerUse(client.player) && !containers.contains(blockPos)) {
                for (Vec3d blockOffsetVector : blockOffsetVectors) {
                    BlockHitResult hitResult = client.player.getWorld().raycast(new RaycastContext(client.player.getEyePos(), Vec3d.of(blockPos).add(blockOffsetVector), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, client.player));
                    if (hitResult.getBlockPos().equals(blockPos) && !containers.contains(blockPos)) {
                        if (client.world.getBlockState(blockPos).contains(Properties.CHEST_TYPE)) {
                            if (client.world.getBlockState(blockPos).get(Properties.CHEST_TYPE).equals(ChestType.LEFT)) {
                                if (!containers.contains(blockPos.offset(client.world.getBlockState(blockPos).get(ChestBlock.FACING).rotateYClockwise()))) {
                                    containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                                    break;
                                }
                            } else if (client.world.getBlockState(blockPos).get(Properties.CHEST_TYPE).equals(ChestType.RIGHT)) {
                                if (!containers.contains(blockPos.offset(client.world.getBlockState(blockPos).get(ChestBlock.FACING).rotateYCounterclockwise()))) {
                                    containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                                    break;
                                }
                            }
                        } else {
                            containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                            break;
                        }
                    }
                }
            }
            if (blocksWithGUIs.contains(client.world.getBlockState(blockPos).getBlock())) {
                for (Vec3d blockOffsetVector : blockOffsetVectors) {
                    BlockHitResult hitResult = client.player.getWorld().raycast(new RaycastContext(client.player.getEyePos(), Vec3d.of(blockPos).add(blockOffsetVector), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, client.player));
                    if (hitResult.getBlockPos().equals(blockPos) && !containers.contains(blockPos)) {
                        if (client.world.getBlockState(blockPos).contains(Properties.CHEST_TYPE)) {
                            if (client.world.getBlockState(blockPos).get(Properties.CHEST_TYPE).equals(ChestType.LEFT)) {
                                if (!containers.contains(blockPos.offset(client.world.getBlockState(blockPos).get(ChestBlock.FACING).rotateYClockwise()))) {
                                    containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                                    break;
                                }
                            } else if (client.world.getBlockState(blockPos).get(Properties.CHEST_TYPE).equals(ChestType.RIGHT)) {
                                if (!containers.contains(blockPos.offset(client.world.getBlockState(blockPos).get(ChestBlock.FACING).rotateYCounterclockwise()))) {
                                    containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                                    break;
                                }
                            }
                        } else {
                            containers.add(new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                            break;
                        }
                    }
                }
            }
        }
        containers.sort(Comparator.comparingDouble(a -> a.getSquaredDistance(client.player.getX(), client.player.getY(), client.player.getZ())));
    }

    public static void openContainer(int container) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.interactionManager == null || client.currentScreen == null) {
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
        client.player.currentScreenHandler.updateToClient();
        client.player.currentScreenHandler.sendContentUpdates();
        client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new BlockPos(targetPos).toCenterPos());
        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(targetPos), Direction.EAST, new BlockPos(targetPos), false));
        shouldCenterCursor = client.currentScreen == null;
    }

}
