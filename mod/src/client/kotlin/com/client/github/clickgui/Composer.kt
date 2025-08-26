package com.client.github.clickgui

import com.client.github.components.*
import com.client.github.clickgui.*
import com.client.github.feature.Module

import org.joml.Matrix4f

import net.minecraft.client.gui.DrawContext

import kotlin.math.*

/**
 * Fast note: Box::create(scaledX: Float, scaledY: Float, scaledW: Float, scaledH: Float, matrix: Matrix4f, color: Int)
 * Text::create(text: String, x: Float, y: Float, drawContext: DrawContext, color: Int, centered?: Boolean)
 * Arrow::create(x: Float, y: Float, matrix: Matrix4f, color: Int, angle: Double)
**/

interface AbstractElement {
  val name: String
  val description: String

  var mouseX: Float
  var mouseY: Float
  
  fun render(matrix: Matrix4f, drawContext: DrawContext)
  fun attach(container: AbstractContainer)
  fun isHovered(): Boolean
}

interface AbstractContainer {
  val x: Float
  val y: Float

  val width: Float

  fun move(offset: Float): Float
  fun runPreRenderTask(matrix: Matrix4f, drawContext: DrawContext)
  fun renderAll(matrix: Matrix4f, drawContext: DrawContext)
  fun resetOffset()

  fun appendChild(child: AbstractElement)
}

val renderElements = HashMap<String, AbstractElement>()
var tabContainers = 0

class TabContainer(val name: String) : AbstractContainer {
  override var x = 7f
  override val y = 7f

  val padding = 7f

  override val width: Float = 75f

  var offsetation = 0f

  var elements: MutableList<AbstractElement> = mutableListOf()

  override fun move(offset: Float): Float {
    offsetation += offset + 3f

    return offsetation
  }

  override fun resetOffset() {
    offsetation = y
  }

  init {
    x += (tabContainers++) * (padding * 3 + width)

    resetOffset()
    
    Composer.addElementToPipeline(this)
  }

  override fun runPreRenderTask(matrix: Matrix4f, drawContext: DrawContext) {
    val startX = x - padding
    val startY = y
    val width = width + padding * 2
    val height = (elements.size + 1) * 23f + padding

    Box.create(startX, startY, width, height, drawContext, 0x80000000.toInt())
    Text.create(name, startX, startY, drawContext, 0xFFFFFFFF.toInt(), true, width, 20f)
  }

  override fun renderAll(matrix: Matrix4f, drawContext: DrawContext) = elements.forEach { it.render(matrix, drawContext) }
  override fun appendChild(child: AbstractElement) { elements.add(child) }
}

class Select<in T>(
  override val name: String,
  override val description: String,
  val rangeLimited: (T) -> Boolean
) : AbstractElement {
  private var x = 0f
  private var y = 0f

  override var mouseX = 0f
  override var mouseY = 0f

  private var width = 0f

  private val offset = 20f

  private val color = 0xAA000000.toInt()
  private val activeColor = 0xFFFFFFFF.toInt() - color
  private val enabledColor = activeColor / 2

  private val fontColor = 0xFFFFFFFF.toInt()

  private var state = false
  private var alreadyClicked = false

  private lateinit var module: Module
  private lateinit var parentContainer: AbstractContainer
 
  fun initModule(that: Module) {
    module = that
  }

  private fun switchState() {
    if (!alreadyClicked) {
      state = !state
      alreadyClicked = true

      if (::module.isInitialized) {
        module.invertState()
      }
    }
  }

  fun updatePosition() {
    if (!::parentContainer.isInitialized) return

    x = parentContainer.x
    width = parentContainer.width

    y = parentContainer.move(offset)
  }

  fun updateEvents() {
    val events = EventFactory.update()

    events.forEach { event ->
      when {
        event is MouseMoveEvent -> {
          mouseX = event.mouseX
          mouseY = event.mouseY
        }
        event is MouseHeldEvent -> {
          if (!event.state) {
            if (event.type == ButtonTypes.LeftButton) alreadyClicked = false

            return
          }

          when(event.type) {
            ButtonTypes.LeftButton -> {
              if (isHovered()) switchState()
            }
            ButtonTypes.RightButton -> {

            }
            else -> { }
          }
        }
      }
    }
  }

  override fun isHovered(): Boolean {
    return mouseX > x && mouseX < x + width &&
      mouseY > y && mouseY < y + offset
  }

  override fun render(matrix: Matrix4f, drawContext: DrawContext) {
    updateEvents()
    updatePosition()

    Box.create(x, y, width, offset, drawContext, if (isHovered()) activeColor else if (state) enabledColor else color)
    Text.create(name, x, y, drawContext, fontColor, true, width, offset)
    Arrow.create(x + width - offset, y + offset / 2, matrix, fontColor, PI / 2)
  }

  override fun attach(container: AbstractContainer) {
    parentContainer = container

    x = container.x
    width = container.width

    y = container.move(offset)
    
    container.appendChild(this)
  }
}

object Composer {
  val elements: MutableList<AbstractContainer> = mutableListOf()

  internal fun makePipeline(container: AbstractContainer, matrix: Matrix4f, context: DrawContext, wrapped: () -> Unit) {
    container.runPreRenderTask(matrix, context)

    wrapped()

    container.resetOffset()
  }

  fun addElementToPipeline(element: AbstractContainer) = elements.add(element)

  fun render(context: DrawContext) {
    val matrix = context.getMatrices().peek().getPositionMatrix()
    
    elements.forEach { makePipeline(it, matrix, context) { it.renderAll(matrix, context) } }
  }
}

