package com.client.github.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.client.toast.SystemToast

import com.client.github.utility.Toast
import com.client.github.utility.PathUtils

object TargetLock {
    var target: Entity? = null

    val mc = MinecraftClient.getInstance()

    fun findAttackTarget(): LivingEntity? {
        mc?.world?.let {
            val entities = (mc.world as ClientWorld).getEntities()
            val playerPos = (mc!!.player as Entity).getPos()

            val sortedEntities = entities.sortedBy {
                entity: Entity ->
                    val health = if (entity is LivingEntity) (entity as LivingEntity).getHealth() else 0f

                    playerPos.squaredDistanceTo(entity.getPos()) + health * 10
            }

            for (entity in sortedEntities) {
                if (entity == null) continue
                if (entity == mc?.player) continue
                if (!entity.isAlive()) continue
                if (!(entity is LivingEntity)) continue
                if (entity is EndCrystalEntity) continue
                if (!entity.isAttackable()) continue

                val entityPos = entity.getPos()

                if (playerPos.y - entityPos.y > 2) continue
                if (!PathUtils.canStraightflyFrom(playerPos, entityPos)) continue

                Toast("Target", "Switch to ${entity?.getName()?.getString()}", SystemToast.Type.TUTORIAL_HINT)

                return entity
            }
        }

        return null
    }

    fun getAttackTarget(): Entity? {
        if (target == null || !target!!.isAlive() || !target!!.isAttackable()) {
            target = findAttackTarget()
        }

        return target
    }
}
