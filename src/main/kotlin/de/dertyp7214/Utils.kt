package de.dertyp7214

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.meta.ItemMeta
import org.fusesource.jansi.Ansi

fun String.ansi(color: Ansi.Color, reset: Boolean = true): String {
    return "${Ansi.ansi().fg(color)}$this${if (reset) Ansi.ansi().reset().toString() else ""}"
}

fun String.chatColor(color: ChatColor): String {
    return "${color}$this${ChatColor.RESET}"
}

fun newItemMeta(material: Material, block: ItemMeta.() -> Unit = {}): ItemMeta {
    return Bukkit.getItemFactory().getItemMeta(material)!!.apply(block)
}
