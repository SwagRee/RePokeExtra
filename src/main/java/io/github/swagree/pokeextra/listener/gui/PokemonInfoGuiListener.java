package io.github.swagree.pokeextra.listener.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokeextra.Main;
import io.github.swagree.pokeextra.gui.PokemonInfoGui;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PokemonInfoGuiListener implements Listener {
    private static final List<Integer> invSlot = Arrays.asList(11, 13, 15, 29, 31, 33);

    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
        if (e.getInventory().getTitle().equalsIgnoreCase("精灵信息面板")) {
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
            if (e.getSlot() == 44) {
                showAllPokemon(player);
            }
            for (int i = 0; i < 6; i++) {
                hashMap.put(invSlot.get(i), i);
            }
            for (Integer s : hashMap.keySet()) {
                if (s == e.getSlot()) {
                    showPokemon(player, hashMap, s);
                }
            }
        }
    }

    private void showPokemon(Player player, HashMap<Integer, Integer> hashMap, Integer s) {
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());
        ItemStack photo = PokemonInfoGui.getPhoto(hashMap.get(s), party);
        List<String> lore = PokemonInfoGui.getLore(hashMap.get(s), player, party);
        ItemMeta itemMeta = photo.getItemMeta();
        itemMeta.setLore(lore);
        itemMeta.setDisplayName("§b" + party.get(hashMap.get(s)).getLocalizedName());
        photo.setItemMeta(itemMeta);
        if (photo != null && photo.getType() != Material.AIR) {
            net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(photo);
            NBTTagCompound tag = new NBTTagCompound();
            nmsItemStack.save(tag);

            String json = tag.toString();
            String message = Main.plugin.getConfig().getString("ShowPokemonMessage.playerMessage").replace("%player%", player.getName()).replace("&", "§");

            String pokemonInfo = Main.plugin.getConfig().getString("ShowPokemonMessage.pokemonMessage")
                    .replace("%pokemon%", party.get(hashMap.get(s)).getLocalizedName()).replace("&", "§");
            TextComponent text = new TextComponent(message + pokemonInfo);
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(json)}));

            player.spigot().sendMessage(text);
            player.closeInventory();

        }
    }

    public static void showAllPokemon(Player player) {
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        String[] ss = new String[6];
        for (int i = 0; i < 6; i++) {
            if (party.get(i) != null) {
                ItemStack photo = PokemonInfoGui.getPhoto(i, party);

                if (photo != null && photo.getType() != Material.AIR) {
                    List<String> lore = PokemonInfoGui.getLore(i, player, party);
                    ItemMeta itemMeta = photo.getItemMeta();
                    itemMeta.setLore(lore);
                    itemMeta.setDisplayName("§b" + party.get(i).getLocalizedName());
                    photo.setItemMeta(itemMeta);
                    net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(photo);
                    NBTTagCompound tag = new NBTTagCompound();
                    nmsItemStack.save(tag);

                    ss[i] = tag.toString();

                }
            } else {
                ss[i] = "";
            }
        }
        String[] sprites = new String[6];
        String spritesText = "";

        for (int i = 0; i < 6; i++) {
            if (party.get(i) != null) {
                sprites[i] = Main.plugin.getConfig().getString("ShowAllPokemonMessage.pokemonMessage").replace("%pokemon%", party.get(i).getLocalizedName()).replace("&", "§");
                spritesText = spritesText + ss[i];
            } else {
                sprites[i] = "";
            }

        }
        String message = Main.plugin.getConfig()
                .getString("ShowAllPokemonMessage.playerMessage")
                .replace("%player%", player.getName())
                .replace("&", "§");

        TextComponent text0 = new TextComponent(message);

        // 将所有 TextComponent 存入数组
        TextComponent[] textComponents = new TextComponent[7];
        textComponents[0] = text0;
        for (int i = 0; i < sprites.length; i++) {
            textComponents[i + 1] = new TextComponent(sprites[i]);
            textComponents[i + 1].setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_ITEM,
                    new BaseComponent[]{new TextComponent(ss[i])} // 假设 ss 和 sprites 长度一致
            ));
        }

        // 向所有在线玩家发送消息
        for (Player p : player.getServer().getOnlinePlayers()) {
            p.spigot().sendMessage(textComponents);
        }

        player.closeInventory();
    }


}
