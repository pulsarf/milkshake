package com.github.client.mixin

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow

import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientPlayerEntity::class)
abstract class AntiHungerMixin {
  @Shadow
  private var field_3920: Boolean = false

  @Inject(
    method = ["sendMovementPackets", "method_3136"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  private fun onSendMovementPackets(callbackInfo: CallbackInfo) {
    val mc = MinecraftClient.getInstance()

    if (
      mc == null ||
      mc.player == null ||
      mc.interactionManager == null
    ) {
      return
    }

    if (
      mc.player!!.isOnGround &&
      mc.player!!.fallDistance <= 0.2 &&
      mc.interactionManager!!.isBreakingBlock()
    ) {
      this.field_3920 = false
    }
  }
}
