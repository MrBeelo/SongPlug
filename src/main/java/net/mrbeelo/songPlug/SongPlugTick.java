package net.mrbeelo.songPlug;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugTick {

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
            if(usingActiveSong >= 224 && usingActiveSong <= 230) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_PARROT_IMITATE_EVOKER, SoundCategory.MASTER, 100f, 0.35f);
                    }
                }
            }

            if(usingActiveSong == 201) {
                Entity target = player.getTargetEntity(18, false);
                if(!isAnEntityItem(target) && target != null) {
                    target.getScoreboardTags().add("GotSupporoliftedBy" + player.getName());
                } else {
                    usingActiveSongScore.setScore(0);
                }


                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_WITHER_HURT, SoundCategory.MASTER, 100f, 1.85f);
                    }
                }
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("GotSupporoliftedBy" + player.getName()) && locationDistance(player, entity) <= 20 && !isAnEntityItem(entity)) {
                        RayTraceResult result = player.getWorld().rayTraceBlocks(
                                player.getEyeLocation(),
                                player.getEyeLocation().getDirection(),
                                9
                        );

                        if (result == null || result.getHitBlock() == null) {
                            entity.teleport(getLocationInFrontOfEntity(player, 9));
                        } else {
                            Location loc = result.getHitPosition().toLocation(player.getWorld());
                            loc.setRotation(player.getLocation().getRotation());
                            entity.teleport(loc);
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
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("GotSupporoliftedBy" + player.getName())) entity.getScoreboardTags().remove("GotSupporoliftedBy" + player.getName());
                    player.getScoreboardTags().remove("UsedSupporolift");
                }
            }
        }

        //SUPPOROKENISIS
        if(player.getScoreboardTags().contains("UsedSupporokenisis")) {
            if(usingActiveSong >= 224 && usingActiveSong <= 230) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_PARROT_IMITATE_EVOKER, SoundCategory.MASTER, 100f, 0.35f);
                    }
                }
            }

            if(usingActiveSong == 201) {
                Entity target = player.getTargetEntity(18, false);
                if(isAnEntityItem(target)) {
                    target.getScoreboardTags().add("GotSupporokenisiedBy" + player.getName());
                } else {
                    usingActiveSongScore.setScore(0);
                }


                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_WITHER_HURT, SoundCategory.MASTER, 100f, 1.85f);
                    }
                }
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player.getName()) && locationDistance(player, entity) <= 20 && isAnEntityItem(entity)) {
                        RayTraceResult result = player.getWorld().rayTraceBlocks(
                                player.getEyeLocation(),
                                player.getEyeLocation().getDirection(),
                                9
                        );

                        if (result == null || result.getHitBlock() == null) {
                            entity.teleport(getLocationInFrontOfEntity(player, 9));
                        } else {
                            Location loc = result.getHitPosition().toLocation(player.getWorld());
                            loc.setRotation(player.getLocation().getRotation());
                            entity.teleport(loc);
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
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player.getName())) entity.getScoreboardTags().remove("GotSupporokenisiedBy" + player.getName());
                    player.getScoreboardTags().remove("UsedSupporokenisis");
                }
            }
        }


        //SUPPOROFORM
        if(player.getScoreboardTags().contains("UsedSupporoform")) {
            Score usingSupporoformScore = scoreType(player, "UsingSupporoform");
            int usingSupporoform = usingSupporoformScore.getScore();
            if(usingSupporoform > 0) usingSupporoformScore.setScore(usingSupporoform - 1);

            if(usingSupporoform >= 224 && usingActiveSong <= 230) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_PARROT_IMITATE_EVOKER, SoundCategory.MASTER, 100f, 0.35f);
                    }
                }
            }

            if(usingSupporoform == 201) {
                ItemStack stack = player.getInventory().getItemInMainHand();
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

                    for(Player player2 : Bukkit.getOnlinePlayers()) {
                        if(locationDistance(player, player2) <= 10) {
                            player2.playSound(player2.getLocation(), Sound.ENTITY_WITHER_HURT, SoundCategory.MASTER, 100f, 1.85f);
                        }
                    }
                }

            }

            if(usingSupporoform == 0) {
                player.getScoreboardTags().remove("UsedSupporoform");
            }
        }

        //SUPPOROSPIKE
        if(player.getScoreboardTags().contains("UsedSupporospike")) {
            Score usingPassiveSongScore = scoreType(player, "UsingSupporospike");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

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
                    for(Entity entity : player.getWorld().getEntities()) {
                        if(entity != player && !isAnEntityItem(entity) && entity.getLocation().distance(interaction.getLocation().add(0, 1, 0)) <= 1.5) {
                            entity.setVelocity(new Vector(entity.getVelocity().getX(), 2, entity.getVelocity().getZ()));
                        }
                    }
                }
            }

            if(usingPassiveSong <= 200 && usingPassiveSong >= 195) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Supporospike" + player.getName())) {
                        int i = 201 - usingPassiveSong;
                        Block block = entity.getLocation().add(0, i, 0).getBlock();
                        if(block.isEmpty()) block.setType(Material.BEDROCK);
                    }
                }
            }

            if(usingPassiveSong == 40) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Supporospike" + player.getName())) {
                        entity.remove();
                        for(int i = 1; i < 7; i++) {
                            Block block = entity.getLocation().add(0, i, 0).getBlock();
                            if(block.getType().equals(Material.BEDROCK)) block.setType(Material.AIR);
                        }
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
            Score usingMobilileapScore = scoreType(player, "UsingMobilileap");
            int usingMobilileap = usingMobilileapScore.getScore();
            if(usingMobilileap > 0) usingMobilileapScore.setScore(usingMobilileap - 1);

            AttributeInstance attribute = player.getAttribute(Attribute.SAFE_FALL_DISTANCE);
            assert attribute != null;

            if(usingMobilileap == 229) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
                    }
                }
            }

            if(usingMobilileap == 201) {
                player.setVelocity(new Vector(0, 1.9, 0));
                attribute.setBaseValue(999);
            }

            if(usingMobilileap <= 200 && usingMobilileap > 0 && player.isOnGround()) {
                usingMobilileapScore.setScore(0);
                attribute.setBaseValue(3);
            }

            if(usingMobilileap == 0) {
                player.getScoreboardTags().remove("UsedMobilileap");
            }
        }

        //MOBILIFLASH
        if(player.getScoreboardTags().contains("UsedMobiliflash")) {
            Score usingMobiliflashScore = scoreType(player, "UsingMobiliflash");
            int usingMobiliflash = usingMobiliflashScore.getScore();
            if(usingMobiliflash > 0) usingMobiliflashScore.setScore(usingMobiliflash - 1);

            if(usingMobiliflash == 229) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
                    }
                }
            }

            if(usingMobiliflash == 201) {
                int power = 18;
                RayTraceResult result = player.getWorld().rayTraceBlocks(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        power
                );

                if (result == null || result.getHitBlock() == null) {
                    player.teleport(getLocationInFrontOfEntity(player, power));
                } else {
                    Location loc = result.getHitPosition().toLocation(player.getWorld());
                    loc.setRotation(player.getLocation().getRotation());
                    player.teleport(loc);
                }

                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
                    }
                }
            }

            if(usingMobiliflash == 0) {
                player.getScoreboardTags().remove("UsedMobiliflash");
            }
        }

        //MOBILIBURST
        if(player.getScoreboardTags().contains("UsedMobiliburst")) {
            Score usingMobiliburstScore = scoreType(player, "UsingMobiliburst");
            int usingMobiliburst = usingMobiliburstScore.getScore();
            if(usingMobiliburst > 0) usingMobiliburstScore.setScore(usingMobiliburst - 1);

            if(usingMobiliburst == 229) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
                    }
                }
            }

            if(usingMobiliburst == 201) {
                int power = 7;
                Entity target = player.getTargetEntity(power);

                RayTraceResult result = player.getWorld().rayTraceBlocks(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        power
                );

                if (result == null || result.getHitBlock() == null) {
                    player.teleport(getLocationInFrontOfEntity(player, (float) getMaxDistanceInFrontOfPlayer(player, power, true)).add(0, 0.5f, 0));
                } else {
                    Location loc = result.getHitPosition().toLocation(player.getWorld());
                    loc.setRotation(player.getLocation().getRotation());
                    player.teleport(loc.add(0, 0.1f, 0));
                }

                player.getWorld().createExplosion(player.getLocation(), 1, false, false);
                if(target != null) target.setVelocity(player.getLocation().getDirection().multiply(0.8f).setY(0.7f));

                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
                    }
                }
            }

            if(usingMobiliburst == 0) {
                player.getScoreboardTags().remove("UsedMobiliburst");
            }
        }

        //MOBILIWINGS
        if(player.getScoreboardTags().contains("UsedMobiliwings")) {
            if(usingActiveSong == 229) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
                    }
                }
            }

            if(usingActiveSong == 201) {
                player.setAllowFlight(true);
                player.setVelocity(new Vector(player.getVelocity().getX(), 1.3, player.getVelocity().getZ()));

                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
                    }
                }
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                //TEMPORARY PARTICLES
                Location center = player.getLocation().add(0, 1, 0);
                for (int i = 0; i < 20; i++) {
                    double x = (Math.random() - 0.5) * 1.5;
                    double y = Math.random() * 1.5;
                    double z = (Math.random() - 0.5) * 1.5;
                    Particle.ENTITY_EFFECT.builder().location(center.clone().add(x, y, z)).count(1).color(Color.YELLOW).allPlayers().spawn();
                }

                Score cooldownScore =  scoreType(player, "YellowEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isOnGround() && player.isSneaking()) usingActiveSongScore.setScore(0);
            }

            if(usingActiveSong == 0) {
                player.setAllowFlight(false);
                player.getScoreboardTags().remove("UsedMobiliwings");
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

            if(usingActiveSong == 229) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
                    }
                }
            }

            if(usingActiveSong == 211) {
                player.setVelocity(new Vector(player.getVelocity().getX(), 0.6, player.getVelocity().getZ()));

                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
                    }
                }
            }

            if(usingActiveSong == 201) {
                Location location = player.getLocation().add(0, -1, 0);
                Block targetBlock = location.getBlock();
                if(targetBlock.isEmpty()) {
                    targetBlock.setType(Material.BAMBOO_MOSAIC_SLAB);
                    Interaction interaction = player.getWorld().spawn(location.getBlock().getLocation().add(0.5, 0.1, 0.5), Interaction.class);
                    interaction.getScoreboardTags().add("Mobilibounce" + player.getName());
                }
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                Material standingBlock = player.getLocation().add(0, -0.1, 0).getBlock().getType();
                if(!standingBlock.equals(Material.AIR) && !standingBlock.equals(Material.BAMBOO_MOSAIC_SLAB)) {
                    for(Entity entity : player.getWorld().getEntities()) {
                        if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName())) {
                            if(entity.getLocation().getBlock().getType().equals(Material.BAMBOO_MOSAIC_SLAB)) entity.getLocation().getBlock().setType(Material.AIR);
                            entity.teleport(player.getLocation().add(0.5, 0.1, 0.5));
                        }
                    }
                }

                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName())) {
                        Location location = entity.getLocation().add(0, 0, 0);
                        for (int i = 0; i < 10; i++) {
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
                    if(targetBlock.isEmpty()) targetBlock.setType(Material.BAMBOO_MOSAIC_SLAB);

                    Interaction interaction = player.getWorld().spawn(location.getBlock().getLocation().add(0.5, 0.1, 0.5), Interaction.class);
                    interaction.getScoreboardTags().add("Mobilibounce" + player.getName());

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
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName())) {
                        if(entity.getLocation().getBlock().getType().equals(Material.BAMBOO_MOSAIC_SLAB)) entity.getLocation().getBlock().setType(Material.AIR);
                        entity.remove();
                    }
                }

                player.getScoreboardTags().remove("UsedMobilibounce");
            }
        }

        /// MOBILIGLIDE
        if(player.getScoreboardTags().contains("UsedMobiliglide")) {
            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.MASTER, 5f, 0.75f);
            }

            if(usingActiveSong == 201) {
                speed.setBaseValue(0.17);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1, true, false, false));
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 100f, 0.85f);
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                for (int i = 0; i < 10; i++) {
                    double x = (Math.random() - 0.5) * 1.5;
                    double y = Math.random() * 1.5;
                    double z = (Math.random() - 0.5) * 1.5;
                    Particle.DUST.builder().location(player.getLocation().clone().add(x, y, z)).count(0).color(Color.YELLOW).allPlayers().spawn();
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
            }
        }

        //--BLUE SONGS--//

        //PROTEHEAL
        if(player.getScoreboardTags().contains("UsedProteheal")) {
            Score usingProtehealScore = scoreType(player, "UsingProteheal");
            int usingProteheal = usingProtehealScore.getScore();
            if(usingProteheal > 0) usingProtehealScore.setScore(usingProteheal - 1);

            if(usingProteheal == 229) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 7) {
                        player2.playSound(player2.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
                    }
                }
            }

            if(usingProteheal == 201) {
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

            if(usingProteheal == 0) {
                player.getScoreboardTags().remove("UsedProteheal");
            }
        }

        //PROTEARMOR
        if(player.getScoreboardTags().contains("UsedProtearmor")) {
            AttributeInstance attack = player.getAttribute(Attribute.ATTACK_DAMAGE);
            AttributeInstance armor = player.getAttribute(Attribute.ARMOR);
            assert attack != null;
            assert armor != null;

            if(usingActiveSong == 229) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 7) {
                        player2.playSound(player2.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
                    }
                }
            }

            if(usingActiveSong == 201) {
                speed.setBaseValue(0);
                jump.setBaseValue(0);
                attack.setBaseValue(0);
                armor.setBaseValue(999);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 100f, 0.8f);
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                Score cooldownScore = scoreType(player, "BlueEnergyCooldown");
                cooldownScore.setScore(200);

                Location center = player.getLocation().add(0, 1, 0);
                for (int i = 0; i < 20; i++) {
                    double x = (Math.random() - 0.5) * 1.5;
                    double y = Math.random() * 1.5;
                    double z = (Math.random() - 0.5) * 1.5;
                    Particle.ENTITY_EFFECT.builder().location(center.clone().add(x, y, z)).count(1).color(Color.BLUE).allPlayers().spawn();
                }

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
            }

            if(usingActiveSong == 0) {
                speed.setBaseValue(0.1f);
                jump.setBaseValue(0.42);
                attack.setBaseValue(1);
                armor.setBaseValue(0);
                player.getScoreboardTags().remove("UsedProtearmor");
            }
        }

        //PROTESPHERE
        if(player.getScoreboardTags().contains("UsedProtesphere")) {
            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 7, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                double radius = 1.2;
                for (int i = 0; i < 20; i++) {
                    double theta = Math.random() * Math.PI * 2;
                    double phi = Math.acos(2 * Math.random() - 1);
                    double x = radius * Math.sin(phi) * Math.cos(theta);
                    double y = radius * Math.sin(phi) * Math.sin(theta);
                    double z = radius * Math.cos(phi);
                    Particle.DUST.builder().location(player.getLocation().clone().add(x, y + 1, z)).count(0).allPlayers().color(Color.BLUE).spawn();
                }

                Score cooldownScore = scoreType(player, "BlueEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
                for(Entity entity : player.getWorld().getEntities()) {
                    if(player.getLocation().distance(entity.getLocation()) <= 1.5f && entity instanceof Arrow arrow) arrow.remove();
                }
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedProtesphere");
            }
        }

        //PROTEPOINT
        if(player.getScoreboardTags().contains("UsedProtepoint")) {
            if(usingActiveSong == 229) {
                playSoundToNearby(player.getLocation(), 7, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingActiveSong == 201) {
                Location location = player.getLocation();
                Marker marker = player.getWorld().spawn(getLocationInFrontOfLoc(location.add(0, 1.5f, 0), 1.5f), Marker.class);
                marker.setRotation(location.getYaw(), 0);
                marker.getScoreboardTags().add("ProtepointMarker" + player.getName());
                Interaction interaction = player.getWorld().spawn(marker.getLocation().add(0, -0.5, 0), Interaction.class);
                interaction.getScoreboardTags().add("ProtepointInteraction" + player.getName());
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                for(Entity marker : player.getWorld().getEntities()) {
                    if(marker.getScoreboardTags().contains("ProtepointMarker" + player.getName())) {
                        Location loc = player.getLocation().add(0, 1.5f, 0);
                        loc.setRotation(marker.getYaw(), 0);
                        marker.teleport(getLocationInFrontOfLoc(loc, 1.5f));
                        for(Entity interaction : player.getWorld().getEntities()) {
                            if(interaction.getScoreboardTags().contains("ProtepointInteraction" + player.getName())) {
                                interaction.teleport(marker.getLocation().add(0, -0.5, 0));
                                for(Entity entity3 : player.getWorld().getEntities()) {
                                    if(!entity3.getScoreboardTags().contains("ProtepointMarker" + player.getName()) &&
                                            !entity3.getScoreboardTags().contains("ProtepointInteraction" + player.getName())) {
                                        if(entity3.getBoundingBox().overlaps(interaction.getBoundingBox()) && isAnEntityItem(entity3)) entity3.remove();
                                    }
                                }
                            }
                        }

                        //TEMPORARY PARTICLES
                        Particle.DUST.builder().location(marker.getLocation()).count(0).allPlayers().color(Color.BLUE).spawn();
                    }
                }

                Score cooldownScore = scoreType(player, "BlueEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedProtepoint");
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("ProtepointMarker" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("ProtepointInteraction" + player.getName())) entity.remove();
                }
            }
        }

        //PROTECLONE
        if(player.getScoreboardTags().contains("UsedProteclone")) {
            Score usingPassiveSongScore = scoreType(player, "UsingProteclone");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong == 229) {
                playSoundToNearby(player.getLocation(), 7, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
            }

            if(usingPassiveSong == 201) {
                Mannequin mannequin = player.getWorld().spawn(player.getLocation(), Mannequin.class);
                mannequin.getScoreboardTags().add("Proteclone" + player.getName());
                mannequin.setProfile(ResolvableProfile.resolvableProfile().uuid(player.getUniqueId()).build());
                mannequin.setImmovable(true);

                player.setVelocity(player.getLocation().getDirection().multiply(-1).setY(0.4));
            }

            if(usingPassiveSong == 161) {
                for(Entity entity : player.getWorld().getEntities()) {
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
                }
            }

            if(usingPassiveSong > 0 && usingPassiveSong <= 200) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Protebarrier" + player.getName())) {
                        BoundingBox box = entity.getBoundingBox();
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        for(int i = 0; i < 30; i++) {
                            Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX(), box.getMaxX()),
                                    random.nextDouble(box.getMinY(), box.getMaxY()), random.nextDouble(box.getMinZ(), box.getMaxZ()));
                            Particle.DUST.builder().location(loc).count(0).allPlayers().color(Color.BLUE).spawn();
                        }

                        for(Entity entity2 : player.getWorld().getEntities()) {
                            if(!entity2.getScoreboardTags().contains("Protebarrier" + player.getName()) && box.overlaps(entity2.getBoundingBox())) {
                                if(isAnEntityItem(entity2)) {
                                    entity2.remove();
                                } else if(entity2 instanceof LivingEntity living) {
                                    living.setVelocity(distanceVector(entity, entity2).setY(0.6));
                                }
                            }
                        }
                    }
                }

                if(player.isSneaking()) {
                    usingPassiveSongScore.setScore(0);
                    for(Entity entity : player.getWorld().getEntities()) {
                        if(entity.getScoreboardTags().contains("Protebarrier" + player.getName())) entity.remove();
                    }
                }
            }

            if(usingPassiveSong == 0) {
                player.getScoreboardTags().remove("UsedProtebarrier");
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Protebarrier" + player.getName())) entity.remove();
                }
            }
        }

        //--RED SONGS--//

        //AGGROSPHERE
        if(player.getScoreboardTags().contains("UsedAggrosphere")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggrosphere");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

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

                BlockDisplay display = (BlockDisplay) player.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
                display.getScoreboardTags().add("AggrosphereDisplay" + player.getName());
                display.setBlock(Bukkit.createBlockData(Material.MAGMA_BLOCK));
                BlockDisplay display2 = (BlockDisplay) player.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
                display2.getScoreboardTags().add("AggrosphereDisplay2" + player.getName());
                display2.setBlock(Bukkit.createBlockData(Material.RED_STAINED_GLASS));
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) {
                        entity.teleport(getLocationInFrontOfEntity(entity, 0.6f));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : player.getWorld().getEntities()) {
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

                        for(Entity entity2 : player.getWorld().getEntities()) {
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
                                        Particle.DUST.builder().location(loc).count(0).allPlayers().color(i % 2 == 0 ? Color.RED : Color.ORANGE).spawn();
                                    }

                                }
                            }
                        }
                    }
                }
            }

            if(usingPassiveSong == 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggrosphereDisplay" + player.getName())) entity.remove();
                    if(entity.getScoreboardTags().contains("AggrosphereDisplay2" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggrosphere");
                }
            }
        }

        //AGGROBEAM
        if(player.getScoreboardTags().contains("UsedAggrobeam")) {
            if(usingActiveSong == 229) {
                speed.setBaseValue(0.03);
                jump.setBaseValue(0.1);
            }

            if(usingActiveSong == 201) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
                    }
                }
            }

            if(usingActiveSong <= 200 && usingActiveSong > 0) {
                Entity target = player.getTargetEntity(18, false);
                if(target instanceof LivingEntity living && !isAnEntityItem(living)) {
                    living.damage(5, player);
                }

                for(int i = 0; i < getMaxDistanceInFrontOfPlayer(player, 18, true) * 5 + 5; i++) {
                    Location location = getLocationInFrontOfEntity(player, (float) i / 5);
                    Particle.DUST.builder().color(Color.RED).location(location.add(0, 1.5, 0)).count(0).allPlayers().spawn();
                }

                Score cooldownScore = scoreType(player, "RedEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) {
                    usingActiveSongScore.setScore(0);
                    speed.setBaseValue(0.1);
                    jump.setBaseValue(0.42);
                }
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedAggrobeam");
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
            }
        }

        //AGGROQUAKE
        if(player.getScoreboardTags().contains("UsedAggroquake")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggroquake");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong == 229) {
                speed.setBaseValue(0);
                jump.setBaseValue(0);
            }

            if(usingPassiveSong == 201) {
                speed.setBaseValue(0.1);
                jump.setBaseValue(0.42);
                player.getWorld().createExplosion(player.getLocation(), 3.8f, false, false);
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
                    if (entity instanceof LivingEntity living && isInLineOfSight(player, living) && player.hasLineOfSight(entity)
                            && living != player && player.getLocation().distance(entity.getLocation()) < distanceFromBarrier) {
                        double distance = player.getLocation().distance(living.getLocation());
                        living.setVelocity(distanceVector(player, entity).multiply(0.75 + distance / 4));
                        living.damage(40 - distance * 5, player);
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
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 80) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("Aggrovortex" + player.getName())) {
                        entity.teleport(getLocationInFrontOfEntity(entity, 1));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : player.getWorld().getEntities()) {
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

                        //TEMPORARY PARTICLES
                        double radius = 0.6;
                        for (int i = 0; i < 30; i++) {
                            double theta = Math.random() * Math.PI * 2;
                            double phi = Math.acos(2 * Math.random() - 1);
                            double x = radius * Math.sin(phi) * Math.cos(theta);
                            double y = radius * Math.sin(phi) * Math.sin(theta);
                            double z = radius * Math.cos(phi);
                            Particle.DUST_COLOR_TRANSITION.builder().location(centerLocation.clone().add(x, y, z)).count(0).allPlayers().colorTransition(Color.RED, Color.fromARGB(0, 255, 0, 0)).spawn();
                        }
                    }
                }
            }

            if(usingPassiveSong >= 0 && usingPassiveSong <= 80) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Aggrovortex" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggrovortex");
                }
            }
        }

        //AGGROSHARD
        if(player.getScoreboardTags().contains("UsedAggroshard")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggroshard");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if (usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

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
            }

            if(usingPassiveSong > 0 && usingPassiveSong <= 200) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("Aggroshard" + player.getName())) {
                        entity.teleport(getLocationInFrontOfEntity(entity, 1.4f));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : player.getWorld().getEntities()) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player) {
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

                        Particle.DUST_COLOR_TRANSITION.builder().location(centerLocation.clone()).count(0).allPlayers().colorTransition(Color.RED, Color.fromARGB(0, 255, 0, 0)).spawn();
                    }
                }
            }

            if(usingPassiveSong == 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Aggroshard" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggroshard");
                }
            }
        }

        //AGGRODETONATE
        if(player.getScoreboardTags().contains("UsedAggrodetonate")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggrodetonate");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

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
                Score aggrodetonateYVelocityScore = scoreType(entity, "AggrodetonateYVelocity");
                aggrodetonateYVelocityScore.setScore(0);
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("Aggrodetonate" + player.getName())) {
                        Score aggrodetonateYVelocityScore = scoreType(entity, "AggrodetonateYVelocity");
                        int aggrodetonateYVelocity = aggrodetonateYVelocityScore.getScore();
                        aggrodetonateYVelocityScore.setScore(aggrodetonateYVelocity + 1);
                        entity.teleport(getLocationInFrontOfEntity(entity, 0.8f).add(0, (double) -aggrodetonateYVelocity / 100, 0));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : player.getWorld().getEntities()) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player) {
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

                        Particle.ENTITY_EFFECT.builder().location(centerLocation.clone()).count(10).allPlayers().color(Color.RED).spawn();
                    }
                }
            }

            if(usingPassiveSong == 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Aggrodetonate" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggrodetonate");
                }
            }
        }

        //AGGROSTORM
        if(player.getScoreboardTags().contains("UsedAggrostorm")) {
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
                        living.setVelocity(living.getVelocity().add(distanceVector(player, entity).multiply(-0.05)));
                        double distance = player.getLocation().distance(living.getLocation());
                        if(7 - distance > 0) living.damage(7 - distance);
                    }
                }

                Score cooldownScore = scoreType(player, "RedEnergyCooldown");
                cooldownScore.setScore(200);

                //TEMPORARY PARTICLES
                Location center = player.getLocation().add(0, 1, 0);
                for (int i = 0; i < 20; i++) {
                    double x = (Math.random() - 0.5) * 1.5;
                    double y = Math.random() * 1.5;
                    double z = (Math.random() - 0.5) * 1.5;
                    Particle.ENTITY_EFFECT.builder().location(center.clone().add(x, y, z)).count(1).color(Color.RED).allPlayers().spawn();
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
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("AggroshockProjectile" + player.getName())) {
                        entity.teleport(getLocationInFrontOfEntity(entity, 1));
                        for(Entity entity2 : player.getWorld().getEntities()) {
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

                        double radius = 0.5;
                        for (int i = 0; i < 20; i++) {
                            double theta = Math.random() * Math.PI * 2;
                            double phi = Math.acos(2 * Math.random() - 1);
                            double x = radius * Math.sin(phi) * Math.cos(theta);
                            double y = radius * Math.sin(phi) * Math.sin(theta);
                            double z = radius * Math.cos(phi);
                            Particle.DUST.builder().location(entity.getLocation().clone().add(x, y + 1, z)).count(0).allPlayers().color(Color.RED).spawn();
                        }
                    }



                    if(entity.getScoreboardTags().contains("Aggroshock" + player.getName()) && entity != player && entity instanceof LivingEntity living) {
                        BoundingBox box = entity.getBoundingBox();
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        for(int i = 0; i < 10; i++) {
                            Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX(), box.getMaxX()),
                                    random.nextDouble(box.getMinY(), box.getMaxY()), random.nextDouble(box.getMinZ(), box.getMaxZ()));
                            Particle.FLAME.builder().location(loc).count(0).allPlayers().spawn();
                        }

                        living.damage(20);

                        if(entity.getScoreboardTags().contains("AggroshockVictim1") && !entity.getScoreboardTags().contains("AggroshockTransfered")) {
                            entity.getScoreboardTags().add("AggroshockTransfered");
                            Entity entity2 = getClosestEntity(entity, 3, null);
                            if(entity2 instanceof LivingEntity && entity2 != player) {
                                entity2.getScoreboardTags().add("Aggroshock" + player.getName());
                                entity2.getScoreboardTags().add("AggroshockVictim2");
                                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);

                                Location location = entity.getLocation().clone().add(0, 1, 0).setDirection(distanceVector(entity, entity2));
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
                                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);

                                Location location = entity.getLocation().clone().add(0, 1, 0).setDirection(distanceVector(entity, entity2));
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
                for(Entity entity : player.getWorld().getEntities()) {
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
