package io.github.swagree.pokeextra.listener.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import io.github.swagree.pokeextra.Main;
import io.github.swagree.pokeextra.util.YmlUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class PokeGuiPClistener implements Listener {

    public static Map<Integer, Integer> invHaspMap = new HashMap<>();

    public static int[] slot = new int[]{
            0, 1, 2, 3, 4, 5,
            9, 10, 11, 12, 13, 14,
            18, 19, 20, 21, 22, 23,
            27, 28, 29, 30, 31, 32,
            36, 37, 38, 39, 40, 41,
            7, 16, 25,
            8, 17, 26
    };

    private static ItemStack getPreviousPageButton() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "上一页");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static ItemStack getNextPageButton() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "下一页");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {


        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof MyHolder)) {
            return;
        }
        MyHolder myHolder = (MyHolder) holder;
        e.setCancelled(true);
        if (e.getSlot() > e.getInventory().getSize() || e.getSlot() < 0) {
            return;
        }

        if (e.getSlot() == 52) { // 上一页
            myHolder.page--;
            if (myHolder.page < 0) {
                myHolder.page = getMaxPage(e.getWhoClicked().getUniqueId()) - 1;
            }
        } else if (e.getSlot() == 53) { // 下一页
            myHolder.page++;
            if (myHolder.page > getMaxPage(e.getWhoClicked().getUniqueId()) - 1) {
                myHolder.page = 0;
            }
        }

        myHolder.setupInv();
        Inventory inventory = myHolder.getInventory();
        e.getWhoClicked().openInventory(inventory);
    }


    @EventHandler
    public void onInventoryRightClick(InventoryClickEvent e) throws Exception {
        InventoryHolder holder = e.getInventory().getHolder();

        if (!(holder instanceof MyHolder)) {
            return;
        }
        MyHolder myHolder = (MyHolder) holder;
        PCStorage pcStorage = myHolder.getPCStorage();
        Player sender = (Player) e.getWhoClicked();
        if (e.isShiftClick() && (sender.hasPermission("rpe.admin") || sender.hasPermission("rpe.pc.remove"))) {
            Pokemon pokemon = null;
            try {
                int slot1 = e.getSlot();

                if (slot1 == 7 || slot1 == 16 || slot1 == 25 || slot1 == 8 || slot1 == 17 || slot1 == 26) {
                    if (slot1 == 7) {
                        pokemon = Pixelmon.storageManager.getParty(myHolder.uuid).get(0);
                        Pixelmon.storageManager.getParty(myHolder.uuid).set(0,null);

                    }
                    if (slot1 == 16) {
                        pokemon = Pixelmon.storageManager.getParty(myHolder.uuid).get(1);
                        Pixelmon.storageManager.getParty(myHolder.uuid).set(1,null);

                    }
                    if (slot1 == 25) {
                        pokemon = Pixelmon.storageManager.getParty(myHolder.uuid).get(2);
                        Pixelmon.storageManager.getParty(myHolder.uuid).set(2,null);

                    }
                    if (slot1 == 8) {
                        pokemon = Pixelmon.storageManager.getParty(myHolder.uuid).get(3);
                        Pixelmon.storageManager.getParty(myHolder.uuid).set(3,null);

                    }
                    if (slot1 == 17) {
                        pokemon = Pixelmon.storageManager.getParty(myHolder.uuid).get(4);
                        Pixelmon.storageManager.getParty(myHolder.uuid).set(4,null);

                    }
                    if (slot1 == 26) {
                        pokemon = Pixelmon.storageManager.getParty(myHolder.uuid).get(5);
                        Pixelmon.storageManager.getParty(myHolder.uuid).set(5,null);
                    }
                } else {
                    pokemon = pcStorage.get(myHolder.page, invHaspMap.get(e.getSlot()));
                    myHolder.getPCStorage().set(myHolder.page, invHaspMap.get(e.getSlot()), null);
                }


            } catch (Exception exception) {
                return;
            }
            if (pokemon == null) {
                return;
            }
            NBTTagCompound pokemonNBTData = pokemon.writeToNBT(new NBTTagCompound());
            String playerName = Bukkit.getOfflinePlayer(myHolder.uuid).getName();
            List<String> pokemonList = YmlUtil.pcBackupConfig.getStringList(playerName);
            pokemonList.add(pokemonNBTData.toString());

            YmlUtil.pcBackupConfig.set(playerName, pokemonList);
            YmlUtil.pcBackupConfig.save(new File(Main.plugin.getDataFolder(), "PcBackup.yml"));
        }

    }

    public static int getMaxPage(UUID uuid) {
        return Pixelmon.storageManager.getPCForPlayer(uuid).getBoxCount();
    }

    /*自己的Holder 是唯一的*/
    public static class MyHolder implements InventoryHolder {
        public Inventory inv;
        public UUID uuid;
        public int page;

        public MyHolder(int page, UUID uuid) {
            this.uuid = uuid;
            this.page = page;
            setupInv();
        }

        public Inventory getInventory() {
            return inv;
        }

        public PCStorage getPCStorage() {
            return Pixelmon.storageManager.getPCForPlayer(this.uuid);
        }

        public void setupInv() {
            this.inv = Bukkit.createInventory(this, 54, "PC当前页数:" + (page + 1) + "/" + PokeGuiPClistener.getMaxPage(uuid));

            this.inv.setItem(52, getPreviousPageButton());
            this.inv.setItem(53, getNextPageButton());


            for (int i = 0; i < 36; i++) {
                Pokemon pokemon = null;
                if (i < 30) {
                    pokemon = Pixelmon.storageManager.getPCForPlayer(uuid).get(this.page, i);
                } else {
                    pokemon = Pixelmon.storageManager.getParty(uuid).get(i - 30);
                }
                if (pokemon == null) {
                    this.inv.setItem(slot[i], null);
                } else {
                    net.minecraft.item.ItemStack photo = ItemPixelmonSprite.getPhoto(pokemon);
                    ItemStack photoItemStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) (Object) photo);
                    ItemMeta itemMeta = photoItemStack.getItemMeta();
                    List<String> list = new ArrayList<>();
                    List<String> lores = Main.plugin.getConfig().getStringList("lore");
                    int level = pokemon.getLevel();
                    int HP = pokemon.getIVs().getStat(StatsType.HP);
                    int Speed = pokemon.getIVs().getStat(StatsType.Speed);
                    int Attack = pokemon.getIVs().getStat(StatsType.Attack);
                    int SpecialAttack = pokemon.getIVs().getStat(StatsType.SpecialAttack);
                    int SpecialDefence = pokemon.getIVs().getStat(StatsType.SpecialDefence);
                    int Defence = pokemon.getIVs().getStat(StatsType.Defence);
                    int evsHP = pokemon.getEVs().getStat(StatsType.HP);
                    int evsSpeed = pokemon.getEVs().getStat(StatsType.Speed);
                    int evsAttack = pokemon.getEVs().getStat(StatsType.Attack);
                    int evsSpecialAttack = pokemon.getEVs().getStat(StatsType.SpecialAttack);
                    int evsSpecialDefense = pokemon.getEVs().getStat(StatsType.SpecialDefence);
                    int evsDefence = pokemon.getEVs().getStat(StatsType.Defence);
                    String ability = pokemon.getAbility().getLocalizedName();
                    String nature = pokemon.getNature().getLocalizedName();
                    String growth = pokemon.getGrowth().getLocalizedName();
                    String gender = pokemon.getGender().getLocalizedName();

                    String isShiny = "否";
                    if (pokemon.isShiny()) {
                        isShiny = "是";
                    }

                    String isBind = "未绑定";
                    if (pokemon.hasSpecFlag("untradeable")) {
                        isBind = "已绑定";
                    }

                    String nickname = "无";
                    if (pokemon.getNickname() != null) {
                        nickname = pokemon.getNickname();
                    }
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

                    itemMeta.setLore(list);
                    itemMeta.setDisplayName(ChatColor.AQUA + pokemon.getLocalizedName());
                    photoItemStack.setItemMeta(itemMeta);
                    this.inv.setItem(slot[i], photoItemStack);
                }
            }
        }
    }
}
