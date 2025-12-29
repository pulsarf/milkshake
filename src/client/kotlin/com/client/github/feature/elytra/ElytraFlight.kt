package com.client.github.feature.elytra

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

import com.client.github.feature.Module
import com.client.github.feature.elytra.ElytraTiming
import com.client.github.feature.elytra.modes.*

import kotlin.math.*

fun toDirVec(pitch: Float, yaw: Float): Vec3d = Vec3d(0.0, 0.0, 1.0).rotateX(pitch).rotateY(yaw)

object ElytraFlight {
  val mod = Module(
    "Elytra",
    "Elytra flight"
  )

  private lateinit var mc: MinecraftClient

  private fun aboutToHitGround(): Boolean? = !(0..3).all { mc!!.world!!.getBlockState(mc!!.player!!.getBlockPos()!!.down(it))!!.isAir() }

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  init {
      Themis
      Bounce
      Angle
      Accelerate
      Packet
      Firework
  }

  fun tick() {
    if (!mod.enabled()) return

    tick(null)
  }

  fun tick(movementVec: Vec3d?) {
    if (!mod.enabled()) return

    val actualMovementVec = Firework.getMovementVector() ?: return

    val fakeMovementVec = if (movementVec == null) {
        Firework.getRawMovementVector() ?: return
    } else {
        movementVec
    }

    if (Firework.mod.enabled()) Firework.tickSafe(movementVec)
    if (Themis.mod.enabled()) return Themis.tick(fakeMovementVec)
    if (Bounce.mod.enabled()) return Bounce.tick(actualMovementVec)
    if (!(mc?.player?.isFallFlying() ?: false)) return

    if (Angle.mod.enabled()) Angle.tick(actualMovementVec)

    if (Accelerate.mod.enabled()) {
        Accelerate.tick(actualMovementVec)
    } else if (Packet.mod.enabled()) Packet.tick(fakeMovementVec)
  }
}
