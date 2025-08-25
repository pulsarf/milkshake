package com.client.github.components

import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.GameRenderer

import org.joml.Matrix4f

import com.mojang.blaze3d.systems.RenderSystem

import com.client.github.components.Line

import kotlin.math.*

object Arrow {
  private val arrowLength = 15f
  private val arrowSpan = 4

  fun create(
    x: Float, y: Float, matrix: Matrix4f, color: Int, angle: Double
  ) {
    val px = x + cos(angle - PI / arrowSpan) * arrowLength
    val py = y + sin(angle - PI / arrowSpan) * arrowLength

    val px1 = x + cos(angle + PI / arrowSpan) * arrowLength
    val py1 = y + sin(angle + PI / arrowSpan) * arrowLength

    Line.create(x, y, px.toFloat(), py.toFloat(), matrix, color)
    Line.create(x, y, px1.toFloat(), py1.toFloat(), matrix, color)
  }
}
