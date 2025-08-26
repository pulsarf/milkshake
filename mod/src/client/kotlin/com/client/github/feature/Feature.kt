package com.client.github.feature

import com.client.github.clickgui.*

object FeatureConfig {
  val config: HashMap<String, Boolean> = HashMap()
  val tabsData: HashMap<String, MutableList<String>> = HashMap()
  val textures = hashMapOf(
    "Combat" to "textures/item/iron_axe.png",
    "Elytra" to "textures/item/elytra.png",
    "Visual" to "textures/item/spyglass.png",
    "Player" to "textures/item/empty_slot_redstone_dust.png",
    "Utility" to "textures/item/ender_eye.png",
    "Boat" to "textures/item/birch_boat.png"
  )
  val tabContainers: HashMap<String, TabContainer> = HashMap()
}

open class Module(
  val featureGroup: String,
  val featureName: String
) {
  init {
    val featureSelector = Select<Unit>(name = featureName, description = "Not implemented", { true })

    if (":" in featureName) {
      val (name, subfeature) = featureName.split(":")

        
    }

    FeatureConfig.config.put(featureName, false)

    if (!FeatureConfig.tabsData.containsKey(featureGroup)) {
      FeatureConfig.tabsData.put(featureGroup, mutableListOf(featureName))
      FeatureConfig.tabContainers.put(featureGroup, TabContainer(
        name = featureGroup
      ))

      val container = FeatureConfig.tabContainers.get(featureGroup)

      featureSelector.initModule(this)

      container?.let { featureSelector.attach(container) }
    } else if ((FeatureConfig.tabsData.get(featureGroup)?.contains(featureName)?.not()) ?: true) {
      val container = FeatureConfig.tabContainers.get(featureGroup)

      featureSelector.initModule(this)

      FeatureConfig.tabsData.get(featureGroup)!!.add(featureName)

      container?.let { featureSelector.attach(container) }
    }
  }

  fun enabled(): Boolean = FeatureConfig.config.getOrDefault(featureName, false)
  fun disabled(): Boolean = enabled().not()

  fun enable() = FeatureConfig.config.put(featureName, true)
  fun invertState() = FeatureConfig.config.put(featureName, disabled())
}
