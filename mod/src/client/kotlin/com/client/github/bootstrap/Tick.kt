package com.client.github.bootstrap

import com.client.github.feature.utility.FastBreak
import com.client.github.feature.utility.FastPlace
import com.client.github.feature.utility.FreeCam
import com.client.github.feature.utility.FullBright
import com.client.github.feature.elytra.ElytraTiming
import com.client.github.feature.elytra.ElytraFlight
import com.client.github.feature.visual.Zoom
import com.client.github.feature.combat.KillAura
import com.client.github.feature.player.AntiFireDamage

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object Tick {
  fun listen() {
    ClientTickEvents.START_CLIENT_TICK.register {
      FastBreak.tick()
      FastPlace.tick()
      FreeCam.tick()
      FullBright.tick()
      ElytraTiming.tick()
      ElytraFlight.tick()
      Zoom.tick()
      KillAura.tick()
      AntiFireDamage.tick()
    }
  }
}
