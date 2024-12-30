package de.dertyp7214

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import de.dertyp7214.CustomSmithingRecipe.Companion.DIAMOND_ELYTRA
import de.dertyp7214.CustomSmithingRecipe.Companion.NETHERITE_ELYTRA
import de.dertyp7214.CustomSmithingRecipe.Companion.NETHERITE_MULTITOOL
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.fusesource.jansi.Ansi

@Suppress("UnstableApiUsage")
class NetheriteElytra : JavaPlugin() {

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

        val diamond = customElytra(
            "Diamond Elytra", mapOf(
                Pair(
                    Attribute.GENERIC_ARMOR,
                    AttributeModifier(
                        NamespacedKey(this, "ARMOR"),
                        8.0,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                    )
                ),
                Pair(
                    Attribute.GENERIC_ARMOR_TOUGHNESS,
                    AttributeModifier(
                        NamespacedKey(this, "ARMOR_TOUGHNESS"),
                        2.0,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                    )
                )
            ).toMultiMap()
        )
        val netherite = customElytra(
            "Netherite Elytra", mapOf(
                Pair(
                    Attribute.GENERIC_ARMOR,
                    AttributeModifier(
                        NamespacedKey(this, "ARMOR"),
                        8.0,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                    )
                ),
                Pair(
                    Attribute.GENERIC_ARMOR_TOUGHNESS,
                    AttributeModifier(
                        NamespacedKey(this, "ARMOR_TOUGHNESS"),
                        3.0,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                    )
                ),
                Pair(
                    Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                    AttributeModifier(
                        NamespacedKey(this, "KNOCKBACK_RESISTANCE"),
                        .1,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                    )
                )
            ).toMultiMap()
        )

        customSmithingRecipe.add(
            CustomSmithingRecipe(
                SmithingTransformRecipe(
                    NamespacedKey(this, "diamond_elytra"),
                    ItemStack(Material.ELYTRA).apply {
                        val meta = itemMeta
                        if (meta != null) diamond(meta)
                        itemMeta = meta
                    },
                    RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK),
                    RecipeChoice.MaterialChoice(Material.DIAMOND_CHESTPLATE),
                    RecipeChoice.MaterialChoice(Material.ELYTRA)
                ),
                DIAMOND_ELYTRA,
                diamond
            )
        )
        customSmithingRecipe.add(
            CustomSmithingRecipe(
                SmithingTransformRecipe(
                    NamespacedKey(this, "netherite_elytra"),
                    ItemStack(Material.ELYTRA).apply {
                        val meta = itemMeta
                        if (meta != null) netherite(meta)
                        itemMeta = meta
                    },
                    RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                    RecipeChoice.MaterialChoice(Material.NETHERITE_CHESTPLATE),
                    RecipeChoice.MaterialChoice(Material.ELYTRA)
                ),
                NETHERITE_ELYTRA,
                netherite
            )
        )
        customSmithingRecipe.add(
            CustomSmithingRecipe(
                SmithingTransformRecipe(
                    NamespacedKey(this, "diamond_elytra_to_netherite_elytra"),
                    ItemStack(Material.ELYTRA).apply {
                        val meta = itemMeta
                        if (meta != null) netherite(meta)
                        itemMeta = meta
                    },
                    RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                    RecipeChoice.MaterialChoice(Material.DIAMOND_CHESTPLATE),
                    RecipeChoice.MaterialChoice(Material.ELYTRA)
                ),
                NETHERITE_ELYTRA,
                netherite
            ) {
                val clickedInventory = it.clickedInventory
                (if (clickedInventory is SmithingInventory) {
                    if (it.rawSlot == 3) {
                        val item0 = clickedInventory.getItem(2)
                        item0?.type == Material.ELYTRA && item0.itemMeta?.persistentDataContainer?.has(
                            DIAMOND_ELYTRA,
                            PersistentDataType.INTEGER
                        ) != true
                    } else true
                } else true).apply { if (!this) it.isCancelled = true }
            })
        customSmithingRecipe.add(
            CustomSmithingRecipe(
                SmithingTransformRecipe(
                    NamespacedKey(this, "netherite_multitool"),
                    ItemStack(Material.NETHERITE_PICKAXE).apply {
                        itemMeta = newItemMeta(Material.NETHERITE_PICKAXE) {
                            val toolComponent = tool

                            listOf(
                                Triple(Tag.MINEABLE_PICKAXE, 9.0f, true),
                                Triple(Tag.MINEABLE_AXE, 9.0f, true),
                                Triple(Tag.MINEABLE_SHOVEL, 9.0f, true),
                            ).forEach {
                                toolComponent.addRule(it.first, it.second, it.third)
                            }

                            setTool(toolComponent)
                            lore = listOf(
                                "Pickaxe, Axe and Shovel"
                            )

                            setDisplayName("${ChatColor.YELLOW}Netherite Multitool")
                        }
                    },
                    RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                    RecipeChoice.MaterialChoice(Material.NETHERITE_PICKAXE),
                    RecipeChoice.MaterialChoice(Material.NETHERITE_SHOVEL)
                ),
                NETHERITE_MULTITOOL,
            ) {
                val clickedInventory = it.clickedInventory
                (if (clickedInventory is SmithingInventory) {
                    if (it.rawSlot == 3) {
                        val item0 = clickedInventory.getItem(1)
                        item0?.type == Material.NETHERITE_PICKAXE && item0.itemMeta?.persistentDataContainer?.has(
                            NETHERITE_MULTITOOL,
                            PersistentDataType.INTEGER
                        ) != true
                    } else true
                } else true).apply { if (!this) it.isCancelled = true }
            }
        )

        server.pluginManager.registerEvents(CustomSmithingRecipe.Event(customSmithingRecipe), this)

        logger.info("Netherite Elytra enabled".ansi(Ansi.Color.GREEN))
    }

    override fun onDisable() {
        logger.info("Netherite Elytra disabled".ansi(Ansi.Color.RED))
    }

    private fun customElytra(
        name: String,
        attributes: Multimap<Attribute, AttributeModifier>?
    ): (meta: ItemMeta) -> Unit {
        return { meta ->
            meta.attributeModifiers = attributes
            meta.setDisplayName("${ChatColor.YELLOW}$name")
            if (meta is Damageable) meta.damage = 0
        }
    }
}
