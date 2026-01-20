package net.mrbeelo.songPlug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugCommandExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        String name = command.getName();
        if(name.equals("class")) {
            if(commandSender instanceof Player player) {
                openClassMenu(player);
            } else {
                commandSender.sendMessage("I don't think you are a player...");
            }
        } else if(name.equals("givesong")) {
            if(commandSender instanceof Player player) giveSong(player, strings[0]);
        } else if(name.equals("getblock")) {
            if(commandSender instanceof Entity entity) {
                Material standingBlock = entity.getLocation().add(0, -0.1, 0).getBlock().getType();
                if(entity instanceof Player player) player.sendMessage("Standing On Block: " + standingBlock.name());
            }
        } else if(name.equals("resetcooldowns")) {
            if(commandSender instanceof Player player) {
                Score redCooldownScore = scoreType(player, "RedEnergyCooldown");
                Score blueCooldownScore = scoreType(player, "BlueEnergyCooldown");
                Score yellowCooldownScore = scoreType(player, "YellowEnergyCooldown");
                Score greenCooldownScore = scoreType(player, "GreenEnergyCooldown");

                redCooldownScore.setScore(0);
                blueCooldownScore.setScore(0);
                yellowCooldownScore.setScore(0);
                greenCooldownScore.setScore(0);
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        String name = command.getName();
        if(name.equals("givesong") && args.length == 1) {
            List<String> songs = new ArrayList<>();
            songs.addAll(List.of(redSongs));
            songs.addAll(List.of(blueSongs));
            songs.addAll(List.of(yellowSongs));
            songs.addAll(List.of(greenSongs));
            return songs;
        }
        return List.of();
    }
}
