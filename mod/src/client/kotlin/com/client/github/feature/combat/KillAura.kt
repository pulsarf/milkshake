package com.client.github.feature.combat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.client.network.ClientPlayerEntity

import com.client.github.feature.Module

object KillAura {
  private lateinit var mc: MinecraftClient
  private var hitCooldown = 0

  val mod = Module("Combat", "KillAura")

  val killauraReach = 6.0

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun tick() {
    if (mc.player == null) return
    if (!mod.enabled()) return

    val swingProgress = mc!!.player!!.getAttackCooldownProgress(0f)

    if (hitCooldown-- > 0) return
    if (swingProgress < 0.95f) return 

    mc?.world?.let {
      val entities = (mc.world as ClientWorld).getEntities()
      val playerPos = (mc.player as Entity).getPos()

      for (entity in entities) {
        if (entity == null) continue
        if (entity == mc?.player) continue

        val entityPos = entity.getPos()

        if (entityPos.squaredDistanceTo(playerPos) > killauraReach) continue

        mc.interactionManager?.attackEntity(mc.player, entity)
        (mc.player as ClientPlayerEntity).swingHand((mc.player as ClientPlayerEntity).getActiveHand())

        hitCooldown = 9
      }
    }
  }
}
