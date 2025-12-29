package com.client.github.feature.combat

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.util.hit.EntityHitResult

import com.client.github.utility.TargetLock
import com.client.github.feature.Module
import com.client.github.feature.Criticals

object KillAura {
  private lateinit var mc: MinecraftClient
  private var hitCooldown = 0

  val mod = Module("Combat", "KillAura")
  val onlyFans = Module("Combat", "KillAura:OnlyCrits")
  val triggerBot = Module("Combat", "KillAura:TriggerBot")

  val killauraReach = 3.0

  fun bootstrap() {
    mc = MinecraftClient.getInstance()

    Criticals.bootstrap()
  }

  internal fun hit(entity: Entity): Boolean? {
    val entityPos = entity.getPos()
    val playerPos = (mc.player as Entity).getPos()

    if (entityPos.distanceTo(playerPos) > killauraReach) return null

    Criticals.prepare()

    mc.interactionManager?.attackEntity(mc.player, entity)
    (mc.player as ClientPlayerEntity).swingHand((mc.player as ClientPlayerEntity).getActiveHand())

    hitCooldown = 11

    return true
  }

  fun tickRegardless() {
      val swingProgress = mc!!.player!!.getAttackCooldownProgress(0.5f)

      if (hitCooldown-- > 0) return
      if (swingProgress < 0.9f) return

      if (onlyFans.enabled() && (
        mc.player!!.fallDistance <= 0 ||
        mc.player!!.isOnGround()
      ) && !Criticals.mod.enabled()) return

      val hitResult = mc.crosshairTarget

      if (triggerBot.enabled()) {
        if (hitResult is EntityHitResult) {
          val entity = hitResult.getEntity()

          if (entity == mc?.player || !entity.isAlive() || entity !is LivingEntity || entity is EndCrystalEntity || !entity.isAttackable()) return

          hit(entity) ?: return
        }

        return
      }

      val entity = TargetLock.getAttackTarget() ?: return

      hit(entity)
  }

  fun tick() {
    if (mc.player == null) return
    if (!mod.enabled()) return

    tickRegardless()
  }
}
