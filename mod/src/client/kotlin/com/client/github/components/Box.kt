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

object Box {
  fun create(
    scaledX: Float, scaledY: Float,
    scaledW: Float, scaledH: Float,
    drawContext: DrawContext, color: Int
  ) {
    drawContext.fill(scaledX.toInt(), scaledY.toInt(), (scaledX + scaledW).toInt(), (scaledY + scaledH).toInt(), color)
  }
}
