package com.client.github.feature.combat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

import com.client.github.feature.Module

object Criticals {
    /**
      * Crit requirements:
      * 1. Weapon reload > 0.9
      * 2. Fall distance > 0f
      * 3. Not on ground
      * 4. Not climbing
      * 5. Not touching water
      * 6. Doesn't have blindness
      * 7. Not in a vehicle
      * 8. Is a LivingEntity
      * 9. Not sprinting
    **/

  private lateinit var mc: MinecraftClient

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun prepare() {
    /**
     * We only care about fall distance, onGround and sprint
    **/

    if (mc.player == null) return

    val player = mc.player as Entity

    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY() + 0.1, player.getZ(), true
    ))
    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY(), player.getZ(), false
    ))
    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY() + 0.01, player.getZ(), false
    ))
    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY(), player.getZ(), false
    ))
  }
}

object KillAura {
  private lateinit var mc: MinecraftClient
  private var hitCooldown = 0

  val mod = Module("Combat", "KillAura")

  val killauraReach = 3.0

  fun bootstrap() {
    mc = MinecraftClient.getInstance()

    Criticals.bootstrap()
  }

  fun tick() {
    if (mc.player == null) return
    if (!mod.enabled()) return

    val swingProgress = mc!!.player!!.getAttackCooldownProgress(0.5f)

    if (hitCooldown-- > 0) return
    if (swingProgress < 0.9f) return 

    mc?.world?.let {
      val entities = (mc.world as ClientWorld).getEntities()
      val playerPos = (mc.player as Entity).getPos()

      for (entity in entities) {
        if (entity == null) continue
        if (entity == mc?.player) continue
        if (!entity.isAlive()) continue
        if (!(entity is LivingEntity)) continue
        if (entity is EndCrystalEntity) continue

        val entityPos = entity.getPos()

        if (entityPos.distanceTo(playerPos) > killauraReach) continue

        Criticals.prepare()
        mc.interactionManager?.attackEntity(mc.player, entity)
        (mc.player as ClientPlayerEntity).swingHand((mc.player as ClientPlayerEntity).getActiveHand())

        hitCooldown = 9
      }
    }
  }
}
