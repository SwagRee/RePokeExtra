package io.github.swagree.pokeextra.listener;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;

import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import io.github.swagree.pokeextra.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class PokeHealthWithBattleListener implements Listener {
    @EventHandler
    public void endBattleToHeal(ForgeEvent forgeEvent) {
        if (forgeEvent.getForgeEvent() instanceof BattleEndEvent) {
            if (((BattleEndEvent) forgeEvent.getForgeEvent()).bc.battleEnded) {
                try {
                    if(!Main.plugin.getConfig().getBoolean("BattleEndToHeal.enable")){
                        return;
                    }
                    // 获取Battle结束事件对象
                    BattleEndEvent battleEvent = (BattleEndEvent) forgeEvent.getForgeEvent();
                    String permission = Main.plugin.getConfig().getString("BattleEndToHeal.permission");
                    List<String> commands = Main.plugin.getConfig().getStringList("BattleEndToHeal.command");
                    // 处理第一个玩家
                    handlePlayerHeal(battleEvent, 0, permission, commands);

                    // 处理第二个玩家（如果存在）
                    if (battleEvent.bc.getPlayers().size() > 1) {
                        handlePlayerHeal(battleEvent, 1, permission, commands);
                    }

                } catch (Exception e) {

                }
            }
        }

    }


    // 提取处理玩家逻辑的方法
    private void handlePlayerHeal(BattleEndEvent battleEvent, int playerIndex, String permission, List<String> commands) {
        PlayerParticipant playerParticipant = battleEvent.bc.getPlayers().get(playerIndex);
        if (playerParticipant != null) {
            UUID uuid = playerParticipant.player.getUniqueID();
            Player player = Bukkit.getPlayer(uuid);

            if (player != null && player.hasPermission(permission)) {
                toExecuteCommands(player, commands);
                for (PixelmonWrapper pixelmonWrapper : playerParticipant.allPokemon) {
                    pixelmonWrapper.pokemon.heal();
                    pixelmonWrapper.clearStatus();
                }
            }
        }
    }

    public void toExecuteCommands(Player bukkitPlayer, List<String> firstCommands) {
        for (String firstCommand : firstCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), firstCommand.replace("%player%", bukkitPlayer.getName()));
        }
    }
}
