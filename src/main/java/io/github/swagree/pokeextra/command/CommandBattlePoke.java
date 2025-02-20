package io.github.swagree.pokeextra.command;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.util.PixelmonPlayerUtils;
import io.github.swagree.pokeextra.api.commandAPI;
import io.github.swagree.pokeextra.api.getPokeAPI;
import io.github.swagree.pokeextra.api.listPokeAPI;
import io.github.swagree.pokeextra.api.setPokeAPI;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;



public class CommandBattlePoke implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //没有op不能执行
        if (!sender.isOp()) {
            return false;
        }
        //获取rgp指令帮助
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            CommandHelper(sender);
            return true;
        }

        //指令过短则返回
        if (args.length < 2) {
            sendShortTip(sender);
            return false;
        }
        //获取宝可梦参数列表
        if (args[0].equalsIgnoreCase("list")) {
            listPokeAPI.getPokemonAttributeList(sender, args);
            return true;
        }


        Player player = Bukkit.getPlayer(args[0]);
        if(player==null){
            player.sendMessage("§c对不起，这个玩家不存在或不在线");
            return false;
        }
        //获取宝可梦的EnumSpecies
        getPokeAPI.getPokeEnumSpecies result = getPokeAPI.getGetPokeEnumSpecies(sender, args, "rbp");
        //实例化宝可梦
        Pokemon pokemon = result.pokemon;
        //设置宝可梦参数 从第2位开始
        if(!result.listFlag){
            setPokeAPI.toSetPokemonAttribute(sender, args, pokemon, 2);
        }
        //对战宝可梦
        battlePOKE(player, pokemon);

        if (commandAPI.toExecuteCommand(result.listFlag, player, result.flagCommands, pokemon,"rbp")) return true;


        return true;
    }

    private void battlePOKE(Player player, Pokemon pokemon) {
        EntityPlayerMP entityPlayerMP = PixelmonPlayerUtils.getUniquePlayerStartingWith(player.getName());
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(entityPlayerMP);
        EntityPixelmon entityPixelmon1 = party.getAndSendOutFirstAblePokemon(entityPlayerMP);
        PlayerParticipant playerParticipant1 = new PlayerParticipant(entityPlayerMP,entityPixelmon1);
        World world = player.getWorld();

        net.minecraft.server.v1_12_R1.World nmsWorld = getNMSWorld(world);


        EntityPixelmon entityPixelmon2 = new EntityPixelmon((net.minecraft.world.World)(Object) nmsWorld);

        entityPixelmon2.setPokemon(pokemon);

        entityPixelmon2.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() );

        nmsWorld.addEntity((net.minecraft.server.v1_12_R1.Entity)(Object)entityPixelmon2);


        WildPixelmonParticipant playerParticipant2 = new WildPixelmonParticipant(entityPixelmon2);

        BattleRules battleRules = new BattleRules();
        battleRules.battleType =  EnumBattleType.Single;
        BattleRegistry.startBattle(new BattleParticipant[]{playerParticipant1},new BattleParticipant[]{playerParticipant2},battleRules);
    }

    public net.minecraft.server.v1_12_R1.World getNMSWorld(World bukkitWorld) {
        CraftWorld craftWorld = (CraftWorld) bukkitWorld;
        return craftWorld.getHandle();
    }



    static void sendShortTip(CommandSender sender) {
        sender.sendMessage("§4命令太短了！检查一下你的命令");
    }


    private static void CommandHelper(CommandSender player) {
        player.sendMessage("§b<§m*-----=======§b热对战宝可梦§b§m=======-----§b>");
        player.sendMessage("§e/rbp 玩家名称 宝可梦名称 [参数] §f- 对战指定的宝可梦 参数可选 [form:形态编号] [level:100]");
        player.sendMessage("§e/rbp 玩家名称 宝可梦名称 flag:uncatchable 能让宝可梦无法捕捉");
        player.sendMessage("§e/rbp 玩家名称 宝可梦名称 flag:removeFlag 能让宝可梦战斗一次结束后消失");
        player.sendMessage("§e/rbp 玩家名称 宝可梦名称 flag:bindowner_玩家名 只能由指定玩家对战 且捕捉后绑定");

        player.sendMessage("§e/rbp list 宝可梦名称 §f- 查询宝可梦的形态编号");

        player.sendMessage("§a宝可梦名称可替换如下：");
        player.sendMessage("§brandom §f全图鉴随机 §brandom:!legendary §f随机里排除神兽 ");
        player.sendMessage("§brandom:!ub §f随机里排除异兽 §brandom:!orb §f排除神兽和异兽 ");

        player.sendMessage("§brandomlegendary §f神兽随机 §brandomultra §f异兽随机");

        player.sendMessage("§bgen1 §f第一世代宝可梦随机 可以把1换成其他世代的数字 §btype:草 §f按属性随机 草可以换成其他属性");
        player.sendMessage("§b1-50 §f在图鉴1-50内随机(也可以1-50,50-100两个范围) §blist:9 在配置文件中名称为9的列表中随机");
        player.sendMessage("§a可选参数如下:");
        player.sendMessage("§blevel:100 §f等级(也可以1-10范围随机 还可以1-10,20-30两个范围随机)  §bmove1:喷水 §f给予的宝可梦1号位有指定技能 以此类推 ");
        player.sendMessage("§bshiny §f闪光  §bbind §f绑定 §bunbreed §f绝育 §bmt §f梦特 ");
        player.sendMessage("§bdoeslevel §f锁等级  §bhelditem:携带物名称 §f例如helditem:pixelmon_focus_sash");
        player.sendMessage("§bpoketexture:材质名 §f指定材质宝可梦 自己学怎么查材质奥 因为这是本地的");
        player.sendMessage("§bflag:自定义标签 §f给予自定义标签宝可梦 可能需要搭配其他插件使用");
        player.sendMessage("§bfriendship:220 §f给予玩家亲密度为220的宝可梦 §begg §f宝可梦变成蛋");
        player.sendMessage("§bgender:male §f给雄性宝可梦 可选参数有[male雄,female雌,none无]");
        player.sendMessage("§bgrowth:huge §f给巨大体型宝可梦 不知道huge怎么填可输入/rgp list growth 查询");
        player.sendMessage("§bpokeball:GreatBall §f给指定球种的宝可梦 不知道huge怎么填可输入/rgp list pokeball 查询");
        player.sendMessage("§bform:1 §f给指定形态的宝可梦 不知道这个1怎么填 可输入/rgp list form 宝可梦名称");
        player.sendMessage("§bnature:gentle §f给指定性格的宝可梦 不知道这个gentle怎么填 可输入/rgp list nature 查询");
        player.sendMessage("§bability:特性名称 §f给指定形态的宝可梦 不知道这个1怎么填 可输入/rgp list ability 宝可梦名称 查询");
        player.sendMessage("§bivshp:31 血量个体为31 可选参数如下：");
        player.sendMessage("§f[ivshp血量,ivsspeed速度,ivsdefence防御,ivsspecialdefence特防,ivsattack攻击,ivsspecialattack特攻 ivsall:31 所有个体全为31]");
        player.sendMessage("§brandomv:2 §f指定宝可梦为随机2v 即使是神兽也可以！ ");
        player.sendMessage("§bevshp:255 §f血量努力值为255 可选参数如下：");
        player.sendMessage("§f[evshp血量,evsspeed速度,evsdefence防御,evsspecialdefence特防,evsattack攻击,evsspecialattack特攻]");
        player.sendMessage("§e内容过多，导致过长 可以鼠标滚轮滚上去 进行查看全部内容");
    }

}
