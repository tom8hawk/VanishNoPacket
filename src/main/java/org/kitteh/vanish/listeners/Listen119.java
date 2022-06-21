package org.kitteh.vanish.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.kitteh.vanish.VanishPlugin;

public final class Listen119 implements Listener {
    private final VanishPlugin plugin;

    public Listen119(VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockReceiveGameEvent(BlockReceiveGameEvent event) {
        Entity trigger = event.getEntity();
        if (trigger instanceof Player && event.getBlock().getType() == Material.SCULK_SENSOR) {
            if (this.plugin.getManager().isVanished((Player) trigger)) {
                event.setCancelled(true);
            }
        }
    }
}