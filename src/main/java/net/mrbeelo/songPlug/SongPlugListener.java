package net.mrbeelo.songPlug;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;

import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugListener implements Listener {
    @EventHandler
    public void scrolled(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Score score = scoreType(player, "fCycle");
        score.setScore(0);
    }

    @EventHandler
    public void pressedF(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if(!player.getScoreboardTags().contains("ArdoniClass")) return;

        Score score = scoreType(player, "fCycle");
        int playerScore = score.getScore();

        if(playerScore == 0) {
            score.setScore(1);
        } else if(playerScore == 1) {
            score.setScore(0);

            int usingActiveSong = scoreValue(player, "UsingActiveSong");
            if(usingActiveSong > 0) {
                player.sendMessage("What are you doing?!?");
                return;
            }

            openSongMenu(player);
        }
    }

    @EventHandler
    public void selectedItem(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if(view.title().equals(Component.text("Song Selection"))) {
            ItemStack stack = event.getCurrentItem();
            if(stack == null) return;

            int songCycle = switch(stack.getItemMeta().getDisplayName()) {
                case "§cAggressium" -> 1;
                case "§eMobilium" -> 2;
                case "§9Protisium" -> 3;
                case "§aSupporium" -> 4;
                default -> 0;
            };

            if(!(event.getWhoClicked() instanceof Player player)) {
                event.setCancelled(true);
                view.close();
                return;
            }

            if(songCycle == 0) {
                event.setCancelled(true);
                view.close();
                player.sendMessage("Cancelled!");
                return;
            }

            Score energyScore = scoreType(player, "songEnergy");
            Score energyRegenScore = scoreType(player, "songEnergyRegen");

            int songEnergy = energyScore.getScore();

            if(songEnergy <= 0) {
                player.sendMessage("You don't have enough energy!");
                event.setCancelled(true);
                view.close();
                return;
            }

            String songColor = switch(songCycle) {
                case 1 -> "Red";
                case 2 -> "Yellow";
                case 3 -> "Blue";
                case 4 -> "Green";
                default -> "ERROR";
            };

            String songName = switch(songCycle) {
                case 1 -> "Aggressium";
                case 2 -> "Mobilium";
                case 3 -> "Protisium";
                case 4 -> "Supporium";
                default -> "ERROR";
            };

            String songPrefix = switch(songCycle) {
                case 1 -> "Aggro";
                case 2 -> "Mobili";
                case 3 -> "Prote";
                case 4 -> "Suppor";
                default -> "ERROR";
            };

            Score songEnergyCooldownScore = scoreType(player, songColor.toLowerCase() + "EnergyCooldown");
            int songEnergyCooldown = songEnergyCooldownScore.getScore();

            if(!player.getScoreboardTags().contains("Has" + songColor + "Song")) {
                player.sendMessage("You don't have any " + songName + " Songs!");
                event.setCancelled(true);
                view.close();
                return;
            }

            if(songEnergyCooldown > 0) {
                player.sendMessage(songName + " Songs on cooldown!");
                event.setCancelled(true);
                view.close();
                return;
            }

            for(String tag : player.getScoreboardTags()) {
                if(tag.startsWith(songPrefix)) {
                    player.sendMessage("Firing " + songName + " song: " + tag);
                    triggerSong(player, tag);
                    handleSongActivationScoreboards(player, energyScore, energyRegenScore, songEnergyCooldownScore);
                    event.setCancelled(true);
                    view.close();
                    return;
                }
            }
        } else if(view.title().equals(Component.text("Class Selection"))) {
            ItemStack stack = event.getCurrentItem();

            if(stack != null && event.getWhoClicked() instanceof Player player) {
                for(String tag : player.getScoreboardTags()) {
                    if(tag.endsWith("Class")) player.getScoreboardTags().remove(tag);
                }

                String[] classNames = {"Human", "Felina", "Ardoni", "Magnorite", "Necromancer"};

                for(String name : classNames) {
                    if(stack.getItemMeta().getDisplayName().substring(2).equals(name)) {
                        player.getScoreboardTags().add(name + "Class");
                        event.setCancelled(true);
                        view.close();
                    }
                }

                event.setCancelled(true);
                view.close();

                if(stack.getItemMeta().getDisplayName().equals("§eArdoni")) {
                    openRaceMenu(player);
                } else {
                    for(String tag : player.getScoreboardTags()) {
                        if(tag.endsWith("Race")) player.getScoreboardTags().remove(tag);
                    }
                }
            }
        } else if(view.title().equals(Component.text("Race Selection"))) {
            ItemStack stack = event.getCurrentItem();

            if(stack != null && event.getWhoClicked() instanceof Player player) {
                for(String tag : player.getScoreboardTags()) {
                    if(tag.endsWith("Race")) player.getScoreboardTags().remove(tag);
                }

                String[] raceNames = {"Clanless", "Sendaris", "Nestoris", "Mendoris", "Kaltaris", "Voltaris"};

                for(String name : raceNames) {
                    if(stack.getItemMeta().getDisplayName().equals(name)) {
                        player.getScoreboardTags().add(name + "ArdoniRace");
                        event.setCancelled(true);
                        view.close();
                    }
                }

                event.setCancelled(true);
                view.close();

                if(stack.getItemMeta().getDisplayName().equals("Cancel")) openClassMenu(player);
            }
        }
    }

    @EventHandler
    public void pressedLeftClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            Block downBlock = player.getLocation().subtract(0, 1, 0).getBlock();

            if(downBlock.getType() == Material.STRUCTURE_BLOCK && player.isSneaking()) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                ItemMeta meta = itemStack.getItemMeta();

                Score infuseCooldownScore = scoreType(player, "InfuseCooldown");

                if(infuseCooldownScore.getScore() > 0) return;
                infuseCooldownScore.setScore(5);

                if(meta == null && itemStack.isEmpty()) {
                    player.sendMessage("Clearing Songs!");

                    String[] songColors = {"Red", "Blue", "Yellow", "Green"};

                    for(String songColor : songColors) {
                        String[] songs = switch (songColor) {
                            case "Red" -> redSongs;
                            case "Blue" -> blueSongs;
                            case "Yellow" -> yellowSongs;
                            case "Green" -> greenSongs;
                            default -> redSongs;
                        };

                        if(player.getScoreboardTags().contains("Has" + songColor + "Song")) {
                            player.getScoreboardTags().remove("Has" + songColor + "Song");
                            for(String song : songs) {
                                if(player.getScoreboardTags().contains(song)) {
                                    player.getScoreboardTags().remove(song);
                                    giveSong(player, song);
                                }
                            }
                        }
                    }

                    return;
                }

                String name = meta.getItemName();

                String songType = "ERROR";
                String songColor = "ERROR";

                if(songIn(name, greenSongs)) {
                    songType = "Supporium";
                    songColor = "Green";
                } else if(songIn(name, yellowSongs)) {
                    songType = "Mobilium";
                    songColor = "Yellow";
                } else if(songIn(name, blueSongs)) {
                    songType = "Protisium";
                    songColor = "Blue";
                } else if(songIn(name, redSongs)) {
                    songType = "Aggressium";
                    songColor = "Red";
                }

                if(songIn(name, redSongs) || songIn(name, blueSongs) || songIn(name, yellowSongs) || songIn(name, greenSongs)) {
                    if(!player.getScoreboardTags().contains("Has" + songColor + "Song")) {
                        player.getScoreboardTags().add(name);
                        player.getScoreboardTags().add("Has" + songColor + "Song");
                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        player.sendMessage("You have been infused with " + name + " (" + songType + ")!");
                    } else {
                        player.sendMessage("You already have a " + songType + " Song!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerDied(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("ArdoniClass")) {
            String[] songColors = {"Red", "Blue", "Yellow", "Green"};

            for(String songColor : songColors) {
                String[] songs = switch (songColor) {
                    case "Red" -> redSongs;
                    case "Blue" -> blueSongs;
                    case "Yellow" -> yellowSongs;
                    case "Green" -> greenSongs;
                    default -> redSongs;
                };

                if(player.getScoreboardTags().contains("Has" + songColor + "Song")) {
                    player.getScoreboardTags().remove("Has" + songColor + "Song");
                    for(String song : songs) {
                        if(player.getScoreboardTags().contains(song)) {
                            player.getScoreboardTags().remove(song);
                            dropSong(player, song);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        String className = "ERROR";
        String raceName = "ERROR";
        NamedTextColor classColor = NamedTextColor.WHITE;
        NamedTextColor raceColor = NamedTextColor.WHITE;


        for(String tag : player.getScoreboardTags()) {
            if(tag.endsWith("Class")) {
                String nameByItself = tag.substring(0, tag.length() - 5);
                className = nameByItself.toUpperCase();

                classColor = switch(nameByItself.toUpperCase()) {
                    case "HUMAN" -> NamedTextColor.GRAY;
                    case "FELINA" -> NamedTextColor.DARK_GREEN;
                    case "ARDONI", "MAGNORITE" -> NamedTextColor.GOLD;
                    case "NECROMANCER" -> NamedTextColor.DARK_GRAY;
                    default -> throw new IllegalStateException("Unexpected value: " + nameByItself.toUpperCase());
                };
            }

            if(tag.endsWith("ArdoniRace")) {
                String nameByItself2 = tag.substring(0, tag.length() - 10);
                raceName = nameByItself2.toUpperCase();

                raceColor = switch(nameByItself2.toUpperCase()) {
                    case "CLANLESS" -> NamedTextColor.WHITE;
                    case "SENDARIS" -> NamedTextColor.BLUE;
                    case "NESTORIS" -> NamedTextColor.YELLOW;
                    case "MENDORIS" -> NamedTextColor.LIGHT_PURPLE;
                    case "KALTARIS" -> NamedTextColor.GREEN;
                    case "VOLTARIS" -> NamedTextColor.RED;
                    default -> throw new IllegalStateException("Unexpected value: " + nameByItself2.toUpperCase());
                };
            }
        }


        String joinedName;
        NamedTextColor joinedColor;

        if(className.equals("ARDONI")) {
            joinedName = className + " - " + raceName;
            joinedColor = raceColor;
        } else {
            joinedName = className;
            joinedColor = classColor;
        }

        String finalJoinedName = joinedName;
        event.renderer((source, sourceDisplayName, message, viewer) ->
                Component.text("[" + finalJoinedName + "] ", joinedColor)
                        .append(sourceDisplayName.color(NamedTextColor.WHITE))
                        .append(Component.text(": ", NamedTextColor.WHITE))
                        .append(message.color(NamedTextColor.WHITE))
        );
    }
}