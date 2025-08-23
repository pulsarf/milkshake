package com.client.github.feature.player

import net.minecraft.entity.Entity
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.block.Blocks
import net.minecraft.block.BlockState

import com.client.github.feature.Module

object PlayerFlight {
  private val mod = Module(
    "Player",
    "Player flight"
  )

  private val aac = Module(
    "Player",
    "Player flight:AAC"
  )

  private val mc = MinecraftClient.getInstance()

  private lateinit var previousPos: BlockPos
  private lateinit var previousBlockState: BlockState

  internal fun aacFlight() {
    val player = mc.player ?: return
    val world = mc.world ?: return

    val blockPos = player.getBlockPos().down()

    if (::previousPos.isInitialized) {
      world.removeBlock(previousPos, false)
      world.setBlockState(previousPos, previousBlockState)
    }

    previousBlockState = world.getBlockState(blockPos)

    world.setBlockState(blockPos, Blocks.DARK_OAK_WOOD.defaultState)

    previousPos = blockPos
  }

  fun tick() {
    if (!mod.enabled()) return

    val player = mc.player ?: return

    if (aac.enabled()) return aacFlight()

    var movementVec = Vec3d.ZERO

    val camera = mc?.gameRenderer?.getCamera() ?: return

    val pitch = camera.getPitch()
    val yaw = camera.getYaw()

    val straight = Vec3d.fromPolar(pitch, yaw)
    val gay = Vec3d.fromPolar(0f, yaw + 90f)

    if (mc?.options?.forwardKey!!.isPressed()) movementVec = movementVec.add(straight)
    if (mc?.options?.backKey!!.isPressed()) movementVec = movementVec.subtract(straight)
    if (mc?.options?.leftKey!!.isPressed()) movementVec = movementVec.subtract(gay)
    if (mc?.options?.rightKey!!.isPressed()) movementVec = movementVec.add(gay)
    
    movementVec = movementVec.multiply(1.0, 0.0, 1.0)

    if (mc?.options?.jumpKey!!.isPressed()) movementVec = movementVec.add(0.0, 1.0, 0.0)
    else if (mc?.options?.sneakKey!!.isPressed()) movementVec = movementVec.add(0.0, -1.0, 0.0)

    player.setVelocity(movementVec)
  }
}

