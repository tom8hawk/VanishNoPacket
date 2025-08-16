package org.kitteh.vanish;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.vanish.hooks.HookManager.HookType;
import org.kitteh.vanish.hooks.plugins.VaultHook;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller of announcing joins and quits that aren't their most honest.
 * Note that delayed announce methods can be called without checking
 * to see if it's enabled first. The methods confirm before doing anything
 * particularly stupid.
 */
public final class VanishAnnounceManipulator {
    private final VanishPlugin plugin;
    private final Map<String, Boolean> playerOnlineStatus;

    VanishAnnounceManipulator(VanishPlugin plugin) {
        this.plugin = plugin;
        this.playerOnlineStatus = new HashMap<>();
    }

    /**
     * Gets the fake online status of a player
     * Called by JSONAPI
     *
     * @param playerName name of the player to query
     * @return true if player is considered online, false if not (or if not on server)
     */
    public boolean getFakeOnlineStatus(String playerName) {
        final Player player = this.plugin.getServer().getPlayerExact(playerName);
        if (player == null) {
            return false;
        }
        playerName = player.getName();
        if (this.playerOnlineStatus.containsKey(playerName)) {
            return this.playerOnlineStatus.get(playerName);
        } else {
            return true;
        }
    }

    /**
     * Marks a player as quit
     * Called when a player quits
     *
     * @param player name of the player who just quit
     * @return the former fake online status of the player
     */
    public boolean playerHasQuit(String player) {
        if (this.playerOnlineStatus.containsKey(player)) {
            return this.playerOnlineStatus.remove(player);
        }
        return true;
    }

    private String injectPlayerInformation(String message, Player player) {
        final VaultHook vault = (VaultHook) this.plugin.getHookManager().getHook(HookType.Vault);
        message = message.replace("%p", player.getName());
        message = message.replace("%d", player.getDisplayName());
        String prefix = vault.getPrefix(player);
        message = message.replace("%up", prefix);
        String suffix = vault.getSuffix(player);
        message = message.replace("%us", suffix);
        return message;
    }

    void fakeJoin(Player player, boolean force) {
        if (force || !(this.playerOnlineStatus.containsKey(player.getName()) && this.playerOnlineStatus.get(player.getName()))) {
            this.plugin.getLogger().info(player.getName() + " faked joining");
            this.playerOnlineStatus.put(player.getName(), true);

            if (Settings.getUseVanillaMessages()) {
                HoverEvent<HoverEvent.ShowEntity> showEntity = HoverEvent.showEntity(
                        HoverEvent.ShowEntity.showEntity(
                                Key.key("minecraft:player"),
                                player.getUniqueId(),
                                Component.text(player.getName())
                        ));

                Component nameWithHover = Component.text(player.getName())
                        .hoverEvent(showEntity)
                        .color(NamedTextColor.YELLOW);

                Component joinMessage = Component.translatable(
                        "multiplayer.player.joined",
                        NamedTextColor.YELLOW,
                        nameWithHover
                );

                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    viewer.sendMessage(joinMessage);
                }

                return;
            }

            this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeJoin(), player));
        }
    }

    void fakeQuit(Player player, boolean force) {
        if (force || !(this.playerOnlineStatus.containsKey(player.getName()) && !this.playerOnlineStatus.get(player.getName()))) {
            this.plugin.getLogger().info(player.getName() + " faked quitting");
            this.playerOnlineStatus.put(player.getName(), false);

            if (Settings.getUseVanillaMessages()) {
                HoverEvent<HoverEvent.ShowEntity> showEntity = HoverEvent.showEntity(
                        HoverEvent.ShowEntity.showEntity(
                                Key.key("minecraft:player"),
                                player.getUniqueId(),
                                Component.text(player.getName())
                        ));

                Component nameWithHover = Component.text(player.getName())
                        .hoverEvent(showEntity)
                        .color(NamedTextColor.YELLOW);

                Component joinMessage = Component.translatable(
                        "multiplayer.player.left",
                        NamedTextColor.YELLOW,
                        nameWithHover
                );

                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    viewer.sendMessage(joinMessage);
                }

                return;
            }

            this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeQuit(), player));
        }
    }

}