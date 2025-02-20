package io.github.swagree.pokeextra.listener.flagListener;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import io.github.swagree.pokeextra.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.List;

public class ReBindPokeListener implements Listener {
    @EventHandler
    public void onBattleStarted(ForgeEvent event) {

        if (event.getForgeEvent() instanceof CaptureEvent.StartCapture) {
            CaptureEvent.StartCapture forgeEvent = (CaptureEvent.StartCapture) event.getForgeEvent();
                try {
                    Field specFlagsField = forgeEvent.getPokemon().getPokemonData().getClass().getDeclaredField("specFlags");
                    specFlagsField.setAccessible(true);
                    List<String> specFlags = (List<String>) specFlagsField.get(forgeEvent.getPokemon().getPokemonData());
                    for (String specFlag : specFlags) {
                        if(specFlag.contains("bindowner_") && !specFlag.equals("bindowner_"+forgeEvent.player.getName())){
                            forgeEvent.setCanceled(true);
                            Bukkit.getPlayer(forgeEvent.player.getUniqueID()).sendMessage(YmlUtil.messageConfig.getString("bindownerMessage").replace("&","§"));
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
        }

        if (event.getForgeEvent() instanceof CaptureEvent.SuccessfulCapture) {
            CaptureEvent.SuccessfulCapture forgeEvent = (CaptureEvent.SuccessfulCapture) event.getForgeEvent();
            try {
                Field specFlagsField = forgeEvent.getPokemon().getPokemonData().getClass().getDeclaredField("specFlags");
                specFlagsField.setAccessible(true);
                List<String> specFlags = (List<String>) specFlagsField.get(forgeEvent.getPokemon().getPokemonData());
                for (String specFlag : specFlags) {
                    if(specFlag.equals("bindowner_"+forgeEvent.player.getName())){
                        forgeEvent.getPokemon().getPokemonData().addSpecFlag("untradeable");
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

