package com.client.github.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.MathHelper
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.block.Blocks
import net.minecraft.block.BlockState
import net.minecraft.world.RaycastContext
import net.minecraft.util.hit.HitResult
import net.minecraft.client.toast.SystemToast
import net.minecraft.entity.Entity

import baritone.api.BaritoneAPI
import baritone.api.pathing.goals.GoalBlock
import baritone.api.utils.BetterBlockPos
import baritone.api.pathing.movement.IMovement

import com.client.github.feature.elytra.ElytraFlight
import com.client.github.utility.Toast
import com.client.github.feature.elytra.modes.*

object PathUtils {
    private var pathLock: IMovement? = null
    private val mc = MinecraftClient.getInstance()

    fun canStraightflyFrom(start: Vec3d, end: Vec3d): Boolean {
        val raycastContext = RaycastContext(
            start,
            end,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            mc.player!!
        )

        val result = mc.world?.raycast(raycastContext)

        return result != null && result!!.type!! == HitResult.Type.MISS
    }

    const val UNITS_PER_BLOCK = 16.0

    fun predictPathEndForElytra(start: Entity, target: Entity): Vec3d {
        val startVec = start.getBlockPos().toCenterPos()
        val endVec = target.getBlockPos().toCenterPos()

        val distance = startVec.distanceTo(endVec)
        val time = distance / UNITS_PER_BLOCK

        val enemyPredictedMovement = target.getVelocity().multiply(time)

        val end = endVec
            .add(enemyPredictedMovement)
            .add(0.0, 3.0, 0.0)

        return end
    }

    fun findPath(start: Vec3d, end: Vec3d): Vec3d {
        if (canStraightflyFrom(start, end)) {
            if (Accelerate.mod.enabled()) {
                val baritone = BaritoneAPI.getProvider().getPrimaryBaritone()
                val behaviour = baritone.getPathingBehavior()

                behaviour.cancelEverything()

                Accelerate.mod.disable()
                Packet.mod.enable()
            }

            return end.subtract(start).normalize()
        }

        if (Firework.mod.disabled()) {
            if (Accelerate.mod.disabled()) {
                Toast("Pathfinder", "Enabled acceleration flight", SystemToast.Type.TUTORIAL_HINT)

                Accelerate.mod.enable()
                Packet.mod.disable()
            }
        }

        val distance = end.distanceTo(start)

        if (distance > 300.0) return Vec3d.ZERO

        return findPathBaritone(start, end)
    }

    fun findPathBaritone(start: Vec3d, end: Vec3d): Vec3d {
        val straight = end.subtract(start).normalize()

        val mc = MinecraftClient.getInstance()
        val world = mc.world ?: return straight
        val player = mc.player ?: return straight

        val baritone = BaritoneAPI.getProvider().getPrimaryBaritone()

        if (!baritone.getPathingBehavior().isPathing()) {
            val settings = BaritoneAPI.getSettings()

            settings.allowSprint.value = true
            settings.allowDiagonalAscend.value = true
            settings.allowDiagonalDescend.value = true
            settings.primaryTimeoutMS.value = 100L
            settings.failureTimeoutMS.value = 200L
            settings.freeLook.value = true
            settings.logAsToast.value = true
            settings.fadePath.value = true
            settings.assumeWalkOnWater.value = false
            settings.assumeWalkOnLava.value = false
            settings.blocksToAvoid.value = listOf(Blocks.WATER, Blocks.LAVA)
            settings.allowBreak.value = false
            settings.allowDownward.value = false
            settings.elytraTermsAccepted.value = true
            settings.elytraConserveFireworks.value = true

            baritone.getCustomGoalProcess().setGoalAndPath(
                GoalBlock(end.x.toInt(), end.y.toInt(), end.z.toInt())
            )

            return straight
        }

        val behaviour = baritone.getPathingBehavior()

        val path = behaviour.getPath()

        if (!path.isPresent()) return straight

        val movements = path.get().movements() as List<IMovement>

        val nearestMovement = movements.find { movement ->
            !movement.update().isComplete()
        } ?: return straight

        val movement = nearestMovement.getDest()

        val movementVec = Vec3d(movement.x.toDouble(), movement.y.toDouble(), movement.z.toDouble())

        val vec = movementVec.subtract(start).normalize()

        return vec
    }
}
