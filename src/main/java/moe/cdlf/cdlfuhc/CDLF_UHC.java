package moe.cdlf.cdlfuhc;

import moe.cdlf.cdlfuhc.commands.*;
import moe.cdlf.cdlfuhc.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class CDLF_UHC extends JavaPlugin {
    private GameManager gameManager;
    private TeamManager teamManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        this.teamManager = new TeamManager();
        this.gameManager = new GameManager(this, this.teamManager);
        this.playerManager = new PlayerManager(this.gameManager, this.teamManager);

        assignCommands();
        registerListeners();
        createRecipes();
    }

    private void assignCommands() {
        registerCommand(new StartGameCommand(gameManager), "start_game");
        registerCommand(new AssignTeamsCommand(teamManager), "assign_teams");
        registerCommand(new CreateScoreboarObjectiveTestCommand(teamManager), "create_scoreboard_objective");
        registerCommand(new ResetScoreboarObjectiveTestCommand(teamManager), "reset_scoreboard_objective");
        registerCommand(new InitiateLobbyCommand(gameManager), "initialize_lobby");
        registerCommand(new ChangeWorldBorderCommand(gameManager), "change_world_border");
        registerCommand(new ReassignTeamsCommand(gameManager), "reassign_teams");
    }

    private void registerListeners() {
        registerListener(new PlayerJoinListener(playerManager));
        registerListener(new PlayerDeathListener(gameManager));
        registerListener(new EntityDamageEventListener());
        registerListener(new PlayerInteractEventListener());
        registerListener(new EntityDamageByEntityEventListener());
        registerListener(new InventoryClickEventListener());
    }

    private void registerCommand(CommandExecutor commandExecutor, String commandName) {
        getCommand(commandName).setExecutor(commandExecutor);
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        teamManager.resetTeams();
        teamManager.resetScoreboardObjective();
    }

    private void createRecipes () {
        createNotchAppleRecipe();
        createTotem();
    }

    private void createNotchAppleRecipe() {
        ItemStack notchApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);

        NamespacedKey key = new NamespacedKey(this, "notch_apple");
        ShapelessRecipe recipe = new ShapelessRecipe(key, notchApple);

        recipe.addIngredient(8, Material.GOLD_BLOCK);
        recipe.addIngredient(1, Material.PLAYER_HEAD);

        Bukkit.addRecipe(recipe);
    }

    private void createTotem() {
        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);

        NamespacedKey key = new NamespacedKey(this, "custom_totem");
        ShapelessRecipe recipe = new ShapelessRecipe(key, totem);

        recipe.addIngredient(3, Material.PLAYER_HEAD);
        recipe.addIngredient(6, Material.GOLD_BLOCK);

        Bukkit.addRecipe(recipe);
    }

}