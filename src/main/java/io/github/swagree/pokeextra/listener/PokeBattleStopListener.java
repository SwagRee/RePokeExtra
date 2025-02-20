package io.github.swagree.pokeextra.listener;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import io.github.swagree.pokeextra.Main;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class PokeBattleStopListener implements Listener {
    @EventHandler
    public void onPlayerCommandStopPokemonBattle(PlayerCommandPreprocessEvent event) {
        EntityPlayerMP player = Pixelmon.storageManager.getParty(event.getPlayer().getUniqueId()).getPlayer();
        BattleControllerBase battleController = BattleRegistry.getBattle(player);
        if(!Main.plugin.getConfig().getBoolean("stopBattle.enable")){
            return;
        }
        if (battleController != null && shouldStopBattle(event.getMessage())) {
            battleController.endBattle();

            String message = Main.plugin.getConfig()
                    .getString("stopBattle.message")
                    .replace("&", "§");

            event.getPlayer().sendMessage(message);

            event.setCancelled(true);
        }
    }

// 提取出判断逻辑为单独方法
        private boolean shouldStopBattle(String playerMessage) {
            List<String> commandList = Main.plugin.getConfig().getStringList("stopBattle.command");
            return commandList.contains(playerMessage);
        }

}
