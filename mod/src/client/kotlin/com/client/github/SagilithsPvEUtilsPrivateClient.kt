package com.github

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window

import com.client.github.feature.FeatureConfig

infix fun Int.mod(mod: Int): Int = (this % mod + mod) % mod

val upArrowBind = KeyBinding(
  "key.move.back",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_DOWN,
  "net.minecraft.client.option"
)

val downArrowBind = KeyBinding(
  "key.move.up",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_UP,
  "net.minecraft.client.option"
)

val tabKeyBind = KeyBinding(
  "key.strafe.left",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_TAB,
  "net.minecraft.client.option"
)

object SagilithsPvEUtilsPrivateClient : ClientModInitializer {
  var MC: MinecraftClient? = null

  var upKey: KeyBinding? = null
  var downKey: KeyBinding? = null
  var tabKey: KeyBinding? = null
  
  var tabViewActive: Boolean = false

  var tabIndex: Int = 0
  var featureIndex: Int = 0

	override fun onInitializeClient() {
    MC = MinecraftClient.getInstance()

    upKey = KeyBindingHelper.registerKeyBinding(upArrowBind)
    downKey = KeyBindingHelper.registerKeyBinding(downArrowBind)
    tabKey = KeyBindingHelper.registerKeyBinding(tabKeyBind)

    HudRenderCallback.EVENT.register(::render)
	}

  private fun renderFeature(
    x: Int,
    y: Int,
    feature: String,
    context: DrawContext,
    active: Boolean
  ) {
    val enabled = FeatureConfig.config.getOrDefault(feature, false)

    context.drawText(
      MC!!.textRenderer,
      feature,
      x + (100 - MC!!.textRenderer.getWidth(feature)) / 2,
      y + 5,
      if (active) {
        if (enabled) 0xFFBAF638.toInt() else 0xFFFFFFFF.toInt() 
      } else {
        if (enabled) 0xFFA9F527.toInt() else 0xFFE6E6E6.toInt()
      },
      true
    )
  }

  private fun renderContent(
    context: DrawContext,
    featureList: List<String>,
    header: String
  ): Boolean {
    if (tabKey!!.wasPressed()) {
      tabViewActive = !tabViewActive
    }

    val selectedFeature = featureList[featureIndex mod featureList.size]

    val window = MC!!.getWindow()

    if (!tabViewActive && selectedFeature == "Back") {
      return false
    } else if (!tabViewActive) {
      FeatureConfig.config.put(selectedFeature, !FeatureConfig.config.getOrDefault(selectedFeature, false))

      tabViewActive = true
    }

    if (upKey!!.wasPressed()) featureIndex++
    if (downKey!!.wasPressed()) featureIndex--
 
    val textRenderer = MC!!.textRenderer

    val offset = textRenderer.fontHeight + 5

    val width = 100
    val height = offset * (featureList.size + 1) + 5

    val x = 0
    val y = 0

    context.fill(
      x, y, width, height,
      0x80000000.toInt()
    )

    context.drawBorder(
      x, y, width, height,
      0xFFFFFFFF.toInt()
    )

    context.drawText(
      textRenderer,
      header,
      (100 - textRenderer.getWidth(header)) / 2,
      y + 5,
      0xFFAAAAAA.toInt(),
      true
    )

    for ((index, feature) in featureList.withIndex()) {
      renderFeature(
        x,
        y + (index + 1) * offset,
        feature,
        context,
        index == featureIndex mod featureList.size
      )
    }

    return true
  }

  private fun renderHackList(
    window: Window,
    context: DrawContext,
    featureList: List<List<String>>
  ) {
    var featureList = featureList.flatten()
    val textRenderer = MC!!.textRenderer 

    val activeFeatures = featureList.filter { feature -> FeatureConfig.config.getOrDefault(feature, false) }
    
    if (activeFeatures.isEmpty()) {
      context.drawText(
        textRenderer,
        "Legit, no hacks",
        window.scaledWidth - textRenderer.getWidth("Legit, no hacks") - 5,
        5,
        0xFFA9F527.toInt(),
        true
      )

      return
    }

    activeFeatures.forEachIndexed { index, feature -> 
      context.drawText(
        textRenderer,
        feature,
        window.scaledWidth - textRenderer.getWidth(feature) - 5,
        index * textRenderer.fontHeight + 5,
        0xFFE6E6E6.toInt(),
        true
      )
    }
  }

  private fun render(
    context: DrawContext,
    tickCounter: RenderTickCounter
  ) {
    val tabsData = FeatureConfig.tabsData

    val textRenderer = MC!!.textRenderer

    val offset = textRenderer.fontHeight + 5

    val height = tabsData.size * offset

    val window = MC!!.getWindow()

    renderHackList(window, context, tabsData.values.toList())

    if (renderContent(
      context, 
      tabsData.values.toList()[tabIndex mod tabsData.size],
      tabsData.keys.toList()[tabIndex mod tabsData.size]
    )) return

    featureIndex = 0

    context.fill(0, 0, 100, 80, 0x80000000.toInt())
    context.drawBorder(0, 0, 100, 80, 0xFFFFFFFF.toInt());

    context.drawText(
      textRenderer,
      "Milkshake",
      (100 - textRenderer.getWidth("Milkshake")) / 2,
      5,
      0xFFAAAAAA.toInt(),
      true
    )

    if (upKey?.wasPressed() ?: false) tabIndex++
    if (downKey?.wasPressed() ?: false) tabIndex--

    for ((index, value) in tabsData.entries.withIndex()) {
      val (group, featureList) = value
      val color = if (index == tabIndex mod tabsData.size) 0xFFA9F527.toInt() else 0xFFE6E6E6.toInt()
      
      context.drawText(
        textRenderer,
        group,
        (100 - textRenderer.getWidth(group)) / 2,
        (index + 1) * offset + 5,
        color,
        false
      )
    } 
  }
}
