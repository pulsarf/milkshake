package com.client.github.feature.elytra

import com.client.github.feature.Module

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d

import kotlin.math.*

abstract class ElytraFlightMode(val name: String) {
    val mod = Module("Elytra", "Elytra flight: $name")
    val mc = MinecraftClient.getInstance()

    fun adjustDirection(movementVec: Vec3d) {
        val player = mc.player ?: return

        if (!player.isFallFlying()) return

        val camera = mc?.gameRenderer?.getCamera() ?: return

        val yaw = atan2(movementVec.z, movementVec.x)
        val pitch = atan2(movementVec.y, sqrt(movementVec.x * movementVec.x + movementVec.z * movementVec.z))

        val yawDeg = (yaw * 180f / PI - 90f).toFloat()
        val pitchDeg = (-pitch * 180 / PI).toFloat()

        player.setPitch(pitchDeg)
        player.setYaw(yawDeg)
        player.headYaw = yawDeg
        player.bodyYaw = yawDeg
    }

    fun getRawMovementVector(): Vec3d {
        var movementVec = Vec3d.ZERO

        if (mc?.options?.forwardKey!!.isPressed()) movementVec = movementVec.add(1.0, 0.0, 0.0)
        if (mc?.options?.backKey!!.isPressed()) movementVec = movementVec.subtract(1.0, 0.0, 0.0)
        if (mc?.options?.leftKey!!.isPressed()) movementVec = movementVec.subtract(0.0, 0.0, 1.0)
        if (mc?.options?.rightKey!!.isPressed()) movementVec = movementVec.add(0.0, 0.0, 1.0)

        movementVec = movementVec.multiply(1.0, 0.0, 1.0)

        if (mc?.options?.jumpKey!!.isPressed()) movementVec = movementVec.add(0.0, 1.0, 0.0)
        else if (mc?.options?.sneakKey!!.isPressed()) movementVec = movementVec.add(0.0, -1.0, 0.0)

        return movementVec
    }

    fun getMovementVector(): Vec3d? {
        var movementVec = Vec3d.ZERO

        val camera = mc?.gameRenderer?.getCamera() ?: return null

        val pitch = camera.getPitch()
        val yaw = camera.getYaw()

        val straight = Vec3d.fromPolar(pitch, yaw)
        val gay = Vec3d.fromPolar(0f, yaw + 90f)

        if (mc?.options?.forwardKey!!.isPressed()) movementVec = movementVec.add(straight)
        if (mc?.options?.backKey!!.isPressed()) movementVec = movementVec.subtract(straight)
        if (mc?.options?.leftKey!!.isPressed()) movementVec = movementVec.subtract(gay)
        if (mc?.options?.rightKey!!.isPressed()) movementVec = movementVec.add(gay)

        movementVec = movementVec.multiply(1.0, 0.0, 1.0)

        if (mc?.options?.jumpKey!!.isPressed()) movementVec = movementVec.add(0.0, 1.0, 0.0)
        else if (mc?.options?.sneakKey!!.isPressed()) movementVec = movementVec.add(0.0, -1.0, 0.0)

        return movementVec
    }

    abstract fun tick(movementVector: Vec3d)
    abstract fun tick()
}
