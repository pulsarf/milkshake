package com.client.github.feature

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
}

open class Module(
  val featureGroup: String,
  val featureName: String
) {
  init {
    FeatureConfig.config.put(featureName, false)

    if (!FeatureConfig.tabsData.containsKey(featureGroup)) {
      FeatureConfig.tabsData.put(featureGroup, mutableListOf("Back", featureName)) 
    } else if ((FeatureConfig.tabsData.get(featureGroup)?.contains(featureName)?.not()) ?: true) {
      FeatureConfig.tabsData.get(featureGroup)!!.add(featureName)
    }
  }

  fun enabled(): Boolean = FeatureConfig.config.getOrDefault(featureName, false)
  fun disabled(): Boolean = enabled().not()

  fun enable() {
    FeatureConfig.config.put(featureName, true)
  }
}
