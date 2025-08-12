package com.client.github.feature.player

import com.client.github.feature.Module
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

object AntiFireDamage {
  val mod = Module(
    "Player",
    "Regenerator"
  )

  private lateinit var mc: MinecraftClient
  
  var regenPerTick = 5

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun tick() {
    if (mc.player == null) return

    val player = mc.player

    player?.timeUntilRegen = -1

    player?.setFireTicks(-1)
    player?.fireTicks = -1
    player?.setOnFire(false)
    player?.wasOnFire = false

    for (i in 0..<regenPerTick) mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.Full(
      player!!.getX(), 
      player!!.getY(), 
      player!!.getZ(),
      player!!.getYaw(),
      player!!.getPitch(),
      player!!.isOnGround()
    ))
  }
}
