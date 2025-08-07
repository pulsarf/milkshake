package com.client.github.feature.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerInteractionManager

import com.client.github.feature.Module

import java.lang.reflect.Field

// blockBreakingCooldown
val breakTargetField: String = "field_3716"

object FastBreak {
  val mod = Module(
    "Utility",
    "Fast break"
  )

  val blockBreakingCooldown: Field = ClientPlayerInteractionManager::class.java.getDeclaredField(breakTargetField)

  val mc = MinecraftClient.getInstance()

  fun tick() {
    if (!mod.enabled()) return  

    blockBreakingCooldown.isAccessible = true
    
    mc?.interactionManager?.let {
      blockBreakingCooldown.setInt(mc?.interactionManager, 0)
    }
  }
}
