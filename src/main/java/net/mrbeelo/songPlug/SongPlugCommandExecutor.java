package net.mrbeelo.songPlug;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.mrbeelo.songPlug.SongPlug.plugin;
import static net.mrbeelo.songPlug.SongPlugClass.resetClassStats;
import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugCommandExecutor implements CommandExecutor, TabCompleter {
    public static String[] commands = {"class", "givesong", "getblock", "resetcooldowns", "energy", "infusesong", "clearsongs", "level", "sowxp", "skull", "weapontype"};

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        String name = command.getName();
        String selector = strings[0];
        if (name.equals("class")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) openClassMenu(player);
            }
        } else if (name.equals("givesong")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) giveSong(player, strings[1]);
            }
        } else if (name.equals("getblock")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                Material standingBlock = entity.getLocation().add(0, -0.1, 0).getBlock().getType();
                if (entity instanceof Player player) player.sendMessage("Standing On Block: " + standingBlock.name());
            }
        } else if (name.equals("resetcooldowns")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) {
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
        } else if (name.equals("energy")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) {
                    Score songEnergyScore = scoreType(player, "SongEnergy");
                    int amount = Integer.parseInt(strings[1]);
                    if (amount >= 0 && amount <= 5) {
                        songEnergyScore.setScore(amount);
                    } else {
                        player.sendMessage("Amount out of bounds!");
                    }
                }
            }
        } else if (name.equals("infusesong")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) {
                    if (strings.length > 2) {
                        infuseSong(player, strings[1], false, strings[2].equals("force"));
                    } else {
                        infuseSong(player, strings[1], false, false);
                    }
                }
            }
        } else if (name.equals("clearsongs")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) clearSongs(player, false);
            }
        } else if(name.equals("level")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) {
                    Score levelScore = scoreType(player, "Level");
                    levelScore.setScore(Integer.parseInt(strings[1]));
                    if(strings.length > 2) {
                        if(strings[2].equals("clearxp")) {
                            Score xpScore = scoreType(player, "XP");
                            xpScore.setScore(0);
                        }
                    }
                }
            }
        } else if(name.equals("sowxp")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) {
                    Score xpScore = scoreType(player, "XP");
                    xpScore.setScore(Integer.parseInt(strings[1]));
                }
            }
        } else if(name.equals("skull")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) {
                    Score skullScore = scoreType(player, "Skull");
                    int amount = Integer.parseInt(strings[1]);
                    if (amount >= 0 && amount <= 5) {
                        skullScore.setScore(amount);
                        resetClassStats(player);
                    } else {
                        player.sendMessage("Amount out of bounds!");
                    }
                }
            }
        } else if(name.equals("weapontype")) {
            for(Entity entity : Bukkit.selectEntities(sender, selector)) {
                if(entity instanceof Player player) {
                    ItemStack stack = player.getInventory().getItemInMainHand();
                    NamespacedKey key = new NamespacedKey(plugin(), "weapon_type");
                    stack.editPersistentDataContainer(pdc -> {
                        pdc.set(key, PersistentDataType.STRING, strings[1]);
                    });
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        String name = command.getName();
        if(Arrays.asList(commands).contains(name) && args.length == 1) {
            List<String> names = new ArrayList<>();
            names.add("@a");
            names.add("@e");
            names.add("@n");
            names.add("@p");
            names.add("@r");
            names.add("@s");
            for(Player player : Bukkit.getOnlinePlayers()) names.add(player.getName());
            return names;
        }

        if((name.equals("givesong") || name.equals("infusesong")) && args.length == 2) {
            List<String> songs = new ArrayList<>();
            songs.addAll(List.of(redSongs));
            songs.addAll(List.of(blueSongs));
            songs.addAll(List.of(yellowSongs));
            songs.addAll(List.of(greenSongs));
            return songs;
        } else if((name.equals("energy") || name.equals("skull")) && args.length == 2) {
            return List.of("0", "1", "2", "3", "4", "5");
        } else if(name.equals("infusesong") && args.length == 3) {
            return List.of("force");
        } else if(name.equals("level") && args.length == 3) {
            return List.of("clearxp");
        }
        return List.of();
    }
}
