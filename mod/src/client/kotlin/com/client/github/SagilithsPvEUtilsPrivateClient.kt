package com.github

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.Identifier

import com.client.github.feature.FeatureConfig
import com.client.github.feature.visual.ExtrasensoryPerception
import com.client.github.feature.visual.Zoom
import com.client.github.feature.elytra.ElytraTiming
import com.client.github.feature.elytra.ElytraFlight
import com.client.github.feature.combat.KillAura
import com.client.github.feature.player.AntiFireDamage
import com.client.github.components.Circle.*
import com.client.github.bootstrap.Tick

import kotlin.math.*

infix fun Int.mod(mod: Int): Int = (this % mod + mod) % mod

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
  private lateinit var tabKey: KeyBinding
  
  private var tabViewActive: Boolean = false
  private var selectedGroup: String? = null
  private var mouseHolding: Boolean = false
  private var clicked: Boolean = false

  private val width = 85
  private var height = 0 
  private val x = 0
  private val y = 0
  private var offset = 0
  private var lastClick = 0f

	override fun onInitializeClient() {
    MC = MinecraftClient.getInstance()

    tabKey = KeyBindingHelper.registerKeyBinding(tabKeyBind)

    HudRenderCallback.EVENT.register(::render)

    Tick.listen()

    ExtrasensoryPerception.bootstrap()
    ElytraTiming.bootstrap()
    ElytraFlight.bootstrap()
    Zoom.bootstrap()
    KillAura.bootstrap()
    AntiFireDamage.bootstrap()

    WorldRenderEvents.BEFORE_DEBUG_RENDER.register(::renderWorld)
	}

  private fun updateMouse() {
    val time = System.currentTimeMillis().toFloat()

    if (MC.mouse.wasLeftButtonClicked()) {
      if (!mouseHolding && time - lastClick > 50f) {
        clicked = true
        mouseHolding = true

        //lastClick = time
      }
    } else {
      mouseHolding = false 
    }
  }

  private fun renderWorld(world: WorldRenderContext) {
    val stack = world?.matrixStack() ?: return
    val consumers = world?.consumers() ?: return
    
    ExtrasensoryPerception.render(stack, consumers)
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

  private fun renderFeatureSelection(context: DrawContext) {
    val group = FeatureConfig.tabsData.get(selectedGroup)
    val len = group?.size ?: return
    val scaledWidth = window.scaledWidth
    val scaledHeight = window.scaledHeight
    val buttonFloat = 60
    val mouseX = MC.mouse.getX().toFloat() / window.getScaleFactor().toFloat()
    val mouseY = MC.mouse.getY().toFloat() / window.getScaleFactor().toFloat()
    var dist = 10000f
    var minIndex = 0

    group?.forEachIndexed({ index, feature ->
      val x = ceil(scaledWidth / 2 + cos(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - 10
      val y = ceil(scaledHeight / 2 + sin(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - 10

      if (hypot(mouseX - x, mouseY - y) < dist) {
        dist = hypot(mouseX - x, mouseY - y)
        minIndex = index
      }
    })

    group?.forEachIndexed({ index, feature ->
      val defaultTexture = Identifier.ofVanilla(FeatureConfig.textures.getOrDefault(feature, "textures/item/spyglass.png"))

      val size = if (index == minIndex) 30 else 20
      val x = ceil(scaledWidth / 2 + cos(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - size / 2
      val y = ceil(scaledHeight / 2 + sin(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - size / 2
      val krovin = (if (FeatureConfig.config.getOrDefault(feature, false)) 0xFFA6FFA6 else 0xFFFFFFFF).toInt()

      if (size == 30 && clicked) {
        clicked = false

        if (feature == "Back") {
          selectedGroup = null

          return
        }

        FeatureConfig.config.put(feature, !FeatureConfig.config.getOrDefault(feature, false))
      }

      context.drawTexture(defaultTexture, x, y, 0f, 0f, size, size, size, size)
      context.drawText(textRenderer, feature, x - textRenderer.getWidth(feature) / 2 + size / 2, y + 35, krovin, true)
    })
  }

  private fun render(
    context: DrawContext,
    tickCounter: RenderTickCounter
  ) {
    window = MC?.getWindow() ?: return
    textRenderer = MC?.textRenderer ?: return
    offset = textRenderer.fontHeight + 5
    height = FeatureConfig.tabsData.size * offset

    renderHackList(window, context, FeatureConfig.tabsData.values.toList())

    if (tabKey.wasPressed())
      tabViewActive = !tabViewActive

    if (!tabViewActive) return

    updateMouse()

    selectedGroup?.let {
      renderFeatureSelection(context)
      
      return@render
    }

    val time = System.currentTimeMillis().toFloat() / 1700

    val scaledWidth = window.scaledWidth
    val scaledHeight = window.scaledHeight

    val len = FeatureConfig.tabsData.keys.size.toFloat()
    val buttonFloat = 60

    val mouseX = MC.mouse.getX().toFloat() / window.getScaleFactor().toFloat()
    val mouseY = MC.mouse.getY().toFloat() / window.getScaleFactor().toFloat()
    
    var dist = 10000f
    var minIndex = 0

    FeatureConfig.tabsData.keys.forEachIndexed { index, featureName ->
      val x = ceil(scaledWidth / 2 + cos(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - 10
      val y = ceil(scaledHeight / 2 + sin(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - 10

      if (hypot(mouseX - x, mouseY - y) < dist) {
        dist = hypot(mouseX - x, mouseY - y)
        minIndex = index
      }
    }

    FeatureConfig.tabsData.keys.forEachIndexed { index, featureName ->
      val defaultTexture = Identifier.ofVanilla(FeatureConfig.textures.get(featureName))

      val size = if (index == minIndex) 30 else 20
      val x = ceil(scaledWidth / 2 + cos(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - size / 2
      val y = ceil(scaledHeight / 2 + sin(index.toFloat() / len * PI.toFloat() * 2f) * buttonFloat).toInt() - size / 2

      if (size == 30 && clicked) {
        selectedGroup = featureName
        clicked = false
      }

      context.drawTexture(defaultTexture, x, y, 0f, 0f, size, size, size, size)
      context.drawText(textRenderer, featureName, x - textRenderer.getWidth(featureName) / 2 + size / 2, y + 35, 0xFFFFFFFF.toInt(), true)
    }
  }
}
