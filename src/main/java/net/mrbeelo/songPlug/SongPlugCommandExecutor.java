package net.mrbeelo.songPlug;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.mrbeelo.songPlug.SongPlugClass.resetClassStats;
import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugCommandExecutor implements CommandExecutor, TabCompleter {
    public static String[] commands = {"class", "givesong", "getblock", "resetcooldowns", "energy", "infusesong", "clearsongs",
            "level", "sowxp", "skull", "givecrate", "shop", "points", "dataint", "resetblockcharges", "printscore",
            "giveweapon", "givenplush", "datastr"};

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        String name = command.getName();
        switch (name) {
            case "class" -> {
                if(strings.length != 0) {
                    String selector = strings[0];
                    for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                        if (entity instanceof Player player) openClassMenu(player);
                    }
                } else {
                    if(sender instanceof Player player) openClassMenu(player);
                }
            }
            case "givesong" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) giveSong(player, strings[1]);
                }
            }
            case "getblock" -> {
                if(strings.length != 0) {
                    String selector = strings[0];
                    for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                        Material standingBlock = entity.getLocation().add(0, -0.1, 0).getBlock().getType();
                        if (entity instanceof Player player) player.sendMessage("Standing On Block: " + standingBlock.name());
                    }
                } else {
                    if(sender instanceof Player player) {
                        Material standingBlock = player.getLocation().add(0, -0.1, 0).getBlock().getType();
                        player.sendMessage("Standing On Block: " + standingBlock.name());
                    }
                }

            }
            case "resetcooldowns" -> {
                String[] scores = {"RedEnergyCooldown", "BlueEnergyCooldown", "YellowEnergyCooldown", "GreenEnergyCooldown",
                        "DisarmCooldown", "EnragedCooldown", "StealthCooldown", "FelineFuryCooldown", "BowMasteryCooldown"};

                if(strings.length != 0) {
                    String selector = strings[0];
                    for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                        if (entity instanceof Player player) {
                            for(String scoreStr : scores) {
                                Score score = scoreType(player, scoreStr);
                                score.setScore(0);
                            }
                        }
                    }
                } else {
                    if(sender instanceof Player player) {
                        for(String scoreStr : scores) {
                            Score score = scoreType(player, scoreStr);
                            score.setScore(0);
                        }
                    }
                }
            }
            case "energy" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) {
                        Score songEnergyScore = scoreType(player, "SongEnergy");
                        int amount = Integer.parseInt(strings[1]);
                        if (amount >= 0 && amount <= 5) {
                            songEnergyScore.setScore(amount);
                        } else {
                            player.sendMessage("Amount out of bounds!");
                        }
                    }
                }
            }
            case "infusesong" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) {
                        if (strings.length > 2) {
                            infuseSong(player, strings[1], false, strings[2].equals("force"));
                        } else {
                            infuseSong(player, strings[1], false, false);
                        }
                    }
                }
            }
            case "clearsongs" -> {
                if(strings.length != 0) {
                    String selector = strings[0];
                    for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                        if (entity instanceof Player player) clearSongs(player, false);
                    }
                } else {
                    if (sender instanceof Player player) clearSongs(player, false);
                }
            }
            case "level" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) {
                        Score levelScore = scoreType(player, "Level");
                        levelScore.setScore(Integer.parseInt(strings[1]));
                        if (strings.length > 2) {
                            if (strings[2].equals("clearxp")) {
                                Score xpScore = scoreType(player, "XP");
                                xpScore.setScore(0);
                            }
                        }
                    }
                }
            }
            case "sowxp" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) {
                        Score xpScore = scoreType(player, "XP");
                        xpScore.setScore(Integer.parseInt(strings[1]));
                    }
                }
            }
            case "skull" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) {
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
            }
            case "givecrate" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) giveCrate(player, strings[1]);
                }
            }
            case "shop" -> {
                if(strings.length != 0) {
                    String selector = strings[0];
                    for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                        if (entity instanceof Player player) openShopMenu(player);
                    }
                } else {
                    if (sender instanceof Player player) openShopMenu(player);
                }
            }
            case "points" -> {
                String selector = strings[0];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if(entity instanceof Player player) {
                        String operator = strings[1];
                        int value = Integer.parseInt(strings[2]);

                        Score pointScore = scoreType(player, "Points");

                        switch(operator) {
                            case "add" -> {
                                pointScore.setScore(pointScore.getScore() + value);
                            }
                            case "remove" -> {
                                int value2 = pointScore.getScore() - value;
                                if(value2 < 0) value2 = 0;
                                pointScore.setScore(value2);
                            }
                            case "set" -> {
                                pointScore.setScore(value);
                            }
                        }
                    }
                }
            }
            case "dataint" -> {
                String selector = strings[0];
                String key = strings[1];
                int value = Integer.parseInt(strings[2]);
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if(entity instanceof Player player) {
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        setCustomItemDataInt(stack, key, value);
                    }
                }
            }
            case "resetblockcharges" -> {
                if(strings.length != 0) {
                    String selector = strings[0];
                    for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                        if (entity instanceof Player player) {
                            Score blockChargesScore = scoreType(player, "BlockCharges");
                            blockChargesScore.setScore(5);
                        }
                    }
                } else {
                    if (sender instanceof Player player) {
                        Score blockChargesScore = scoreType(player, "BlockCharges");
                        blockChargesScore.setScore(5);
                    }
                }
            }
            case "printscore" -> {
                String score = strings[1];
                for (Entity entity : Bukkit.selectEntities(sender, strings[0])) {
                    entity.sendMessage("Score " + score + ": " + scoreValue(entity, score));
                }
            }
            case "giveweapon" -> {
                for (Entity entity : Bukkit.selectEntities(sender, strings[0])) {
                    if(entity instanceof Player player) player.give(weaponStack(strings[1]));
                }
            }
            case "givenplush" -> {
                ItemStack stack = new ItemStack(Material.NETHERITE_INGOT);
                ItemMeta meta = stack.getItemMeta();
                meta.setItemModel(new NamespacedKey("minecraft", "nplushie"));
                meta.itemName(Component.text("N Plushie"));
                stack.setItemMeta(meta);
                setCustomItemDataInt(stack, "nplush", 1);

                if(strings.length != 0) {
                    String selector = strings[0];
                    for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                        if (entity instanceof Player player) player.give(stack);
                    }
                } else {
                    if (sender instanceof Player player) player.give(stack);
                }
            }
            case "datastr" -> {
                String selector = strings[0];
                String key = strings[1];
                String value = strings[2];
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if(entity instanceof Player player) {
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        setCustomItemDataString(stack, key, value);
                    }
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
        } else if(name.equals("givecrate") & args.length == 2) {
            return List.of(rarities);
        } else if(name.equals("points") & args.length == 2) {
            return List.of("add", "remove", "set");
        } else if(name.equals("giveweapon") & args.length == 2) {
            return List.of(weapons);
        }
        return List.of();
    }
}
