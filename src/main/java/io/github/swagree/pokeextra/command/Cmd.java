package io.github.swagree.pokeextra.command;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenScreen;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ClientChangeOpenPC;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ClientInitializePC;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumGuiScreen;
import io.github.swagree.pokeextra.Main;
import io.github.swagree.pokeextra.gui.PokemonInfoGui;
import io.github.swagree.pokeextra.listener.gui.PokeGuiPClistener;
import io.github.swagree.pokeextra.listener.gui.PokemonInfoGuiListener;
import io.github.swagree.pokeextra.util.YmlUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class Cmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            commandHelper(sender);
            return true;
        }
        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.reload")) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadAllConfig(sender);
            }
        }
        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.info")) {
            if (args[0].equalsIgnoreCase("info")) {
                toShowOnePokemon((Player) sender);
            }
            if (args[0].equalsIgnoreCase("sa")) {
                toShowAllPokemon((Player) sender);
            }
        }
        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.heal")) {
            if (args[0].equalsIgnoreCase("heal")) {
                toHealPoke(args);
            }
        }

        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.ppc")) {
            if (args[0].equalsIgnoreCase("ppc")) {
                toOpenNativePC((Player) sender);
            }
        }
        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.evo")) {
            if (args[0].equalsIgnoreCase("evo")) {
                toEvoPoke(sender, args);
            }
        }
        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.tradeevo")) {
            if (args[0].equalsIgnoreCase("tradeevo")) {
                toTradeEvoPoke(sender, args);
            }
        }

        if (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.pc")) {
            if (args[0].equalsIgnoreCase("pc")) {
                handlePCCommand(sender, args);
            }
        }


        return false;
    }

    private void handlePCCommand(CommandSender sender, String[] args) {
        // 处理打开PC界面
        if (args.length == 1) {
            openPCInventory(sender);
            return;
        }

        // 处理 "back" 命令
        if (args[1].equalsIgnoreCase("back")) {
            if (args.length > 2) {
                restorePokemon(sender, args[2]);
            } else {
                sender.sendMessage("请提供玩家名称以还原宝可梦！");
            }
            return;
        }

        // 处理其他玩家的PC
        if (args.length > 1) {
            openPlayerPC(sender, args[1]);
        }
    }

    private void openPCInventory(CommandSender sender) {
        try {
            Player player = (Player) sender;
            PokeGuiPClistener.MyHolder myHolder = new PokeGuiPClistener.MyHolder(0, player.getUniqueId());
            player.openInventory(myHolder.getInventory());
        } catch (Exception e) {
            sender.sendMessage("出现问题，无法打开PC界面！");
        }
    }

    /**
     * 还原宝可梦
     */
    private void restorePokemon(CommandSender sender, String playerName) {
        try {
            UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
            List<String> pokemonList = YmlUtil.pcBackupConfig.getStringList(playerName);
            int size = pokemonList.size();

            if (size == 0) {
                sender.sendMessage("§4对不起，" + playerName + "没有可以还原的宝可梦了！");
                return;
            }

            for (int i = 0; i < size; i++) {
                NBTTagCompound nbt = MojangsonParser.parse(pokemonList.get(i));
                Pokemon pokemon1 = Pixelmon.pokemonFactory.create((net.minecraft.nbt.NBTTagCompound) (Object) nbt);
                Pixelmon.storageManager.getParty(uuid).add(pokemon1);
            }

            YmlUtil.pcBackupConfig.set(playerName, null);
            YmlUtil.pcBackupConfig.save(new File(Main.plugin.getDataFolder(), "PcBackup.yml"));
            sender.sendMessage("§b为" + playerName + "还原成功");
        } catch (MojangsonParseException | IOException e) {
            sender.sendMessage("处理宝可梦时出现问题：" + e.getMessage());
        }
    }

    /**
     * 打开其他玩家的PC
     */
    private void openPlayerPC(CommandSender sender, String playerName) {
        OfflinePlayer targetPlayer = getOfflinePlayerByName(playerName);
        if (targetPlayer != null) {
            Player senderPlayer = (Player) sender;
            PokeGuiPClistener.MyHolder myHolder = new PokeGuiPClistener.MyHolder(0, targetPlayer.getUniqueId());
            senderPlayer.openInventory(myHolder.getInventory());
        } else {
            sender.sendMessage("玩家 " + playerName + " 不存在！");
        }
    }

    /**
     * 根据玩家名称获取离线玩家
     */
    private OfflinePlayer getOfflinePlayerByName(String playerName) {
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                return p;
            }
        }
        for (OfflinePlayer p : getServer().getOfflinePlayers()) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                return p;
            }
        }
        return null;
    }

    private void toTradeEvoPoke(CommandSender sender, String[] args) {
        int slot = 0;
        try {
            UUID uniqueId = Bukkit.getPlayer(args[1]).getUniqueId();
            slot = Integer.parseInt(args[2]) - 1;
            Pokemon pokemon = Pixelmon.storageManager.getParty(uniqueId).get(slot);
            EntityPixelmon pixelmon = pokemon.getOrSpawnPixelmon(pokemon.getOwnerPlayer());

            boolean b = pixelmon.testTradeEvolution(pokemon.getSpecies());
            if (!b) {
                Bukkit.getPlayer(args[1]).sendMessage("§c尝试进化失败了！请检查您的宝可梦携带物等条件是否满足！以及不要佩戴不变之石");

            }
            List<String> firstCommands = Main.plugin.getConfig().getStringList("tradeEvoCommand");
            toExecuteCommands((Player) sender, firstCommands);

        } catch (Exception e) {
            sender.sendMessage("§c输入的有点问题！玩家不存在或者需要是数字！");
        }
    }

    private void toEvoPoke(CommandSender sender, String[] args) {
        int slot = 0;
        try {
            UUID uniqueId = Bukkit.getPlayer(args[1]).getUniqueId();
            slot = Integer.parseInt(args[2]) - 1;
            Pokemon pokemon = Pixelmon.storageManager.getParty(uniqueId).get(slot);
            EntityPixelmon pixelmon = pokemon.getOrSpawnPixelmon(pokemon.getOwnerPlayer());

            boolean b = pixelmon.testLevelEvolution(100);
            if (!b) {
                Bukkit.getPlayer(args[1]).sendMessage("§c尝试进化失败了！请检查是否满足进化等级等条件!");
            }
            List<String> firstCommands = Main.plugin.getConfig().getStringList("evoCommand");
            toExecuteCommands((Player) sender, firstCommands);

        } catch (Exception e) {
            sender.sendMessage("§c输入的有点问题！玩家不存在或者需要是数字！");
        }
    }

    private static void toOpenNativePC(Player sender) {
        Player player = sender;

        PCStorage pcStorage = Pixelmon.storageManager.getPCForPlayer(player.getUniqueId());
        EntityPlayerMP entityPlayerMP = Pixelmon.storageManager.getParty(player.getUniqueId()).getPlayer();
        Pixelmon.network.sendTo(new ClientInitializePC(pcStorage), entityPlayerMP);
        Pixelmon.network.sendTo(new ClientChangeOpenPC(pcStorage.uuid), entityPlayerMP);
        pcStorage.sendContents(entityPlayerMP);
        OpenScreen.open(entityPlayerMP, EnumGuiScreen.PC);
    }

    private static void toHealPoke(String[] args) {
        String beHealMan = args[1];
        UUID uniqueId = Bukkit.getPlayer(beHealMan).getUniqueId();
        Pixelmon.storageManager.getParty(uniqueId).heal();
        Player player = Bukkit.getPlayer(uniqueId);
        List<String> commands = Main.plugin.getConfig().getStringList("Heal.command");
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }

    }

    private static void toShowAllPokemon(Player sender) {
        PokemonInfoGuiListener.showAllPokemon(sender);
    }

    private static void toShowOnePokemon(Player sender) {
        PokemonInfoGui.Gui(sender);
    }


    private static void reloadAllConfig(CommandSender sender) {
        sender.sendMessage("重载成功！");
        YmlUtil.reloadConfigAll();
    }

    private void toExecuteCommands(Player bukkitPlayer, List<String> firstCommands) {
        for (String firstCommand : firstCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), firstCommand.replace("%player%", bukkitPlayer.getName()));
        }
    }

    private void commandHelper(CommandSender sender) {
        sender.sendMessage("§b<§m*-----=======§b热宝可梦额外§b §m=======-----§b>");

        sender.sendMessage("§e/rpe heal ID §f- 治疗玩家宝可梦,配置文件可自定义执行命令");
        sender.sendMessage("§e/rpe ppc §f- 打开原版PC界面(需要权限rpe.ppc)");
        sender.sendMessage("§e/rpe pc ID §f- 打开插件pc界面(shift+左键删宝可梦 不行就多试几次)");
        sender.sendMessage("§e/rpe pc back ID §f- 还原某个玩家被删除的所有宝可梦");
        sender.sendMessage("§e/rpe info §f- 打开精灵信息面板");
        sender.sendMessage("§e/rpe sa §f- 展示全队精灵");
        sender.sendMessage("§e/rpe evo ID 位置 §f- 等级进化指定玩家指定位置的宝可梦");
        sender.sendMessage("§e/rpe tradeevo ID 位置 §f- 通讯进化指定玩家指定位置的宝可梦");
        sender.sendMessage("§e/rgp §f- 查询给予命令的帮助");
        sender.sendMessage("§e/rep §f- 查询编辑命令的帮助");
        sender.sendMessage("§e/rbp §f- 查询对战宝可梦命令的帮助");
        sender.sendMessage("§e/rsp §f- 查询生成宝可梦命令的帮助");

        sender.sendMessage("§e/rdp ID 位置 §f- 删除玩家背包指定位置宝可梦");
        sender.sendMessage("§e/rpe help §f- 查看帮助");
    }
}
