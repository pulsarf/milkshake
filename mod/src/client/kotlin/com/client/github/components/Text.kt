package com.client.github.components

import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.MinecraftClient

import org.joml.Matrix4f

import kotlin.math.*

import com.mojang.blaze3d.systems.RenderSystem

object Text {
  internal val mc = MinecraftClient.getInstance()

  fun create(
    text: String, x: Float, y: Float, drawContext: DrawContext, color: Int, centered: Boolean, fullOX: Float, fullOY: Float
  ) {
    val textRenderer = mc.textRenderer ?: return

    if (centered) create(text, x + fullOX / 2 - textRenderer.getWidth(text) / 2, y + fullOY / 2 - textRenderer.fontHeight / 2, drawContext, color)
  }

  fun create(
    text: String, x: Float, y: Float, drawContext: DrawContext, color: Int
  ) {
    val textRenderer = mc.textRenderer ?: return

    drawContext.drawText(textRenderer, text, ceil(x).toInt(), ceil(y).toInt(), color, false)
  }
}
