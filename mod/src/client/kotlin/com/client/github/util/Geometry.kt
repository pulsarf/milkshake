package com.client.github.util

import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Quaternionf
import org.joml.Vector4f

import net.minecraft.client.MinecraftClient

import com.mojang.blaze3d.systems.RenderSystem

class Point(
  val x: Int, 
  val y: Int, 
  val z: Int
)

object Geometry {
  val mc = MinecraftClient.getInstance()

  fun project(
    cam: Vector3d,
    entity: Vector3d,
    width: Int,
    height: Int
  ): Pair<Int, Int>? {
    val camera = mc.gameRenderer.getCamera()
    val rot: Quaternionf = camera.getRotation()

    val proj = RenderSystem.getProjectionMatrix()

    val view = Matrix4f()
      .translation(-cam.x().toFloat(), -cam.y().toFloat(), -cam.z().toFloat())
      .rotate(rot.conjugate())

    val clip = Vector4f(entity.x().toFloat(), entity.y().toFloat(), entity.z().toFloat(), 1.0f)
      .mulProject(Matrix4f(proj).mul(view))

    if (clip.w() <= 0f) return null

    val ndc = clip.div(clip.w())

    val sx = (ndc.x() + 1f) / 2f * width
    val sy = (1f - ndc.y()) / 2f * height

    return Pair(sx.toInt(), sy.toInt())
  }
}
