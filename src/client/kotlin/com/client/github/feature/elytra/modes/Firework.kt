package com.client.github.feature.elytra.modes

import net.minecraft.util.math.Vec3d
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.FireworkRocketItem
import net.minecraft.util.Hand
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket

import com.client.github.feature.elytra.ElytraFlightMode

object Firework : ElytraFlightMode("Firework") {
    fun tickSafe(movementVector: Vec3d?) {
        if (movementVector != null) {
            adjustDirection(movementVector)
        }

        tick()
    }

    override fun tick(movementVector: Vec3d) {
        val player = mc.player ?: return
        val offhand = player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT)
        val item = offhand.getItem()
        val vel = player.getVelocity().length()

        if (
            item is FireworkRocketItem &&
            player.isFallFlying() &&
            vel < 1.67
        ) {
            mc.networkHandler?.sendPacket(
                PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0)
            )

            //player.swingHand(Hand.OFF_HAND)
        }
    }

    override fun tick() {
        val movementVector = getRawMovementVector()

        tick(movementVector ?: return)
    }
}
