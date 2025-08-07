package com.client.github.feature.utility

import net.minecraft.client.MinecraftClient

import com.client.github.feature.Module

import java.lang.reflect.Field

// itemUseCooldown
val itemTargetField: String = "field_1752" 

object FastPlace {
  val mod = Module(
    "Utility",
    "Fast place"
  )

  val mc = MinecraftClient.getInstance()

  val itemUseCooldown = MinecraftClient::class.java.getDeclaredField(itemTargetField)

  fun tick() {
    if (!mod.enabled()) return 

    mc?.let {
      itemUseCooldown.set(mc, 0)
    }
  }
}
