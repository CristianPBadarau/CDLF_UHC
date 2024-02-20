package moe.cdlf.cdlfuhc.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InventoryClickEventListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            checkForPumpkin((Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            checkForPumpkin((Player) event.getPlayer());
        }
    }

    private void checkForPumpkin(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.CARVED_PUMPKIN) {
            // If the player is wearing a carved pumpkin, give them night vision
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,
                    Integer.MAX_VALUE, 0, false, false, false));
        } else {
            // If the player is not wearing a carved pumpkin, remove night vision
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}
