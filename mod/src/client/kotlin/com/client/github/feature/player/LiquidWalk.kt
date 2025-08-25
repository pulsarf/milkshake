package com.client.github.feature.player

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.block.FluidBlock
import net.minecraft.util.math.BlockPos

import com.client.github.feature.Module

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object LiquidWalk : Module(
  "Player",
  "Liquid walk"
) {
  private val mc = MinecraftClient.getInstance()
  private lateinit var waterPos: BlockPos
  private val spoofOnGround = Module("Player", "Liquid walk:Spoof on ground")

  private fun getMostTopFluidPos(entity: Entity): BlockPos? { 
    (0..3).forEach {
      val blockPos = if (entity.isInFluid()) entity.getBlockPos().up(it) else entity.getBlockPos().down(it)
      val state = entity.getWorld().getFluidState(blockPos)
      val block = state.getBlockState().getBlock()

      if (block !is FluidBlock) {
        return@getMostTopFluidPos if (::waterPos.isInitialized) waterPos else null
      }

      waterPos = blockPos

      if (!entity.isInFluid) return@getMostTopFluidPos waterPos
    }

    return null
  }

  fun tick(that: Entity, cir: CallbackInfoReturnable<Boolean>) {
    if (!enabled()) return
    if (that?.getUuid() != mc?.player?.getUuid()) return
     
    with(that) {
      val player = mc.player ?: return@tick

      if (isRegionUnloaded()) return
      if (isInFluid()) {
        val vel = getVelocity()
        
        setVelocity(0.0, 0.3, 0.0)
        return
      }

      getMostTopFluidPos(this)?.let {
        val yMod = it.up().getY().toDouble()

        if (player.getY() <= yMod || player.isFallFlying()) return

        setPosition(pos.getX(), yMod, pos.getZ())
        if (spoofOnGround.enabled()) setOnGround(true)
      }
    }
  }
}

