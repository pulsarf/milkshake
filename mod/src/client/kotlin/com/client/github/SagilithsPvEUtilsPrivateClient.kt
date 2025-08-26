package com.github

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.fabric.api.resource.ResourcePackActivationType

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
import com.client.github.feature.visual.*
import com.client.github.feature.elytra.*
import com.client.github.feature.combat.*
import com.client.github.feature.player.*
import com.client.github.components.Circle.*
import com.client.github.bootstrap.Tick
import com.client.github.clickgui.Composer

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
  
  var tabViewActive: Boolean = false
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
    HoldHit.bootstrap()

    WorldRenderEvents.BEFORE_DEBUG_RENDER.register(::renderWorld)

    val container = FabricLoader.getInstance().getModContainer("sagiliths-pve-utils-private")

    ResourceManagerHelper.registerBuiltinResourcePack(
      Identifier.of("sagiliths-pve-utils-private", "sagiliths-pve-utils-private"),
      container.get(),
      ResourcePackActivationType.ALWAYS_ENABLED
    )
	}

  private fun updateMouse() {
    val time = System.currentTimeMillis().toFloat()

    if (MC.mouse.wasLeftButtonClicked()) {
      if (!mouseHolding) {
        clicked = true
        mouseHolding = true
      }
    } else {
      mouseHolding = false 
    }

    if (MC.mouse.isCursorLocked())
      MC.mouse.unlockCursor()
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
 
  private fun render(
    context: DrawContext,
    tickCounter: RenderTickCounter
  ) {
    window = MC?.getWindow() ?: return
    textRenderer = MC?.textRenderer ?: return
    offset = textRenderer.fontHeight + 5
    height = FeatureConfig.tabsData.size * offset

    LagAlert.render(context)
    DangerMobAlert.render(context)

    renderHackList(window, context, FeatureConfig.tabsData.values.toList()) 

    if (tabKey.wasPressed())
      tabViewActive = !tabViewActive

    if (!tabViewActive) return

    Composer.render(context)

    updateMouse()
  }
}
