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

import baritone.api.BaritoneAPI

object ElytraTarget : Module("Elytra", "Elytra target") {
    private lateinit var mc: MinecraftClient

    fun bootstrap() {
        mc = MinecraftClient.getInstance()
    }

    public enum class State {
        ONGROUND,
        OVERTAKE,
        BARITONE,
        LANDING
    }

    private var state = State.ONGROUND

    private fun setFireworkFlightConfig() {
        if (Accelerate.mod.enabled() || Packet.mod.enabled()) {
            Accelerate.mod.disable()
            Packet.mod.disable()
        }

        if (!ElytraFlight.mod.enabled()) {
            ElytraFlight.mod.enable()
            Firework.mod.enable()
        }
    }

    private fun setBlatantFlightConfig() {
        ElytraFlight.mod.enable()
        Accelerate.mod.enable()
    }

    private fun isInFireworkMode(): Boolean {
        return Firework.mod.enabled()
    }

    private fun isInBlatantMode(): Boolean {
        return (!Accelerate.mod.enabled() && !Packet.mod.enabled()) || !ElytraFlight.mod.enabled()
    }

    private fun overtakeTarget(target: Entity) {
        ElytraTiming.enter()

        val playerPos = mc!!.player!!.getBlockPos().toCenterPos()
        val targetPos = PathUtils.predictPathEndForElytra(mc!!.player!!, target)

        val path = PathUtils.findPath(playerPos, targetPos)

        if (isInFireworkMode()) {
            setFireworkFlightConfig()
        } else if (isInBlatantMode()) {
            setBlatantFlightConfig()
        }

        ElytraFlight.tick(path)
    }

    fun tick() {
        if (!enabled()) return

        val player = mc.player ?: return

        val target = TargetLock.getAttackTarget()

        when (state) {
            State.ONGROUND -> {
                if (!player.isOnGround()) {
                    state = State.BARITONE
                } else if (target != null) {
                    player.jump()
                    ElytraTiming.enter()
                }
            }
            State.BARITONE -> {
                if (target == null) {
                    state = State.LANDING

                    if (state != State.LANDING) {
                        Toast("Elytra target", "Elytra target frozen", SystemToast.Type.TUTORIAL_HINT)
                    }
                } else {
                    ElytraTiming.enter()

                    val playerPos = mc!!.player!!.getBlockPos().toCenterPos()
                    val targetPos = PathUtils.predictPathEndForElytra(mc!!.player!!, target)

                    val path = PathUtils.findPathBaritone(playerPos, targetPos)

                    if (isInFireworkMode()) {
                        setFireworkFlightConfig()
                    } else if (isInBlatantMode()) {
                        setBlatantFlightConfig()
                    }

                    ElytraFlight.tick(path)

                    if (PathUtils.canStraightflyFrom(playerPos, targetPos)) {
                        state = State.OVERTAKE
                    }
                }
            }
            State.OVERTAKE -> {
                if (target == null) {
                    state = State.LANDING

                    if (state != State.LANDING) {
                        Toast("Elytra target", "Elytra target frozen", SystemToast.Type.TUTORIAL_HINT)
                    }
                } else {
                    if (Accelerate.mod.enabled()) {
                        val baritone = BaritoneAPI.getProvider().getPrimaryBaritone()
                        val behaviour = baritone.getPathingBehavior()

                        behaviour.cancelEverything()

                        Accelerate.mod.disable()
                        Packet.mod.enable()
                    }

                    val playerPos = mc!!.player!!.getBlockPos().toCenterPos()
                    val targetPos = PathUtils.predictPathEndForElytra(mc!!.player!!, target)

                    overtakeTarget(target!!)

                    if (!KillAura.mod.enabled()) {
                        KillAura.tickRegardless()
                    }

                    if (!PathUtils.canStraightflyFrom(playerPos, targetPos)) {
                        state = State.BARITONE
                    }
                }
            }
            State.LANDING -> {
                if (ElytraFlight.mod.enabled()) {
                    ElytraFlight.mod.disable()
                }

                state = State.ONGROUND
            }
        }
    }
}
