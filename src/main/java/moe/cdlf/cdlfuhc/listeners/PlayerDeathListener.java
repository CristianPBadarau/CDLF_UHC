package moe.cdlf.cdlfuhc.listeners;

import moe.cdlf.cdlfuhc.GameManager;
import moe.cdlf.cdlfuhc.models.GameState;
import moe.cdlf.cdlfuhc.TeamManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;


public class PlayerDeathListener implements Listener {
    private final GameManager gameManager;
    private final TeamManager teamManager;

    public PlayerDeathListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.teamManager = gameManager.getTeamManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameManager.getGameState() != GameState.RUNNING) {
            return;
        }
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        handlePlayerDeath(player, event);

        EntityDamageEvent damageEvent = player.getLastDamageCause();

        if (damageEvent instanceof EntityDamageByEntityEvent) {
            handleEntityDamage(event, (EntityDamageByEntityEvent) damageEvent, killer, player);
        } else if (damageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDeathMessage(player.getName() + " se ha estampado contra el suelo.");
        }

        gameManager.checkIfPlayersAreAlive(player);

        getPlayerHead(player);
    }

    private static void handleEntityDamage(PlayerDeathEvent event, EntityDamageByEntityEvent damageEvent
            , Player killer, Player player) {
        EntityDamageByEntityEvent entityDamageEvent = damageEvent;
        Entity aggressor = entityDamageEvent.getDamager();
        if (aggressor instanceof Player) {
            if (killer != null) {
                event.setDeathMessage(killer.getName() + " le ha abierto el culo a "
                        + player.getName() + ". Un aplauso por favor.");
            }
        } else if (aggressor instanceof Monster) {

        }
    }

    private void handlePlayerDeath(Player player, PlayerDeathEvent event) {
        Location location = player.getLocation();
        player.setRespawnLocation(location);
        event.getEntity().getWorld().strikeLightningEffect(location);
        player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        player.setFlySpeed(0.4f);
    }

    public void getPlayerHead(Player player) {
        ItemStack skull = createSkull(player);
        createDeathChest(player, skull);
    }

    private ItemStack createSkull(Player player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 623, true);
        skullMeta.isUnbreakable();
        skullMeta.setDisplayName("Cabeza de " + player.getName());
        skullMeta.setLore(java.util.Arrays.asList(
                "Se puede usar de florero",
                "También te la puedes equipar, pero cuidado que solo tiene 3 de durabilidad!",
                "Te la puedes comer. Esto te curará 3 corazones y 3 de hambre ",
                "pero el item desaparecerá de tu inventario asi que un trofeo menos!",
                "Si juntas 3 cabezas y 6 bloques de oro en una linea puedes hacer un totem.",
                "Con una cabeza y 8 bloques de oro puedes hacer una manzana de bloques de oro!",
                "PD: Si la pones en el suelo cagaste porque ya no tendrá ningun efecto especial",
                "ni te la podras comer."));
        skull.setItemMeta(skullMeta);
        return skull;
    }

    private void createDeathChest(Player player, ItemStack skull) {
        // Create a chest at the player's location
        Location location = player.getLocation();
        location.getBlock().setType(Material.CHEST);
        Block block = location.getBlock();
        Chest chest = (Chest) block.getState();
        // Add the player's head to the chest
        chest.getBlockInventory().addItem(skull);
        chest.setCustomName("Cabeza de " + player.getName());
        Location spiralStartLocation = location.clone();
        spiralStartLocation.setY(spiralStartLocation.getY() + 4);
        for (int i = 1; i < 250; i++) {
            Location blockLocation = location.clone();
            blockLocation.setY(blockLocation.getY() + i);
            blockLocation.getBlock().setType(Material.AIR);
        }
        // createSpiral(spiralStartLocation);
        createRandomLanternPattern(spiralStartLocation, Material.LANTERN, 40, 250);
    }

    private Material getMaterialForY(int y) {
        switch (y % 5) {
            case 0:
                return Material.GLOWSTONE;
            case 1:
                return Material.REDSTONE_LAMP;
            case 2:
                return Material.JACK_O_LANTERN;
            case 3:
                return Material.SEA_LANTERN;
            case 4:
                return Material.CHERRY_WOOD;
            default:
                return Material.CHERRY_LEAVES;
        }
    }

    private void createSpiral(Location start) {
        World world = start.getWorld();
        double radius = 0;
        double angle = 0;
        for (int y = start.getBlockY(); y < world.getMaxHeight(); y++) {
            // Calculate the x and z coordinates for the current angle and radius
            int x = start.getBlockX() + (int) (radius * Math.cos(angle));
            int z = start.getBlockZ() + (int) (radius * Math.sin(angle));

            // Set the block at the calculated coordinates to the specified material
            Material blockMaterial = getMaterialForY(y);
            world.getBlockAt(x, y, z).setType(blockMaterial);

            // Increase the angle and radius for the next step in the spiral
            angle += Math.PI / 16;
            radius += 0.1;
        }
    }

    private void createRandomLanternPattern(Location start, Material lanternMaterial, int width, int height) {
        World world = start.getWorld();
        Random random = new Random();

        for (int i = 0; i < height; i++) {
            // Generate random x and z coordinates within the specified width
            int x = start.getBlockX() + random.nextInt(width) - width / 2;
            int z = start.getBlockZ() + random.nextInt(width) - width / 2;

            // Calculate the y coordinate for the current block
            int y = start.getBlockY() + i;

            world.getBlockAt(x, y, z).setType(i % 2 == 0 ? lanternMaterial : Material.SOUL_LANTERN);
        }
    }
}
