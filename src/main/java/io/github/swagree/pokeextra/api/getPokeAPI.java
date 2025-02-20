package io.github.swagree.pokeextra.api;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import io.github.swagree.pokeextra.Main;
import io.github.swagree.pokeextra.handler.ListProcessingResult;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class getPokeAPI {
    public static class getPokeEnumSpecies {
        public final Boolean listFlag;
        public final List<String> flagCommands;
        public final Pokemon pokemon;

        public getPokeEnumSpecies(Boolean listFlag, List<String> flagCommands, Pokemon pokemon) {
            this.listFlag = listFlag;
            this.flagCommands = flagCommands;
            this.pokemon = pokemon;
        }
    }

    public static Map<String, int[]> getGenMap() {
        Map<String, int[]> generationRanges = new HashMap<>();
        generationRanges.put("gen1", new int[]{1, 151});
        generationRanges.put("gen2", new int[]{152, 251});
        generationRanges.put("gen3", new int[]{252, 386});
        generationRanges.put("gen4", new int[]{387, 493});
        generationRanges.put("gen5", new int[]{494, 649});
        generationRanges.put("gen6", new int[]{650, 721});
        generationRanges.put("gen7", new int[]{722, 809});
        generationRanges.put("gen8", new int[]{810, 905});
        generationRanges.put("gen9", new int[]{906, 1025});
        return generationRanges;
    }

    public static getPokeEnumSpecies getGetPokeEnumSpecies(CommandSender sender, String[] args, String commandType) {
        Boolean listFlag = false;
        EnumSpecies enumSpecies = null;
        List<String> flagCommands = Collections.emptyList();
        String argument = args[1].toLowerCase();

        Map<String, int[]> generationRanges = getGenMap();

        if (generationRanges.containsKey(argument)) {
            int[] range = generationRanges.get(argument);
            enumSpecies = getGenPokemon(range[0], range[1]);
        } else {
            switch (argument) {
                case "random":
                    enumSpecies = getRandmonPokeByRule(commandType);
                    break;
                case "random3":
                    enumSpecies = getRandmonPokeByRule3(commandType);
                    break;
                case "random:!legendary":
                    enumSpecies = getRandmonPokeNoLegendary(commandType);
                    break;
                case "random:!ub":
                    enumSpecies = getRandmonPokeNoUltra(commandType);
                    break;
                case "random:!orb":
                    enumSpecies = getRandmonPokeNoLegendaryAndUltra(commandType);
                    break;
                case "randomlegendary":
                    enumSpecies = getRandomLegendaryByRule(commandType);
                    break;
                case "randomultra":
                    enumSpecies = getRandUltra();
                    break;
                default:
                    if (argument.startsWith("list:")) {
                        ListProcessingResult result = processList(args[1], sender, enumSpecies, commandType);
                        listFlag = result.isListFlag();
                        flagCommands = result.getFlagCommands();
                        return new getPokeEnumSpecies(listFlag, flagCommands, result.getPokemon());
                    }
                    if (argument.startsWith("type:")) {
                        enumSpecies = getPokeByType(sender, args, enumSpecies);
                        break;
                    }
                    if (argument.contains("-")) {
                        enumSpecies = getRangePoke(args);
                        break;
                    }
                    enumSpecies = getPokeFromName(args);
            }
        }
        Pokemon pokemon = Pixelmon.pokemonFactory.create(enumSpecies);
        return new getPokeEnumSpecies(listFlag, flagCommands, pokemon);
    }
    public static EnumSpecies getRandmonPokeByRule3(String commandType) {
        Random random = new Random();
        Result result = getResult(commandType); // 获取规则结果
        int attempts = 0;
        int maxAttempts = 100;
        EnumSpecies enumSpecies;

        do {
            if (random.nextInt(12) == 0) {
                enumSpecies = EnumSpecies.randomLegendary();
            } else {
                enumSpecies = EnumSpecies.randomPoke();
            }

            // 如果 Pokémon 是传说中的，并且通过黑名单检查，直接返回
            if (!isBlacklisted(enumSpecies, result)) {
                return enumSpecies;
            }

            attempts++;
        } while (attempts < maxAttempts);

        throw new IllegalStateException("Unable to generate a valid Pokémon after " + maxAttempts + " attempts.");
    }

    public static ListProcessingResult processList(String arg, CommandSender sender, EnumSpecies enumSpecies, String commandType) {
        boolean listFlag = false;
        List<String> flagCommands = new ArrayList<>();
        Pokemon listPoke = null;

        if (arg.startsWith("list:")) {
            listFlag = true;
            String[] split = arg.split(":");
            if (split.length > 1) {
                String s = split[1];
                String pokemonListFileName = Main.plugin.getConfig().getString(commandType + ".list." + s + ".pokemonList");
                File file = new File(Main.plugin.getDataFolder(), "pokelist/" + pokemonListFileName + ".yml");
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                List<String> pokemonList = yamlConfiguration.getStringList("pokemon");
                flagCommands = Main.plugin.getConfig().getStringList(commandType + ".list." + s + ".command");
                listPoke = getListPoke(sender, pokemonList);
            }
        }
        return new ListProcessingResult(listFlag, flagCommands, listPoke);
    }

    public static EnumSpecies getPokeFromName(String[] args) {
        Optional<EnumSpecies> fromName = EnumSpecies.getFromName(args[1]);
        return fromName.orElse(null);
    }

    private static final Random random = new Random();

    public static EnumSpecies getRangePoke(String[] args) {
        String[] ranges = args[1].split(",");
        int[] lowerBounds = new int[ranges.length];
        int[] upperBounds = new int[ranges.length];
        int[] weights = new int[ranges.length]; // 假设每个范围的权重

        // 解析范围和权重
        for (int i = 0; i < ranges.length; i++) {
            String[] split = ranges[i].split("-");
            int weight = 1; // 默认权重为1，可以根据需要调整
            lowerBounds[i] = Integer.parseInt(split[0]);
            upperBounds[i] = Integer.parseInt(split[1]);
            weights[i] = weight; // 可以根据实际情况设置不同的权重
        }

        // 根据权重选择一个范围
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }
        int randomWeight = random.nextInt(totalWeight) + 1;
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
            if (randomWeight <= sum) {
                int rangeIndex = random.nextInt(upperBounds[i] - lowerBounds[i] + 1) + lowerBounds[i];
                return getGenPokemon(rangeIndex);
            }
        }
        return null; // 如果没有找到合适的范围，返回null
    }

    private static EnumSpecies getGenPokemon(int index) {
        // 根据index获取对应的Pokemon种类
        // 这里需要实现具体的逻辑，比如根据index返回EnumSpecies中的一个值
        return EnumSpecies.values()[index];
    }

    public static EnumSpecies getPokeByType(CommandSender sender, String[] args, EnumSpecies enumSpecies) {
        String[] split = args[1].split(":");
        String type = split[1];
        boolean legendaryFlag = split.length == 3 && split[2].equalsIgnoreCase("true");

        int attempts = 0;
        int maxAttempts = 100;
        do {
            enumSpecies = legendaryFlag ? EnumSpecies.randomLegendary() : EnumSpecies.randomPoke(false);
            if (enumSpecies == null) continue;
            boolean matchesType = enumSpecies.getBaseStats().getTypeList().stream()
                    .anyMatch(enumType -> enumType.getLocalizedName().equals(type));
            if (matchesType) return enumSpecies;
            attempts++;
        } while (attempts < maxAttempts);

        sender.sendMessage("§cUnable to find a Pokémon of type " + type + " after " + maxAttempts + " attempts.");
        return null;
    }

    public static Pokemon getListPoke(CommandSender sender, List<String> stringList) {
        if (stringList.isEmpty()) {
            sender.sendMessage("§cThe selected list is empty or does not exist.");
            return null;
        }

        // 根据权重随机选择宝可梦名称
        String selectedPokemonName = getString(stringList);

        // 找到对应的完整配置项
        String selectItem = stringList.stream()
                .filter(s -> s.split(",")[0].equals(selectedPokemonName))
                .findFirst()
                .get();

        String[] split = selectItem.split(",");
        Pokemon pokemon = Pixelmon.pokemonFactory.create(EnumSpecies.getFromName(split[0]).get());
        if (split.length > 2) {
            String attributes = split[2];
            String[] attributeBySplit = attributes.split(" ");
            setPokeAPI.toSetPokemonAttribute(sender, attributeBySplit, pokemon, 0);
        }
        return pokemon;
    }

    private static String getString(List<String> stringList) {
        int totalWeight = 0;
        // 计算总权重
        for (String item : stringList) {
            String[] split = item.split(",");
            int weight = split.length > 1 ? Integer.parseInt(split[1]) : 1; // 如果split.length为1，则权重为1
            totalWeight += weight;
        }

        Random random = new Random();
        int randomNum = random.nextInt(totalWeight) + 1;

        int accumulatedWeight = 0;
        for (String item : stringList) {
            String[] split = item.split(",");
            int weight = split.length > 1 ? Integer.parseInt(split[1]) : 1; // 如果split.length为1，则权重为1
            if (randomNum <= accumulatedWeight + weight) {
                return split[0];
            }
            accumulatedWeight += weight;
        }
        // 这个返回语句实际上不会执行，因为上面的循环保证了一定会返回一个宝可梦名称
        return "";
    }


    private static EnumSpecies getGenPokemon(int num1, int num2) {
        Random random = new Random();
        int index = random.nextInt(num2 - num1 + 1) + num1;
        return EnumSpecies.getFromDex(index);
    }

    public static EnumSpecies getRandUltra() {
        Random random = new Random();
        return UltraALL[random.nextInt(UltraALL.length)];
    }
    public static EnumSpecies getRandmonPokeNoLegendary(String commandType) {
        Result result = getResult(commandType);
        int attempts = 0;
        int maxAttempts = 100;
        EnumSpecies enumSpecies;
        do {
            enumSpecies = EnumSpecies.randomPoke();
            if(enumSpecies.isLegendary()){
                continue;
            }
            boolean isBlacklisted = isBlacklisted(enumSpecies, result);
            if (!isBlacklisted) return enumSpecies;
            attempts++;
        } while (attempts < maxAttempts);

        throw new IllegalStateException("Unable to generate a valid Pokémon after " + maxAttempts + " attempts.");
    }

    public static EnumSpecies getRandmonPokeNoUltra(String commandType) {
        Result result = getResult(commandType);
        int attempts = 0;
        int maxAttempts = 100;
        EnumSpecies enumSpecies;
        do {
            enumSpecies = EnumSpecies.randomPoke();

            // 重置 Flag
            boolean Flag = false;

            // 检查是否属于 UltraALL
            for (EnumSpecies species : UltraALL) {
                if (species.getUnlocalizedName().equals(enumSpecies.getUnlocalizedName())) {
                    Flag = true;
                    break;
                }
            }
            if (Flag) {
                continue;
            }

            // 检查是否在黑名单中
            boolean isBlacklisted = isBlacklisted(enumSpecies, result);
            if (!isBlacklisted) {
                return enumSpecies;
            }

            attempts++;
        } while (attempts < maxAttempts);

        throw new IllegalStateException("Unable to generate a valid Pokémon after " + maxAttempts + " attempts.");
    }


    public static EnumSpecies getRandmonPokeByRule(String commandType) {
        Result result = getResult(commandType);
        int attempts = 0;
        int maxAttempts = 100;
        EnumSpecies enumSpecies;
        do {
            enumSpecies = EnumSpecies.randomPoke();
            boolean isBlacklisted = isBlacklisted(enumSpecies, result);
            if (!isBlacklisted) return enumSpecies;
            attempts++;
        } while (attempts < maxAttempts);

        throw new IllegalStateException("Unable to generate a valid Pokémon after " + maxAttempts + " attempts.");
    }

    public static EnumSpecies getRandomLegendaryByRule(String commandType) {
        Result result = getResult(commandType);
        int attempts = 0;
        int maxAttempts = 100;
        EnumSpecies enumSpecies;
        do {
            enumSpecies = EnumSpecies.randomLegendary();
            boolean isBlacklisted = isBlacklisted(enumSpecies, result);
            if (!isBlacklisted) return enumSpecies;
            attempts++;
        } while (attempts < maxAttempts);

        throw new IllegalStateException("Unable to generate a valid Legendary Pokémon after " + maxAttempts + " attempts.");
    }

    public static EnumSpecies getRandmonPokeNoLegendaryAndUltra(String commandType) {
        Result result = getResult(commandType);
        int attempts = 0;
        int maxAttempts = 100;
        EnumSpecies enumSpecies;
        boolean Flag;

        do {
            // 每次循环都重置 Flag
            Flag = false;

            // 随机获取一个宝可梦
            enumSpecies = EnumSpecies.randomPoke();

            // 检查是否属于 UltraALL
            for (EnumSpecies species : UltraALL) {
                if (species.getUnlocalizedName().equals(enumSpecies.getUnlocalizedName())) {
                    Flag = true;
                    break;
                }
            }

            // 检查是否是传说宝可梦
            if (enumSpecies.isLegendary()) {
                Flag = true;
            }

            // 如果是 UltraALL 或传说宝可梦，则跳过
            if (Flag) {
                continue;
            }

            // 检查是否在黑名单中
            boolean isBlacklisted = isBlacklisted(enumSpecies, result);
            if (!isBlacklisted) {
                return enumSpecies;
            }

            attempts++;
        } while (attempts < maxAttempts);

        throw new IllegalStateException("Unable to generate a valid Pokémon after " + maxAttempts + " attempts.");
    }

    public static final EnumSpecies[] UltraALL = new EnumSpecies[]{
            EnumSpecies.Buzzwole, EnumSpecies.Pheromosa, EnumSpecies.Xurkitree,
            EnumSpecies.Celesteela, EnumSpecies.Guzzlord, EnumSpecies.Kartana,
            EnumSpecies.Blacephalon, EnumSpecies.Poipole, EnumSpecies.Naganadel,
            EnumSpecies.Stakataka
    };
    private static Result getResult(String commandType) {
        String pokemonListFileName = Main.plugin.getConfig().getString(commandType + ".blacklist");
        File file = new File(Main.plugin.getDataFolder(), "blacklist/" + pokemonListFileName + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        List<String> blacklist = yamlConfiguration.getStringList("pokemon");
        List<String> pokemonContainNameList = yamlConfiguration.getStringList("pokemonContainName");
        return new Result(blacklist, pokemonContainNameList);
    }

    private static boolean isBlacklisted(EnumSpecies enumSpecies, Result result) {
        String localizedName = enumSpecies.getLocalizedName();
        String unlocalizedName = enumSpecies.getUnlocalizedName();
        return result.pokemonContainNameList.stream().anyMatch(name -> localizedName.contains(name) || unlocalizedName.contains(name)) ||
                result.blacklist.stream().anyMatch(name -> name.equals(localizedName) || name.equals(unlocalizedName));
    }

    private static class Result {
        public final List<String> blacklist;
        public final List<String> pokemonContainNameList;

        public Result(List<String> blacklist, List<String> pokemonContainNameList) {
            this.blacklist = blacklist;
            this.pokemonContainNameList = pokemonContainNameList;
        }
    }


}
