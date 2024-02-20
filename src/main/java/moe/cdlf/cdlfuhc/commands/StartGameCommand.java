package moe.cdlf.cdlfuhc.commands;

import moe.cdlf.cdlfuhc.GameManager;
import moe.cdlf.cdlfuhc.models.GameState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGameCommand implements CommandExecutor {
    private final GameManager gameManager;

    public StartGameCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (gameManager.getGameState() == GameState.RUNNING){
            sender.sendMessage("El juego ya está en marcha!");
            return true;
        }
        gameManager.setTimeUntilGameStart(60);

        gameManager.startGame();

        Bukkit.broadcastMessage("El juego ha comenzado!");
        return true;
    }
}
