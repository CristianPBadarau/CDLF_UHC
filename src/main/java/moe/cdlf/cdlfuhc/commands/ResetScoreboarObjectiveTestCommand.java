package moe.cdlf.cdlfuhc.commands;

import moe.cdlf.cdlfuhc.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetScoreboarObjectiveTestCommand implements CommandExecutor {
    private final TeamManager teamManager;

    public ResetScoreboarObjectiveTestCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        teamManager.resetScoreboardObjective();
        return true;
    }
}
