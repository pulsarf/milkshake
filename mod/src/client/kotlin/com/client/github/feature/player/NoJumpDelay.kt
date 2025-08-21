package com.client.github.feature.player

import net.minecraft.entity.LivingEntity
import net.minecraft.client.MinecraftClient

import com.client.github.feature.Module

object NoJumpDelay {
  internal val field = LivingEntity::class.java.getDeclaredField("field_6228")

  private val mod = Module(
    "Player",
    "No jump delay"
  )

  private val mc = MinecraftClient.getInstance()

  init {
    field.isAccessible = true
  }

  fun tick() {
    if (!mod.enabled()) return

    field.set(
      mc?.player ?: return,
      0
    )
  }
}
