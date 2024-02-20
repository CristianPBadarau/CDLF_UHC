package moe.cdlf.cdlfuhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class EntityDamageEventListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet != null && helmet.getType() == Material.PLAYER_HEAD && helmet.getItemMeta() instanceof SkullMeta) {
                ItemMeta meta = helmet.getItemMeta();
                int damage = ((Damageable) meta).getDamage() + 1;

                if (damage >= 3) {
                    // The helmet has taken 3 hits, so break it
                    player.getInventory().setHelmet(null);
                } else {
                    // The helmet has taken less than 3 hits, so increase its damage
                    ((Damageable) meta).setDamage(damage);
                    helmet.setItemMeta(meta);
                }
            }
        }
    }
}
