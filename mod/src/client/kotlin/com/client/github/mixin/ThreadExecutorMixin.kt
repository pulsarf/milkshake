package com.client.github.milkshake.mixin

import net.minecraft.util.thread.ThreadExecutor

import java.util.concurrent.locks.LockSupport

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite

@Mixin(ThreadExecutor::class)
abstract class ThreadExecutorMixin {
  @Overwrite
  open fun waitForTasks() = LockSupport.parkNanos("waiting for tasks", 500000)
}
