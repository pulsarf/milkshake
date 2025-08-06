package com.github.client.mixin

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

import com.client.github.feature.Module

@Mixin(ClientPlayerEntity::class)
abstract class AntiHungerMixin {
  @Shadow
  private var field_3920: Boolean = false

  internal val mod = Module("Player", "Anti hunger")

  @Inject(
    method = ["sendMovementPackets", "method_3136"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  private fun onSendMovementPackets(callbackInfo: CallbackInfo) {
    val mc = MinecraftClient.getInstance()

    if (!mod.enabled()) {
      return
    }
 
    if (
      mc == null ||
      mc.player == null ||
      mc.interactionManager == null
    ) {
      return
    }

    if (
      !mc.interactionManager!!.isBreakingBlock()
    ) {
      println("antihunger mixin works")

      this.field_3920 = false
    }
  }
}
