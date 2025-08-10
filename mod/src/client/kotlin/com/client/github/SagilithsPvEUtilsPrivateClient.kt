package com.github

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.render.WorldRenderer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

import com.client.github.feature.FeatureConfig
import com.client.github.feature.visual.ExtrasensoryPerception
import com.client.github.feature.elytra.ElytraTiming
import com.client.github.feature.elytra.ElytraFlight

import com.client.github.bootstrap.Tick

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
  private lateinit var MC: MinecraftClient
  private lateinit var window: Window
  private lateinit var textRenderer: TextRenderer

  private lateinit var upKey: KeyBinding
  private lateinit var downKey: KeyBinding
  private lateinit var tabKey: KeyBinding
  
  private var tabViewActive: Boolean = false
  private var tabIndex: Int = 0
  private var featureIndex: Int = 0
  private var offset: Int = 0

  private val width = 100
  private var height = 0 
  private val x = 0
  private val y = 0

	override fun onInitializeClient() {
    MC = MinecraftClient.getInstance()

    upKey = KeyBindingHelper.registerKeyBinding(upArrowBind)
    downKey = KeyBindingHelper.registerKeyBinding(downArrowBind)
    tabKey = KeyBindingHelper.registerKeyBinding(tabKeyBind)

    HudRenderCallback.EVENT.register(::render)

    println("========================================== WORK!")

    Tick.listen()

    ExtrasensoryPerception.bootstrap()
    ElytraTiming.bootstrap()
    ElytraFlight.bootstrap()

    WorldRenderEvents.BEFORE_DEBUG_RENDER.register(::renderWorld)
	}

  private fun renderWorld(world: WorldRenderContext) {
    val stack = world?.matrixStack() ?: return
    val consumers = world?.consumers() ?: return
    
    ExtrasensoryPerception.render(stack, consumers)
  }

  private fun renderFeature(
    x: Int,
    y: Int,
    feature: String,
    context: DrawContext,
    active: Boolean
  ) {
    val enabled = FeatureConfig.config.getOrDefault(feature, false)

    val color = when {
      active && enabled -> 0xFFBAF636.toInt()
      active -> 0xFFFFFFFF.toInt()
      enabled -> 0xFFA9F527.toInt()
      else -> 0xFFE6E6E6.toInt()
    }

    context.drawText(
      textRenderer,
      feature,
      x + (100 - textRenderer.getWidth(feature)) / 2,
      y + 5,
      color, 
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

    if (!tabViewActive && selectedFeature == "Back") {
      return false
    } else if (!tabViewActive) {
      FeatureConfig.config.put(selectedFeature, !FeatureConfig.config.getOrDefault(selectedFeature, false))

      tabViewActive = true
    }

    if (upKey!!.wasPressed()) featureIndex++
    if (downKey!!.wasPressed()) featureIndex--

    val contentHeight = offset * (featureList.size + 1) + 5
 
    context.fill(
      x, y, width, contentHeight,
      0x80000000.toInt()
    )

    context.drawBorder(
      x, y, width, contentHeight,
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
        index * offset,
        0xFFE6E6E6.toInt(),
        true
      )
    }
  }

  private fun render(
    context: DrawContext,
    tickCounter: RenderTickCounter
  ) {
    if ((
      ::window.isInitialized.not() || ::textRenderer.isInitialized.not()
    ) && (
      MC.getWindow() != null &&
      MC.textRenderer != null
    )) {
      window = MC.getWindow()
      textRenderer = MC.textRenderer

      offset = textRenderer.fontHeight + 5 
      height = FeatureConfig.tabsData.size * offset
      
      return
    } 

    window?.let {
      renderHackList(window as Window, context, FeatureConfig.tabsData.values.toList())
    }

    if (FeatureConfig.tabsData.size == 0) return

    if (renderContent(
      context, 
      FeatureConfig.tabsData.values.toList()[tabIndex mod FeatureConfig.tabsData.size],
      FeatureConfig.tabsData.keys.toList()[tabIndex mod FeatureConfig.tabsData.size]
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

    for ((index, value) in FeatureConfig.tabsData.entries.withIndex()) {
      val (group, featureList) = value
      val color = if (index == tabIndex mod FeatureConfig.tabsData.size) 0xFFA9F527.toInt() else 0xFFE6E6E6.toInt()
      
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
