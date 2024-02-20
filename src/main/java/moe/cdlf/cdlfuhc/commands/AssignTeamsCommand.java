package moe.cdlf.cdlfuhc.commands;

import moe.cdlf.cdlfuhc.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AssignTeamsCommand implements CommandExecutor {
    private final TeamManager teamManager;

    public AssignTeamsCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (teamManager.areTeamsAssigned()) {
            sender.sendMessage("Los equipos ya están asignados!");
            return true;
        }
        teamManager.assignPlayersToTeams();
        teamManager.displayAllTeamsInChat();
        return true;
    }
}
