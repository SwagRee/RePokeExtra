package io.github.swagree.pokeextra.api;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class listPokeAPI {
    public static void getPokemonAttributeList(CommandSender sender, String[] args) {
        //宝可梦球
        if (args[1].equalsIgnoreCase("pokeball")) {
            getPokeBallList(sender);
        }
        // 体型
        if (args[1].equalsIgnoreCase("growth")) {
            getGrowthList(sender);
        }
        // 形态
        if (args[1].equalsIgnoreCase("form")) {
            if (getFormList(sender, args));
        }
        // 特性
        if (args[1].equalsIgnoreCase("ability")) {
            getAbilityList(sender, args);
        }
        // 性格
        if (args[1].equalsIgnoreCase("nature")) {
            getNatureList(sender);
        }
    }
    private static void getNatureList(CommandSender sender) {
        EnumNature[] values = EnumNature.values();

        sender.sendMessage("§a宝可梦性格如下:");
        for (EnumNature enumNature : values) {
            sender.sendMessage(enumNature + " " + enumNature.getLocalizedName());
        }
    }

    private static void getAbilityList(CommandSender sender, String[] args) {
        Optional<EnumSpecies> fromName = EnumSpecies.getFromName(args[2]);
        EnumSpecies enumSpecies = fromName.get();
        Pokemon pokemon = Pixelmon.pokemonFactory.create(enumSpecies);

        List<AbilityBase> allAbilities = pokemon.getBaseStats().getAllAbilities();
        sender.sendMessage("§a" + pokemon.getLocalizedName() + "有如下特性:");
        for (int i = 0; i < allAbilities.size(); i++) {
            sender.sendMessage("§e" + i + " " + allAbilities.get(i).getUnlocalizedName().replace("ability.", "").replace(".name", "") + " " + allAbilities.get(i).getLocalizedName());
        }
        sender.sendMessage("§e使用指令给予指定特性的宝可梦，需要用数字！");
    }

    private static boolean getFormList(CommandSender sender, String[] args) {
        Optional<EnumSpecies> fromName = EnumSpecies.getFromName(args[2]);
        EnumSpecies enumSpecies = fromName.get();
        Pokemon pokemon = Pixelmon.pokemonFactory.create(enumSpecies);
        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);

        if (possibleForms.size() == 1) {
            sender.sendMessage("§4对不起 这只精灵没有什么特殊形态");
            return true;
        }
        sender.sendMessage("§e" + pokemon.getLocalizedName() + "有如下形态:");
        for (int j = 1; j < possibleForms.size(); j++) {
            sender.sendMessage(j + " §b" + possibleForms.get(j).getLocalizedName() + " §f" + possibleForms.get(j).getUnlocalizedName().replace("pixelmon.generic.form.", ""));
        }
        return false;
    }

    private static void getGrowthList(CommandSender sender) {
        EnumGrowth[] values = EnumGrowth.values();
        sender.sendMessage("§a宝可梦体型如下:");
        for (EnumGrowth growth : values) {
            sender.sendMessage("§b" + growth + "  §f" + growth.getLocalizedName());
        }
    }

    private static void getPokeBallList(CommandSender sender) {
        EnumPokeballs[] values = EnumPokeballs.values();
        sender.sendMessage("§a宝可梦球列表如下:");
        for (EnumPokeballs pokeBall : values) {
            sender.sendMessage("§b" + pokeBall + "  §f" + pokeBall.getLocalizedName());
        }
    }
}
