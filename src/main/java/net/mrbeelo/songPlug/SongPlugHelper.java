package net.mrbeelo.songPlug;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongPlugHelper {
    public static void command(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    static String[] greenSongs = {"Supporolift", "Supporoform", "Supporokenisis", "Supporospike"};
    static String[] yellowSongs = {"Mobilileap", "Mobiliflash", "Mobilibounce", "Mobiliburst", "Mobiliglide", "Mobiliwings"};
    static String[] blueSongs = {"Protesphere", "Protebarrier", "Protepoint", "Protearmor", "Proteclone", "Proteheal"};
    static String[] redSongs = {"Aggrobeam", "Aggroblast", "Aggrostorm", "Aggrosphere", "Aggroquake", "Aggroshock",
            "Aggrovortex", "Aggroshard", "Aggrodetonate"};

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
        if(songIn(name, blueSongs)) material = Material.CYAN_STAINED_GLASS;
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
        if(songIn(name, blueSongs)) material = Material.CYAN_STAINED_GLASS;
        if(songIn(name, yellowSongs)) material = Material.YELLOW_STAINED_GLASS;
        if(songIn(name, greenSongs)) material = Material.LIME_STAINED_GLASS;

        dropCustomItemName(player, material, name);
    }

    public static void infuseSong(Player player, String name, boolean includeStacks, boolean force) {
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
            if(!player.getScoreboardTags().contains("Has" + songColor + "Song") || force) {
                if(force) {
                    if(songIn(name, redSongs)) for(String name2 : redSongs) player.getScoreboardTags().remove(name2);
                    if(songIn(name, blueSongs)) for(String name2 : blueSongs) player.getScoreboardTags().remove(name2);
                    if(songIn(name, yellowSongs)) for(String name2 : yellowSongs) player.getScoreboardTags().remove(name2);
                    if(songIn(name, greenSongs)) for(String name2 : greenSongs) player.getScoreboardTags().remove(name2);
                }
                player.getScoreboardTags().add(name);
                player.getScoreboardTags().add("Has" + songColor + "Song");
                if(includeStacks) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                player.sendMessage("You have been infused with " + name + "!");
            } else {
                player.sendMessage("You already have a " + songType + " Song!");
            }
        }
    }

    public static void clearSongs(Player player, boolean includeStacks) {
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
                        if(includeStacks) giveSong(player, song);
                    }
                }
            }
        }
    }

    public static Score scoreType(Entity entity, String scoreboardName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = getNotNullObjective(scoreboard, scoreboardName);
        return objective.getScore(entity.getScoreboardEntryName());
    }

    public static int scoreValue(Entity entity, String scoreboardName) {
        Score score = scoreType(entity, scoreboardName);
        return score.getScore();
    }

    public static void handleSongActivationScoreboards(Player player, Score energyScore, Score energyRegenScore, Score energyCooldownScore) {
        updateBossBar(player, energyScore.getScore() - 1);
        energyScore.setScore(energyScore.getScore() - 1);
        energyRegenScore.setScore(0);
        energyCooldownScore.setScore(200);
    }

    public static Location getLocationInFrontOfLoc(Location loc, float blocks) {
        Vector direction = loc.getDirection().normalize();
        return loc.clone().add(direction.multiply(blocks));
    }

    public static Location getRelativeLocationFromLoc(Location loc, float x, float y, float z) {
        Vector forward = loc.getDirection().normalize();
        Vector up = new Vector(0, 1, 0);
        Vector right = forward.clone().crossProduct(up).normalize();
        up = right.clone().crossProduct(forward).normalize();
        return loc.clone().add(right.multiply(x).add(up.multiply(y)).add(forward.multiply(z)));
    }

    public static double entityDistance(Entity e1, Entity e2) {
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

    public static void openSongMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("Song Selection"));
        menu.setItem(4, customNameItemStack(Material.RED_STAINED_GLASS, Component.text("Aggressium").color(NamedTextColor.RED)));
        menu.setItem(12, customNameItemStack(Material.CYAN_STAINED_GLASS, Component.text("Protisium").color(NamedTextColor.BLUE)));
        menu.setItem(13, customNameItemStack(Material.BARRIER, Component.text("Cancel").color(NamedTextColor.GRAY)));
        menu.setItem(14, customNameItemStack(Material.YELLOW_STAINED_GLASS, Component.text("Mobilium").color(NamedTextColor.YELLOW)));
        menu.setItem(22, customNameItemStack(Material.LIME_STAINED_GLASS, Component.text("Supporium").color(NamedTextColor.GREEN)));
        player.openInventory(menu);
    }

    public static void openClassMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("Class Selection"));
        menu.setItem(11, customNameItemStack(Material.PLAYER_HEAD, Component.text("Human").color(NamedTextColor.YELLOW)));
        menu.setItem(12, customNameItemStack(Material.OAK_LOG, Component.text("Felina").color(NamedTextColor.YELLOW)));
        menu.setItem(13, customNameItemStack(Material.DARK_PRISMARINE, Component.text("Ardoni").color(NamedTextColor.YELLOW)));
        menu.setItem(14, customNameItemStack(Material.MAGMA_BLOCK, Component.text("Magnorite").color(NamedTextColor.YELLOW)));
        menu.setItem(15, customNameItemStack(Material.ZOMBIE_HEAD, Component.text("Necromancer").color(NamedTextColor.YELLOW)));
        player.openInventory(menu);
    }

    public static void openRaceMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("Race Selection"));
        menu.setItem(10, customNameItemStack(Material.WHITE_WOOL, Component.text("Clanless")));
        menu.setItem(11, customNameItemStack(Material.CYAN_WOOL, Component.text("Sendaris")));
        menu.setItem(12, customNameItemStack(Material.YELLOW_WOOL, Component.text("Nestoris")));
        menu.setItem(13, customNameItemStack(Material.PURPLE_WOOL, Component.text("Mendoris")));
        menu.setItem(14, customNameItemStack(Material.LIME_WOOL, Component.text("Kaltaris")));
        menu.setItem(15, customNameItemStack(Material.RED_WOOL, Component.text("Voltaris")));
        menu.setItem(26, customNameItemStack(Material.BARRIER, Component.text("Cancel")));
        player.openInventory(menu);
    }

    public static double getMaxDistanceInFrontOfPlayer(Player player, double max, boolean includeEntities) {
        if(includeEntities) {
            Entity target = player.getTargetEntity((int) max);
            if(target != null) return entityDistance(player, target);
        }

        RayTraceResult result = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                max
        );

        if (result != null && result.getHitBlock() != null) {
            Location loc = result.getHitPosition().toLocation(player.getWorld());
            loc.setRotation(player.getLocation().getRotation());
            return player.getLocation().distance(loc);
        }

        return max;
    }

    public static void playSoundToNearby(Location location, double distance, Sound sound, SoundCategory category, float volume, float pitch) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(location.distance(player.getLocation()) <= distance) {
                player.playSound(player.getLocation(), sound, category, volume, pitch);
            }
        }
    }

    public static List<Entity> getEntities(Entity entity) {
        return entity.getWorld().getEntities();
    }

    public static List<Entity> getNearbyEntities(Location location, double distance) {
        List<Entity> list = new ArrayList<>();
        for(Entity entity : location.getWorld().getEntities()) if(location.distance(entity.getLocation()) <= distance) list.add(entity);
        return list;
    }

    public static Vector entityDistanceVector(Entity source, Entity target) {
        Vector vector = target.getLocation().toVector().subtract(source.getLocation().toVector());
        if (vector.lengthSquared() < 1e-6) return new Vector(0, 0, 0);
        return vector.normalize();
    }

    public static boolean aggroblastSightHelper(Player player, LivingEntity entity) {
        if(entity == player) return false;
        Vector playerVector = player.getLocation().getDirection().normalize().setY(player.getLocation().getDirection().normalize().getY() / 3);
        Vector entityVector = entityDistanceVector(player, entity).normalize();
        double angle = Math.toDegrees(playerVector.angle(entityVector));
        player.sendMessage("Angle for " + entity.getName() + ": " + angle + " deg.");
        return angle < 17.5f;
    }

    public static Entity getClosestEntity(Entity entity, double radius, String doesntContainTag) {
        Entity closestEntity = null;
        double closestDistance = 9999;

        for (Entity entity2 : entity.getWorld().getNearbyEntities(entity.getLocation(), radius, radius, radius)) {
            if (entity2 == entity) continue;
            if(doesntContainTag != null && entity2.getScoreboardTags().contains(doesntContainTag)) continue;
            double distance = entity.getLocation().distance(entity2.getLocation());
            if(distance < closestDistance) {
                closestEntity = entity2;
                closestDistance = distance;
            }

        }

        return closestEntity;
    }


    public static Location perspectiveOffset(Player player, Location base, double distance) {
        Vector forward = player.getLocation().getDirection().normalize();
        Vector up = new Vector(0, 1, 0);
        Vector offset = forward.clone().crossProduct(up).normalize();
        return base.clone().add(offset.multiply(distance));
    }

    public static Pair<Float, Boolean> aggrovortexHelper(int index) {
        float size;
        boolean red;

        size = switch(index) {
            case 0, 3 -> 0.7f;
            case 1, 2 -> 1f;
            case 4,5,6,8 -> 0.3f;
            case 7 -> 0.5f;
            default -> 1.0f;
        };

        red = switch(index) {
            case 0,1,2,3,7 -> true;
            default -> false;
        };

        return new Pair<>() {
            @Override
            public Boolean setValue(Boolean aBoolean) {
                return null;
            }

            @Override
            public Float getLeft() {
                return size;
            }

            @Override
            public Boolean getRight() {
                return red;
            }
        };
    }

    public static int pseudorandom(int value) {
        return switch (value % 10) {
            case 0 -> 5;
            case 1 -> 3;
            case 2 -> 6;
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 8;
            case 6 -> 9;
            case 7 -> 7;
            case 8 -> 0;
            case 9 -> 4;
            default -> 0;
        };
    }

    public static Team getTeam(String name) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
            if(name.equals("NoCollisions")) team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }

        return team;
    }

    public static Location getCenter(Entity entity) {
        return entity.getBoundingBox().getCenter().toLocation(entity.getWorld());
    }

    public static ResolvableProfile playerPatchedCapeProfile(Player player, String capeKey) {
        ResolvableProfile.SkinPatch patch = ResolvableProfile.SkinPatch.skinPatch().cape(Key.key(capeKey)).build();
        return ResolvableProfile.resolvableProfile().skinPatch(patch).name(player.getName()).build();
    }

    public static ResolvableProfile patchedPlayerProfile(String playerPatch) {
        ResolvableProfile.SkinPatch patch = ResolvableProfile.SkinPatch.skinPatch().body(Key.key("block/gold_block")).build();
        return ResolvableProfile.resolvableProfile().skinPatch(patch).build();
    }

    public static BlockDisplay summonDisplay(Location location, String tag, Material block) {
        BlockDisplay display = location.getWorld().spawn(location, BlockDisplay.class);
        display.setBlock(Bukkit.createBlockData(Material.CYAN_STAINED_GLASS));
        display.getScoreboardTags().add(tag);
        display.setBlock(Bukkit.createBlockData(block));
        return display;
    }

    public static void setNonCollidable(Entity entity, boolean b) {
        Team noCollTeam = getTeam("NoCollisions");
        if(b) {
            noCollTeam.addEntity(entity);
        } else {
            noCollTeam.removeEntity(entity);
        }
    }

    public static void setNonCollidable(Entity entity) {
        setNonCollidable(entity, true);
    }

    public static void triggerSong(Player player, String song) {
        if(songIn(song, redSongs)) for(String sng : redSongs) player.getScoreboardTags().remove("Used" + sng);
        if(songIn(song, blueSongs)) for(String sng : blueSongs) player.getScoreboardTags().remove("Used" + sng);
        if(songIn(song, yellowSongs)) for(String sng : yellowSongs) player.getScoreboardTags().remove("Used" + sng);
        if(songIn(song, greenSongs)) for(String sng : greenSongs) player.getScoreboardTags().remove("Used" + sng);
        player.getScoreboardTags().add("Used" + song);

        switch(song) {
            case "Supporolift", "Supporokenisis", "Aggrobeam", "Mobiliwings", "Mobilibounce", "Mobiliglide", "Protesphere", "Protepoint",
                 "Aggrostorm", "Protearmor":
                Score activeScore = scoreType(player, "UsingActiveSong");
                activeScore.setScore(230);
                break;
            case "Aggrosphere", "Proteheal", "Mobilileap", "Mobiliflash", "Aggroquake", "Mobiliburst", "Supporoform", "Aggroblast", "Aggrovortex",
                 "Aggroshard", "Aggrodetonate", "Supporospike", "Proteclone", "Protebarrier", "Aggroshock":
                Score passiveScore = scoreType(player, "Using" + song);
                passiveScore.setScore(230);
                break;
            default: break;
        }
    }
}
