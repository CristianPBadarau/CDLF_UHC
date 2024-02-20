package moe.cdlf.cdlfuhc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerManager {

    private final TeamManager teamManager;
    private final GameManager gameManager;

    public PlayerManager(GameManager gameManager, TeamManager teamManager) {
        this.teamManager = teamManager;
        this.gameManager = gameManager;
    }

    public void addPlayer(Player player) {
        setPlayerScoreboard(player);
    }

    public void setPlayerScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
