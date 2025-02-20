package io.github.swagree.pokeextra.command;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokeextra.Main;
import io.github.swagree.pokeextra.api.commandAPI;
import io.github.swagree.pokeextra.api.listPokeAPI;
import io.github.swagree.pokeextra.api.setPokeAPI;
import io.github.swagree.pokeextra.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandEditPoke implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            CommandHelper(sender);
            return false;
        }



        if (args.length < 2) {
            commandAPI.sendShortTip(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            listPokeAPI.getPokemonAttributeList(sender, args);
            return true;
        }


        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            sender.sendMessage("§4对不起，这个玩家不存在");
            return false;
        }
        Pokemon pokemon = getPokemon(args[1], offlinePlayer);
        setPokeAPI.toSetPokemonAttribute(sender, args, pokemon, 2);
        offlinePlayer.getPlayer().sendMessage(YmlUtil.messageConfig.getString("repMessage").replace("&","§").replace("%pokemon%",pokemon.getLocalizedName()));
        return false;
    }


    private static void CommandHelper(CommandSender sender) {
        sender.sendMessage("§b<§m*-----======= §b热编辑宝可梦§b §m=======-----§b>");
        sender.sendMessage("§e/rep 玩家名 宝可梦位置 参数 §f- 编辑宝可梦 案例:/rep test 1 unbind");
        sender.sendMessage("§e/rep reload §f- 重载插件");
        sender.sendMessage("§a可选参数如下:");
        sender.sendMessage("§blevel:100 §f等级(也可以1-10范围随机 还可以1-10,20-30两个范围随机)  §bmove1:喷水 §f给予的宝可梦1号位有指定技能 以此类推 ");
        sender.sendMessage("§bshiny §f闪光 §bunshiny §f解闪 §bbind §f绑定 §bunbreed §f绝育 §bmt §f梦特 ");
        sender.sendMessage("§bdoeslevel §f锁等级  §bhelditem:携带物名称 §f例如helditem:pixelmon_focus_sash");
        sender.sendMessage("§bpoketexture:材质名 §f指定材质宝可梦 自己学怎么查材质奥 因为这是本地的");
        sender.sendMessage("§bflag:自定义标签 §f给予自定义标签宝可梦 可能需要搭配其他插件使用");
        sender.sendMessage("§bremoveflag:自定义标签 §f删除指定标签 §bunbind §f解绑");
        sender.sendMessage("§brdNature §f随机性格 §brdGrowth §f随机体型");
        sender.sendMessage("§bfriendship:220 §f给予玩家亲密度为220的宝可梦 §begg §f宝可梦变成蛋");
        sender.sendMessage("§bgender:male §f给雄性宝可梦 可选参数有[male雄,female雌,none无]");
        sender.sendMessage("§bgrowth:huge §f给巨大体型宝可梦 不知道huge怎么填可输入/rgp list growth 查询");
        sender.sendMessage("§bpokeball:GreatBall §f给指定球种的宝可梦 不知道huge怎么填可输入/rgp list pokeball 查询");
        sender.sendMessage("§bform:1 §f给指定形态的宝可梦 不知道这个1怎么填 可输入/rgp list form 宝可梦名称");
        sender.sendMessage("§bnature:gentle §f给指定性格的宝可梦 不知道这个gentle怎么填 可输入/rgp list nature 查询");
        sender.sendMessage("§bability:特性名称 §f给指定形态的宝可梦 不知道这个1怎么填 可输入/rgp list ability 宝可梦名称 查询");
        sender.sendMessage("§bivshp:31 血量个体为31 可选参数如下：");
        sender.sendMessage("§f[ivshp血量,ivsspeed速度,ivsdefence防御,ivsspecialdefence特防,ivsattack攻击,ivsspecialattack特攻 ivsall:31 所有个体全为31]");
        sender.sendMessage("§brandomv:2 §f指定宝可梦为随机2v 即使是神兽也可以！ ");
        sender.sendMessage("§bevshp:255 §f血量努力值为255 可选参数如下：");
        sender.sendMessage("§f[evshp血量,evsspeed速度,evsdefence防御,evsspecialdefence特防,evsattack攻击,evsspecialattack特攻]");
        sender.sendMessage("§e内容过多，导致过长 可以鼠标滚轮滚上去 进行查看全部内容");
    }


    private Pokemon getPokemon(String num, OfflinePlayer offlinePlayer) {
        return Pixelmon.storageManager.getParty(offlinePlayer.getUniqueId()).get(Integer.valueOf(num) - 1);
    }
}
