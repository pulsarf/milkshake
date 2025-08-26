package com.client.github.milkshake.mixin

import net.minecraft.client.util.NarratorManager
import net.minecraft.client.util.NarratorManager.InactiveNarratorLibraryException

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite

@Mixin(NarratorManager::class)
abstract class NarratorManagerMixin {
  @Overwrite
  fun method_52183(active: Boolean): Nothing = throw InactiveNarratorLibraryException("Narrator library disabled")
}
