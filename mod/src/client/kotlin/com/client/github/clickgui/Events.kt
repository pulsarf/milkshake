package com.client.github.clickgui

import net.minecraft.client.MinecraftClient

import org.lwjgl.glfw.GLFW

interface AbstractEvent { }

enum class ButtonTypes {
  LeftButton,
  MiddleButton,
  RightButton
}

class MouseMoveEvent(val mouseX: Float, val mouseY: Float) : AbstractEvent
class MouseHeldEvent(val type: ButtonTypes, val state: Boolean): AbstractEvent

object EventFactory {
  private val mc = MinecraftClient.getInstance()
  private val mouse = mc.mouse

  internal fun instantiateMouseMoveEvent(): MouseMoveEvent {
    val window = mc!!.getWindow()

    val x = mouse.getX() / window.getScaleFactor().toFloat()
    val y = mouse.getY() / window.getScaleFactor().toFloat()

    return MouseMoveEvent(x.toFloat(), y.toFloat())
  }

  internal fun instantiateMouseHeldEvents(): List<MouseHeldEvent> {
    val list = mutableListOf<MouseHeldEvent>()

    for (type in ButtonTypes.entries) {
      list += MouseHeldEvent(type, when(type) {
        ButtonTypes.LeftButton -> mouse.wasLeftButtonClicked()
        ButtonTypes.MiddleButton -> mouse.wasMiddleButtonClicked()
        ButtonTypes.RightButton -> mouse.wasRightButtonClicked()
        else -> false
      })
    }

    return list
  }

  fun update(): List<AbstractEvent> {
    return listOf(instantiateMouseMoveEvent()) + instantiateMouseHeldEvents()
  }
}
