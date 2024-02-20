package moe.cdlf.cdlfuhc.commands;

import moe.cdlf.cdlfuhc.GameManager;
import moe.cdlf.cdlfuhc.models.GameState;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChangeWorldBorderCommand implements CommandExecutor {
    private final GameManager gameManager;

    public ChangeWorldBorderCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Debes especificar el tamaño y el tiempo del muro!");
            return true;
        }
        // get world border args
        int size = Integer.parseInt(args[0]);
        int seconds = Integer.parseInt(args[1]);
        if (seconds < 0 || size < 0) {
            sender.sendMessage("Los valores deben ser positivos!");
            return true;
        }
        WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
        wb.setSize(size, seconds);
        gameManager.broadcastServerMessage("Se ha actualizado el muro!");
        return true;
    }

}
