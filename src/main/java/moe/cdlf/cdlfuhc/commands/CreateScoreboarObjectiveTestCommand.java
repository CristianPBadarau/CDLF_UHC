package moe.cdlf.cdlfuhc.commands;

import moe.cdlf.cdlfuhc.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreateScoreboarObjectiveTestCommand implements CommandExecutor {
    private final TeamManager teamManager;

    public CreateScoreboarObjectiveTestCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        teamManager.createScoreboardObjective();
        Bukkit.broadcastMessage("Teams and objectives created!");
        return true;
    }
}
