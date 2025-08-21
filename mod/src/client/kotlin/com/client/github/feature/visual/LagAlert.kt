package com.client.github.feature.visual

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

object LagAlert {
  private val mc = MinecraftClient.getInstance()

  internal var lastWorldUpdate: Long = 0
  internal var lastWorldTime: Long = 0

  fun tick() {
    val player = mc?.player ?: return
    val world = player.getWorld() ?: return
    val currentTime = world.getTime()

    if (lastWorldTime != currentTime) {
      lastWorldUpdate = System.currentTimeMillis()
    }

    lastWorldTime = currentTime
  }

  fun render(context: DrawContext) {
    val lagTime = System.currentTimeMillis() - lastWorldUpdate

    if (lagTime < 100) return

    val textRenderer = mc?.textRenderer ?: return
    val window = mc?.getWindow() ?: return

    val scaledWidth = window.scaledWidth
    val scaledHeight = window.scaledHeight

    val text = "Lagging for ${lagTime / 20} ticks!"

    context.drawText(textRenderer, text, (scaledWidth - textRenderer.getWidth(text)) / 2, 10, 0xFFFFFFFF.toInt(), true)
  }
}
