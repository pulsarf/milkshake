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

  private val grimFlight = Module(
    "Elytra",
    "Elytra flight:AAC"
  )

  private lateinit var mc: MinecraftClient

  public var velocity = 0.2

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
      val rot = mc!!.player!!.getYaw() * 0.017453292f
      val cr = cos(rot) * cos(rot)
      val hs = sqrt(straight.getX() * straight.getX() + straight.getZ() * straight.getZ())

      movementVec = movementVec.add(0.0, 0.01 * (cr * 0.75 - 1), 0.0)
      
      // fall slowdown

      if (hs > 0.0) {
        if (movementVec.getY() < 0.0) {
          val mot = -movementVec.getY() / 10.0 * cr
          movementVec = movementVec.add(straight.getX() * mot / hs, mot, straight.getZ() * mot / hs)
        }

        if (rot < 0f) {
          val mot = hs * sin(rot + PI) * 0.04
          movementVec = movementVec.add(-straight.getX() * mot / hs, mot * 3.2, -straight.getZ() * mot / hs)
        }
      }

      mc?.player?.addVelocity(movementVec.multiply(velocity))
    } else mc?.player?.setVelocity(movementVec)
  }
}
