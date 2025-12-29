package com.client.github.feature.elytra

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.client.toast.SystemToast
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.Vec3d

import com.client.github.feature.Module
import com.client.github.feature.elytra.ElytraFlight
import com.client.github.feature.elytra.ElytraTiming
import com.client.github.utility.Toast
import com.client.github.feature.combat.KillAura
import com.client.github.utility.PathUtils
import com.client.github.utility.TargetLock
import com.client.github.feature.elytra.modes.*

object ElytraTarget : Module("Elytra", "Elytra target") {
    private lateinit var mc: MinecraftClient

    fun bootstrap() {
        mc = MinecraftClient.getInstance()
    }

    private var stateFreeze = false

    fun tick() {
        if (mc.player == null) return
        if (!enabled()) return

        val target = TargetLock.getAttackTarget()

        if (target == null) {
            if (!stateFreeze) {
                Toast("Elytra target", "Elytra target frozen", SystemToast.Type.TUTORIAL_HINT)

                if (ElytraFlight.mod.enabled()) {
                    ElytraFlight.mod.disable()
                }

                if (KillAura.mod.enabled()) {
                    KillAura.mod.disable()
                }

                mc!!.player!!.stopFallFlying()

                stateFreeze = true
            }

            return
        }

        stateFreeze = false

        ElytraTiming.enter()

        val playerPos = mc!!.player!!.getBlockPos().toCenterPos()
        val targetPos = PathUtils.predictPathEndForElytra(mc!!.player!!, target)

        val path = PathUtils.findPath(playerPos, targetPos)

        if (Firework.mod.enabled()) {
            if (Accelerate.mod.enabled() || Packet.mod.enabled()) {
                Accelerate.mod.disable()
                Packet.mod.disable()
            }

            if (!ElytraFlight.mod.enabled() || !KillAura.mod.enabled()) {
                ElytraFlight.mod.enable()
                Firework.mod.enable()
                KillAura.mod.enable()
            }
        } else if ((!Accelerate.mod.enabled() && !Packet.mod.enabled()) || !ElytraFlight.mod.enabled() || !KillAura.mod.enabled()) {
            KillAura.mod.enable()
            ElytraFlight.mod.enable()
            Accelerate.mod.enable()
        }


        ElytraFlight.tick(path)
    }
}
