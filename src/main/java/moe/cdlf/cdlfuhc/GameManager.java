package moe.cdlf.cdlfuhc;

import moe.cdlf.cdlfuhc.models.GameStage;
import moe.cdlf.cdlfuhc.models.GameState;
import moe.cdlf.cdlfuhc.models.GameType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameManager {
    private final CDLF_UHC plugin;
    private final TeamManager teamManager;
    private GameState gameState = GameState.LOBBY;
    private GameType gameType = GameType.TEAM;
    private GameStage gameStage = GameStage.NONE;

    public GameStage getGameStage() {
        return gameStage;
    }

    public void setGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
    }


    public GameManager(CDLF_UHC cdlfUhc, TeamManager teamManager) {
        this.teamManager = teamManager;
        this.plugin = cdlfUhc;
    }

    private boolean isPvPEnabled = false;

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void getGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isPvPEnabled() {
        return isPvPEnabled;
    }

    public void setPvPEnabled(boolean PvPEnabled) {
        isPvPEnabled = PvPEnabled;
    }


    public void setTimeUntilGameStart(int timeUntilGameStart) {

    }

    // stage 1
    public void startLobby(Player player) {
        setGameState(GameState.STARTING);
        worldConfig();
        broadcastTitle("Preparación", "El juego comenzara pronto");
        teleportPlayersToLobby(player);
        setWorldBorder(800, player.getLocation());
        preChargeWorldBorderChunks();
        broadcastServerMessage("Se ha establecido el muro a 800 bloques!");
        teamManager.createScoreboardObjective();
        teamManager.assignPlayersToTeams();
        broadcastServerMessage("Se han asignado los equipos!");
        for (Team team : teamManager.getTeams().values()) {
            if (team.getEntries().size() > 1)
                broadcastServerMessage("Equipo " + team.getName() + ": " + team.getEntries());
        }
    }

    public void reassignTeams() {
        teamManager.resetScoreboardObjective();
        teamManager.createScoreboardObjective();
        teamManager.assignPlayersToTeams();
        broadcastServerMessage("Se han reasignado los equipos!");
        for (Team team : teamManager.getTeams().values()) {
            if (team.getEntries().size() > 1)
                broadcastServerMessage("Equipo " + team.getName() + ": " + team.getEntries());
        }
    }

    // stage 2
    public void startGame() {
        startCountdown(10);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            broadcastTitle("El juego ha comenzado!", "Buena suerte!");
            setGameState(GameState.RUNNING);
            teleportTeamsToRandomLocations();
            disablePvP(60);
        }, 10 * 20);
        int startStage3InMinutes = 1;
        int startStage4InMinutes = startStage3InMinutes + 1;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::stage3, startStage3InMinutes * 60 * 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::startDeathMatch, startStage4InMinutes * 60 * 20);
    }

    // stage 3
    public void stage3() {
        broadcastTitle("Cuidado!", "El muro ha empezado a cerrarse!");
        setWorldBorder(300, 5 * 60);
    }

    // stage 4
    public void startDeathMatch() {
        broadcastTitle("La pelea a muerte ha comenzado!", "Buena suerte!");
        broadcastServerMessage("El muro se va a reducir al minimo!");
        int worldBorderReductionInMinutes = 3;
        int worldBorderSize = 50;
        setWorldBorder(worldBorderSize, worldBorderReductionInMinutes * 60);
        setGameStage(GameStage.STAGE4);
    }

    public void endGame(Player player) {
        if (player != null) {
            Team team = teamManager.getTeamOfPlayer(player.getName());
            if (team != null && team.getEntries().size() > 1) {
                Bukkit.broadcastMessage("El juego ha terminado! El equipo " + team.getName() + " ha ganado!");
            } else {
                Bukkit.broadcastMessage("El juego ha terminado! " + player.getName()
                        + " ha tenido que carrear al equipo" + team.getName() + " a la victoria!");
            }
        }

        setGameState(GameState.ENDED);
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public void preChargeWorldBorderChunks() {
        WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
        int size = (int) wb.getSize();
        int chunks = size / 16;
        int radius = chunks / 2;
        Location center = wb.getCenter();
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                center.getChunk().load();
            }
        }
    }

    public void givePlayersCompass(Player player1, Player player2) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        player1.getInventory().addItem(compass);
        player2.getInventory().addItem(compass);
        player1.setCompassTarget(player2.getLocation());
        player2.setCompassTarget(player1.getLocation());
    }

    public void checkDistanceBetweenPlayers(Player player1, Player player2) {
        // get the distance between the players and point the players to each other
        double distance = Math.sqrt(player1.getLocation().distance(player2.getLocation()));
        givePlayersCompass(player1, player2);

        if (distance < 80) {
            Bukkit.broadcastMessage("Ya solo quedan 2!, la pelea a muerte con cuchillos entre " + player1.getName() + " y " + player2.getName());
        }
    }

    public void checkIfPlayersAreAlive(Player player) {
        if (gameType == GameType.SOLO) {
            if (Bukkit.getOnlinePlayers().size() == 1) {
                endGame(Bukkit.getOnlinePlayers().stream().findFirst().get());
            }
        } else {
            ArrayList<Player> players = (ArrayList<Player>) Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList());
            ArrayList<Team> aliveTeams = new ArrayList<>();
            for (Player p : players) {
                Team t = teamManager.getTeamOfPlayer(p.getName());
                if (aliveTeams.contains(t)) {
                    continue;
                }
                aliveTeams.add(t);
            }
            if (aliveTeams.size() == 1) {
                if (player != null) endGame(player);
                else endGame(players.get(0));
            } else if (aliveTeams.size() == 2) {
                checkDistanceBetweenPlayers(players.get(0), players.get(1));
                broadcastServerMessage("Ya solo quedan 2 equipos!");
                broadcastServerMessage("Se os ha entregado una brújula para que os encontréis!");
            }
        }
    }

    public void broadcastTitle(String title, String subtitle) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 70, 20);
        }
    }

    public void broadcastServerMessage(String message) {
        Bukkit.broadcastMessage(message);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void disablePvP(int timeUntilPvP) {
        setPvPEnabled(false);
        broadcastServerMessage("El pvp está desactivado y tenéis un periodo de gracia de unos cuantos segundos!");
        makePlayersInvincible(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            setPvPEnabled(true);
            broadcastTitle("Suerte!", "Estas a tu suerte :P!");
            makePlayersInvincible(false);
        }, timeUntilPvP * 20);
    }

    // world config
    public void worldConfig() {
        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        }
    }

    public void makePlayersInvincible(boolean invincible) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setSaturation(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setInvulnerable(invincible);
        }
    }

    public void teleportPlayersToLobby(Player player) {
        player.getWorld().setSpawnLocation(player.getLocation());
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location tpLocation = player.getLocation().add(Math.random() * 10, 0, Math.random() * 10);
            p.teleport(tpLocation.getWorld().getHighestBlockAt(tpLocation).getLocation());
        }
    }

    public void setWorldBorder(int size, Location location) {
        Bukkit.getWorlds().forEach(world -> {
            world.getWorldBorder().setCenter(location);
            world.getWorldBorder().setSize(size);
            world.getWorldBorder().setWarningDistance(5);
            world.getWorldBorder().setWarningTime(3);
        });
    }

    public void setWorldBorder(int size, int time) {
        Bukkit.getWorlds().forEach(world -> {
            world.getWorldBorder().setSize(size, time);
        });
    }

    public void teleportTeamsToRandomLocations() {
        WorldBorder worldBorder = Bukkit.getWorlds().get(0).getWorldBorder();

        // Calculate the maximum and minimum possible coordinates
        double minX = worldBorder.getCenter().getX() - worldBorder.getSize() / 4;
        double maxX = worldBorder.getCenter().getX() + worldBorder.getSize() / 4;
        double minZ = worldBorder.getCenter().getZ() - worldBorder.getSize() / 4;
        double maxZ = worldBorder.getCenter().getZ() + worldBorder.getSize() / 4;

        // Define the initial teleportation coordinates and the distance between teams
        double x = minX;
        double z = minZ;
        double distanceBetweenTeams = 150;

        // teleport all teams to random locations
        for (Team team : teamManager.getTeams().values()) {
            for (String player : team.getEntries()) {
                Player p = Bukkit.getPlayer(player);
                // Teleport the player to the current coordinates
                p.teleport(new Location(Bukkit.getWorlds().get(0), x, Bukkit.getWorlds().get(0).getHighestBlockYAt((int) x, (int) z), z));
            }

            // Increment the x coordinate for the next team
            x += distanceBetweenTeams;

            // If the x coordinate is outside the world border, reset it to the minimum and increment the z coordinate
            if (x > maxX) {
                x = minX;
                z += distanceBetweenTeams;
            }

            // If the z coordinate is outside the world border, reset it to the minimum
            if (z > maxZ) {
                z = minZ;
            }
        }
    }

    public void startCountdown(int timeInSeconds) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        int taskId = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            int countdown = timeInSeconds;

            @Override
            public void run() {
                if (countdown > 0) {
                    broadcastTitle(countdown + "", "");
                    countdown--;
                }
            }
        }, 0L, 20L);

        scheduler.runTaskLater(plugin, () -> scheduler.cancelTask(taskId), (timeInSeconds + 1) * 20L);
    }
}
