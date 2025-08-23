package com.client.github.feature.elytra

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

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

  val bounceFlight = Module(
    "Elytra",
    "Elytra flight:Bounce"
  )

  val bounceFlightLegit = Module(
    "Elytra",
    "Elytra flight:Bounce:Legit"
  )

  val boostFlight = Module(
    "Elytra",
    "Elytra flight:Boost"
  )

  private lateinit var mc: MinecraftClient

  public var velocity = 0.02

  internal fun bounceFly() {
    val player = mc.player ?: return

    if (bounceFlightLegit.enabled()) mc.options.jumpKey.setPressed(true)

    if (!player.isFallFlying() && !player.isOnGround()) {
      if (!bounceFlightLegit.enabled()) player.jump()
      player.startFallFlying()
      mc?.networkHandler?.sendPacket(ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
    }
  }

  internal fun boostFlight() {

  }

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun tick() {
    if (!mod.enabled()) return
    if (bounceFlight.enabled()) return bounceFly()
    if (!(mc?.player?.isFallFlying() ?: false)) return

    if (boostFlight.enabled()) return boostFlight()
 
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
