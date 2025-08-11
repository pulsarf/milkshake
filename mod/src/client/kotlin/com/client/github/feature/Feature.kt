package com.client.github.feature

object FeatureConfig {
  val config: HashMap<String, Boolean> = HashMap()
  val tabsData: HashMap<String, MutableList<String>> = HashMap()
}

open class Module(
  val featureGroup: String,
  val featureName: String
) {
  init {
    FeatureConfig.config.put(featureName, false)

    if (!FeatureConfig.tabsData.containsKey(featureGroup)) {
      FeatureConfig.tabsData.put(featureGroup, mutableListOf(featureName)) 
    } else if ((FeatureConfig.tabsData.get(featureGroup)?.contains(featureName)?.not()) ?: true) {
      FeatureConfig.tabsData.get(featureGroup)!!.add(featureName)
    }
  }

  open fun enabled(): Boolean = FeatureConfig.config.getOrDefault(featureName, false)
}
