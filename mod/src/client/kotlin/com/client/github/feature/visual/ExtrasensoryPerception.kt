package com.client.github.feature.visual

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.world.ClientWorld
import net.minecraft.client.render.Tessellator

import com.client.github.feature.Module

import kotlin.math.*

fun toRadians(deg: Double): Double = deg / 180.0 * PI

object ExtrasensoryPerception {
  private val mod = Module(
    "Visual",
    "Entity tracers"
  )

  private lateinit var mc: MinecraftClient

  private fun drawLine(
    sx: Int, xy: Int,
    ex: Int, ey: Int,
    color: Triple<Int, Int, Int>
  ) {
    val tsl = Tessellator.getInstance()

    tsl?.let {

    }
  }

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun render(drawContext: DrawContext) {
    mc?.world?.let {
      val entities = (mc.world as ClientWorld).getEntities()

      val player = mc.player

      val camX = player!!.getX()
      val camY = player!!.getY()
      val camZ = player!!.getZ()
      val camPitch = toRadians(player!!.getPitch().toDouble())
      val camYaw = toRadians(player!!.getYaw().toDouble())
      val camFov = mc?.options?.getFov()?.getValue()

      if (camFov == null) return

      val sw = drawContext.getScaledWindowWidth()
      val sh = drawContext.getScaledWindowHeight()

      val sfov = 1.0 / tan(toRadians(camFov / 2.0))

      val yx = cos(camYaw)
      val yy = sin(camYaw)
      val px = cos(camPitch)
      val py = sin(camPitch)

      val gameRenderer = mc.gameRenderer

      for (entity in entities) {
        val color = when {
          entity.isPlayer() -> Triple(255, 40, 40)
          entity is LivingEntity -> Triple(255, 255, 40)
          else -> Triple(40, 255, 40)
        }

        val relX = entity.getX() - camX
        val relY = entity.getY() - camY
        val relZ = entity.getZ() - camZ

        val cx = relX * yx + relZ * yy
        val cz = -relZ * yy + relX * yx
        val cy = relY * px - cz * py
        val czf = relY * py + cz * px

        if (czf <= 0) continue

        val x = sfov * cx / czf
        val y = sfov * cy / czf

        val pjx = ((x + 1) * sw / 2).toInt()
        val pjy = ((1 - y) * sh / 2).toInt()
        
        drawLine(sw / 2, sh / 2, pjx, pjy, color)
      }
    }
  }
}
