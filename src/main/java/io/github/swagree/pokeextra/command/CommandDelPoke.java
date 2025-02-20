package io.github.swagree.pokeextra.command;

import com.pixelmonmod.pixelmon.Pixelmon;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandDelPoke implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.del")) {
            try {
                delPokeBySlot(args);
                return true;
            } catch (Exception e) {
                sender.sendMessage("§c输入的有点问题！玩家不存在或或宝可梦或需要的是数字！");
            }
        }
        return false;
    }

    private static void delPokeBySlot(String[] args) {
        int slot;
        UUID uniqueId = Bukkit.getPlayer(args[0]).getUniqueId();
        slot = Integer.parseInt(args[1]) - 1;
        Pixelmon.storageManager.getParty(uniqueId).set(slot, null);
    }
}
