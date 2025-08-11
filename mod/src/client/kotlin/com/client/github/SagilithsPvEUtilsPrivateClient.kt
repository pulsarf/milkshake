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
import com.client.github.feature.visual.Zoom
import com.client.github.feature.elytra.ElytraTiming
import com.client.github.feature.elytra.ElytraFlight
import com.client.github.feature.combat.KillAura

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

val leftArrowBind = KeyBinding(
  "key.move.left",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_LEFT,
  "net.minecraft.client.option"
)

val rightArrowBind = KeyBinding(
  "key.move.right",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_RIGHT,
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
  private lateinit var leftKey: KeyBinding
  private lateinit var rightKey: KeyBinding
  
  private var tabViewActive: Boolean = false
  private var tabIndex: Int = 0
  private var featureIndex: Int = 0
  private var offset: Int = 0

  private val width = 85
  private var height = 0 
  private val x = 0
  private val y = 0

	override fun onInitializeClient() {
    MC = MinecraftClient.getInstance()

    upKey = KeyBindingHelper.registerKeyBinding(upArrowBind)
    downKey = KeyBindingHelper.registerKeyBinding(downArrowBind)
    tabKey = KeyBindingHelper.registerKeyBinding(tabKeyBind)
    leftKey = KeyBindingHelper.registerKeyBinding(leftArrowBind)
    rightKey = KeyBindingHelper.registerKeyBinding(rightArrowBind)

    HudRenderCallback.EVENT.register(::render)

    println("========================================== WORK!")

    Tick.listen()

    ExtrasensoryPerception.bootstrap()
    ElytraTiming.bootstrap()
    ElytraFlight.bootstrap()
    Zoom.bootstrap()
    KillAura.bootstrap()

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
      x + (width - textRenderer.getWidth(feature)) / 2,
      y + 5,
      color, 
      true
    )
  } 

  private fun renderContent(
    context: DrawContext,
    featureList: List<String>,
    header: String,
    tabIndex: Int,
    selectedIndex: Int,
    pressed: Boolean
  ): Boolean {
    if (upKey!!.wasPressed()) featureIndex++
    if (downKey!!.wasPressed()) featureIndex--

    val contentHeight = offset * (featureList.size + 1) + 15
    val windowX = x + (tabIndex * (width + 15)) + 15

    context.drawBorder(windowX, 10, width, contentHeight, 0xFFFFFFFF.toInt())

    for ((index, feature) in featureList.withIndex()) {
      val windowY = y + (index + 1) * offset + 10 

      val active = index == featureIndex mod featureList.size && selectedIndex == tabIndex

      if (pressed && active) FeatureConfig.config.put(feature, !FeatureConfig.config.getOrDefault(feature, false))

      renderFeature(
        windowX,
        windowY,
        feature,
        context,
        active 
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

    val pressed = tabKey!!.wasPressed()

    for (ctabIndex in 0..<FeatureConfig.tabsData.size) renderContent(
      context, 
      FeatureConfig.tabsData.values.toList()[ctabIndex],
      FeatureConfig.tabsData.keys.toList()[ctabIndex],
      ctabIndex,
      tabIndex mod FeatureConfig.tabsData.size,
      pressed
    )

    if (rightKey?.wasPressed() ?: false) tabIndex++
    if (leftKey?.wasPressed() ?: false) tabIndex--

    for ((index, value) in FeatureConfig.tabsData.entries.withIndex()) {
      val (group, featureList) = value
      val color = if (index == tabIndex mod FeatureConfig.tabsData.size) 0xFFA9F527.toInt() else 0xFFE6E6E6.toInt()
      
      context.drawText(
        textRenderer,
        group,
        ((width + 15) * index) + 15 + (width - textRenderer.getWidth(group)) / 2,
        15,
        color,
        false
      )
    } 
  }
}
