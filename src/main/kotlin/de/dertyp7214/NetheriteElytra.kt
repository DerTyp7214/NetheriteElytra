package de.dertyp7214

import org.bukkit.plugin.java.JavaPlugin
import org.fusesource.jansi.Ansi

class NetheriteElytra : JavaPlugin() {

    override fun onEnable() {
        logger.info("Netherite Elytra enabled".ansi(Ansi.Color.GREEN))
    }

    override fun onDisable() {
        logger.info("Netherite Elytra disabled".ansi(Ansi.Color.RED))
    }
}
