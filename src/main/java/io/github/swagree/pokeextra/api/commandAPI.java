package io.github.swagree.pokeextra.api;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokeextra.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class commandAPI {
    public static boolean toExecuteCommand(Boolean listFlag, Player player, List<String> flagCommands, Pokemon pokemon,String commandType) {
        if (listFlag) {
            toExecuteCommands(player, flagCommands, pokemon.getLocalizedName());
            return true;
        }
        toExecutedCommonCommands(player, pokemon,commandType);
        return true;
    }


    public static void toExecutedCommonCommands(Player player, Pokemon pokemon,String commandType) {
        List<String> commonCommands = Main.plugin.getConfig().getStringList(commandType+".commands");

        toExecuteCommands(player, commonCommands, pokemon.getLocalizedName());
    }



    public static void toExecuteCommands(Player bukkitPlayer, List<String> firstCommands, String pokeName) {
        for (String firstCommand : firstCommands) {
            if (firstCommand.startsWith("tell")) {
                String[] split = firstCommand.split(":");
                String colorMessage = split[1].replace("&", "§").replace("%player%", bukkitPlayer.getName()).replace("%pokemon%", pokeName);
                bukkitPlayer.sendMessage(colorMessage);
                continue;
            }
            if (firstCommand.startsWith("title")) {
                String[] split = firstCommand.split(":");
                String colorMessage = split[1].replace("&", "§").replace("%player%", bukkitPlayer.getName()).replace("%pokemon%", pokeName);
                showTitle(bukkitPlayer, colorMessage);
                continue;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), firstCommand.replace("%player%", bukkitPlayer.getName()).replace("%pokemon%", pokeName));

        }
    }

    private static void showTitle(Player player, String title) {
        String subtitle = "";
        int fadeInTime = 20;
        int stayTime = 40;
        int fadeOutTime = 20;
        player.sendTitle(title, subtitle, fadeInTime, stayTime, fadeOutTime);
    }

    public static void sendShortTip(CommandSender sender) {
        sender.sendMessage("§4命令太短了！检查一下你的命令");
    }


}
