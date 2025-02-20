package io.github.swagree.pokeextra.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokeextra.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PokemonInfoGui {

    private static final List<Integer> invSlot = Arrays.asList(11, 13, 15, 29, 31, 33);

    public static void Gui(Player player) {
        String title = "精灵信息面板";

        Inventory inv = Bukkit.createInventory(null, 45, title);
        UUID uuid = player.getUniqueId();

        for (int i = 0; i < 6; i++) {
            int item = invSlot.get(i);
            SpriteInGui(uuid, inv, i, item, player);
        }
        ItemStack goldBlock = new ItemStack(Material.GOLD_BLOCK, 1);
        ItemMeta itemMeta = goldBlock.getItemMeta();
        itemMeta.setDisplayName("§e展示全队宝可梦");
        goldBlock.setItemMeta(itemMeta);
        inv.setItem(44,goldBlock);

        player.openInventory(inv);
    }

    private static void SpriteInGui( UUID uuid,Inventory inv, int playerSlot, int invSlot, Player player) {
        PlayerPartyStorage pokemon = Pixelmon.storageManager.getParty(uuid);

        if (pokemon.get(playerSlot) == null) {
            ItemStack kong = new ItemStack(Material.BARRIER, 1);
            ItemMeta itemMeta = kong.getItemMeta();
            itemMeta.setDisplayName("§c无精灵");
            kong.setItemMeta(itemMeta);
            inv.setItem(invSlot, kong);
        } else {

            ItemStack poke = getPhoto(playerSlot, pokemon);

            ItemMeta pmeta = poke.getItemMeta();
            pmeta.setDisplayName("§a" + pokemon.get(playerSlot).getLocalizedName());
            if (pokemon.get(playerSlot).isEgg()) {
                pmeta.setDisplayName("§a" + pokemon.get(playerSlot).getLocalizedName() + "的蛋");
                boolean eggDetailFlag = Main.plugin.getConfig().getBoolean("eggDetail");
                if (!eggDetailFlag) {
                    int eggSteps = pokemon.get(playerSlot).getEggSteps();
                    int allSteps = (pokemon.get(playerSlot).getBaseStats().getEggCycles() - 1) * 256;
                    List<String> lores = new ArrayList<>();
                    lores.add("§b当前步数:§f" + eggSteps + "   §b需要的总步数:§f" + allSteps);
                    pmeta.setLore(lores);
                    poke.setItemMeta(pmeta); // 更新物品的元数据
                    inv.setItem(invSlot, poke); // 更新物品栏里的相应物品

                    return;
                }
            }

            List<String> list = getLore(playerSlot, player, pokemon);

            pmeta.setLore(list); // 设置 Lore
            poke.setItemMeta(pmeta); // 更新物品的元数据
            inv.setItem(invSlot, poke); // 更新物品栏里的相应物品
        }

    }

    public static List<String> getLore(int playerSlot, Player player, PlayerPartyStorage pokemon) {
        int level = pokemon.get(playerSlot).getLevel();
        int HP = pokemon.get(playerSlot).getIVs().getStat(StatsType.HP);
        int Speed = pokemon.get(playerSlot).getIVs().getStat(StatsType.Speed);
        int Attack = pokemon.get(playerSlot).getIVs().getStat(StatsType.Attack);
        int SpecialAttack = pokemon.get(playerSlot).getIVs().getStat(StatsType.SpecialAttack);
        int SpecialDefence = pokemon.get(playerSlot).getIVs().getStat(StatsType.SpecialDefence);
        int Defence = pokemon.get(playerSlot).getIVs().getStat(StatsType.Defence);
        int evsHP = pokemon.get(playerSlot).getEVs().getStat(StatsType.HP);
        int evsSpeed = pokemon.get(playerSlot).getEVs().getStat(StatsType.Speed);
        int evsAttack = pokemon.get(playerSlot).getEVs().getStat(StatsType.Attack);
        int evsSpecialAttack = pokemon.get(playerSlot).getEVs().getStat(StatsType.SpecialAttack);
        int evsSpecialDefense = pokemon.get(playerSlot).getEVs().getStat(StatsType.SpecialDefence);
        int evsDefence = pokemon.get(playerSlot).getEVs().getStat(StatsType.Defence);
        String ability = pokemon.get(playerSlot).getAbility().getLocalizedName();
        String nature = pokemon.get(playerSlot).getNature().getLocalizedName();
        String growth = pokemon.get(playerSlot).getGrowth().getLocalizedName();
        String gender = pokemon.get(playerSlot).getGender().getLocalizedName();

        String isShiny = "否";
        if (pokemon.get(playerSlot).isShiny()) {
            isShiny = "是";
        }

        String isBind = "未绑定";
        if (pokemon.get(playerSlot).hasSpecFlag("untradeable")) {
            isBind = "已绑定";
        }

        String nickname = "无";
        if (pokemon.get(playerSlot).getNickname() != null) {
            nickname = pokemon.get(playerSlot).getNickname();
        }

        List<String> list = new ArrayList<>();
        List<String> lores = Main.plugin.getConfig().getStringList("lore");
        String playerNamePlaceholder = "";

        for (String lore : lores) {
            playerNamePlaceholder = lore.replace("&","§")
                    .replace("%LEVEL%", String.valueOf(level))
                    .replace("%IVS_HP%", String.valueOf(HP))
                    .replace("%IVS_Attack%", String.valueOf(Attack))
                    .replace("%IVS_Speed%", String.valueOf(Speed))
                    .replace("%IVS_Defence%", String.valueOf(Defence))
                    .replace("%IVS_SpecialAttack%", String.valueOf(SpecialAttack))
                    .replace("%IVS_SpecialDefence%", String.valueOf(SpecialDefence))
                    .replace("%IVS_Defence%", String.valueOf(Defence))
                    .replace("%EVS_HP%", String.valueOf(evsHP))
                    .replace("%EVS_Attack%", String.valueOf(evsAttack))
                    .replace("%EVS_Speed%", String.valueOf(evsSpeed))
                    .replace("%EVS_Defence%", String.valueOf(evsDefence))
                    .replace("%EVS_SpecialAttack%", String.valueOf(evsSpecialAttack))
                    .replace("%EVS_SpecialDefence%", String.valueOf(evsSpecialDefense))
                    .replace("%EVS_Defence%", String.valueOf(evsDefence))
                    .replace("%BIND%", isBind)
                    .replace("%Shiny%", isShiny)
                    .replace("%Ability%", ability)
                    .replace("%Nature%", nature)
                    .replace("%Growth%", growth)
                    .replace("%Gender%", gender)
                    .replace("%Nick_Name%", nickname);

            list.add(playerNamePlaceholder);
        }
        return list;
    }

    public static ItemStack getPhoto(int playerSlot, PlayerPartyStorage pokemon) {
        net.minecraft.item.ItemStack nmeitem = ItemPixelmonSprite.getPhoto(pokemon.get(playerSlot));
        ItemStack poke = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) (Object) nmeitem);
        return poke;
    }
}