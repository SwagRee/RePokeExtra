package io.github.swagree.pokeextra.util;

import io.github.swagree.pokeextra.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class YmlUtil {


    public static File filePcBackup = new File(Main.plugin.getDataFolder(), "PcBackup.yml");
    public static YamlConfiguration pcBackupConfig = YamlConfiguration.loadConfiguration(filePcBackup);

    public static File fileMessage = new File(Main.plugin.getDataFolder(), "message.yml");
    public static YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(fileMessage);

    // 存储 pokelist 文件夹下的配置文件
    public static final Map<String, YamlConfiguration> pokelistConfigs = new HashMap<>();

    // 存储 blacklist 文件夹下的配置文件
    public static final Map<String, YamlConfiguration> blacklistConfigs = new HashMap<>();



    // 设置 PcBackup 配置
    public static void setPcBackup(YamlConfiguration pcBackupConfig) {
        YmlUtil.pcBackupConfig = pcBackupConfig;
    }

    // 设置 message 配置
    public static void setMessageConfig(YamlConfiguration messageConfig) {
        YmlUtil.messageConfig = messageConfig;
    }

    // 加载 pokelist 文件夹下所有 .yml 文件
    private static void loadPokelistConfigs() {
        pokelistConfigs.clear(); // 清空之前加载的内容

        File pokelistFolder = new File(Main.plugin.getDataFolder(), "pokelist");
        if (!pokelistFolder.exists()) {
            pokelistFolder.mkdirs(); // 如果文件夹不存在，创建它
        }

        File[] files = pokelistFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                pokelistConfigs.put(file.getName(), config);
            }
        }
    }

    // 加载 blacklist 文件夹下所有 .yml 文件
    private static void loadBlacklistConfigs() {
        blacklistConfigs.clear(); // 清空之前加载的内容

        File blacklistFolder = new File(Main.plugin.getDataFolder(), "blacklist");
        if (!blacklistFolder.exists()) {
            blacklistFolder.mkdirs(); // 如果文件夹不存在，创建它
        }

        File[] files = blacklistFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                blacklistConfigs.put(file.getName(), config);
            }
        }
    }

    // 重载所有配置文件
    public static void reloadConfigAll() {
        // 重载主插件配置
        Main.plugin.reloadConfig();

        // 重置 LimitPoke 配置


        // 重置 PcBackup 配置
        File pcBackupFile = new File(Main.plugin.getDataFolder(), "PcBackup.yml");
        YamlConfiguration newPcBackupConfig = YamlConfiguration.loadConfiguration(pcBackupFile);
        setPcBackup(newPcBackupConfig);

        // 重置 message 配置
        File messageFile = new File(Main.plugin.getDataFolder(), "message.yml");
        YamlConfiguration newMessageConfig = YamlConfiguration.loadConfiguration(messageFile);
        setMessageConfig(newMessageConfig);

        // 重置 pokelist 文件夹下的 .yml 配置文件
        loadPokelistConfigs();

        // 重置 blacklist 文件夹下的 .yml 配置文件
        loadBlacklistConfigs();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"plugin reload repokeextra");
    }
}