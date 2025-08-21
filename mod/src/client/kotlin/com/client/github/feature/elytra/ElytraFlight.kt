package com.client.github.feature.elytra

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper

import com.client.github.feature.Module

import kotlin.math.*

fun toDirVec(pitch: Float, yaw: Float): Vec3d = Vec3d(0.0, 0.0, 1.0).rotateX(pitch).rotateY(yaw)

object ElytraFlight {
  private val mod = Module(
    "Elytra",
    "Elytra flight"
  )

  val grimFlight = Module(
    "Elytra",
    "Elytra flight:AAC"
  )

  private lateinit var mc: MinecraftClient

  public var velocity = 0.02

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun tick() {
    if (!mod.enabled()) return
    if (!(mc?.player?.isFallFlying() ?: false)) return

    var movementVec = Vec3d.ZERO

    val camera = mc?.gameRenderer?.getCamera() ?: return

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

    if (grimFlight.enabled()) {
      mc?.player?.addVelocity(movementVec.multiply(velocity))
    } else mc?.player?.setVelocity(movementVec)
  }
}
