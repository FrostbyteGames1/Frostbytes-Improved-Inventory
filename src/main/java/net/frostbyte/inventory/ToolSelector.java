package net.frostbyte.inventory;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ToolSelector implements ClientTickEvents.EndTick{

    public KeyBinding toggleAutoSwitch;
    public boolean autoSwitch = true;
    public static boolean firstRun = true;
    MinecraftClient mc;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(toggleAutoSwitch = new KeyBinding("Toggle Tool Auto Switch", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_M, "Improved Inventory"));
    }

    public void processKeyBinds() {
        if (toggleAutoSwitch.wasPressed()) {
            autoSwitch = !autoSwitch;
            message();
        }
    }
    
    public float getAttackDamageOfItemInSlot(int itemSlot) {
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

        if (firstRun) {
            message();
            firstRun = false;
        }

        processKeyBinds();

        if (mc.options.attackKey.isPressed() && !player.isSpectator() && !player.isCreative() && autoSwitch) {
            HitResult target = mc.crosshairTarget;
            if (target.getType() == HitResult.Type.ENTITY) {
                float maxDPS = getAttackDamageOfItemInSlot(player.getInventory().selectedSlot) * getAttackSpeedOfItemInSlot(player.getInventory().selectedSlot);
                int maxDamageSlot = player.getInventory().selectedSlot;
                for (int i = 0; i < 9; i++) {
                    mc.player.sendMessage(Text.of("[" + (i + 1) + "] " + mc.player.getInventory().getStack(i).getItem() + ": " + getAttackSpeedOfItemInSlot(i) + " * " + getAttackDamageOfItemInSlot(i) + " = " + getAttackSpeedOfItemInSlot(i) * getAttackDamageOfItemInSlot(i)));
                    if (maxDPS < getAttackDamageOfItemInSlot(i) * getAttackSpeedOfItemInSlot(i)) {
                        maxDPS = getAttackDamageOfItemInSlot(i) * getAttackSpeedOfItemInSlot(i);
                        maxDamageSlot = i;
                    }
                }
                player.getInventory().selectedSlot = maxDamageSlot;
            } else if (target.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) target).getBlockPos();
                BlockState blockState = mc.world.getBlockState(((BlockHitResult) target).getBlockPos());
                int slot = player.getInventory().selectedSlot;
                float fastestBreak = player.getInventory().getStack(slot).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(slot), blockState);
                int fastestBreakSlot = player.getInventory().selectedSlot;
                for (int i = 0; i < 9; i++) {
                    if (player.getInventory().getStack(i).getItem().canMine(blockState, mc.world, blockPos, player) && player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), blockState) > fastestBreak) {
                        fastestBreak = player.getInventory().getStack(i).getItem().getMiningSpeedMultiplier(player.getInventory().getStack(i), blockState);
                        fastestBreakSlot = i;
                    }
                }
                if (!player.getInventory().getStack(fastestBreakSlot).getItem().isSuitableFor(blockState) && player.getInventory().getStack(fastestBreakSlot).getItem().isDamageable()) {
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
                mc.player.getInventory().selectedSlot = fastestBreakSlot;
            }
        }

        player.getInventory().markDirty();
    }

    private void message() {
        String m = "[" + Formatting.GOLD + "Improved Inventory" + Formatting.WHITE + "] " + "Tool Switch Switch: ";
        if (autoSwitch) {
            m = m + Formatting.GREEN + "Active";
        }else {
            m = m + Formatting.RED + "Inactive";
        }
        mc.player.sendMessage(Text.literal(m), false);
    }

}
