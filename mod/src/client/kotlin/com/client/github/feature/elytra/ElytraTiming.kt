package com.client.github.feature.elytra

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket

import com.client.github.feature.Module

object ElytraTiming {
  private val mod = Module(
    "Elytra",
    "Elytra timing"
  )

  private lateinit var mc: MinecraftClient
  private var tickCount = 0
  private var requireReset = false

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun quit() {
    mc?.networkHandler?.sendPacket(ClientCommandC2SPacket(mc!!.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
    mc?.player?.stopFallFlying()
  }

  fun enter() {
    mc?.networkHandler?.sendPacket(ClientCommandC2SPacket(mc!!.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
    mc?.player?.startFallFlying()

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

    if (tickCount % 19 == 0) {
      quit()
      
      requireReset = true
    }
  }
}
