package dertyp7214.de

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.SmithingInventory
import org.bukkit.inventory.SmithingRecipe
import org.bukkit.inventory.meta.ItemMeta

class CustomSmithingRecipe(private val smithingRecipe: SmithingRecipe, private val customMeta: (meta: ItemMeta) -> Unit = {}) {

    private fun register() {
        Bukkit.addRecipe(smithingRecipe)
    }

    private fun modifyItem(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory

        if (clickedInventory is SmithingInventory) {
            if (event.rawSlot == 2) {
                val item0 = clickedInventory.getItem(0)
                val item1 = clickedInventory.getItem(1)
                val item2 = clickedInventory.getItem(2)

                if (item0?.type == smithingRecipe.base.itemStack.type && item1?.type == smithingRecipe.addition.itemStack.type && item2 != null) {
                    item2.apply {
                        val meta = itemMeta
                        if (meta != null) {
                            customMeta(meta)
                        }
                        itemMeta = meta
                    }
                }
            }
        }
    }

    class Event(private val recipes: List<CustomSmithingRecipe>) : Listener {

        init {
            recipes.forEach { it.register() }
        }

        @EventHandler
        fun onClick(event: InventoryClickEvent) {
            recipes.forEach {
                it.modifyItem(event)
            }
        }
    }
}