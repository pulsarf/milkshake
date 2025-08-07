package com.client.github.feature.utility

import com.client.github.feature.Module

object FreeCam {
  val mod = Module(
    "Utility",
    "Free cam"
  )

  fun tick() { 
    if (!mod.enabled()) return
  }
}
