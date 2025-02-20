package io.github.swagree.pokeextra.listener.flagListener;

import catserver.api.bukkit.event.ForgeEvent;
import com.google.common.collect.ImmutableMap;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent.StartCapture;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import io.github.swagree.pokeextra.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReBattlePokeListener implements Listener {
    @EventHandler
    public void onBattleStarted(ForgeEvent event) {

        if (event.getForgeEvent() instanceof StartCapture) {
            StartCapture forgeEvent = (StartCapture)event.getForgeEvent();
            boolean removeFlag = forgeEvent.getPokemon().getPokemonData().hasSpecFlag("removeFlag");
            if (removeFlag) {
                forgeEvent.setCanceled(true);
            }
            boolean uncatchFlag = forgeEvent.getPokemon().getPokemonData().hasSpecFlag("uncatchable");
            if (uncatchFlag) {
                forgeEvent.setCanceled(true);
            }
        }

        if (event.getForgeEvent() instanceof BeatWildPixelmonEvent) {
            BeatWildPixelmonEvent forgeEvent = (BeatWildPixelmonEvent)event.getForgeEvent();
            PixelmonWrapper[] allPokemon = forgeEvent.wpp.allPokemon;

            for(PixelmonWrapper pixelmonWrapper : allPokemon) {
                ConfigurationSection allFlagCommand = Main.plugin.getConfig().getConfigurationSection("rbp.flagCommands");

                for(String key : allFlagCommand.getKeys(false)) {
                    if (pixelmonWrapper.pokemon.hasSpecFlag(key)) {
                        for(String command : Main.plugin.getConfig().getStringList("rbp.flagCommands." + key + ".success")) {
                            command = command.replace("%player%", forgeEvent.player.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&","§"));
                        }
                    }
                }
            }
        }

        if (event.getForgeEvent() instanceof BattleEndEvent) {
            BattleEndEvent forgeEvent = (BattleEndEvent)event.getForgeEvent();
                for (BattleParticipant participant : forgeEvent.bc.participants) {
                    PixelmonWrapper[] allPokemon = participant.allPokemon;
                    for (PixelmonWrapper pixelmonWrapper : allPokemon) {
                        if(pixelmonWrapper.pokemon == null){
                            continue;
                        }
                        if(pixelmonWrapper.entity == null){
                            continue;
                        }
                        if (pixelmonWrapper.pokemon.hasSpecFlag("removeFlag")) {

                            pixelmonWrapper.entity.getStoragePokemonData().setHealth(0);
                        }
                        if (pixelmonWrapper.pokemon.hasSpecFlag("catchRemoveFlag")) {
                            pixelmonWrapper.entity.getStoragePokemonData().setHealth(0);
                        }
                    }
                }

            for(BattleParticipant participant : forgeEvent.bc.participants) {
                ImmutableMap<BattleParticipant, BattleResults> results = forgeEvent.results;
                BattleResults battleResults = (BattleResults)results.get(participant);
                if (battleResults.equals(BattleResults.VICTORY) || battleResults.equals(BattleResults.DRAW) || battleResults.equals(BattleResults.FLEE)) {
                    PixelmonWrapper[] allPokemon = participant.allPokemon;

                    for(PixelmonWrapper pixelmonWrapper : allPokemon) {
                        ConfigurationSection allFlagCommand = Main.plugin.getConfig().getConfigurationSection("rbp.flagCommands");

                        for(String key : allFlagCommand.getKeys(false)) {
                            if (pixelmonWrapper.pokemon.hasSpecFlag(key)) {
                                for(String command : Main.plugin.getConfig().getStringList("rbp.flagCommands." + key + ".fail")) {
                                    for(PlayerParticipant playerParticipant : forgeEvent.bc.getPlayers()) {
                                        String name = playerParticipant.player.getName();
                                        if (!name.isEmpty()) {
                                            command = command.replace("%player%", name).replace("&","§");
                                        }
                                    }

                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
 