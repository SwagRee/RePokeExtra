package io.github.swagree.pokeextra;

import io.github.swagree.pokeextra.command.*;
import io.github.swagree.pokeextra.listener.*;
import io.github.swagree.pokeextra.listener.flagListener.ReBattlePokeListener;
import io.github.swagree.pokeextra.listener.flagListener.ReBindPokeListener;
import io.github.swagree.pokeextra.listener.gui.PokeGuiPClistener;
import io.github.swagree.pokeextra.listener.gui.PokemonInfoGuiListener;

import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public static Main plugin;
    @Override
    public void onEnable() {

        Bukkit.getConsoleSender().sendMessage("§7[RePokeExtra] §b作者§fSwagRee §cQQ:§f352208610");

        Bukkit.getPluginManager().registerEvents(new PokeGuiPClistener(),this);
        Bukkit.getPluginManager().registerEvents(new PokeBattleStopListener(),this);
        Bukkit.getPluginManager().registerEvents(new PokeHealthWithBattleListener(),this);
        Bukkit.getPluginManager().registerEvents(new PokemonInfoGuiListener(),this);

        Bukkit.getPluginManager().registerEvents(new ReBattlePokeListener(),this);
        Bukkit.getPluginManager().registerEvents(new ReBindPokeListener(),this);

        getCommand("rpe").setExecutor(new Cmd());
        getCommand("rgp").setExecutor(new CommandGivePoke());
        getCommand("rep").setExecutor(new CommandEditPoke());
        getCommand("rdp").setExecutor(new CommandDelPoke());
        getCommand("rbp").setExecutor(new CommandBattlePoke());
        getCommand("rsp").setExecutor(new CommandSpawnPoke());

        saveDefaultConfig();

        loadConfig();

        for (int i = 0; i < 36; i++) {
            PokeGuiPClistener.invHaspMap.put(PokeGuiPClistener.slot[i], i);
        }

        plugin = this;

    }

    private void loadConfig() {


        File messageFile = new File(getDataFolder(), "message.yml");

        if (!messageFile.exists()) {
            saveResource("message.yml", false);
        }

        File pcBackupFile = new File(getDataFolder(), "PcBackup.yml");

        if (!pcBackupFile.exists()) {
            saveResource("PcBackup.yml", false);
        }

        File example = new File(getDataFolder(), "pokelist/example.yml");
        if (!example.exists()) {
            saveResource("pokelist/example.yml", false);
        }

        File blackList = new File(getDataFolder(), "blacklist/blacklist.yml");
        if (!blackList.exists()) {
            saveResource("blacklist/blacklist.yml", false);
        }


    }


}
