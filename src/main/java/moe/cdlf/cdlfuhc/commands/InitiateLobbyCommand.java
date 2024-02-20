package moe.cdlf.cdlfuhc.commands;

import moe.cdlf.cdlfuhc.GameManager;
import moe.cdlf.cdlfuhc.models.GameState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InitiateLobbyCommand implements CommandExecutor {
    private final GameManager gameManager;

    public InitiateLobbyCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (gameManager.getGameState() == GameState.RUNNING){
            sender.sendMessage("El juego ya está en marcha!");
            return true;
        }
        // get player from sender
        Player player = (Player) sender;
        gameManager.startLobby(player);
        return true;
    }
}
