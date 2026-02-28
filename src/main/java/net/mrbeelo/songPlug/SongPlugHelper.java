package net.mrbeelo.songPlug;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.mrbeelo.songPlug.SongPlug.plugin;
import static net.mrbeelo.songPlug.SongPlugTick.updateBossBar;

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

    public static void removeItems(Inventory inventory, Material material, int amount) {
        int remaining = amount;
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType() != material) continue;
            int take = Math.min(stack.getAmount(), remaining);
            stack.setAmount(stack.getAmount() - take);
            remaining -= take;
        }
    }

    public static void dropSong(Player player, String name) {
        Material material = Material.BARRIER;

        if(songIn(name, redSongs)) material = Material.RED_STAINED_GLASS;
        if(songIn(name, blueSongs)) material = Material.CYAN_STAINED_GLASS;
        if(songIn(name, yellowSongs)) material = Material.YELLOW_STAINED_GLASS;
        if(songIn(name, greenSongs)) material = Material.LIME_STAINED_GLASS;

        dropCustomItemName(player, material, name);
    }

    public static boolean infuseSong(Player player, String name, boolean includeStacks, boolean force) {
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

                return true;
            } else {
                player.sendMessage("You already have a " + songType + " Song!");
            }
        }

        return false;
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

    public static boolean isNetheriteArmor(ItemStack stack) {
        Material mat = stack.getType();
        return mat.equals(Material.NETHERITE_HELMET) || mat.equals(Material.NETHERITE_CHESTPLATE) || mat.equals(Material.NETHERITE_LEGGINGS) || mat.equals(Material.NETHERITE_BOOTS);
    }

    public static boolean isFish(Material material) {
        return material.equals(Material.TROPICAL_FISH) || material.equals(Material.PUFFERFISH) || material.equals(Material.COD) ||
                material.equals(Material.COOKED_COD) || material.equals(Material.SALMON) || material.equals(Material.COOKED_SALMON);
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

    public static int getLevel(Player player) {
        return scoreValue(player, "Level");
    }

    public static void handleSongActivationScoreboards(Player player, Score energyScore, Score energyRegenScore, Score energyCooldownScore) {
        energyScore.setScore(energyScore.getScore() - 1);
        updateBossBar(player);
        energyRegenScore.setScore(0);

        Score warSongScore = scoreType(player, "WarSongMeter");
        if(getLevel(player) >= 50) warSongScore.setScore(warSongScore.getScore() + 1);

        if(scoreValue(player, "WarSongMeter") == 4 && getLevel(player) >= 50) {
            warSongScore.setScore(0);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.5f);
        } else {
            energyCooldownScore.setScore(200);
        }
    }

    public static boolean isUndead(Entity entity) {
        return switch(entity.getType()) {
            case CAMEL_HUSK,
                 DROWNED,
                 GIANT,
                 HUSK,
                 PARCHED,
                 PHANTOM,
                 SKELETON,
                 SKELETON_HORSE,
                 STRAY,
                 WITHER,
                 WITHER_SKELETON,
                 ZOGLIN,
                 ZOMBIE,
                 ZOMBIE_HORSE,
                 ZOMBIE_VILLAGER,
                 ZOMBIFIED_PIGLIN,
                 BOGGED,
                 ZOMBIE_NAUTILUS -> true;
            default -> false;
        };
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

    public static ItemStack potionStack(PotionType type) {
        ItemStack stack = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.setBasePotionType(type);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack enchantmentStack(Enchantment enchantment, int level) {
        return customEnchantmentStack(enchantment, level, null);
    }

    public static ItemStack customEnchantmentStack(Enchantment enchantment, int level, String name) {
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        if(name != null) meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack customPotionStack(Material material, PotionEffectType effectType, int duration, int amplifier, String name) {
        ItemStack stack = new ItemStack(material);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.addCustomEffect(new PotionEffect(effectType, duration, amplifier),  true);
        meta.setBasePotionType(PotionType.WATER);
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack customPotionStack(PotionEffectType effectType, int duration, int amplifier, String name) {
        return customPotionStack(Material.POTION, effectType, duration, amplifier, name);
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
        menu.setItem(26, customNameItemStack(Material.STRUCTURE_VOID, Component.text("None")));
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

    public static void openBrewingMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("Brewing Stand"));
        menu.setItem(0, customPotionStack(PotionEffectType.INVISIBILITY, 400, 0, "Potion of Invisibility"));
        menu.setItem(1, customPotionStack(PotionEffectType.SPEED, 400, 0, "Potion of Speed"));
        menu.setItem(2, customPotionStack(PotionEffectType.FIRE_RESISTANCE, 400, 0, "Potion of Fire Resistance"));
        menu.setItem(3, customPotionStack(PotionEffectType.HASTE, 400, 0, "Potion of Haste"));
        menu.setItem(4, customPotionStack(Material.SPLASH_POTION, PotionEffectType.WEAKNESS, 400, 0, "Splash Potion of Weakness"));
        player.openInventory(menu);
    }

    public static void openEnchantingMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("Enchantment Table"));
        menu.setItem(0, customEnchantmentStack(Enchantment.SILK_TOUCH, 1, "Enchanted Book of Silk Touch"));
        menu.setItem(1, customEnchantmentStack(Enchantment.UNBREAKING, 2, "Enchanted Book of Unbreaking"));
        menu.setItem(2, customEnchantmentStack(Enchantment.EFFICIENCY, 1, "Enchanted Book of Efficiency"));
        menu.setItem(3, customEnchantmentStack(Enchantment.SHARPNESS, 1, "Enchanted Book of Sharpness"));
        menu.setItem(4, customEnchantmentStack(Enchantment.LOOTING, 2, "Enchanted Book of Looting"));
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

    public static Collection<Entity> getGlobalEntityCollection() {
        Collection<Entity> entities = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) entities.addAll(world.getEntities());
        return entities;
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

    public static void setCustomItemData(ItemStack stack, String key, String value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin(), key);
        stack.editPersistentDataContainer(pdc -> {
            pdc.set(namespacedKey, PersistentDataType.STRING, value);
        });
    }

    public static String getCustomItemData(ItemStack stack, String key) {
        ItemMeta meta = stack.getItemMeta();
        if(meta != null) {
            NamespacedKey namespacedKey = new NamespacedKey(plugin(), key);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            return container.get(namespacedKey, PersistentDataType.STRING);
        }
        return null;
    }

    public static boolean aggroblastSightHelper(Player player, LivingEntity entity) {
        if(entity == player) return false;
        Vector playerVector = player.getLocation().getDirection().normalize().setY(player.getLocation().getDirection().normalize().getY() / 3);
        Vector entityVector = entityDistanceVector(player, entity).normalize();
        double angle = Math.toDegrees(playerVector.angle(entityVector));
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

    public static int pseudoRandom(int seed) {
        seed ^= (seed << 13);
        seed ^= (seed >>> 17);
        seed ^= (seed << 5);
        return seed;
    }

    public static void aggressiumCharge(Player player, int score) {
        int time = 231 - score; //1-30

        Location center = getCenter(player);
        float radius = 1f;

        if(time > 18 && time <= 25) radius = 1f - (time - 18) * 0.75f / 10f;
        if(time > 25) radius = 1f / 2f + (time - 25) / 10f * 2.4f;


        for (int i = 0; i < 100; i++) {
            double theta = Math.random() * Math.PI * 2;
            double phi = Math.acos(2 * Math.random() - 1);
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);
            if(i % 7 == 0) center.getWorld().spawnParticle(Particle.DUST, center.clone().add(x, y, z), 0, new Particle.DustOptions(Color.RED, 0.7f));
        }
    }

    public static void mobiliumCharge(Player player, int score) {
        int time = 231 - score; //1-30

        for(int j = 1; j <= 3; j++) {
            Location center = getCenter(player);
            float radius = 1f;
            int points = 100;

            Vector circleVector = switch(j) {
                case 1 -> new Vector(0.13f, 0.96f, 0.25f);
                case 2 -> new Vector(0.97f, 0.21f, 0.26f);
                case 3 -> new Vector(0.36f, 0.32f, 0.95f);
                default -> new Vector(0, 0, 0);
            };

            Vector circleAddVector = switch(j) {
                case 1 -> new Vector(time / 60f, time / -40f, time / -80f);
                case 2 -> new Vector(time / -45f, time / -55f, time / 70f);
                case 3 -> new Vector(time / -90f, time / 60f, time / -45f);
                default -> new Vector(0, 0, 0);
            };

            Vector forward = circleVector.add(circleAddVector);
            Vector up = new Vector(0, 1, 0);
            if (Math.abs(forward.dot(up)) > 0.99) up = new Vector(1, 0, 0);

            Vector right = forward.clone().crossProduct(up).normalize();
            up = right.clone().crossProduct(forward).normalize();

            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                Vector offset = right.clone().multiply(Math.cos(angle) * radius).add(up.clone().multiply(Math.sin(angle) * radius));
                Location loc = center.clone().add(offset);


                player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.YELLOW, 0.6f * (31 - time) / 30f));
            }
        }
    }

    public static void protisiumCharge(Player player, int score) {
        int time = 231 - score; //1-30

        Location center = getCenter(player);
        float radius = 1.15f;
        int points = 100;
        for (int i = 0; i < points; i++) {
            double theta = Math.random() * Math.PI * 2;
            double phi = Math.acos(2 * Math.random() - 1);
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            center.getWorld().spawnParticle(Particle.DUST, center.clone().add(x, y, z), 0, new Particle.DustOptions(Color.fromRGB(71, 110, 253), 0.6f));
        }
    }

    public static void supporiumCharge(Player player, int score) {
        int time = 231 - score; //1-30
        BoundingBox pBox = player.getBoundingBox();
        float offset = 0.5f;

        BoundingBox box = new BoundingBox(pBox.getMinX() - offset, pBox.getMinY() - offset, pBox.getMinZ() - offset,
                pBox.getMaxX() + offset, pBox.getMaxY() + offset, pBox.getMaxZ() + offset);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for(int i = 0; i < 5; i++) {
            Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX(), box.getMaxX()),
                    random.nextDouble(box.getMinY(), box.getMaxY()), random.nextDouble(box.getMinZ(), box.getMaxZ()));
            player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 0, new Particle.DustTransition(
                    Color.fromRGB(137, 251, 104), Color.fromRGB(58, 255, 0), time / 30f));
        }
    }

    public static List<Player> allExceptPlayer(Player player) {
        List<Player> players = new ArrayList<>();
        for(Player plr : Bukkit.getOnlinePlayers()) {
            if(!plr.getName().equals(player.getName())) players.add(plr);
        }

        return players;
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

    public static String capitalize(String string) {
        if(string.length() <= 1) return string.toUpperCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static int getSowClass(Player player) {
        if(player.getScoreboardTags().contains("HumanClass")) return 0;
        if(player.getScoreboardTags().contains("FelinaClass")) return 1;
        if(player.getScoreboardTags().contains("ArdoniClass")) return 2;
        if(player.getScoreboardTags().contains("MagnoriteClass")) return 3;
        if(player.getScoreboardTags().contains("NecromancerClass")) return 4;
        return 5;
    }

    public static TextComponent getSowClassComponent(Player player, boolean parentheses, boolean allUppercase) {
        String className = "NONE";
        String raceName = "NONE";
        NamedTextColor classColor = NamedTextColor.WHITE;
        NamedTextColor raceColor = NamedTextColor.WHITE;

        for (String tag : player.getScoreboardTags()) {
            if (tag.endsWith("Class")) {
                String nameByItself = tag.substring(0, tag.length() - 5);
                className = nameByItself.toUpperCase();

                classColor = switch (nameByItself.toUpperCase()) {
                    case "HUMAN" -> NamedTextColor.GRAY;
                    case "FELINA" -> NamedTextColor.DARK_GREEN;
                    case "ARDONI", "MAGNORITE" -> NamedTextColor.GOLD;
                    case "NECROMANCER" -> NamedTextColor.DARK_GRAY;
                    default -> throw new IllegalStateException("Unexpected value: " + nameByItself.toUpperCase());
                };
            }

            if (tag.endsWith("ArdoniRace")) {
                String nameByItself2 = tag.substring(0, tag.length() - 10);
                raceName = nameByItself2.toUpperCase();

                raceColor = switch (nameByItself2.toUpperCase()) {
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

        if(!allUppercase) {
            className = capitalize(className);
            raceName = capitalize(raceName);
        }

        String joinedName;
        NamedTextColor joinedColor;

        if (className.equals("ARDONI") || className.equals("Ardoni")) {
            if(parentheses) {
                joinedName = className + " (" + raceName + ")";
            } else {
                joinedName = className + " - " + raceName;
            }

            joinedColor = raceColor;
        } else {
            joinedName = className;
            joinedColor = classColor;
        }

        return Component.text(joinedName, joinedColor);
    }

    public static void triggerSong(Player player, String song) {
        String[] activeSongs = {"Supporolift", "Supporokenisis", "Aggrobeam", "Mobiliwings", "Mobilibounce", "Mobiliglide", "Protesphere", "Protepoint",
                "Aggrostorm", "Protearmor"};
        String[] passiveSongs = {"Aggrosphere", "Proteheal", "Mobilileap", "Mobiliflash", "Aggroquake", "Mobiliburst", "Supporoform", "Aggroblast", "Aggrovortex",
                "Aggroshard", "Aggrodetonate", "Supporospike", "Proteclone", "Protebarrier", "Aggroshock"};

        Score score;

        if(Arrays.stream(activeSongs).toList().contains(song)) {
            score = scoreType(player, "UsingActiveSong");
        } else if(Arrays.stream(passiveSongs).toList().contains(song)) {
            score = scoreType(player, "Using" + song);
        } else {
            score = null;
        }

        if(score != null) {
            if(player.getScoreboardTags().contains("Used" + song)) {
                score.setScore(0);
                Bukkit.getScheduler().runTaskLater(SongPlug.getPlugin(SongPlug.class), () -> {
                    score.setScore(230);
                    player.getScoreboardTags().add("Used" + song);
                }, 1L);
            } else {
                score.setScore(230);
                player.getScoreboardTags().add("Used" + song);
            }
        }
    }
}
