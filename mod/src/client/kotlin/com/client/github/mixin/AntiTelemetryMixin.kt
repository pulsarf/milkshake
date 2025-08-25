package com.client.github.milkshake.mixin

import net.minecraft.client.MinecraftClient
import net.minecraft.client.session.telemetry.TelemetryManager
import com.mojang.authlib.minecraft.UserApiService
import net.minecraft.client.session.Session
import net.minecraft.client.session.telemetry.TelemetrySender

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/*
 * This is hell
**/

@Mixin(TelemetryManager::class)
abstract class AntiTelemetryMixin {
  @Inject(
    method = ["Lnet/minecraft/client/session/telemetry/TelemetryManager;getSender()Lnet/minecraft/client/session/telemetry/TelemetrySender;"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  private fun _getSender(
    cir: CallbackInfoReturnable<TelemetrySender>
  ) {
    cir.setReturnValue(TelemetrySender.NOOP)
  }

  @Inject(
    method = ["Lnet/minecraft/client/session/telemetry/TelemetryManager;computeSender()Lnet/minecraft/client/session/telemetry/TelemetrySender;"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  private fun _computeSender(
    cir: CallbackInfoReturnable<TelemetrySender>
  ) {
    cir.setReturnValue(TelemetrySender.NOOP)
  }
}
