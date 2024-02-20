package moe.cdlf.cdlfuhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageByEntityEventListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Bukkit.getLogger().info("EntityDamageByEntityEvent");
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isInventoryEmpty(player) && item.getType() == Material.FEATHER) {
                event.setDamage(10.0);
            }
        }
    }

    private boolean isInventoryEmpty(Player player) {
        // Check if player has any items in their inventory but a feather
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR && item.getType() != Material.FEATHER) {
                return false;
            }
        }

        // Check if player has any armor equipped
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

}
