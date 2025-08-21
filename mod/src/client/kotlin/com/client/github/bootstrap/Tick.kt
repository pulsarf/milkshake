package com.client.github.bootstrap

import com.client.github.feature.utility.*
import com.client.github.feature.elytra.*
import com.client.github.feature.visual.*
import com.client.github.feature.combat.*
import com.client.github.feature.player.*

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
      HoldHit.tick()
      AntiFireDamage.tick()
      Sprint.tick() // IMPORTANT: Sprint module should be ran after all hits, otherwise we'll get consta sweep hits
      NoJumpDelay.tick()
      LagAlert.tick()
      DangerMobAlert.tick()
    }
  }
}
