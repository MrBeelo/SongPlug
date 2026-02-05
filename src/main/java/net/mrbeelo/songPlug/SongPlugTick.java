package net.mrbeelo.songPlug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import static net.mrbeelo.songPlug.SongPlugClass.resetClassStats;
import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugTick {
    public static void classTick(Player player) {
        Score infuseDebuffScore = scoreType(player, "InfuseDebuff");
        int infuseDebuff = infuseDebuffScore.getScore();
        if(infuseDebuff > 0) infuseDebuffScore.setScore(infuseDebuff - 1);
        if(infuseDebuff == 1) resetClassStats(player);

        Score xpScore = scoreType(player, "XP");
        int xp = xpScore.getScore();
        Score levelScore = scoreType(player, "Level");
        int level = levelScore.getScore();

        if(xp >= level * 10 + 100) {
            int rem = (level != 0) ? xp % (level * 10 + 100) : 0;
            xpScore.setScore(rem);
            levelScore.setScore(level + 1);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.0f);
        }
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

    public static void updateRegen(Player player, Score energyScore) {
        if(player.getScoreboardTags().contains("ArdoniClass")) {
            Score energyRegenScore = scoreType(player, "SongEnergyRegen");
            int songEnergyRegen = energyRegenScore.getScore();

            if(energyScore.getScore() < 5) {
                if(songEnergyRegen < 500) {
                    energyRegenScore.setScore(songEnergyRegen + 1);
                } else {
                    energyRegenScore.setScore(0);
                    updateBossBar(player, energyScore.getScore() + 1);
                    energyScore.setScore(energyScore.getScore() + 1);
                }
            }
        }
    }

    public static void updateCooldowns(Player player) {
        if(player.getScoreboardTags().contains("ArdoniClass")) {
            Score redCooldownScore = scoreType(player, "RedEnergyCooldown");
            Score blueCooldownScore = scoreType(player, "BlueEnergyCooldown");
            Score yellowCooldownScore = scoreType(player, "YellowEnergyCooldown");
            Score greenCooldownScore = scoreType(player, "GreenEnergyCooldown");
            Score infuseCooldownScore = scoreType(player, "InfuseCooldown");

            int redEnergyCooldown = redCooldownScore.getScore();
            int blueEnergyCooldown = blueCooldownScore.getScore();
            int yellowEnergyCooldown = yellowCooldownScore.getScore();
            int greenEnergyCooldown = greenCooldownScore.getScore();
            int infuseCooldown = infuseCooldownScore.getScore();

            if(redEnergyCooldown > 0) redCooldownScore.setScore(redEnergyCooldown - 1);
            if(blueEnergyCooldown > 0) blueCooldownScore.setScore(blueEnergyCooldown - 1);
            if(yellowEnergyCooldown > 0) yellowCooldownScore.setScore(yellowEnergyCooldown - 1);
            if(greenEnergyCooldown > 0) greenCooldownScore.setScore(greenEnergyCooldown - 1);
            if(infuseCooldown > 0) infuseCooldownScore.setScore(infuseCooldown - 1);
        }
    }

    public static void updateSidebar(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        Scoreboard board = player.getScoreboard();

        if (board == manager.getMainScoreboard()) {
            board = manager.getNewScoreboard();
            player.setScoreboard(board);
        }

        Objective sidebar = board.getObjective(DisplaySlot.SIDEBAR);

        if (sidebar == null) {
            if(board.getObjective("Stats") == null) {
                Objective objective = board.registerNewObjective("Stats", Criteria.DUMMY, Component.text("SHITS OF WAR - STATS", NamedTextColor.GOLD));
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(board);
            } else {
                Objective objective = board.getObjective("Stats");
                assert objective != null;
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(board);
            }
        } else if(sidebar.getName().equals("Stats")) {
            for (String entry : Objects.requireNonNull(sidebar.getScoreboard()).getEntries()) sidebar.getScore(entry).resetScore();
            sidebar.getScore("§7").setScore(6);
            sidebar.getScore("XP: " + scoreValue(player, "XP") + "/" + (scoreValue(player, "Level") * 10 + 100)).setScore(5);
            sidebar.getScore("Level: " + scoreValue(player, "Level")).setScore(4);
            sidebar.getScore("§6").setScore(3);
            sidebar.getScore("Class: " + getSowClass(player, true, false).content()).setScore(2);
            sidebar.getScore("§5").setScore(1);
        }
    }

    public static void updateSongs(Player player) {
        Score usingActiveSongScore = scoreType(player, "UsingActiveSong");
        int usingActiveSong = usingActiveSongScore.getScore();
        if(usingActiveSong > 0) usingActiveSongScore.setScore(usingActiveSong - 1);

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        assert speed != null;
        AttributeInstance jump = player.getAttribute(Attribute.JUMP_STRENGTH);
        assert jump != null;

        //--GREEN SONGS--//

        //SUPPOROLIFT
        if(player.getScoreboardTags().contains("UsedSupporolift")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                supporiumCharge(player, usingActiveSong);
            }

            if(usingActiveSong >= 224 && usingActiveSong <= 230) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_PARROT_IMITATE_EVOKER, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingActiveSong == 201) {
                Entity target = player.getTargetEntity(18, false);
                if(!isAnEntityItem(target) && target != null) {
                    target.getScoreboardTags().add("GotSupporoliftedBy" + player.getName());
                } else {
                    usingActiveSongScore.setScore(0);
                }

                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_WITHER_HURT, SoundCategory.MASTER, 100f, 1.85f);
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("GotSupporoliftedBy" + player.getName()) && entityDistance(player, entity) <= 20 && !isAnEntityItem(entity)) {
                        RayTraceResult result = player.getWorld().rayTraceBlocks(
                                player.getEyeLocation(),
                                player.getEyeLocation().getDirection(),
                                9
                        );

                        if (result == null || result.getHitBlock() == null) {
                            entity.teleport(getLocationInFrontOfLoc(player.getLocation(), 9));
                        } else {
                            Location loc = result.getHitPosition().toLocation(player.getWorld());
                            loc.setRotation(player.getLocation().getRotation());
                            entity.teleport(loc);
                        }

                        float offset = 0.4f;
                        BoundingBox box = entity.getBoundingBox();
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        for(int i = 0; i < 13; i++) {
                            Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX() - offset, box.getMaxX() + offset),
                                    random.nextDouble(box.getMinY() - offset, box.getMaxY() + offset), random.nextDouble(box.getMinZ() - offset, box.getMaxZ() + offset));
                            Color color = (i % 2 == 0) ? Color.fromRGB(165, 255, 161) : Color.LIME;
                            Particle.DUST.builder().location(loc).count(0).allPlayers().color(color).spawn();
                        }

                        Score cooldownScore = scoreType(player, "GreenEnergyCooldown");
                        cooldownScore.setScore(200);

                        if(player.isSneaking()) {
                            if(player.getLocation().getRotation().pitch() < 0) {
                                entity.setVelocity(entity.getLocation().getDirection().multiply(1.5f));
                            }
                            usingActiveSongScore.setScore(0);
                        }
                    }
                }
            }

            if(usingActiveSong == 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("GotSupporoliftedBy" + player.getName())) entity.getScoreboardTags().remove("GotSupporoliftedBy" + player.getName());
                    player.getScoreboardTags().remove("UsedSupporolift");
                }
            }
        }

        //SUPPOROKENISIS
        if(player.getScoreboardTags().contains("UsedSupporokenisis")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                supporiumCharge(player, usingActiveSong);
            }

            if(usingActiveSong >= 224 && usingActiveSong <= 230) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_PARROT_IMITATE_EVOKER, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingActiveSong == 201) {
                Entity target = player.getTargetEntity(18, false);
                if(isAnEntityItem(target)) {
                    target.getScoreboardTags().add("GotSupporokenisiedBy" + player.getName());
                } else {
                    usingActiveSongScore.setScore(0);
                }


                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_WITHER_HURT, SoundCategory.MASTER, 100f, 1.85f);
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player.getName()) && entityDistance(player, entity) <= 20 && isAnEntityItem(entity)) {
                        RayTraceResult result = player.getWorld().rayTraceBlocks(
                                player.getEyeLocation(),
                                player.getEyeLocation().getDirection(),
                                9
                        );

                        if (result == null || result.getHitBlock() == null) {
                            entity.teleport(getLocationInFrontOfLoc(player.getLocation(), 9));
                        } else {
                            Location loc = result.getHitPosition().toLocation(player.getWorld());
                            loc.setRotation(player.getLocation().getRotation());
                            entity.teleport(loc);
                        }

                        float offset = 0.4f;
                        BoundingBox box = entity.getBoundingBox();
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        for(int i = 0; i < 13; i++) {
                            Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX() - offset, box.getMaxX() + offset),
                                    random.nextDouble(box.getMinY() - offset, box.getMaxY() + offset), random.nextDouble(box.getMinZ() - offset, box.getMaxZ() + offset));
                            Color color = (i % 2 == 0) ? Color.fromRGB(165, 255, 161) : Color.LIME;
                            Particle.DUST.builder().location(loc).count(0).allPlayers().color(color).spawn();
                        }

                        Score cooldownScore = scoreType(player, "GreenEnergyCooldown");
                        cooldownScore.setScore(200);

                        if(player.isSneaking()) {
                            usingActiveSongScore.setScore(0);
                        }
                    }
                }
            }

            if(usingActiveSong == 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player.getName())) entity.getScoreboardTags().remove("GotSupporokenisiedBy" + player.getName());
                    player.getScoreboardTags().remove("UsedSupporokenisis");
                }
            }
        }


        //SUPPOROFORM
        if(player.getScoreboardTags().contains("UsedSupporoform")) {
            Score usingPassiveSongScore = scoreType(player, "UsingSupporoform");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                supporiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong >= 224 && usingActiveSong <= 230) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_PARROT_IMITATE_EVOKER, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingPassiveSong == 201) {
                Material[] acceptableItems = {Material.COPPER_INGOT, Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND};
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(!Arrays.asList(acceptableItems).contains(stack.getType())) return;
                if(!stack.isEmpty()) {
                    int random = Random.from(RandomGenerator.getDefault()).nextInt(0, 10000);
                    Material newStackMaterial = switch (stack.getType()) {
                        case Material.COPPER_INGOT -> switch (random % 5) {
                            case 0 -> Material.COPPER_HELMET;
                            case 1 -> Material.COPPER_CHESTPLATE;
                            case 2 -> Material.COPPER_LEGGINGS;
                            case 3 -> Material.COPPER_BOOTS;
                            case 4 -> Material.WAXED_COPPER_GOLEM_STATUE;
                            default -> Material.BARRIER;
                        };
                        case Material.IRON_INGOT -> switch (random % 4) {
                            case 0 -> Material.IRON_HELMET;
                            case 1 -> Material.IRON_CHESTPLATE;
                            case 2 -> Material.IRON_LEGGINGS;
                            case 3 -> Material.IRON_BOOTS;
                            default -> Material.BARRIER;
                        };
                        case Material.GOLD_INGOT -> switch (random % 4) {
                            case 0 -> Material.GOLDEN_HELMET;
                            case 1 -> Material.GOLDEN_CHESTPLATE;
                            case 2 -> Material.GOLDEN_LEGGINGS;
                            case 3 -> Material.GOLDEN_BOOTS;
                            default -> Material.BARRIER;
                        };
                        case Material.DIAMOND -> switch (random % 4) {
                            case 0 -> Material.DIAMOND_HELMET;
                            case 1 -> Material.DIAMOND_CHESTPLATE;
                            case 2 -> Material.DIAMOND_LEGGINGS;
                            case 3 -> Material.DIAMOND_BOOTS;
                            default -> Material.BARRIER;
                        };
                        default -> Material.STRUCTURE_BLOCK;
                    };

                    ItemStack newStack = new ItemStack(newStackMaterial);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), newStack);

                    playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_WITHER_HURT, SoundCategory.MASTER, 100f, 1.85f);

                    BoundingBox box = player.getBoundingBox();
                    Location location = getRelativeLocationFromLoc(box.getCenter().toLocation(player.getWorld()).setRotation(player.getYaw(), player.getPitch()),
                            0.5f, -0.2f, 0);

                    float boxSize = 0.2f;
                    BoundingBox box2 = new BoundingBox(location.getX() - boxSize, location.getY() - boxSize, location.getZ() - boxSize,
                            location.getX() + boxSize, location.getY() + boxSize, location.getZ() + boxSize);
                    ThreadLocalRandom threadRandom = ThreadLocalRandom.current();
                    for(int i = 0; i < 30; i++) {
                        Location loc = new Location(player.getWorld(), threadRandom.nextDouble(box2.getMinX(), box2.getMaxX()),
                                threadRandom.nextDouble(box2.getMinY(), box2.getMaxY()), threadRandom.nextDouble(box2.getMinZ(), box2.getMaxZ()));
                        Particle.DUST.builder().location(loc).count(0).allPlayers().color(Color.LIME).spawn();
                    }
                }

            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedSupporoform");
            }
        }

        //SUPPOROSPIKE
        if(player.getScoreboardTags().contains("UsedSupporospike")) {
            Score usingPassiveSongScore = scoreType(player, "UsingSupporospike");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                supporiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 201) {
                Block targetBlock = player.getTargetBlock(null, 7);
                Location spawnLocation = null;
                if(targetBlock.isEmpty()) {
                    Location location = targetBlock.getLocation();

                    for(int i = 1; i < 5; i++) {
                        if(location.add(0, -i, 0).getBlock().isSolid()) {
                            spawnLocation = location.add(0, -i + 1, 0);
                            break;
                        }
                    }
                } else {
                    spawnLocation = targetBlock.getLocation();
                }

                Location pivot = targetBlock.getBoundingBox().getCenter().toLocation(player.getWorld());
                Vector3f size = new Vector3f(1.3f, 0.5f, 1.3f);
                BoundingBox box = new BoundingBox(pivot.getX() - size.x, pivot.getY() - size.y + 1f, pivot.getZ() - size.z,
                        pivot.getX() + size.x, pivot.getY() + size.y + 1f, pivot.getZ() + size.z);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                for(int i = 0; i < 30; i++) {
                    Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX(), box.getMaxX()),
                            random.nextDouble(box.getMinY(), box.getMaxY()), random.nextDouble(box.getMinZ(), box.getMaxZ()));
                    player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.LIME, 1.2f));
                }

                if(spawnLocation == null) {
                    player.sendMessage("Spike position nulled. Refunding!");
                    usingPassiveSongScore.setScore(0);
                    Score greenCooldownScore = scoreType(player, "GreenEnergyCooldown");
                    greenCooldownScore.setScore(0);
                    Score songEnergyScore = scoreType(player, "SongEnergy");
                    songEnergyScore.setScore(songEnergyScore.getScore() + 1);
                } else {
                    assert spawnLocation != null;
                    Interaction interaction = player.getWorld().spawn(spawnLocation, Interaction.class);
                    interaction.getScoreboardTags().add("Supporospike" + player.getName());
                    for(Entity entity : getEntities(player)) {
                        if(entity != player && !isAnEntityItem(entity) && entity.getLocation().distance(interaction.getLocation().add(0, 1, 0)) <= 1.5) {
                            entity.setVelocity(new Vector(entity.getVelocity().getX(), 2, entity.getVelocity().getZ()));
                        }
                    }
                }
            }

            if(usingPassiveSong <= 200 && usingPassiveSong >= 197) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Supporospike" + player.getName())) {
                        int i = 201 - usingPassiveSong;
                        Block block = entity.getLocation().add(0, i, 0).getBlock();
                        Material type = switch(i) {
                            case 1 -> Material.COBBLESTONE;
                            case 2 -> Material.STONE;
                            case 3 -> Material.ANDESITE;
                            case 4 -> Material.ANDESITE_STAIRS;
                            default -> Material.BEDROCK;
                        };

                        if(block.isEmpty()) {
                            block.setType(type);
                            Interaction interaction = player.getWorld().spawn(block.getLocation(), Interaction.class);
                            interaction.getScoreboardTags().add("SupporospikeCheck" + player.getName());
                        }
                    }
                }
            }

            if(usingPassiveSong == 40) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Supporospike" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("SupporospikeCheck" + player.getName())) {
                        entity.remove();
                        Block block = entity.getLocation().getBlock();
                        block.setType(Material.AIR);
                    }
                }
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedSupporospike");
            }
        }

        //--YELLOW SONGS--//

        //MOBILILEAP
        if(player.getScoreboardTags().contains("UsedMobilileap")) {
            Score usingPassiveSongScore = scoreType(player, "UsingMobilileap");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            AttributeInstance attribute = player.getAttribute(Attribute.SAFE_FALL_DISTANCE);
            assert attribute != null;

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                mobiliumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
            }

            if(usingPassiveSong == 201) {
                player.setVelocity(new Vector(0, 1.9, 0));
                attribute.setBaseValue(999);

                BoundingBox box = player.getBoundingBox();
                Location location = new Location(player.getWorld(), box.getCenterX(), box.getMinY(), box.getCenterZ());
                BlockDisplay display = summonDisplay(location, "MobilileapDisplay" + player.getName(), Material.YELLOW_STAINED_GLASS);

                Vector3f size = new Vector3f(1.5f, 0.4f, 1.5f);
                Vector3f translation = new Vector3f(-0.5f * size.x, -0.5f * size.y, -0.5f * size.z);
                display.setTransformation(new Transformation(translation, new Quaternionf(), size, new Quaternionf()));

                for(int i = -10; i <= 10; i++) {
                    for(int j = -10; j <= 10; j++) {
                        Location loc = player.getBoundingBox().getCenter().toLocation(player.getWorld()).add(i / 10f, -0.7f, j / 10f);
                        player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.YELLOW, 0.7f));
                    }
                }
            }

            if(usingPassiveSong <= 200 && usingPassiveSong >= 194) {
                for(int i = 1; i <= 10; i++) {
                    Location loc = player.getBoundingBox().getCenter().toLocation(player.getWorld()).add(0, -i / 10f, 0);
                    player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.YELLOW, 1.3f));
                }

                Location location = player.getBoundingBox().getCenter().toLocation(player.getWorld()).add(0, -5, 0);
                Vector3f size = new Vector3f(0.7f, 5f, 0.7f);
                BoundingBox box = new BoundingBox(location.getX() - size.x, location.getY() - size.y, location.getZ() - size.z,
                        location.getX() + size.x, location.getY() + size.y, location.getZ() + size.z);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                for(int i = 0; i < 30; i++) {
                    Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX(), box.getMaxX()),
                            random.nextDouble(box.getMinY(), box.getMaxY()), random.nextDouble(box.getMinZ(), box.getMaxZ()));
                    player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.YELLOW, 1f));
                }
            }

            if(usingPassiveSong == 193) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("MobilileapDisplay" + player.getName())) entity.remove();
                }
            }

            if(usingPassiveSong <= 193 && usingPassiveSong > 0 && player.isOnGround()) {
                usingPassiveSongScore.setScore(0);
                attribute.setBaseValue(3);
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedMobilileap");
            }
        }

        //MOBILIFLASH
        if(player.getScoreboardTags().contains("UsedMobiliflash")) {
            Score usingPassiveSongScore = scoreType(player, "UsingMobiliflash");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                mobiliumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
            }

            if(usingPassiveSong == 201) {
                int power = 18;

                Location pivot = getLocationInFrontOfLoc(player.getLocation(), 0.3f).add(0, 1.3f, 0);
                BlockDisplay display = summonDisplay(pivot, "MobiliflashDisplay" + player.getName(), Material.YELLOW_STAINED_GLASS);

                float blockSize = 0.3f;
                Vector3f size = new Vector3f(blockSize, blockSize, (float) getMaxDistanceInFrontOfPlayer(player, power, true));
                Vector3f translation = new Vector3f(-0.5f * size.x, -0.5f * size.y, 0);
                display.setTransformation(new Transformation(translation, new Quaternionf(), size, new Quaternionf()));

                RayTraceResult result = player.getWorld().rayTraceBlocks(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        power
                );

                if (result == null || result.getHitBlock() == null) {
                    player.teleport(getLocationInFrontOfLoc(player.getLocation(), power));
                } else {
                    Location loc = result.getHitPosition().toLocation(player.getWorld());
                    loc.setRotation(player.getLocation().getRotation());
                    player.teleport(loc);
                }

                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
            }

            if(usingPassiveSong == 198) {
                BoundingBox box = player.getBoundingBox();
                ThreadLocalRandom random = ThreadLocalRandom.current();
                float offset = 0.5f;

                for(int i = 0; i < 20; i++) {
                    Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX() - offset, box.getMaxX() + offset),
                            random.nextDouble(box.getMinY() - offset, box.getMaxY() + offset), random.nextDouble(box.getMinZ() - offset, box.getMaxZ() + offset));
                    Particle.DUST.builder().location(loc).count(0).allPlayers().color(Color.YELLOW).spawn();
                }

                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("MobiliflashDisplay" + player.getName())) entity.remove();
                }
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedMobiliflash");
            }
        }

        //MOBILIBURST
        if(player.getScoreboardTags().contains("UsedMobiliburst")) {
            Score usingPassiveSongScore = scoreType(player, "UsingMobiliburst");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                mobiliumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
            }

            if(usingPassiveSong == 201) {
                int power = 7;
                Entity target = player.getTargetEntity(power);

                RayTraceResult result = player.getWorld().rayTraceBlocks(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        power
                );

                Location center = player.getBoundingBox().getCenter().toLocation(player.getWorld());
                center.setRotation(player.getYaw(), player.getPitch());
                for(int i = 0; i < getMaxDistanceInFrontOfPlayer(player, power, true) * 5; i++) {
                    Location loc = getLocationInFrontOfLoc(player.getLocation(), i / 5f);
                    Particle.DUST.builder().location(loc.add(0, 1.2f, 0)).count(0).allPlayers().color(Color.YELLOW).spawn();
                }

                if (result == null || result.getHitBlock() == null) {
                    player.teleport(getLocationInFrontOfLoc(player.getLocation(), (float) getMaxDistanceInFrontOfPlayer(player, power, true)).add(0, 0.5f, 0));
                } else {
                    Location loc = result.getHitPosition().toLocation(player.getWorld());
                    loc.setRotation(player.getLocation().getRotation());
                    player.teleport(loc.add(0, 0.1f, 0));
                }

                player.getWorld().createExplosion(player.getLocation(), 1, false, false);
                if(target != null) target.setVelocity(player.getLocation().getDirection().multiply(0.8f).setY(0.7f));

                Location pivot = getLocationInFrontOfLoc(player.getLocation(), 0.5f).add(0, 1, 0);
                for(int i = -7; i <= 7; i++) {
                    for(int j = -12; j <= 12; j++) {
                        Location location = getRelativeLocationFromLoc(pivot, i / 10f, j / 10f, 0);
                        Particle.DUST.builder().location(location).count(0).allPlayers().color(Color.YELLOW).spawn();
                    }
                }

                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedMobiliburst");
            }
        }

        //MOBILIWINGS
        if(player.getScoreboardTags().contains("UsedMobiliwings")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                mobiliumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
            }

            if(usingActiveSong == 201) {
                player.setAllowFlight(true);
                player.setVelocity(new Vector(player.getVelocity().getX(), 1.3, player.getVelocity().getZ()));

                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);

                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1, true, false, false));

                Mannequin mannequin = player.getWorld().spawn(player.getLocation(), Mannequin.class);
                mannequin.getScoreboardTags().add("Mobiliwings" + player.getName());
                mannequin.setPose(Pose.FALL_FLYING);
                mannequin.getEquipment().setChestplate(new ItemStack(Material.ELYTRA));
                mannequin.setProfile(playerPatchedCapeProfile(player, "block/gold_block"));
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Mobiliwings" + player.getName())) entity.teleport(player.getLocation());
                }

                Score cooldownScore =  scoreType(player, "YellowEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isOnGround() && player.isSneaking()) usingActiveSongScore.setScore(0);
            }

            if(usingActiveSong == 0) {
                player.setAllowFlight(false);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.getScoreboardTags().remove("UsedMobiliwings");
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Mobiliwings" + player.getName())) entity.remove();
                }
            }
        }

        //MOBILIBOUNCE
        if(player.getScoreboardTags().contains("UsedMobilibounce")) {
            Score mobilibounceDelayScore = scoreType(player, "MobilibouncePlatformDelay");
            int mobilibounceDelay = mobilibounceDelayScore.getScore();
            if(mobilibounceDelay > 0) mobilibounceDelayScore.setScore(mobilibounceDelay - 1);

            Score mobilibounceLaunchDelayScore = scoreType(player, "MobilibounceLaunchDelay");
            int mobilibounceLaunchDelay = mobilibounceLaunchDelayScore.getScore();
            if(mobilibounceLaunchDelay > 0) mobilibounceLaunchDelayScore.setScore(mobilibounceLaunchDelay - 1);

            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                mobiliumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
            }

            if(usingActiveSong == 211) {
                player.setVelocity(new Vector(player.getVelocity().getX(), 0.6, player.getVelocity().getZ()));

                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
            }

            if(usingActiveSong == 201) {
                Location location = player.getLocation().add(0, -1, 0);
                Block targetBlock = location.getBlock();
                if(targetBlock.isEmpty()) {
                    targetBlock.setType(Material.BARRIER);
                    Interaction interaction = player.getWorld().spawn(location.getBlock().getLocation().add(0.5, 0.1, 0.5), Interaction.class);
                    interaction.getScoreboardTags().add("Mobilibounce" + player.getName());

                    BlockDisplay display = summonDisplay(location.getBlock().getLocation().add(0, 0.5f, 0), "MobilibounceDisplay" + player.getName(),
                            Material.YELLOW_STAINED_GLASS);
                    display.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 0.5f, 1), new Quaternionf()));
                }
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                Material standingBlock = player.getLocation().add(0, -0.1, 0).getBlock().getType();
                if(!standingBlock.equals(Material.AIR) && !standingBlock.equals(Material.BARRIER)) {
                    for(Entity entity : getEntities(player)) {
                        if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName())) {
                            if(entity.getLocation().getBlock().getType().equals(Material.BARRIER)) entity.getLocation().getBlock().setType(Material.AIR);
                            entity.teleport(player.getLocation().add(0.5, 0.1, 0.5));
                        }

                        if(entity.getScoreboardTags().contains("MobilibounceDisplay" + player.getName())) entity.remove();
                    }
                }

                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName())) {
                        Location location = entity.getLocation().add(0, 0.5f, 0);
                        for (int i = 0; i < 3; i++) {
                            double x = (Math.random() - 0.5) * 1.5;
                            double y = Math.random() * 0.5;
                            double z = (Math.random() - 0.5) * 1.5;
                            Particle.DUST.builder().location(location.clone().add(x, y, z)).count(1).color(Color.YELLOW).allPlayers().spawn();
                        }
                    }
                }

                if(mobilibounceDelay == 1) {
                    player.setVelocity(new Vector(0, 0, 0));
                    Location location = player.getLocation().add(0, -0.1, 0);
                    Block targetBlock = location.getBlock();
                    if(targetBlock.isEmpty()) targetBlock.setType(Material.BARRIER);

                    Interaction interaction = player.getWorld().spawn(location.getBlock().getLocation().add(0.5, 0.1, 0.5), Interaction.class);
                    interaction.getScoreboardTags().add("Mobilibounce" + player.getName());

                    BlockDisplay display = summonDisplay(location.getBlock().getLocation().add(0, 0.5f, 0), "MobilibounceDisplay" + player.getName(),
                            Material.YELLOW_STAINED_GLASS);
                    display.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 0.5f, 1), new Quaternionf()));

                    player.teleport(targetBlock.getLocation().add(0.5, 1, 0.5).setRotation(player.getYaw(), player.getPitch()));
                }

                if(mobilibounceLaunchDelay == 1) {
                    player.setVelocity(player.getLocation().getDirection());
                }

                Score cooldownScore =  scoreType(player, "YellowEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
            }

            if(usingActiveSong == 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName())) {
                        if(entity.getLocation().getBlock().getType().equals(Material.BARRIER)) entity.getLocation().getBlock().setType(Material.AIR);
                        entity.remove();
                    }
                    if(entity.getScoreboardTags().contains("MobilibounceDisplay" + player.getName())) entity.remove();
                }

                player.getScoreboardTags().remove("UsedMobilibounce");
            }
        }

        /// MOBILIGLIDE
        if(player.getScoreboardTags().contains("UsedMobiliglide")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                mobiliumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
            }

            if(usingActiveSong == 201) {
                speed.setBaseValue(0.17);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1, true, false, false));
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);

                for(int i = 1; i <= 3; i++) {
                    Mannequin mannequin = player.getWorld().spawn(player.getLocation(), Mannequin.class);
                    mannequin.getScoreboardTags().add("Mobiliglide" + i + player.getName());
                    mannequin.setCollidable(false);
                    mannequin.setProfile(patchedPlayerProfile("block/gold_block"));
                    setNonCollidable(mannequin);
                }
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                for(Entity entity : getEntities(player)) {
                    if(entity instanceof Mannequin) {
                        for(String tag : entity.getScoreboardTags()) {
                            if(tag.startsWith("Mobiliglide") && tag.endsWith(player.getName())) {
                                tag = tag.substring("Mobiliglide".length(), tag.length() - player.getName().length());
                                int index = Integer.parseInt(tag);

                                float manSpeed = switch(index) {
                                    case 1 -> -0.40f;
                                    case 2 -> -0.34f;
                                    case 3 -> -0.32f;
                                    default -> 0.0f;
                                };

                                entity.setVelocity(entityDistanceVector(player, entity).multiply(manSpeed));
                                entity.setRotation(player.getYaw(), player.getPitch());
                            }
                        }
                    }
                }

                Score cooldownScore =  scoreType(player, "YellowEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) {
                    usingActiveSongScore.setScore(0);
                    player.removePotionEffect(PotionEffectType.SLOW_FALLING);
                }
            }

            if(usingActiveSong == 0) {
                speed.setBaseValue(0.1);
                player.getScoreboardTags().remove("UsedMobiliglide");
                for(Entity entity : getNearbyEntities(player.getLocation(), 9)) {
                    if(entity instanceof Mannequin) {
                        for(String tag : entity.getScoreboardTags()) {
                            if(tag.startsWith("Mobiliglide") && tag.endsWith(player.getName())) entity.remove();
                        }
                    }
                }
            }
        }

        //--BLUE SONGS--//

        //PROTEHEAL
        if(player.getScoreboardTags().contains("UsedProteheal")) {
            Score usingPassiveSongScore = scoreType(player, "UsingProteheal");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                protisiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingPassiveSong == 201) {
                Entity target = player.getTargetEntity(7, false);
                int amount = 20;
                if(target instanceof Player player2) {
                    player2.heal(amount);
                    player2.playSound(player2.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 100f, 0.8f);
                } else {
                    player.heal(amount);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 100f, 0.8f);
                }

            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedProteheal");
            }
        }

        //PROTEARMOR
        if(player.getScoreboardTags().contains("UsedProtearmor")) {
            AttributeInstance attack = player.getAttribute(Attribute.ATTACK_DAMAGE);
            AttributeInstance armor = player.getAttribute(Attribute.ARMOR);
            AttributeInstance scale = player.getAttribute(Attribute.SCALE);
            assert attack != null;
            assert armor != null;
            assert speed != null;

            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                protisiumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingActiveSong == 201) {
                speed.setBaseValue(0);
                jump.setBaseValue(0);
                attack.setBaseValue(0);
                armor.setBaseValue(999);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 100f, 0.8f);

                Mannequin mannequin = player.getWorld().spawn(player.getLocation(), Mannequin.class);
                mannequin.getScoreboardTags().add("Protearmor" + player.getName());
                mannequin.setImmovable(true);
                mannequin.setCollidable(false);

                scale.setBaseValue(1.4f);

                mannequin.setProfile(patchedPlayerProfile("block/lapis_block"));

                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1, true, false, false));
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                Score cooldownScore = scoreType(player, "BlueEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
            }

            if(usingActiveSong == 0) {
                speed.setBaseValue(0.1f);
                jump.setBaseValue(0.42);
                attack.setBaseValue(1);
                armor.setBaseValue(0);
                scale.setBaseValue(1);
                player.getScoreboardTags().remove("UsedProtearmor");
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Protearmor" + player.getName())) entity.remove();
                }
            }
        }

        //PROTESPHERE
        if(player.getScoreboardTags().contains("UsedProtesphere")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                protisiumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 7, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                Location center = getCenter(player);
                float radius = 1.3f;
                int points = 100;
                for (int i = 0; i < points; i++) {
                    double theta = Math.random() * Math.PI * 2;
                    double phi = Math.acos(2 * Math.random() - 1);
                    double x = radius * Math.sin(phi) * Math.cos(theta);
                    double y = radius * Math.sin(phi) * Math.sin(theta);
                    double z = radius * Math.cos(phi);
                    center.getWorld().spawnParticle(Particle.DUST, center.clone().add(x, y, z), 0, new Particle.DustOptions(Color.fromRGB(71, 110, 253), 0.6f));
                    Particle.DUST.builder().location(center.clone().add(x, y, z)).receivers(allExceptPlayer(player)).data(new Particle.DustOptions(Color.fromRGB(71, 110, 253), 1.5f)).spawn();
                }

                for(Entity entity : getEntities(player)) {
                    if(!(entity instanceof BlockDisplay) && player.getBoundingBox().overlaps(entity.getBoundingBox())) {
                        if(entity instanceof LivingEntity living && entity != player) living.setVelocity(entityDistanceVector(player, entity).setY(0.6));
                    }
                }

                Score cooldownScore = scoreType(player, "BlueEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
                for(Entity entity : getEntities(player)) {
                    if(player.getLocation().distance(entity.getLocation()) <= 1.5f && entity instanceof Arrow arrow) arrow.remove();
                }
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedProtesphere");
            }
        }

        //PROTEPOINT
        if(player.getScoreboardTags().contains("UsedProtepoint")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                protisiumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 7, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingActiveSong == 201) {
                Location location = player.getLocation();
                Marker marker = player.getWorld().spawn(getLocationInFrontOfLoc(location.add(0, 1.5f, 0), 0.7f), Marker.class);
                marker.setRotation(location.getYaw(), 0);
                marker.getScoreboardTags().add("ProtepointMarker" + player.getName());
                Interaction interaction = player.getWorld().spawn(marker.getLocation().add(0, -0.5, 0), Interaction.class);
                interaction.getScoreboardTags().add("ProtepointInteraction" + player.getName());
                BlockDisplay display = summonDisplay(location, "ProtepointDisplay" + player.getName(), Material.CYAN_STAINED_GLASS);
                display.setBrightness(new Display.Brightness(15, 15));
                display.teleport(marker.getLocation().setDirection(player.getLocation().getDirection()));
                display.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 0.4f), new Quaternionf()));
                display.setRotation(display.getYaw(), 0);
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                for(Entity marker : getEntities(player)) {
                    if(marker.getScoreboardTags().contains("ProtepointMarker" + player.getName())) {
                        Location loc = player.getLocation().add(0, 1.5f, 0);
                        loc.setRotation(marker.getYaw(), 0);
                        marker.teleport(getLocationInFrontOfLoc(loc, 0.7f));
                        for(Entity interaction : getEntities(player)) {
                            if(interaction.getScoreboardTags().contains("ProtepointInteraction" + player.getName())) {
                                interaction.teleport(marker.getLocation().add(0, -0.5, 0));
                                for(Entity entity3 : getEntities(player)) {
                                    if(!entity3.getScoreboardTags().contains("ProtepointMarker" + player.getName()) &&
                                            !entity3.getScoreboardTags().contains("ProtepointInteraction" + player.getName())) {
                                        if(entity3.getBoundingBox().overlaps(interaction.getBoundingBox()) && isAnEntityItem(entity3)) entity3.remove();
                                    }
                                }
                            }
                        }

                        for(Entity entity : getEntities(player)) {
                            if(entity.getScoreboardTags().contains("ProtepointDisplay" + player.getName()) && entity instanceof BlockDisplay display) {
                                display.teleport(marker.getLocation().setDirection(display.getLocation().getDirection()));
                                Vector3f size = new Vector3f(1, 1, 0.4f);
                                Vector3f offset = new Vector3f(size.x * -0.5f, size.y * -0.5f, size.z * -0.5f);
                                display.setTransformation(new Transformation(offset, new Quaternionf(), size, new Quaternionf()));
                            }
                        }
                    }
                }

                Score cooldownScore = scoreType(player, "BlueEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedProtepoint");
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("ProtepointMarker" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("ProtepointInteraction" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("ProtepointDisplay" + player.getName())) entity.remove();
                }
            }
        }

        //PROTECLONE
        if(player.getScoreboardTags().contains("UsedProteclone")) {
            Score usingPassiveSongScore = scoreType(player, "UsingProteclone");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                protisiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                playSoundToNearby(player.getLocation(), 7, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingPassiveSong == 201) {
                Mannequin mannequin = player.getWorld().spawn(player.getLocation(), Mannequin.class);
                mannequin.getScoreboardTags().add("Proteclone" + player.getName());
                mannequin.setImmovable(true);
                setNonCollidable(mannequin);

                mannequin.setProfile(patchedPlayerProfile("block/lapis_block"));

                player.setVelocity(player.getLocation().getDirection().multiply(-0.3f).setY(0.3));
            }

            if(usingPassiveSong == 161) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Proteclone" + player.getName())) entity.remove();
                }
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedProteclone");
            }
        }

        //PROTEBARRIER
        if(player.getScoreboardTags().contains("UsedProtebarrier")) {
            Score usingPassiveSongScore = scoreType(player, "UsingProtebarrier");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                protisiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                playSoundToNearby(player.getLocation(), 7, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingPassiveSong == 201) {
                Block targetBlock = player.getTargetBlock(null, 10);
                Location spawnLocation = null;
                if(targetBlock.isEmpty()) {
                    Location location = targetBlock.getLocation();
                    for(int i = 1; i < 5; i++) {
                        if(location.add(0, -i, 0).getBlock().isSolid()) {
                            spawnLocation = location.add(0, -i + 1, 0);
                            break;
                        }
                    }
                } else {
                    spawnLocation = targetBlock.getLocation();
                }

                if(spawnLocation == null) {
                    player.sendMessage("Barrier position nulled. Refunding!");
                    usingPassiveSongScore.setScore(0);
                    Score blueCooldownScore = scoreType(player, "BlueEnergyCooldown");
                    blueCooldownScore.setScore(0);
                    Score songEnergyScore = scoreType(player, "SongEnergy");
                    songEnergyScore.setScore(songEnergyScore.getScore() + 1);
                } else {
                    assert spawnLocation != null;
                    for(int i = 0; i <= 8; i++) {
                        Location location = perspectiveOffset(player, spawnLocation, i - 4);
                        Interaction interaction = player.getWorld().spawn(location, Interaction.class);
                        interaction.getScoreboardTags().add("Protebarrier" + player.getName());
                        interaction.setInteractionHeight(7);

                    }

                    spawnLocation.setRotation(player.getYaw(), 0);
                    spawnLocation = getLocationInFrontOfLoc(spawnLocation, -1);

                    BlockDisplay display = summonDisplay(spawnLocation.add(0, -7, 0), "ProtebarrierDisplay" + player.getName(), Material.CYAN_STAINED_GLASS);
                    display.setBrightness(new Display.Brightness(15, 15));

                    Vector3f size = new Vector3f(9, 7, 0.7f);
                    Vector3f translation = new Vector3f(size.x * -0.5f, size.y, size.z);
                    display.setTransformation(new Transformation(translation, new Quaternionf(), size, new Quaternionf()));
                }
            }

            if(usingPassiveSong > 0 && usingPassiveSong <= 200) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Protebarrier" + player.getName())) {
                        for(Entity entity2 : getEntities(player)) {
                            if(!entity2.getScoreboardTags().contains("Protebarrier" + player.getName()) && entity.getBoundingBox().overlaps(entity2.getBoundingBox())) {
                                if(isAnEntityItem(entity2)) {
                                    entity2.remove();
                                } else if(entity2 instanceof LivingEntity living) {
                                    living.setVelocity(entityDistanceVector(entity, entity2).setY(0.6));
                                }
                            }
                        }
                    }
                }

                if(player.isSneaking()) {
                    usingPassiveSongScore.setScore(0);
                    for(Entity entity : getEntities(player)) {
                        if(entity.getScoreboardTags().contains("Protebarrier" + player.getName())) entity.remove();
                    }
                }
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedProtebarrier");
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Protebarrier" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("ProtebarrierDisplay" + player.getName())) entity.remove();
                }
            }
        }

        //--RED SONGS--//

        //AGGROSPHERE
        if(player.getScoreboardTags().contains("UsedAggrosphere")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggrosphere");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                aggressiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingPassiveSong == 201) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);

                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("Aggrosphere" + player.getName());
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);

                BlockDisplay display = summonDisplay(location, "AggrosphereDisplay" + player.getName(), Material.MAGMA_BLOCK);
                BlockDisplay display2 = summonDisplay(location, "AggrosphereDisplay2" + player.getName(), Material.RED_STAINED_GLASS);

            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 0) {
                for(Entity entity : getEntities(player)) {
                    if (entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) {
                        entity.teleport(getLocationInFrontOfLoc(entity.getLocation(), 0.6f));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : getEntities(player)) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player &&
                                    !(entity2 instanceof BlockDisplay)) {
                                collidedEntity = entity2;
                            }
                        }

                        if((collidedEntity != null) || centerLocation.getBlock().isSolid()) {
                            if(collidedEntity instanceof LivingEntity living) {
                                living.damage(32, entity);
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(9999, living);
                                }
                            }

                            player.getWorld().createExplosion(centerLocation, 0.5f, false, false);

                            for(Player player2 : Bukkit.getOnlinePlayers()) {
                                if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                    Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                    supporokenisisPlayerActiveScore.setScore(0);
                                }
                            }

                            usingPassiveSongScore.setScore(0);
                        }

                        for(Entity entity2 : getEntities(player)) {
                            if((entity2.getScoreboardTags().contains("AggrosphereDisplay" + player.getName()) || entity2.getScoreboardTags().contains("AggrosphereDisplay2" + player.getName())) && entity2 instanceof BlockDisplay display) {
                                float turningSpeed = entity2.getScoreboardTags().contains("AggrosphereDisplay" + player.getName()) ? 3.6f : 1.2f;
                                float size = entity2.getScoreboardTags().contains("AggrosphereDisplay" + player.getName()) ? 0.45f : 0.7f;

                                Location pivot = entity.getBoundingBox().getCenter().toLocation(player.getWorld());
                                display.teleport(pivot);

                                Quaternionf rotation = new Quaternionf().rotateXYZ((float) Math.toRadians(usingPassiveSong) * turningSpeed, (float) Math.toRadians(usingPassiveSong) * turningSpeed, (float) Math.toRadians(usingPassiveSong) * turningSpeed);
                                Vector3f centerOffset = new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size);

                                rotation.transform(centerOffset);
                                display.setTransformation(new Transformation(centerOffset, new Quaternionf(), new Vector3f(size, size, size), rotation));

                                if(entity2.getScoreboardTags().contains("AggrosphereDisplay2" + player.getName())) {
                                    BoundingBox box = entity2.getBoundingBox();
                                    ThreadLocalRandom random = ThreadLocalRandom.current();
                                    float offset = 0.5f;

                                    for(int i = 0; i < 10; i++) {
                                        Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX() - offset, box.getMaxX() + offset),
                                                random.nextDouble(box.getMinY() - offset, box.getMaxY() + offset), random.nextDouble(box.getMinZ() - offset, box.getMaxZ() + offset));
                                        Particle.DUST.builder().location(getLocationInFrontOfLoc(loc.setDirection(entity.getLocation().getDirection()), 1)).count(0).allPlayers().color(i % 2 == 0 ? Color.RED : Color.ORANGE).spawn();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(usingPassiveSong == 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggrosphereDisplay" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggrosphereDisplay2" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggrosphere");
                }
            }
        }

        //AGGROBEAM
        if(player.getScoreboardTags().contains("UsedAggrobeam")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                aggressiumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingActiveSong == 201) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);

                Location pivot = player.getBoundingBox().getCenter().toLocation(player.getWorld());
                BlockDisplay display = summonDisplay(pivot, "AggrobeamDisplay" + player.getName(), Material.MAGMA_BLOCK);
                BlockDisplay display2 = summonDisplay(pivot, "AggrobeamDisplay2" + player.getName(), Material.RED_STAINED_GLASS);
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                Entity target = player.getTargetEntity(18, false);
                if(target instanceof LivingEntity living && !isAnEntityItem(living)) {
                    living.damage(5, player);
                }

                Score cooldownScore = scoreType(player, "RedEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) {
                    usingActiveSongScore.setScore(0);
                    speed.setBaseValue(0.1);
                    jump.setBaseValue(0.42);
                }

                for(Entity entity : getEntities(player)) {
                    if((entity.getScoreboardTags().contains("AggrobeamDisplay" + player.getName()) || entity.getScoreboardTags().contains("AggrobeamDisplay2" + player.getName())) && entity instanceof BlockDisplay display) {
                        Location pivot = getLocationInFrontOfLoc(player.getLocation(), 0.3f).add(0, 1.3f, 0);
                        float blockSize = entity.getScoreboardTags().contains("AggrobeamDisplay" + player.getName()) ? 0.25f : 0.3f;
                        Vector3f size = new Vector3f(blockSize, blockSize, (float) getMaxDistanceInFrontOfPlayer(player, 18, true));
                        Vector3f translation = new Vector3f(-0.5f * size.x, -0.5f * size.y, 0);
                        display.teleport(pivot);
                        display.setTransformation(new Transformation(translation, new Quaternionf(), size, new Quaternionf()));
                    }
                }
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedAggrobeam");
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("AggrobeamDisplay" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggrobeamDisplay2" + player.getName())) entity.remove();
                }
            }
        }

        //AGGROQUAKE
        if(player.getScoreboardTags().contains("UsedAggroquake")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggroquake");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                aggressiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0);
                jump.setBaseValue(0);
            }

            if(usingPassiveSong == 201) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
                player.getWorld().createExplosion(player.getLocation(), 3.8f, false, false);

                BoundingBox box = player.getBoundingBox();
                Location location = new Location(player.getWorld(), box.getCenterX(), box.getMinY(), box.getCenterZ());
                BlockDisplay display = summonDisplay(location, "AggroquakeDisplay" + player.getName(), Material.MAGMA_BLOCK);

                Vector3f size = new Vector3f(6f, 0.4f, 6f);
                Vector3f translation = new Vector3f(-0.5f * size.x, -0.5f * size.y, -0.5f * size.z);
                display.setTransformation(new Transformation(translation, new Quaternionf(), size, new Quaternionf()));

                for(int i = -10; i <= 10; i++) {
                    for(int j = -10; j <= 10; j++) {
                        Location loc = player.getBoundingBox().getCenter().toLocation(player.getWorld()).add(i / 10f * 6f, -0.7f, j / 10f * 6f);
                        player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.RED, 0.7f));
                    }
                }
            }

            if(usingPassiveSong >= 185 && usingPassiveSong <= 200) {
                Location center = getCenter(player);
                float index = (202 - usingPassiveSong) / 2f;
                float radius = index / 2.2f;

                for (int i = 0; i < index * 40; i++) {
                    double theta = Math.random() * Math.PI * 2;
                    double phi = Math.acos(2 * Math.random() - 1);

                    double x = radius * Math.sin(phi) * Math.cos(theta);
                    double y = radius * Math.sin(phi) * Math.sin(theta);
                    double z = radius * Math.cos(phi);

                    double topThreshold = radius * 0.4;
                    if (y > topThreshold) continue;

                    center.getWorld().spawnParticle(Particle.DUST, center.clone().add(x, y, z), 5, new Particle.DustOptions(Color.RED, 1.2f));
                }
            }

            if(usingPassiveSong <= 155) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("AggroquakeDisplay" + player.getName())) entity.remove();
                }
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedAggroquake");
            }
        }

        //AGGROBLAST
        if(player.getScoreboardTags().contains("UsedAggroblast")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggroblast");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                aggressiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingPassiveSong == 201) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
                double distanceFromBarrier = 9999;
                for(Entity entity : getNearbyEntities(player.getLocation(), 5)) {
                    for(String tag : entity.getScoreboardTags()) {
                        if((tag.startsWith("Protebarrier") || tag.startsWith("Protepoint")) && tag.length() > 12 && player.getLocation().distance(entity.getLocation()) <= 4) {
                            double d = player.getLocation().distance(entity.getLocation());
                            if(d < distanceFromBarrier) distanceFromBarrier = d;
                        }
                    }
                }

                for(Entity entity : getNearbyEntities(player.getLocation(), 5)) {
                    if (entity instanceof LivingEntity living && aggroblastSightHelper(player, living) && player.hasLineOfSight(entity)
                            && living != player && player.getLocation().distance(entity.getLocation()) < distanceFromBarrier) {
                        double distance = player.getLocation().distance(living.getLocation());
                        living.setVelocity(entityDistanceVector(player, entity).multiply(1.4 - distance / 3));
                        living.damage(40 - distance * 5, player);
                    }
                }

                for(int j = 1; j < 4 * 4; j++) {
                    float maxDistance = (float) getMaxDistanceInFrontOfPlayer(player, j / 4f, false);
                    if(distanceFromBarrier < maxDistance) maxDistance = (float) distanceFromBarrier;
                    Location center = getLocationInFrontOfLoc(player.getLocation(), maxDistance);
                    float radius = 0.4f + (j-1) / 15f;
                    int points = (j <= 5) ? 20 : j * 5;

                    Vector forward = player.getEyeLocation().getDirection().normalize();
                    Vector up = new Vector(0, 1, 0);
                    if (Math.abs(forward.dot(up)) > 0.99) up = new Vector(1, 0, 0);

                    Vector right = forward.clone().crossProduct(up).normalize();
                    up = right.clone().crossProduct(forward).normalize();

                    for (int i = 0; i < points; i++) {
                        double angle = 2 * Math.PI * i / points;
                        Vector offset = right.clone().multiply(Math.cos(angle) * radius).add(up.clone().multiply(Math.sin(angle) * radius));
                        Location loc = center.clone().add(offset).add(up);
                        player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.RED, 1f));
                        if(i % 2 == 0) player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions(Color.fromRGB(255, 104, 34), 1f));
                    }
                }

                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedAggroblast");
            }
        }

        //AGGROVORTEX
        if(player.getScoreboardTags().contains("UsedAggrovortex")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggrovortex");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                aggressiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingPassiveSong == 201) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("Aggrovortex" + player.getName());
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);

                for(int i = 0; i < 9; i++) {
                    BlockDisplay display = summonDisplay(getLocationInFrontOfLoc(player.getLocation(), i), "AggrovortexDisplay" + i + player.getName(),
                            Material.MAGMA_BLOCK);
                    BlockDisplay display2 = summonDisplay(getLocationInFrontOfLoc(player.getLocation(), i), "AggrovortexDisplayOut" + i + player.getName(),
                            aggrovortexHelper(i).getRight() ? Material.RED_STAINED_GLASS : Material.ORANGE_STAINED_GLASS);
                }
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 80) {
                for(Entity entity : getEntities(player)) {
                    if (entity.getScoreboardTags().contains("Aggrovortex" + player.getName())) {
                        entity.teleport(getLocationInFrontOfLoc(entity.getLocation(), 0.5f));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : getEntities(player)) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player) {
                                collidedEntity = entity2;
                            }
                        }

                        if((collidedEntity != null) || centerLocation.getBlock().isSolid()) {
                            if(collidedEntity instanceof LivingEntity living) {
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(336 / 2, living);
                                } else {
                                    living.damage(20, entity);
                                    player.getWorld().createExplosion(collidedEntity.getLocation().add(0, 1, 0), 0.5f, false, false);
                                }
                            } else {
                                player.getWorld().createExplosion(centerLocation, 0.5f, false, false);
                            }

                            for(Player player2 : Bukkit.getOnlinePlayers()) {
                                if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                    Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                    supporokenisisPlayerActiveScore.setScore(0);
                                }
                            }

                            usingPassiveSongScore.setScore(0);
                        }

                        for(Entity entity2 : getEntities(player)) {
                            if (!(entity2 instanceof BlockDisplay display)) continue;
                            for (String tag : entity2.getScoreboardTags()) {
                                if (tag.startsWith("AggrovortexDisplay") && tag.endsWith(player.getName())) {
                                    int index;
                                    boolean outer = false;

                                    String tempTag = tag;
                                    tempTag = tempTag.substring("AggrovortexDisplay".length(), tag.length() - player.getName().length());
                                    if (tempTag.startsWith("Out")) {
                                        tempTag = tempTag.substring("Out".length());
                                        outer = true;
                                    }

                                    index = Integer.parseInt(tempTag);

                                    float turningSpeed = 1.5f + (pseudoRandom(index) % 10) / 2f;
                                    float size = aggrovortexHelper(index).getLeft();
                                    if (outer) size += 0.25f;

                                    Location pivot = getLocationInFrontOfLoc(entity.getLocation(), -index / 2f).add(0, 1, 0);
                                    display.teleport(pivot);

                                    Quaternionf rotation = new Quaternionf().rotateZ((float) Math.toRadians(usingPassiveSong) * turningSpeed);
                                    Vector3f centerOffset = new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size);

                                    rotation.transform(centerOffset);
                                    display.setTransformation(new Transformation(centerOffset, new Quaternionf(), new Vector3f(size, size, outer ? 0.4f : 0.2f), rotation));
                                }
                            }
                        }
                    }
                }
            }

            if(usingPassiveSong >= 0 && usingPassiveSong <= 80) {
                player.getScoreboardTags().remove("UsedAggrovortex");
                if(usingPassiveSong > 0) usingPassiveSongScore.setScore(0);
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Aggrovortex" + player.getName())) entity.remove();
                    for(String tag : entity.getScoreboardTags()) {
                        if(tag.startsWith("AggrovortexDisplay") && tag.endsWith(player.getName())) entity.remove();
                    }
                }
            }
        }

        //AGGROSHARD
        if(player.getScoreboardTags().contains("UsedAggroshard")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggroshard");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if (usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                aggressiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingPassiveSong == 183) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
            }

            if(usingPassiveSong <= 201 && usingPassiveSong >= 183 && usingPassiveSong % 3 == 0) {
                int random = Random.from(RandomGenerator.getDefault()).nextInt(-10, 10);
                Location location = player.getLocation();
                if(usingPassiveSong != 201) location.setRotation(player.getYaw() + random, player.getPitch() + (float) random / 4);

                ArmorStand stand = player.getWorld().spawn(location, ArmorStand.class);
                stand.setGravity(false);
                stand.setVisibleByDefault(false);
                stand.getScoreboardTags().add("Aggroshard" + player.getName());
                playSoundToNearby(player.getLocation(), 10, Sound.ITEM_CROSSBOW_SHOOT, SoundCategory.MASTER, 100f, 0.35f);

                Location pivot = stand.getBoundingBox().getCenter().toLocation(player.getWorld());

                BlockDisplay display = summonDisplay(pivot, "AggroshardDisplay" + player.getName(), Material.MAGMA_BLOCK);
                BlockDisplay display2 = summonDisplay(pivot, "AggroshardDisplay2" + player.getName(), Material.RED_STAINED_GLASS);
            }

            if(usingPassiveSong > 0 && usingPassiveSong <= 200) {
                for(Entity entity : getEntities(player)) {
                    if (entity.getScoreboardTags().contains("Aggroshard" + player.getName())) {
                        entity.teleport(getLocationInFrontOfLoc(entity.getLocation(), 1.4f));
                        Location centerLocation = getCenter(entity);

                        Entity collidedEntity = null;
                        for(Entity entity2 : getEntities(player)) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player &&
                                    (entity2 instanceof BlockDisplay)) {
                                collidedEntity = entity2;
                            }
                        }

                        if((collidedEntity != null) || centerLocation.getBlock().isSolid()) {
                            if(collidedEntity instanceof LivingEntity living) {
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(30, living);
                                } else {
                                    living.damage(40);
                                }
                            }

                            for(Player player2 : Bukkit.getOnlinePlayers()) {
                                if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                    Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                    supporokenisisPlayerActiveScore.setScore(0);
                                }
                            }
                        }

                        for(Entity entity2 : getEntities(player)) {
                            if((entity2.getScoreboardTags().contains("AggroshardDisplay" + player.getName()) ||
                                    entity2.getScoreboardTags().contains("AggroshardDisplay2" + player.getName())) &&
                                    entity2 instanceof BlockDisplay display && getCenter(entity).distance(getCenter(entity2)) <= 1.4f) {

                                Vector3f size = new Vector3f(0.2f, 0.2f, 0.4f);
                                if(entity2.getScoreboardTags().contains("AggroshardDisplay2" + player.getName())) size = size.mul(1.4f);

                                Vector3f translation = new Vector3f(size.x * -0.5f, size.y * -0.5f, size.z * -0.5f);

                                display.setTransformation(new Transformation(translation, new Quaternionf(), size, new Quaternionf()));
                                display.teleport(entity.getBoundingBox().getCenter().toLocation(player.getWorld()));
                                display.setRotation(entity.getYaw(), entity.getPitch());
                            }
                        }

                        Particle.DUST_COLOR_TRANSITION.builder().location(centerLocation.clone()).count(0).allPlayers().colorTransition(Color.RED, Color.fromARGB(0, 255, 0, 0)).spawn();
                    }
                }
            }

            if(usingPassiveSong == 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Aggroshard" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggroshardDisplay" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggroshardDisplay2" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggroshard");
                }
            }
        }

        //AGGRODETONATE
        if(player.getScoreboardTags().contains("UsedAggrodetonate")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggrodetonate");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                aggressiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingPassiveSong == 201) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("Aggrodetonate" + player.getName());
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);

                Location pivot = player.getBoundingBox().getCenter().toLocation(player.getWorld());
                BlockDisplay display = summonDisplay(pivot, "AggrodetonateDisplay" + player.getName(), Material.MAGMA_BLOCK);
                BlockDisplay display2 = summonDisplay(pivot, "AggrodetonateDisplay2" + player.getName(), Material.RED_STAINED_GLASS);
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 0) {
                for(Entity entity : getEntities(player)) {
                    if (entity.getScoreboardTags().contains("Aggrodetonate" + player.getName())) {
                        entity.setRotation(entity.getLocation().getYaw(), entity.getLocation().getPitch() + 0.8f);
                        entity.teleport(getLocationInFrontOfLoc(entity.getLocation(), 0.8f));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        for(Entity entity2 : getEntities(player)) {
                            if((entity2.getScoreboardTags().contains("AggrodetonateDisplay" + player.getName()) || entity2.getScoreboardTags().contains("AggrodetonateDisplay2" + player.getName())) && entity2 instanceof BlockDisplay display) {
                                Location pivot = entity.getBoundingBox().getCenter().toLocation(player.getWorld());
                                float blockSize = entity2.getScoreboardTags().contains("AggrodetonateDisplay" + player.getName()) ? 0.25f : 0.3f;
                                Vector3f size = new Vector3f(blockSize, blockSize, 2f);
                                Vector3f translation = new Vector3f(-0.5f * size.x, -0.5f * size.y, -0.5f * size.z);
                                display.teleport(pivot.setDirection(entity.getLocation().getDirection()));
                                display.setTransformation(new Transformation(translation, new Quaternionf(), size, new Quaternionf()));

                                if(entity2.getScoreboardTags().contains("AggrodetonateDisplay2" + player.getName())) {
                                    BoundingBox box = entity2.getBoundingBox();
                                    ThreadLocalRandom random = ThreadLocalRandom.current();
                                    float offset = 0.5f;
                                    for(int i = 0; i < 10; i++) {
                                        Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX() - offset, box.getMaxX() + offset),
                                                random.nextDouble(box.getMinY() - offset, box.getMaxY() + offset), random.nextDouble(box.getMinZ() - offset, box.getMaxZ() + offset));
                                        Particle.DUST.builder().location(getLocationInFrontOfLoc(loc.setDirection(entity.getLocation().getDirection()), 1)).count(0).allPlayers().color(i % 2 == 0 ? Color.RED : Color.ORANGE).spawn();
                                    }
                                }
                            }
                        }

                        Entity collidedEntity = null;
                        for(Entity entity2 : getEntities(player)) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player &&
                                    !(entity2 instanceof BlockDisplay)) {
                                collidedEntity = entity2;
                            }
                        }

                        if((collidedEntity != null) || centerLocation.getBlock().isSolid()) {
                            if(collidedEntity instanceof LivingEntity living) {
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(9999, living);
                                } else {
                                    living.damage(30, entity);
                                    player.getWorld().createExplosion(collidedEntity.getLocation().add(0, -1, 0), 0.5f, false, false);
                                }
                            } else {
                                player.getWorld().createExplosion(centerLocation, 0.5f, false, false);
                            }

                            for(Player player2 : Bukkit.getOnlinePlayers()) {
                                if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                    Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                    supporokenisisPlayerActiveScore.setScore(0);
                                }
                            }

                            usingPassiveSongScore.setScore(0);
                        }
                    }
                }
            }

            if(usingPassiveSong == 0) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Aggrodetonate" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggrodetonateDisplay" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggrodetonateDisplay2" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggrodetonate");
                }
            }
        }

        //AGGROSTORM
        if(player.getScoreboardTags().contains("UsedAggrostorm")) {
            if(usingActiveSong >= 201 && usingActiveSong <= 230) {
                aggressiumCharge(player, usingActiveSong);
            }

            if(usingActiveSong == 229) {
                speed.setBaseValue(0.01);
                jump.setBaseValue(0.1);
            }

            if(usingActiveSong == 201) {
                jump.setBaseValue(0);
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                for(Entity entity : getNearbyEntities(player.getLocation(), 9)) {
                    if(entity instanceof LivingEntity living && living != player) {
                        living.setVelocity(living.getVelocity().add(entityDistanceVector(player, entity).multiply(-0.05)));
                        double distance = player.getLocation().distance(living.getLocation());
                        if(7 - distance > 0) living.damage(7 - distance);
                    }
                }

                Score cooldownScore = scoreType(player, "RedEnergyCooldown");
                cooldownScore.setScore(200);

                for(int i = 0; i < 6; i++) {
                    float degrees = (usingActiveSong / 2f + i * 20) + ((i % 2 == 0) ? 180 : 0);
                    float radius = (pseudoRandom(i) % 10) / 8f + 0.3f;

                    BoundingBox box = player.getBoundingBox();
                    Location center = new Location(player.getWorld(), box.getCenterX(), box.getMinY() + 0.3f, box.getCenterZ());

                    Location loc = center.clone().add(Math.cos(degrees) * radius, i / 3f, Math.sin(degrees) * radius);

                    player.getWorld().spawnParticle(Particle.DUST, loc, 3, new Particle.DustOptions(Color.RED, (pseudoRandom(i) % 10) / 10f));
                }

                if(player.isSneaking()) {
                    usingActiveSongScore.setScore(0);
                    speed.setBaseValue(0.1);
                    jump.setBaseValue(0.42);
                }
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedAggrostorm");
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
            }
        }

        //AGGROSHOCK
        if(player.getScoreboardTags().contains("UsedAggroshock")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggroshock");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong >= 201 && usingPassiveSong <= 230) {
                aggressiumCharge(player, usingPassiveSong);
            }

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingPassiveSong == 201) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);

                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("AggroshockProjectile" + player.getName());
            }

            if(usingPassiveSong > 180 && usingPassiveSong <= 200) {
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("AggroshockProjectile" + player.getName())) {
                        entity.teleport(getLocationInFrontOfLoc(entity.getLocation(), 1));
                        for(Entity entity2 : getEntities(player)) {
                            if(entity2 != entity && entity2 != player && entity2 instanceof LivingEntity) {
                                if(entity.getBoundingBox().overlaps(entity2.getBoundingBox())) {
                                    entity.remove();
                                    playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
                                    if(entity2 instanceof Player player2 && player2.getActiveItem().getType().equals(Material.SHIELD)) {
                                        ItemStack stack = player2.getActiveItem();
                                        stack.damage(9999, player);
                                    } else {
                                        entity2.getScoreboardTags().add("Aggroshock" + player.getName());
                                        entity2.getScoreboardTags().add("AggroshockVictim1");
                                    }
                                }
                            }
                        }

                        double radius = 0.4;
                        for (int i = 0; i < 20; i++) {
                            double theta = Math.random() * Math.PI * 2;
                            double phi = Math.acos(2 * Math.random() - 1);
                            double x = radius * Math.sin(phi) * Math.cos(theta);
                            double y = radius * Math.sin(phi) * Math.sin(theta);
                            double z = radius * Math.cos(phi);
                            Particle.DUST.builder().location(entity.getLocation().clone().add(x, y + 1, z)).count(0).allPlayers().color((i % 2 == 0) ? Color.RED : Color.ORANGE).spawn();
                        }
                    }



                    if(entity.getScoreboardTags().contains("Aggroshock" + player.getName()) && entity != player && entity instanceof LivingEntity living) {
                        BoundingBox box = entity.getBoundingBox();
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        for(int i = 0; i < 10; i++) {
                            Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX(), box.getMaxX()),
                                    random.nextDouble(box.getMinY(), box.getMaxY()), random.nextDouble(box.getMinZ(), box.getMaxZ()));
                            player.getWorld().spawnParticle(Particle.DUST, loc, 0, new Particle.DustOptions((i % 2 == 0) ? Color.RED : Color.ORANGE, 0.7f));
                        }

                        living.damage(20);

                        if(entity.getScoreboardTags().contains("AggroshockVictim1") && !entity.getScoreboardTags().contains("AggroshockTransfered")) {
                            entity.getScoreboardTags().add("AggroshockTransfered");
                            Entity entity2 = getClosestEntity(entity, 3, null);
                            if(entity2 instanceof LivingEntity && entity2 != player) {
                                entity2.getScoreboardTags().add("Aggroshock" + player.getName());
                                entity2.getScoreboardTags().add("AggroshockVictim2");
                                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_BEE_HURT, SoundCategory.MASTER, 100f, 0.35f);

                                Location location = entity.getLocation().clone().add(0, 1, 0).setDirection(entityDistanceVector(entity, entity2));
                                double distance = entity.getLocation().distance(entity2.getLocation());
                                for(int i = 0; i < distance * 5; i++) {
                                    Particle.DUST.builder().location(getLocationInFrontOfLoc(location, (float) i / 5)).count(2).allPlayers().color(Color.RED).spawn();
                                }
                            }
                        }

                        if(entity.getScoreboardTags().contains("AggroshockVictim2") && !entity.getScoreboardTags().contains("AggroshockTransfered")) {
                            entity.getScoreboardTags().add("AggroshockTransfered");
                            Entity entity2 = getClosestEntity(entity, 3, "AggroshockVictim1");
                            if(entity2 instanceof LivingEntity && entity2 != player) {
                                entity2.getScoreboardTags().add("Aggroshock" + player.getName());
                                entity2.getScoreboardTags().add("AggroshockVictim3");
                                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_BEE_HURT, SoundCategory.MASTER, 100f, 0.35f);

                                Location location = entity.getLocation().clone().add(0, 1, 0).setDirection(entityDistanceVector(entity, entity2));
                                double distance = entity.getLocation().distance(entity2.getLocation());
                                for(int i = 0; i < distance * 5; i++) {
                                    Particle.DUST.builder().location(getLocationInFrontOfLoc(location, (float) i / 5)).count(2).allPlayers().color(Color.RED).spawn();
                                }
                            }
                        }
                    }
                }
            }

            if(usingPassiveSong == 180) {
                player.getScoreboardTags().remove("UsedAggroshock");
                for(Entity entity : getEntities(player)) {
                    if(entity.getScoreboardTags().contains("Aggroshock" + player.getName())) {
                        entity.getScoreboardTags().remove("Aggroshock" + player.getName());
                        entity.getScoreboardTags().remove("AggroshockVictim1");
                        entity.getScoreboardTags().remove("AggroshockVictim2");
                        entity.getScoreboardTags().remove("AggroshockVictim3");
                        entity.getScoreboardTags().remove("AggroshockTransfered");
                    }
                    if(entity.getScoreboardTags().contains("AggroshockProjectile" + player.getName())) entity.remove();
                }
            }
        }
    }
}