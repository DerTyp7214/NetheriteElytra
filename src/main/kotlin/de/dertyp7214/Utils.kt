package de.dertyp7214

import org.bukkit.ChatColor
import org.fusesource.jansi.Ansi

fun String.ansi(color: Ansi.Color, reset: Boolean = true): String {
    return "${Ansi.ansi().fg(color)}$this${if (reset) Ansi.ansi().reset().toString() else ""}"
}

fun String.chatColor(color: ChatColor): String {
    return "${color}$this${ChatColor.RESET}"
}