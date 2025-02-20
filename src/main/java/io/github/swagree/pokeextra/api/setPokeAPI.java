package io.github.swagree.pokeextra.api;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class setPokeAPI {
    public static void toSetPokemonAttribute(CommandSender sender, String[] args, Pokemon pokemon, Integer argsSlot) {
        for (int i = argsSlot; i < args.length; i++) {
            //闪光
            if (args[i].equalsIgnoreCase("shiny")) {
                setPokeShiny(pokemon);
            }

            //非闪光
            if (args[i].equalsIgnoreCase("unshiny")) {
                setPokeUnShiny(pokemon);
            }
            if (args[i].equalsIgnoreCase("unbind")) {
                removePokemonBind(pokemon);
            }
            //等级
            if (args[i].startsWith("level:")) {
                setPokeLevel(pokemon, args[i]);
            }
            if (args[i].equalsIgnoreCase("egg")) {
                pokemon.makeEgg();
            }
            //球种
            if (args[i].startsWith("pokeball:")) {
                setPokePokeBall(pokemon, args[i]);
            }
            if (args[i].startsWith("growth:")) {
                setPokeGrowth(pokemon, args[i]);
            }
            //性别
            if (args[i].startsWith("gender:")) {
                setPokeGender(pokemon, args[i]);
            }
            //标签类型
            if (args[i].equalsIgnoreCase("bind")) {
                setPokeBind(pokemon);
            }
            if (args[i].equalsIgnoreCase("unbreed")) {
                removePokeBind(pokemon);
            }
            if (args[i].startsWith("flag:")) {
                setPokeCustomizeFlag(pokemon, args[i]);
            }
            if (args[i].startsWith("removeflag:")) {
                removePokeCustomizeFlag(pokemon, args[i]);
            }
            if (args[i].startsWith("friendship:")) {
                setPokeFriendShop(pokemon, args[i]);
            }
            //性格
            if (args[i].startsWith("nature")) {
                setPokeNature(pokemon, args[i]);
            }
            //形态
            if (args[i].startsWith("form:")) {
                setPokeForm(pokemon, args[i]);
            }
            if (args[i].startsWith("helditem:")) {
                setPokeHeldItem(pokemon, args[i]);
            }
            if (args[i].startsWith("ability:")) {
                setPokeAbility(pokemon, args[i]);
            }
            if (args[i].equalsIgnoreCase("mt")) {
                setPokeMT(pokemon);
            }
            if (args[i].startsWith("move")) {
                setPokeMove(sender, pokemon, args[i]);
            }
            if (args[i].startsWith("ivs")) {
                setPokeIvs(args, i, pokemon);
            }

            if (args[i].startsWith("evs")) {
                setPokeEvs(args, i, pokemon);
            }

            if (args[i].startsWith("randomv")) {
                toSetPokeRandomIvs(sender, pokemon, args[i]);
            }

            if (args[i].equalsIgnoreCase("doeslevel")) {
                setPokeDoesLevel(pokemon);
            }

            if (args[i].startsWith("poketexture:")) {
                setPokeTexture(pokemon, args[i]);
            }
            if (args[i].equalsIgnoreCase("rdgrowth")) {
                EnumGrowth randomGrowth = EnumGrowth.getRandomGrowth();
                pokemon.setGrowth(randomGrowth);
            }
            if (args[i].equalsIgnoreCase("rdnature")) {
                EnumNature randomNature = EnumNature.getRandomNature();
                pokemon.setNature(randomNature);
            }
            if (args[i].equalsIgnoreCase("clearevs")) {
                setPokeClearEvs(pokemon);
            }
        }
    }

    private static void removePokemonBind(Pokemon pokemon) {
        pokemon.removeSpecFlag("untradeable");
    }

    private static void toSetPokeRandomIvs(CommandSender sender, Pokemon pokemon, String args) {
        String[] split = args.split(":");
        Integer randomNum = Integer.valueOf(split[1]);
        if (randomNum > 6 || randomNum < 0) {
            sender.sendMessage("§c对不起！您想要的v数过大或过小了");
        }
        ArrayList<Integer> nums = new ArrayList<>();
        for (int z = 0; z < 6; z++) {
            nums.add(z);
        }
        ArrayList<Integer> copy = nums;
        ArrayList<Integer> realNums = new ArrayList<>();
        for (int rn = 0; rn < randomNum; rn++) {
            Random random1 = new Random();
            int i1 = nums.size();
            int i2 = random1.nextInt(i1);
            realNums.add(nums.get(i2));
            nums.remove(nums.get(i2));
        }
        List<StatsType> statsTypes = new ArrayList<>();
        statsTypes.add(StatsType.HP);
        statsTypes.add(StatsType.Speed);
        statsTypes.add(StatsType.Defence);
        statsTypes.add(StatsType.SpecialDefence);
        statsTypes.add(StatsType.Attack);
        statsTypes.add(StatsType.SpecialAttack);


        for (Integer num : realNums) {
            pokemon.getIVs().setStat(statsTypes.get(num), 31);
        }
        for (Integer realNum : realNums) {
            copy.remove(realNum);
        }
        for (Integer cop : copy) {
            pokemon.getIVs().setStat(statsTypes.get(cop), new Random().nextInt(31));
        }
    }

    private static void setPokeDoesLevel(Pokemon pokemon) {
        pokemon.setDoesLevel(false);
    }

    private static void setPokeClearEvs(Pokemon pokemon) {
        pokemon.setDoesLevel(false);
        pokemon.getEVs().setStat(StatsType.Speed, 0);
        pokemon.getEVs().setStat(StatsType.Attack, 0);
        pokemon.getEVs().setStat(StatsType.SpecialDefence, 0);
        pokemon.getEVs().setStat(StatsType.SpecialAttack, 0);
        pokemon.getEVs().setStat(StatsType.Defence, 0);
        pokemon.getEVs().setStat(StatsType.HP, 0);
    }

    private static void setPokeTexture(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String textureName = String.valueOf(split[1]);
        pokemon.setCustomTexture(textureName);
    }

    private static void setPokeForm(Pokemon pokemon, String args) {
        // 假设 args 是字符串数组，pokemon 是目标 Pokemon 对象
        String[] inputs = args.split(":");
        String input = inputs[1];
        try {
            // 尝试将输入解析为整数
            int formIndex = Integer.parseInt(input);

            List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);

            if (formIndex >= 0 && formIndex < possibleForms.size()) {
                pokemon.setForm(possibleForms.get(formIndex));
            }
        } catch (NumberFormatException e) {
            // 如果输入不是整数，尝试作为表单名称处理
            IEnumForm formEnum = pokemon.getSpecies().getFormEnum(input);
            if (formEnum != null) {
                pokemon.setForm(formEnum);
            } else {
                System.out.println("无法找到对应的形态：" + input);
            }
        }

    }

    private static void setPokeNature(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String s = String.valueOf(split[1]);
        EnumNature enumNature = enumNatureValueOfIgnoreCase(s);
        pokemon.setNature(enumNature);
    }

    private static void setPokeFriendShop(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        Integer num = Integer.valueOf(split[1]);
        pokemon.setFriendship(num);
    }

    private static void setPokeCustomizeFlag(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String s = String.valueOf(split[1]);
        pokemon.addSpecFlag(s);
    }

    private static void removePokeCustomizeFlag(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String s = String.valueOf(split[1]);
        pokemon.removeSpecFlag(s);
    }

    private static void removePokeBind(Pokemon pokemon) {
        pokemon.addSpecFlag("unbreedable");
    }

    private static void setPokeBind(Pokemon pokemon) {
        pokemon.addSpecFlag("untradeable");
    }

    private static void setPokeGender(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String sex = String.valueOf(split[1]);
        if (sex.equalsIgnoreCase("female")) {
            pokemon.setGender(Gender.Female);
        }
        if (sex.equalsIgnoreCase("male")) {
            pokemon.setGender(Gender.Male);
        }
        if (sex.equalsIgnoreCase("none")) {
            pokemon.setGender(Gender.None);
        }
    }

    private static void setPokeGrowth(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String growthName = String.valueOf(split[1]);
        EnumGrowth enumGrowth = enumGrowthValueOfIgnoreCase(growthName);
        pokemon.setGrowth(enumGrowth);
    }

    private static void setPokePokeBall(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String pokeBallName = String.valueOf(split[1]);
        EnumPokeballs enumPokeballs = enumPokeBallsValueOfIgnoreCase(pokeBallName);
        pokemon.setCaughtBall(enumPokeballs);
    }

    private static void setPokeLevel(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String levelRanges = split[1];

        // 解析多个范围并随机选择一个等级
        String[] ranges = levelRanges.split(",");
        Random random = new Random();
        int selectedLevel = -1;

        // 假设每个范围的权重，可以根据实际情况调整
        int[] weights = new int[ranges.length]; // 权重数组
        for (int i = 0; i < ranges.length; i++) {
            weights[i] = 1; // 默认权重为1
        }

        // 根据权重选择一个范围
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }
        int randomWeight = random.nextInt(totalWeight);
        int sum = 0;
        for (int i = 0; i < ranges.length; i++) {
            sum += weights[i];
            if (randomWeight < sum) {
                String range = ranges[i];
                if (range.contains("-")) {
                    // 处理范围，例如 1-50
                    String[] bounds = range.split("-");
                    int lowerBound = Integer.parseInt(bounds[0].trim());
                    int upperBound = Integer.parseInt(bounds[1].trim());

                    // 确保范围有效
                    if (lowerBound <= upperBound) {
                        int randomLevel = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
                        selectedLevel = randomLevel;
                    }
                } else {
                    // 处理单个数值，例如 56
                    int singleLevel = Integer.parseInt(range.trim());
                    selectedLevel = singleLevel;
                }
                break; // 一旦选择了一个范围，就退出循环
            }
        }

        if (selectedLevel > 0) {
            pokemon.setLevel(selectedLevel);
        }
    }

    private static void setPokeShiny(Pokemon pokemon) {
        pokemon.setShiny(true);
    }

    private static void setPokeUnShiny(Pokemon pokemon) {
        pokemon.setShiny(false);
    }

    private static void setPokeHeldItem(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        String helditem = String.valueOf(split[1]);
        ItemStack itemStack = new ItemStack(Material.getMaterial(helditem.toUpperCase()));
        net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.item.ItemStack nmsCopy1 = (net.minecraft.item.ItemStack) (Object) nmsCopy;
        pokemon.setHeldItem(nmsCopy1);
    }

    private static void setPokeAbility(Pokemon pokemon, String args) {
        String[] split = args.split(":");
        List<AbilityBase> allAbilities = pokemon.getBaseStats().getAllAbilities();
        pokemon.setAbility(allAbilities.get(Integer.valueOf(split[1])));
    }

    private static void setPokeMT(Pokemon pokemon) {
        if (pokemon.getAbilitySlot() != 2) {
            pokemon.setAbilitySlot(2);
        }
    }

    private static void setPokeMove(CommandSender sender, Pokemon pokemon, String args) {
        String[] split = args.split(":");
        char[] chars = split[0].toCharArray();
        char aChar = chars[chars.length - 1];
        String s = String.valueOf(aChar);
        Attack attack = new Attack(split[1]);
        if (!Attack.hasAttack(attack.toString())) {
            sender.sendMessage("§4对不起没有这个技能！");
        }
        pokemon.getMoveset().set(Integer.valueOf(s) - 1, attack);
    }

    private static void setPokeEvs(String[] args, int i, Pokemon pokemon) {
        Map<String, StatsType> evsMapping = new HashMap<>();
        evsMapping.put("evshp", StatsType.HP);
        evsMapping.put("evsspeed", StatsType.Speed);
        evsMapping.put("evsdefence", StatsType.Defence);
        evsMapping.put("evsspecialdefence", StatsType.SpecialDefence);
        evsMapping.put("evsattack", StatsType.Attack);
        evsMapping.put("evsspecialattack", StatsType.SpecialAttack);

        // 遍历映射，匹配前缀并设置对应的 EV
        for (Map.Entry<String, StatsType> entry : evsMapping.entrySet()) {
            if (args[i].startsWith(entry.getKey())) {
                String[] split = args[i].split(":");
                try {
                    int num = Integer.parseInt(split[1]); // 转换为整数
                    pokemon.getEVs().setStat(entry.getValue(), num);
                } catch (NumberFormatException e) {
                    // 处理非法数字格式
                    System.out.println("Invalid EV value: " + split[1]);
                }
                break; // 匹配到后直接退出
            }
        }
    }

    private static void setPokeIvs(String[] args, int i, Pokemon pokemon) {
        Map<String, StatsType> ivsMapping = new HashMap<>();
        ivsMapping.put("ivshp", StatsType.HP);
        ivsMapping.put("ivsspeed", StatsType.Speed);
        ivsMapping.put("ivsdefence", StatsType.Defence);
        ivsMapping.put("ivsspecialdefence", StatsType.SpecialDefence);
        ivsMapping.put("ivsattack", StatsType.Attack);
        ivsMapping.put("ivsspecialattack", StatsType.SpecialAttack);

        // 特殊处理 "ivsall"
        if (args[i].startsWith("ivsall")) {
            String[] split = args[i].split(":");
            try {
                int num = Integer.parseInt(split[1]); // 转换为整数
                for (StatsType stat : StatsType.values()) {
                    pokemon.getIVs().setStat(stat, num);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid IV value: " + split[1]);
            }
        } else {
            // 遍历映射，匹配前缀并设置对应的 IV
            for (Map.Entry<String, StatsType> entry : ivsMapping.entrySet()) {
                if (args[i].startsWith(entry.getKey())) {
                    String[] split = args[i].split(":");
                    try {
                        int num = Integer.parseInt(split[1]); // 转换为整数
                        pokemon.getIVs().setStat(entry.getValue(), num);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid IV value: " + split[1]);
                    }
                    break; // 匹配到后直接退出
                }
            }
        }
    }

    public static EnumGrowth enumGrowthValueOfIgnoreCase(String name) {
        for (EnumGrowth enumValue : EnumGrowth.values()) {
            if (enumValue.name().equalsIgnoreCase(name)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum constant " + EnumGrowth.class.getSimpleName() + "." + name);
    }

    public static EnumNature enumNatureValueOfIgnoreCase(String name) {
        for (EnumNature enumValue : EnumNature.values()) {
            if (enumValue.name().equalsIgnoreCase(name)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum constant " + EnumGrowth.class.getSimpleName() + "." + name);
    }

    public static EnumPokeballs enumPokeBallsValueOfIgnoreCase(String name) {
        for (EnumPokeballs enumValue : EnumPokeballs.values()) {
            if (enumValue.name().equalsIgnoreCase(name)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum constant " + EnumGrowth.class.getSimpleName() + "." + name);
    }

}
