package com.client.github.feature.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.SimpleOption

import com.client.github.feature.Module

import java.lang.reflect.Field

object FullBright {
  val mod = Module(
    "Utility",
    "Full brightness"
  )

  val mc = MinecraftClient.getInstance()

  val field = SimpleOption::class.java.getDeclaredField("field_37868") // value field

  fun tick() { 
    if (!mod.enabled()) return
 
    val gamma = mc?.options?.getGamma()

    gamma?.let {
      field.isAccessible = true
      field.set(gamma, 2.0)
    }
  }
}
