package moe.cdlf.cdlfuhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerInteractEventListener implements Listener {
    // this will simulate eating the head
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Bukkit.getLogger().info("PlayerInteractEvent1");

        if (item != null && item.getType() == Material.PLAYER_HEAD && item.getItemMeta() instanceof SkullMeta) {
            Bukkit.getLogger().info("PlayerInteractEvent2");
            if (event.getAction().name().contains("RIGHT_CLICK")) {
                Player localPlayer = event.getPlayer();
                // just consume the head if the player is pointing at an entity or at air
                if (event.hasBlock()) {
                    return;
                }
                Bukkit.getLogger().info("PlayerInteractEvent3");
                // Simulate eating the head
                localPlayer.setFoodLevel(event.getPlayer().getFoodLevel() + 6);
                localPlayer.setHealth(Math.min(localPlayer.getHealth() + 6, localPlayer.getMaxHealth()));

                Bukkit.getLogger().info("PlayerInteractEvent3");
                // Remove one player head from the player's hand
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    localPlayer.getInventory().removeItem(item);
                }

                event.setCancelled(true);
            }
        }
    }
}
