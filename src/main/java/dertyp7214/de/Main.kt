package dertyp7214.de

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import dertyp7214.de.CustomSmithingRecipe.Companion.DIAMOND_ELYTRA
import dertyp7214.de.CustomSmithingRecipe.Companion.NETHERITE_ELYTRA
import net.md_5.bungee.api.ChatColor.YELLOW
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UnstableApiUsage")
class Main : JavaPlugin() {

    companion object {
        val customSmithingRecipe: ArrayList<CustomSmithingRecipe> = ArrayList()
        var plugin: JavaPlugin? = null
            private set
    }

    private fun <K, V> Map<K, V>.toMultiMap(): Multimap<K, V> {
        return MultimapBuilder.hashKeys().arrayListValues().build<K, V>().apply {
            this@toMultiMap.forEach { (t, u) -> put(t, u) }
        }
    }

    override fun onEnable() {

        plugin = this

        val diamond = customElytra("Diamond Elytra", mapOf(
                Pair(
                        Attribute.GENERIC_ARMOR,
                        AttributeModifier(
                                UUID.randomUUID(),
                                "ARMOR",
                                8.0,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.CHEST
                        )
                ),
                Pair(
                        Attribute.GENERIC_ARMOR_TOUGHNESS,
                        AttributeModifier(
                                UUID.randomUUID(),
                                "ARMOR_TOUGHNESS",
                                2.0,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.CHEST
                        )
                )
        ).toMultiMap())
        val netherite = customElytra("Netherite Elytra", mapOf(
                Pair(
                        Attribute.GENERIC_ARMOR,
                        AttributeModifier(
                                UUID.randomUUID(),
                                "ARMOR",
                                8.0,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.CHEST
                        )
                ),
                Pair(
                        Attribute.GENERIC_ARMOR_TOUGHNESS,
                        AttributeModifier(
                                UUID.randomUUID(),
                                "ARMOR_TOUGHNESS",
                                3.0,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.CHEST
                        )
                ),
                Pair(
                        Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                        AttributeModifier(
                                UUID.randomUUID(),
                                "KNOCKBACK_RESISTANCE",
                                .1,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.CHEST
                        )
                )
        ).toMultiMap())

        customSmithingRecipe.add(CustomSmithingRecipe(
                SmithingRecipe(
                        NamespacedKey(this, "diamond_elytra"),
                        ItemStack(Material.ELYTRA),
                        RecipeChoice.MaterialChoice(Material.DIAMOND_CHESTPLATE),
                        RecipeChoice.MaterialChoice(Material.ELYTRA)),
                DIAMOND_ELYTRA,
                diamond
        ))
        customSmithingRecipe.add(CustomSmithingRecipe(SmithingRecipe(
                NamespacedKey(this, "netherite_elytra"),
                ItemStack(Material.ELYTRA),
                RecipeChoice.MaterialChoice(Material.NETHERITE_CHESTPLATE),
                RecipeChoice.MaterialChoice(Material.ELYTRA)),
                NETHERITE_ELYTRA,
                netherite
        ))
        customSmithingRecipe.add(CustomSmithingRecipe(SmithingRecipe(
                NamespacedKey(this, "diamond_elytra_to_netherite_elytra"),
                ItemStack(Material.ELYTRA),
                RecipeChoice.MaterialChoice(Material.ELYTRA),
                RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT)),
                NETHERITE_ELYTRA,
                netherite
        ) {
            val clickedInventory = it.clickedInventory
            (if (clickedInventory is SmithingInventory) {
                if (it.rawSlot == 2) {
                    val item0 = clickedInventory.getItem(0)
                    item0?.type == Material.ELYTRA && item0.itemMeta?.persistentDataContainer?.has(DIAMOND_ELYTRA, PersistentDataType.INTEGER) == true
                } else false
            } else false).apply { if (!this) it.isCancelled = true }
        })

        server.pluginManager.registerEvents(CustomSmithingRecipe.Event(customSmithingRecipe), this)
    }

    override fun onDisable() {
    }

    private fun customElytra(name: String, attributes: Multimap<Attribute, AttributeModifier>?): (meta: ItemMeta) -> Unit {
        return { meta ->
            meta.attributeModifiers = attributes
            meta.setDisplayName("${YELLOW}$name")
            if (meta is Damageable) meta.damage = 0
        }
    }
}