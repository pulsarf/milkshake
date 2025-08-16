package com.client.github.components.Circle

import net.minecraft.util.math.Vec3d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.client.render.*

import org.joml.*

import kotlin.math.*

import com.mojang.blaze3d.systems.RenderSystem

/**
 * Forgive me for what I'm about to do.
**/

object CircleConstants {
  const val SEGMENTS = 6
}

@Suppress("NOTHING_TO_INLINE")
inline fun Circle(
  stack: Matrix4f,
  centerX: Int,
  centerY: Int,
  radius: Int = 40,
  backgroundColor: Int = 0xFFEAE7E7.toInt()
) { 
  val tsl = Tessellator.getInstance()
  val buf = tsl.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
  var angle = 0.0f
  val offset = 2 * PI.toFloat() / CircleConstants.SEGMENTS

  buf.vertex(stack, centerX.toFloat(), centerY.toFloat(), 5f).color(backgroundColor)

  repeat(CircleConstants.SEGMENTS + 1) {
    val x = centerX + radius * cos(angle)
    val y = centerY + radius * sin(angle)

    buf.vertex(stack, x, y, 5f).color(backgroundColor)

    angle += offset
  }

  RenderSystem.setShader(GameRenderer::getPositionColorProgram)
  RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
  BufferRenderer.drawWithGlobalProgram(buf.end());
}
