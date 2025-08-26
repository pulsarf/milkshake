package com.client.github.milkshake.mixin

import com.github.SagilithsPvEUtilsPrivateClient

import net.minecraft.client.Mouse

import com.client.github.feature.player.LiquidWalk

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Mouse::class)
abstract class MouseMixin {
  @Inject(
    method = ["Lnet/minecraft/client/Mouse;lockCursor()V"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  private fun _lockMouse(cir: CallbackInfo) {
    if (SagilithsPvEUtilsPrivateClient.tabViewActive) {
      cir.cancel()
    }
  }
}
