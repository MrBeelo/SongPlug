package net.mrbeelo.songPlug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static net.mrbeelo.songPlug.SongPlugHelper.customNameItemStack;
import static net.mrbeelo.songPlug.SongPlugHelper.giveSong;

public class SongPlugCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        String name = command.getName();
        if(name.equals("class")) {
            if(commandSender instanceof Player player) {
                Inventory menu = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("Class Selection"));
                menu.setItem(11, customNameItemStack(Material.PLAYER_HEAD, Component.text("Human")));
                menu.setItem(12, customNameItemStack(Material.OAK_LOG, Component.text("Felina")));
                menu.setItem(13, customNameItemStack(Material.DARK_PRISMARINE, Component.text("Ardoni")));
                menu.setItem(14, customNameItemStack(Material.MAGMA_BLOCK, Component.text("Magnorite")));
                menu.setItem(15, customNameItemStack(Material.ZOMBIE_HEAD, Component.text("Necromancer")));
                player.openInventory(menu);
            } else {
                commandSender.sendMessage("I don't think you are a player...");
            }
        } else if(name.equals("givesong")) {
            Player target = Bukkit.getPlayer(strings[0]);
            giveSong(target, strings[1]);
        }

        return false;
    }
}
