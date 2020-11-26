package dertyp7214.de

import net.md_5.bungee.api.ChatColor.WHITE
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.SmithingInventory
import org.bukkit.inventory.SmithingRecipe
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min

class CustomSmithingRecipe(private val smithingRecipe: SmithingRecipe, private val customMeta: (meta: ItemMeta) -> Unit = {}) {

    companion object {
        private val CUSTOM_DURABILITY = NamespacedKey(Main.plugin!!, "customDurability")
        private val CUSTOM_DURABILITY_MAX = NamespacedKey(Main.plugin!!, "customDurabilityMax")

        private fun changeCustomDurability(itemMeta: ItemMeta, value: Int) {
            itemMeta.persistentDataContainer.apply {
                var dur = get(CUSTOM_DURABILITY, PersistentDataType.INTEGER)
                val durMax = get(CUSTOM_DURABILITY_MAX, PersistentDataType.INTEGER)
                if (dur != null && durMax != null) {
                    dur = max(dur - value, 0)
                    set(CUSTOM_DURABILITY, PersistentDataType.INTEGER, dur)
                    itemMeta.lore = listOf(
                            "", "${WHITE}Durability: $dur / $durMax"
                    )
                }
            }
        }
    }

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
                            meta.persistentDataContainer.set(CUSTOM_DURABILITY, PersistentDataType.INTEGER, item0.type.maxDurability.toInt())
                            meta.persistentDataContainer.set(CUSTOM_DURABILITY_MAX, PersistentDataType.INTEGER, item0.type.maxDurability.toInt())
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

        @EventHandler
        fun onDamage(event: EntityDamageByEntityEvent) {
            val player = event.entity
            if (player is Player) {
                player.inventory.chestplate?.apply {
                    val meta = itemMeta
                    if (meta is Damageable) {
                        val value = meta.persistentDataContainer.get(CUSTOM_DURABILITY, PersistentDataType.INTEGER)
                        if (value != null && value > 0) {
                            if (event.damage >= 1) changeCustomDurability(meta, (event.damage / 4).toInt())
                        } else {
                            var damage = event.getDamage(EntityDamageEvent.DamageModifier.ARMOR)
                            damage += .18
                            damage += .04 * getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)
                            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, damage)
                        }
                    }
                    itemMeta = meta
                }
            }
        }

        @EventHandler
        fun mending(event: PlayerItemMendEvent) {
            val meta = event.item.itemMeta
            if (meta is Damageable) {
                val value = meta.persistentDataContainer.get(CUSTOM_DURABILITY, PersistentDataType.INTEGER)
                val valueMax = meta.persistentDataContainer.get(CUSTOM_DURABILITY_MAX, PersistentDataType.INTEGER)
                if (value != null && valueMax != null) changeCustomDurability(meta, -min(event.repairAmount, valueMax - value))
                event.item.itemMeta = meta
            }
        }

        @EventHandler
        fun xp(event: PlayerExpChangeEvent) {
            val meta = event.player.inventory.chestplate?.itemMeta
            if (meta is Damageable) {
                val value = meta.persistentDataContainer.get(CUSTOM_DURABILITY, PersistentDataType.INTEGER)
                val valueMax = meta.persistentDataContainer.get(CUSTOM_DURABILITY_MAX, PersistentDataType.INTEGER)
                if (value != null && valueMax != null) {
                    val diff = valueMax - value
                    val remainingXp = event.amount - (min(event.amount * 2, diff) / 2)
                    changeCustomDurability(meta, -min(event.amount * 2, valueMax - value))
                    event.amount = remainingXp
                }
                event.player.inventory.chestplate?.itemMeta = meta
            }
        }
    }
}