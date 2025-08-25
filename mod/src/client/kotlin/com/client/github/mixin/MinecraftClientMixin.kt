package com.client.github.milkshake.mixin

import net.minecraft.client.MinecraftClient

import java.lang.Void
import java.util.concurrent.CompletableFuture

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(MinecraftClient::class)
abstract class MinecraftClientMixin {
  @Inject(
    method = ["Lnet/minecraft/client/MinecraftClient;reloadResources()Ljava/util/concurrent/CompletableFuture;"],
    at = [
      At("RETURN")
    ],
    cancellable = true
  )
  private fun _reloadResources(
    cir: CallbackInfoReturnable<CompletableFuture<Void>>
  ) {
    (this as MinecraftClient).setOverlay(null)
  }
}
