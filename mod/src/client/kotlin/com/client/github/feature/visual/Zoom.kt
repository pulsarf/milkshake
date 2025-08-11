package com.client.github.feature.visual

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

import com.client.github.feature.Module

val zoomKeyBind = KeyBinding(
  "Zoom key",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_V,
  "net.minecraft.client.option"
)

object Zoom {
  private val status: Boolean = false
  private lateinit var mc: MinecraftClient
  private var oldFov: Int? = null
  private lateinit var zoomBind: KeyBinding

  public val zoomMultiplier = 3

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
    zoomBind = KeyBindingHelper.registerKeyBinding(zoomKeyBind)
  }

  fun tick() {
    if (!zoomBind.wasPressed()) return

    if (oldFov != null) {
      mc.options.getFov().setValue(oldFov)

      oldFov = null
    } else {
      oldFov = mc.options.getFov().getValue()

      mc.options.getFov().setValue(oldFov?.div(zoomMultiplier))
    }
  }
}
