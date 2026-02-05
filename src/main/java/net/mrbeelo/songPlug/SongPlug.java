package net.mrbeelo.songPlug;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import java.util.Objects;

import static net.mrbeelo.songPlug.SongPlugHelper.*;
import static net.mrbeelo.songPlug.SongPlugTick.*;

//! WARNING
//! All the code within this project is absolutely terrible and unoptimized
//! It was made solely for the use in a purpur server
//! Please don't go running complicated code each tick, like I did in runPlayerLogic and updateBossBar
//! Thank you for listening.

public final class SongPlug extends JavaPlugin {
    private static SongPlug instance;

    public static void log(String msg) {
        instance.getLogger().info(msg);
    }

    @Override
    public void onEnable() {
        getLogger().info("ENABLING SONGS OF WAR PLUGIN");
        instance = this;
        getServer().getPluginManager().registerEvents(new SongPlugListener(), this);
        getServer().getScheduler().runTaskTimer(this, this::tick, 0,  1);

        String[] commands = {"class", "givesong", "getblock", "resetcooldowns", "energy", "infusesong", "clearsongs"};
        for(String command : commands) Objects.requireNonNull(getCommand(command)).setExecutor(new SongPlugCommandExecutor());
    }

    @Override
    public void onDisable() {
        getLogger().info("DISABLING SONGS OF WAR PLUGIN");
    }

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Score energyScore = scoreType(player, "SongEnergy");
            classTick(player);
            updateBossBar(player, energyScore.getScore());
            updateRegen(player, energyScore);
            updateCooldowns(player);
            updateSidebar(player);
            updateSongs(player);
        }
    }
}
