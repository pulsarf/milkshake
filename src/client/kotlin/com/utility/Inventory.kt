package com.client.github.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.SlotActionType

object Inventory {
    private val mc = MinecraftClient.getInstance()

    fun toNetworkSlot(slot: Int): Int {
		if (slot >= 0 && slot < 9) return slot + 36
		if (slot >= 36 && slot < 40) return 44 - slot
		if (slot == 40) return 45

		return slot;
	}

    fun findSlot(item: Class<*>): Int {
        for (slot in 0 until 40) {
            val stack = mc?.player?.getInventory()?.getStack(slot) ?: continue

            if (
                !stack.isEmpty() &&
                item.isAssignableFrom(stack.item::class.java)
            ) {
                return slot
            }
        }

        return -1
    }

    fun pickupSlot(slot: Int) {
        val player = mc.player ?: return
        val iManager = mc.interactionManager ?: return

        iManager.clickSlot(0, toNetworkSlot(slot), 0, SlotActionType.PICKUP, player)
    }
}
