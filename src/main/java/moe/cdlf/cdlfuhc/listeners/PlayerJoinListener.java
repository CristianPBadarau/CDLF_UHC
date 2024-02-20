package moe.cdlf.cdlfuhc.listeners;

import moe.cdlf.cdlfuhc.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerManager playerManager;

    public PlayerJoinListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.broadcastMessage("Bienvenido al servidor, " + event.getPlayer().getName() + "!");
        playerManager.addPlayer(event.getPlayer());
        playerManager.setPlayerScoreboard(event.getPlayer());
    }
}
