package com.client.github.feature.elytra

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket
import net.minecraft.util.math.Vec3d
import net.minecraft.entity.Entity

import com.client.github.feature.Module
import com.client.github.feature.FeatureConfig
import com.client.github.feature.elytra.ElytraFlight

object ElytraTiming {
  private val mod = Module(
    "Elytra",
    "Elytra timing"
  )

  private lateinit var mc: MinecraftClient
  private var tickCount = 0
  private var requireReset = false
  private lateinit var resetPos: Vec3d

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
    mod.enable()
  }

  internal fun filterForGrimAC(): Boolean? {
    if (mc?.player?.hasVehicle() ?: true) return null
    if (mc?.player?.isOnGround() ?: true) return null
    if (mc?.player?.isTouchingWater() ?: true) return null

    return true
  }

  fun quit() {
    if (mc?.player?.isFallFlying()?.not() ?: true) return
    filterForGrimAC() ?: return

    mc?.networkHandler?.sendPacket(ClientCommandC2SPacket(mc!!.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
    mc?.player?.stopFallFlying()
  }

  fun enter() {
    if (mc?.player?.isFallFlying() ?: true) return
    filterForGrimAC() ?: return

    mc?.networkHandler?.sendPacket(ClientCommandC2SPacket(mc!!.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
    mc?.player?.startFallFlying()

    if (FeatureConfig.config.getOrDefault("Elytra flight", false) && !ElytraFlight.grimFlight.enabled()) {
      mc?.player?.setPosition(resetPos.getX(), resetPos.getY(), resetPos.getZ())
    }

    requireReset = false
  }

  fun tick() {
    if (!mod.enabled()) return

    if (!(mc?.player?.isFallFlying() ?: false)) {
      if (requireReset) enter()

      tickCount = 0

      return
    }

    tickCount++

    if (tickCount % 21 == 0) {
      tickCount = 0
    }

    if (tickCount % 15 == 0) {
      quit()
      
      resetPos = (mc.player as Entity).getPos()
      requireReset = true
    }
  }
}
