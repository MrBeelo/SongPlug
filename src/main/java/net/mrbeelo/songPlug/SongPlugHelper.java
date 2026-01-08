package net.mrbeelo.songPlug;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class SongPlugHelper {
    public static void command(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    static String[] greenSongs = {"Supporolift", "Supporoform", "Supporokenisis", "Supporospike"};
    static String[] yellowSongs = {"Mobilileap", "Mobiliflash", "Mobilibounce", "Mobiliburst", "Mobiliglide", "Mobiliwings"};
    static String[] blueSongs = {"Protesphere", "Protebarrier", "Protepoint", "Protearmor", "Proteclone", "Proteheal"};
    static String[] redSongs = {"Aggrobeam", "Aggroblast", "Aggrostorm", "Aggrosphere", "Aggroquake", "Aggroshock",
            "Aggrovortex", "Aggroshard", "Aggrodetonate"};

    public static void increaseSongCycle(Score score) {
        int playerScore = score.getScore();
        if(playerScore >= 4) {
            score.setScore(1);
        } else {
            score.setScore(playerScore + 1);
        }
    }

    public static void decreaseSongCycle(Score score) {
        int playerScore = score.getScore();
        if(playerScore <= 1) {
            score.setScore(4);
        } else {
            score.setScore(playerScore - 1);
        }
    }

    public static Objective getNotNullObjective(Scoreboard scoreboard, String name) {
        Objective objective = scoreboard.getObjective(name);

        if(objective == null) {
            SongPlug.log("Scoreboard " + name + " does not exist! Creating new one!");
            scoreboard.registerNewObjective(name, Criteria.DUMMY, Component.text(name));
            objective = scoreboard.getObjective(name);
        }

        return objective;
    }

    public static void updateBossBar(Player player, int songEnergy) {
        NamespacedKey key = new NamespacedKey("songplug", "song_energy_" + player.getName().toLowerCase() + "_key");
        KeyedBossBar keyedBossBar = Bukkit.getBossBar(key);

        if(keyedBossBar == null) {
            keyedBossBar = Bukkit.createBossBar(key, "Song Energy", BarColor.YELLOW, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
        }

        if(player.getScoreboardTags().contains("ArdoniClass")) {
            keyedBossBar.addPlayer(player);
            keyedBossBar.setProgress((double) songEnergy / 5);
        } else {
            keyedBossBar.removePlayer(player);
        }
    }

    public static boolean songIn(String song, String[] songArray) {
        return Arrays.asList(songArray).contains(song);
    }

    public static void giveCustomItemName(Player player, Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text(name));
        item.setItemMeta(meta);
        player.give(item);
    }

    public static void giveSong(Player player, String name) {
        Material material = Material.BARRIER;

        if(songIn(name, redSongs)) material = Material.RED_STAINED_GLASS;
        if(songIn(name, blueSongs)) material = Material.BLUE_STAINED_GLASS;
        if(songIn(name, yellowSongs)) material = Material.YELLOW_STAINED_GLASS;
        if(songIn(name, greenSongs)) material = Material.LIME_STAINED_GLASS;

        giveCustomItemName(player, material, name);
    }

    public static void dropCustomItemName(Player player, Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text(name));
        item.setItemMeta(meta);
        player.getWorld().dropItem(player.getLocation(), item);
    }

    public static void dropSong(Player player, String name) {
        Material material = Material.BARRIER;

        if(songIn(name, redSongs)) material = Material.RED_STAINED_GLASS;
        if(songIn(name, blueSongs)) material = Material.BLUE_STAINED_GLASS;
        if(songIn(name, yellowSongs)) material = Material.YELLOW_STAINED_GLASS;
        if(songIn(name, greenSongs)) material = Material.LIME_STAINED_GLASS;

        dropCustomItemName(player, material, name);
    }

    public static Score scoreType(Player player, String scoreboardName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = getNotNullObjective(scoreboard, scoreboardName);
        return objective.getScore(player.getName());
    }

    public static int scoreValue(Player player, String scoreboardName) {
        Score score = scoreType(player, scoreboardName);
        return score.getScore();
    }

    public static void handleSongActivationScoreboards(Player player, Score energyScore, Score energyRegenScore, Score energyCooldownScore) {
        updateBossBar(player, energyScore.getScore() - 1);
        energyScore.setScore(energyScore.getScore() - 1);
        energyRegenScore.setScore(0);
        energyCooldownScore.setScore(200);
    }

    public static void triggerSong(Player player, String song) {
        if(songIn(song, redSongs)) for(String sng : redSongs) player.getScoreboardTags().remove("Used" + sng);
        if(songIn(song, blueSongs)) for(String sng : blueSongs) player.getScoreboardTags().remove("Used" + sng);
        if(songIn(song, yellowSongs)) for(String sng : yellowSongs) player.getScoreboardTags().remove("Used" + sng);
        if(songIn(song, greenSongs)) for(String sng : greenSongs) player.getScoreboardTags().remove("Used" + sng);
        player.getScoreboardTags().add("Used" + song);

        switch(song) {
            case "Supporolift", "Supporokenisis":
                Score activeScore = scoreType(player, "UsingActiveSong");
                activeScore.setScore(230);
                break;
            case "Aggrosphere":
                Score usingAggrosphereScore = scoreType(player, "UsingAggrosphere");
                usingAggrosphereScore.setScore(230);
                break;
            default: break;
        }
    }

    public static Location getLocationInFrontOfEntity(Entity entity, float blocks) {
        Location loc = entity.getLocation();
        Vector direction = loc.getDirection().normalize();
        return loc.clone().add(direction.multiply(blocks));
    }

    public static double locationDistance(Entity e1, Entity e2) {
        return e1.getLocation().distance(e2.getLocation());
    }

    public static ItemStack customNameItemStack(Material material, Component component) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(component);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static boolean isAnEntityItem(Entity entity) {
        return entity instanceof Arrow || entity instanceof Fireball || entity instanceof FallingBlock
                || entity instanceof Interaction || entity instanceof ArmorStand;
    }
}
