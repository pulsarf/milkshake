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

object Line {
  fun create(
    x: Float, y: Float, x1: Float, y1: Float,
    matrix: Matrix4f, color: Int
  ) {
    val tsl = Tessellator.getInstance()
    val buffer = tsl.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR)

    buffer.vertex(matrix, x, y, 5f).color(color)
    buffer.vertex(matrix, x1, y1, 5f).color(color)

    RenderSystem.setShader(GameRenderer::getPositionColorProgram)
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

    BufferRenderer.drawWithGlobalProgram(buffer.end())
  }
}
