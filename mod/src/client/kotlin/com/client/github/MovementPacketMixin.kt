package com.client.github.milkshake.mixin.antihunger

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.entity.Entity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.Mutable
import org.spongepowered.asm.mixin.gen.Accessor

import org.objectweb.asm.Opcodes

import com.client.github.feature.player.AntiHunger
import com.client.github.feature.player.NoFall

@Mixin(PlayerMoveC2SPacket::class)
abstract class AntiHungerMixin {
  @Accessor("onGround")
  protected abstract fun getOnGround(): Boolean
    
  @Mutable
  @Accessor("onGround")
  protected abstract fun setOnGround(value: Boolean)

  @Inject(
    method = ["<init>(DDDFFZZZ)V"],
    at = [At(
      value = "FIELD",
      target = "Lnet/minecraft/class_2828;field_29179:Z",
      opcode = Opcodes.PUTFIELD,
      shift = At.Shift.AFTER
    )]
  )
  private fun onGround(callbackInfo: CallbackInfo) {
    val mc = MinecraftClient.getInstance()

    if (mc == null || mc.player == null || mc.interactionManager == null) return

    if (
      NoFall.mod.enabled() &&
      (mc.player as Entity).getVelocity().y < -0.5
    ) {
      setOnGround(true)
    } else if (
      AntiHunger.mod.enabled() &&
      !mc.interactionManager!!.isBreakingBlock()
    ) {
      setOnGround(false)
    }
  }
}
