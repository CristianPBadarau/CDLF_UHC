package moe.cdlf.cdlfuhc.commands;

import moe.cdlf.cdlfuhc.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReassignTeamsCommand implements CommandExecutor {
    private final GameManager gameManager;

    public ReassignTeamsCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        gameManager.reassignTeams();
        return true;
    }
}
