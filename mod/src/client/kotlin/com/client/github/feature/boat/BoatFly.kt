package com.client.github.feature.boat

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.vehicle.BoatEntity

import com.client.github.feature.Module

import kotlin.math.*

fun toDirVec(pitch: Float, yaw: Float): Vec3d = Vec3d(0.0, 0.0, 1.0).rotateX(pitch).rotateY(yaw)

object BoatFlight {
  private val mod = Module(
    "Boat",
    "BoatFly"
  )

  private val aac = Module(
    "Boat",
    "BoatFly:Vel"
  )

  private val antiKick = Module(
    "Boat",
    "BoatFly:AntiKick"
  )

  private val autoBreak = Module(
    "Boat",
    "BoatAutoBreak"
  )

  private lateinit var mc: MinecraftClient

  public var velocity = 0.1
  
  init {
    mc = MinecraftClient.getInstance()
  }

  fun tick() {
    if (!(mc?.player?.hasVehicle() ?: false)) {
      if (autoBreak.enabled()) {
        val player = mc.player ?: return
        val world = mc.world ?: return

        val entities = world.getEntities()

        val boats = entities.toList().filter { 
          it is BoatEntity && 
          player.squaredDistanceTo(it) < 4.0 &&
          it.isAttackable() &&
          it.isAlive()
        }

        boats.forEach {
          mc.interactionManager?.attackEntity(player, it)
          player.swingHand(player.getActiveHand())
        }
      }

      return
    }

    if (!mod.enabled()) return

    val vehicle = mc?.player?.getVehicle() ?: return

    if (antiKick.enabled()) {
      mc?.player?.stopRiding()
      mc?.player?.startRiding(vehicle)
    }

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

    if (aac.enabled()) {
      vehicle.addVelocity(movementVec.multiply(velocity))
    } else vehicle.setVelocity(movementVec)
  }
}
