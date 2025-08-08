package com.client.github.feature.visual

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.world.ClientWorld
import net.minecraft.client.render.*
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.client.util.math.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem

import com.client.github.feature.Module
import com.client.github.util.Geometry
import com.client.github.util.Point

import kotlin.math.*

import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector4f
import org.joml.Vector2d

import org.lwjgl.opengl.GL11;

fun toRadians(deg: Double): Double = deg / 180.0 * PI

object ExtrasensoryPerception {
  private val mod = Module(
    "Visual",
    "Entity tracers"
  )

  private lateinit var mc: MinecraftClient

  private fun getVCP(): VertexConsumerProvider.Immediate = mc.getBufferBuilders().getEntityVertexConsumers()

  private fun drawLine(
    start: Vec3d,
    end: Vec3d,
    color: Triple<Int, Int, Int>,
    matrix: Matrix4f,
    entry: MatrixStack.Entry,
    buf: VertexConsumer
  ) {
    var (rc, gc, bc) = color

    val r = rc / 255.0f
    val g = gc / 255.0f
    val b = bc / 255.0f

    val norm = Vector3d(end.getX(), end.getY(), end.getZ()).sub(start.getX(), start.getY(), start.getZ()).normalize()

    RenderSystem.setShader(GameRenderer::getPositionColorProgram)

    val dist = sqrt((start.getX() - end.getX()).pow(2) + (start.getY() - end.getY()).pow(2) + (start.getZ() - end.getZ()).pow(2))
    val alpha = (1f - dist / 5000f).coerceIn(0.0, 0.9).toFloat()

    buf.vertex(matrix, start.getX().toFloat(), start.getY().toFloat(), start.getZ().toFloat())
      .color(r, g, b, alpha)
      .normal(entry, norm.x().toFloat(), norm.y().toFloat(), norm.z().toFloat())
    buf.vertex(matrix, end.getX().toFloat(), end.getY().toFloat(), end.getZ().toFloat())
      .color(r, g, b, alpha)
      .normal(entry, norm.x().toFloat(), norm.y().toFloat(), norm.z().toFloat())
 
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); 
  }

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun render(drawContext: DrawContext) {
    if (!mod.enabled()) return

    mc?.world?.let {
      val entities = (mc.world as ClientWorld).getEntities()

      val camFov = mc.options?.getFov()?.getValue() ?: return@render

      val sw = drawContext.getScaledWindowWidth()
      val sh = drawContext.getScaledWindowHeight()

      val gameRenderer = mc.gameRenderer
      val camera = mc.gameRenderer.getCamera()
      val cameraRotation = camera.getRotation()

      val matrices = drawContext.getMatrices()

      matrices.push()

      matrices.translate(sw / 2.0, sh / 2.0, 0.0)
      matrices.multiply(cameraRotation)
      matrices.scale(1f, -1f, -1f)

      val entry = matrices.peek()
      val matrix = entry.getPositionMatrix()

      val cameraPos = mc.getBlockEntityRenderDispatcher().camera.getPos()
      
      val projMatrix = gameRenderer.getBasicProjectionMatrix(toRadians(camFov.toDouble()))
      val viewMatrix = RenderSystem.getModelViewMatrix()

      for (entity in entities) {
        val vcp = getVCP()

        val buffer = vcp.getBuffer(RenderLayer.LINES)

        val color = when {
          entity.isPlayer() -> Triple(180, 40, 40)
          entity is LivingEntity -> Triple(180, 180, 40)
          else -> continue
        }

        val pos = entity.getPos() ?: continue

        val tmpVec = Vector4f(
          pos.getX().toFloat() - cameraPos.getX().toFloat(),
          pos.getY().toFloat() - cameraPos.getY().toFloat(),
          pos.getZ().toFloat() - cameraPos.getZ().toFloat(), 1f
        ).mul(viewMatrix).mul(projMatrix)

        val nx = tmpVec.x() / tmpVec.w()
        val ny = tmpVec.y() / tmpVec.w()
        val nz = tmpVec.z() / tmpVec.w()

        if (nx <= -1.0 || nx >= 1.0 || ny <= -1.0 || ny >= 1.0) continue

        val screenX = ((nx + 1.0) / 2.0) * sw
        val screenY = sh - ((ny + 1.0) / 2.0) * sh
 
        drawLine(Vec3d.ZERO, pos.add(0.0, entity.height / 2.0, 0.0).subtract(cameraPos), color, matrix, entry, buffer)
        //drawLine(Vec3d.ZERO.add(sw / 2.0, sh / 2.0, 0.0), Vec3d(screenX.toDouble(), screenY.toDouble(), nz.toDouble()), color, matrix, entry, buffer)

        vcp.draw(RenderLayer.LINES)
      }

      matrices.pop()
    }
  }
}
