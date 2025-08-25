package com.client.github.clickgui

import com.client.github.components.*

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
  
  fun render(matrix: Matrix4f, drawContext: DrawContext)
  fun attach(container: AbstractContainer)
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

class TabContainer(val name: String) : AbstractContainer {
  override val x = 50f
  override val y = 50f

  val padding = 7f

  override val width: Float = 80f

  var offsetation = 0f

  var elements: MutableList<AbstractElement> = mutableListOf()

  override fun move(offset: Float): Float {
    offsetation += offset + 3f

    return offsetation
  }

  override fun resetOffset() {
    offsetation = x
  }

  init {
    resetOffset()
  }

  override fun runPreRenderTask(matrix: Matrix4f, drawContext: DrawContext) {
    val startX = x - padding
    val startY = y
    val width = width + padding * 2
    val height = (elements.size + 1) * 20f + padding * 2

    Box.create(startX, startY, width, height, drawContext, 0x80000000.toInt())
    Text.create(name, x, y - 20f - padding / 2, drawContext, 0xFFFFFFFF.toInt(), true, width, height)
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

  private var width = 0f

  private val offset = 20f

  private val color = 0xAA000000.toInt()
  private val fontColor = 0xFFFFFFFF.toInt()

  override fun render(matrix: Matrix4f, drawContext: DrawContext) {
    Box.create(x, y, width, offset, drawContext, color)
    Text.create(name, x, y, drawContext, fontColor, true, width, offset)
    Arrow.create(x + width - offset, y + offset / 2, matrix, fontColor, PI / 2)
  }

  override fun attach(container: AbstractContainer) {
    x = container.x
    width = container.width

    y = container.move(offset)
    
    container.appendChild(this)
  }
}

object Composer {
  val testSelect = Select<Int>(name = "Test", description = "Lorem impsum", { true })
  val testSelect1 = Select<Int>(name = "Test1", description = "ermmm what the sigma", { true })

  val testContainer = TabContainer("TestContainer")

  init { testSelect.attach(testContainer); testSelect1.attach(testContainer) }

  internal fun makePipeline(container: AbstractContainer, matrix: Matrix4f, context: DrawContext, wrapped: () -> Unit) {
    container.runPreRenderTask(matrix, context)

    wrapped()

    container.resetOffset()
  }

  fun render(context: DrawContext) {
    val matrix = context.getMatrices().peek().getPositionMatrix()
    
    makePipeline(testContainer, matrix, context) {
      testContainer.renderAll(matrix, context)
    }
  }
}

