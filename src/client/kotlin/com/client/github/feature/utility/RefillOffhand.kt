package com.client.github.feature.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.client.toast.SystemToast
import net.minecraft.item.FireworkRocketItem
import net.minecraft.entity.player.PlayerInventory

import com.client.github.feature.Module
import com.client.github.utility.Inventory
import com.client.github.feature.elytra.ElytraTarget
import com.client.github.utility.Toast

object RefillOffhand : Module("Utility", "Refill offhand") {
    private val mc = MinecraftClient.getInstance()

    public enum class State {
        SearchItem,
        MoveToOffhand
    }

    private var state: State = State.SearchItem
    private var slotToMove = 0

    fun tick() {
        val player = mc.player ?: return
        val inventory = player.getInventory()

        val itemClass = when {
            ElytraTarget.enabled() -> FireworkRocketItem::class.java
            else -> return
        }

        when (state) {
            State.SearchItem -> {
                val slot = Inventory.findSlot(itemClass)
                val swappable = inventory.getSwappableHotbarSlot()

                if (
                    slot != -1 &&
                    !itemClass.isInstance(inventory.getStack(swappable).getItem())
                ) {
                    Inventory.pickupSlot(slot)

                    state = State.MoveToOffhand
                    slotToMove = slot
                }
            }
            State.MoveToOffhand -> {
                Inventory.pickupSlot(PlayerInventory.OFF_HAND_SLOT)
                Inventory.pickupSlot(slotToMove)

                state = State.SearchItem
            }
        }
    }
}
