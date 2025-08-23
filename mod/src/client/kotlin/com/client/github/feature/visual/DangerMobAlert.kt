package com.client.github.feature.visual

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.Entity

import com.client.github.feature.Module

object DangerMobAlert {
  private val mc = MinecraftClient.getInstance()

  private val mod = Module(
    "Visual",
    "Hostile alert"
  )

  init { mod.enable() }

  private lateinit var hostileEntities: List<Entity>

  fun tick() {
    if (!mod.enabled()) return

    val player = mc?.player ?: return
    val world = mc?.world ?: return
    val entities = world?.getEntities() ?: return

    hostileEntities = entities
      .toList()
      .filter { it !is PassiveEntity && it != mc.player && it?.getName() != null && it.squaredDistanceTo(player) < 6 }
  }

  fun render(context: DrawContext) {
    if (!::hostileEntities.isInitialized) return
    if (!mod.enabled()) return

    val textRenderer = mc?.textRenderer ?: return
    val window = mc?.getWindow() ?: return
    val player = mc?.player ?: return

    val scaledWidth = window.scaledWidth
    val scaledHeight = window.scaledHeight

    if (hostileEntities.size == 0) return

    val nearestEntity = hostileEntities.sortedWith { a, b ->
      when {
        a.squaredDistanceTo(player) < b.squaredDistanceTo(player) -> -1
        a.squaredDistanceTo(player) > b.squaredDistanceTo(player) -> 1
        else -> 0
      }
    }.get(0) ?: return

    val text = "Hostile ${nearestEntity?.getName()?.asTruncatedString(17)} in reach!"

    context.drawText(textRenderer, text, (scaledWidth - textRenderer.getWidth(text)) / 2, 25, 0xFFFF0000.toInt(), true)
  }
}
