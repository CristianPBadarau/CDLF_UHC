package moe.cdlf.cdlfuhc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;

import java.util.*;

public class TeamManager {
    public Map<String, Team> getTeams() {
        return teams;
    }

    private final Map<String, Team> teams = new HashMap<>();
    private final String[] teamNames = {"Kazajistán", "Uzbekistán", "Kirguistán", "Pakistán", "Tayikistán", "Turkmenistán", "Perú"};

    public TeamManager() {
        resetScoreboardObjective();
    }

    private boolean areTeamsAssigned = false;

    public void createTeam(String name) {
        Bukkit.getLogger().info("Creating team " + name);
        if (teams.containsKey(name)) {
            deleteTeam(name);
        }
        Team team = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().registerNewTeam(name);
        teams.put(name, team);
    }

    public void addPlayerToTeam(String playerName, String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            team.addEntry(playerName);
        }
    }

    public void removePlayerFromTeam(String playerName) {
        // find the team the player is in and remove them
        Team team = getTeamOfPlayer(playerName);
        if (team != null) {
            team.removeEntry(playerName);
        }
    }

    public void deleteTeam(String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            team.unregister();
            teams.remove(teamName);
        }
    }

    public Team getTeam(String teamName) {
        return teams.get(teamName);
    }

    public Team getTeamOfPlayer(String playerName) {
        for (Team team : teams.values()) {
            if (team.hasEntry(playerName)) {
                return team;
            }
        }
        return null;
    }

    public void createScoreboardObjective() {
        resetScoreboardObjective();
        Arrays.stream(teamNames).forEach(this::createTeam);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective teamsObjective = scoreboard.registerNewObjective("teams", "dummy", "Teams");
        // add all teams to the scoreboard
        for (Team team : teams.values()) {
            teamsObjective.getScore(team.getName()).setScore(team.getEntries().size());
            team.setPrefix(team.getName() + " - ");
        }
        teamsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        // set the scoreboard for all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    public void resetScoreboardObjective() {
        // remove current team list from the server
        teams.clear();
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(Team::unregister);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        scoreboard.getObjectives().forEach(Objective::unregister);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
        Bukkit.getScoreboardManager().getMainScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        Bukkit.getScoreboardManager().getMainScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
        Bukkit.getScoreboardManager().getMainScoreboard().clearSlot(DisplaySlot.PLAYER_LIST);
        areTeamsAssigned = false;
    }

    // update the scoreboard with the current team sizes
    public void updateScoreboardObjective() {
        Objective teamsObjective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams");
        for (Team team : teams.values()) {
            teamsObjective.getScore(team.getName()).setScore(team.getEntries().size());
        }
    }

    public void assignPlayersToTeams() {
        // get all online players and shuffle them
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);
        // also shuffle the team list
        final List<Team> teamList = new ArrayList<>(teams.values());
        Collections.shuffle(teamList);
        // assign each player to a team
        int teamIndex = 0;
        for (Player player : players) {
            Team team = teamList.get(teamIndex);
            team.addEntry(player.getName());
            teamIndex++;
            if (teamIndex >= teams.size()) {
                teamIndex = 0;
            }
        }
        updateScoreboardObjective();
        areTeamsAssigned = true;
    }

    public void resetTeams() {
        // remove all players from the teams
        for (Team team : teams.values()) {
            team.getEntries().clear();
        }
        areTeamsAssigned = false;
        // remove all teams from the server
        for (Team team : teams.values()) {
            team.unregister();
        }
    }

    public boolean areTeamsAssigned() {
        return areTeamsAssigned;
    }

    public void displayAllTeamsInChat() {
        Bukkit.broadcastMessage("Teams:");
        for (Team team : teams.values()) {
            if (!team.getEntries().isEmpty()) Bukkit.broadcastMessage(team.getName() + ": " + team.getEntries());
        }
    }

    public boolean arePlayersInTheSameTeam(String name1, String name2){
        Team team1 = getTeamOfPlayer(name1);
        Team team2 = getTeamOfPlayer(name2);
        return team1 != null && team2 != null && team1.equals(team2);
    }

    public Player getPlayerOfTeam(Team team){
        Bukkit.getLogger().info("all teams " + teams);
        for (Team t : teams.values()) {
            if (t.equals(team)) {
                Bukkit.getLogger().info("team " + t);
                for (String player : t.getEntries()) {
                    Bukkit.getLogger().info("player " + player);
                    return Bukkit.getPlayer(player);
                }
            }
        }
        return null;
    }
}
