package net.mrbeelo.songPlug;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;
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
                attribute.setBaseValue(20);
            }

            if(usingMobilileap <= 200 && usingMobilileap > 0 && player.isOnGround()) {
                usingMobilileapScore.setScore(0);
                attribute.setBaseValue(attribute.getDefaultValue());
            }

            if(usingMobilileap == 0) {
                attribute.setBaseValue(attribute.getDefaultValue());
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
                int power = 8;
                Location location = player.getLocation();
                Location newLocation1 = getLocationInFrontOfEntity(player, (float) getMaxDistanceInFrontOfPlayer(player, power, true) - 1);
                Location newLocation2 = new Location(player.getWorld(), newLocation1.getX(), location.getY(), newLocation1.getZ(), location.getYaw(), location.getPitch());
                player.getWorld().createExplosion(newLocation2, 2, false, false);
                player.teleport(newLocation2);

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
                player.setAllowFlight(true);
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
                    Interaction interaction = player.getWorld().spawn(location.getBlock().getLocation().add(0.5, 0, 0.5), Interaction.class);
                    interaction.getScoreboardTags().add("Mobilibounce" + player.getName());
                }
            }

            if(usingActiveSong > 0 && usingActiveSong <= 200) {
                Material standingBlock = player.getLocation().add(0, -0.1, 0).getBlock().getType();
                if(!standingBlock.equals(Material.AIR) && !standingBlock.equals(Material.BAMBOO_MOSAIC_SLAB)) {
                    for(Entity entity : player.getWorld().getEntities()) {
                        if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName())) {
                            if(entity.getLocation().getBlock().getType().equals(Material.BAMBOO_MOSAIC_SLAB)) entity.getLocation().getBlock().setType(Material.AIR);
                            entity.remove();
                            usingActiveSongScore.setScore(0);
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
                    if(targetBlock.isEmpty()) {
                        targetBlock.setType(Material.BAMBOO_MOSAIC_SLAB);
                        Interaction interaction = player.getWorld().spawn(location.getBlock().getLocation().add(0.5, 0, 0.5), Interaction.class);
                        interaction.getScoreboardTags().add("Mobilibounce" + player.getName());
                    }
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
            AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
            assert speed != null;

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

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
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
            AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
            AttributeInstance jump = player.getAttribute(Attribute.JUMP_STRENGTH);
            AttributeInstance attack = player.getAttribute(Attribute.ATTACK_DAMAGE);
            AttributeInstance armor = player.getAttribute(Attribute.ARMOR);

            assert speed != null;
            assert jump != null;
            assert attack != null;
            assert armor != null;

            if(usingActiveSong == 69) {
                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 7) {
                        player2.playSound(player2.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 100f, 0.2f);
                    }
                }
            }

            if(usingActiveSong == 41) {
                speed.setBaseValue(0);
                jump.setBaseValue(0);
                attack.setBaseValue(0);
                armor.setBaseValue(999);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 100f, 0.8f);
            }

            if(usingActiveSong <= 40 && usingActiveSong > 0) {
                Score cooldownScore = scoreType(player, "BlueEnergyCooldown");
                cooldownScore.setScore(200);

                Location center = player.getLocation().add(0, 1, 0);
                for (int i = 0; i < 20; i++) {
                    double x = (Math.random() - 0.5) * 1.5;
                    double y = Math.random() * 1.5;
                    double z = (Math.random() - 0.5) * 1.5;
                    Particle.ENTITY_EFFECT.builder().location(center.clone().add(x, y, z)).count(1).color(Color.BLUE).allPlayers().spawn();
                }
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

                if(player.isSneaking()) usingActiveSongScore.setScore(0);
                for(Entity entity : player.getWorld().getEntities()) {
                    if(player.getLocation().distance(entity.getLocation()) <= 1.5f && entity instanceof Arrow arrow) arrow.remove();
                }
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedProtesphere");
            }
        }

        //--RED SONGS--//

        //AGGROSPHERE
        if(player.getScoreboardTags().contains("UsedAggrosphere")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggrosphere");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong == 201) {
                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("Aggrosphere" + player.getName());
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) {
                        entity.teleport(getLocationInFrontOfEntity(entity, 0.6f));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : player.getWorld().getEntities()) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player) {
                                collidedEntity = entity2;
                            }
                        }

                        if((collidedEntity != null) || centerLocation.getBlock().isSolid()) {
                            if(collidedEntity instanceof LivingEntity living) {
                                living.damage(80, entity);
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(9999, living);
                                }
                            }

                            player.getWorld().createExplosion(centerLocation, 2, false, false);

                            for(Player player2 : Bukkit.getOnlinePlayers()) {
                                if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                    Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                    supporokenisisPlayerActiveScore.setScore(0);
                                }
                            }

                            usingPassiveSongScore.setScore(0);
                        }

                        double radius = 0.2;
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

            if(usingPassiveSong == 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggrosphere");
                }
            }
        }

        //AGGROBEAM
        if(player.getScoreboardTags().contains("UsedAggrobeam")) {
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
                    living.damage(4, player);
                }

                for(int i = 0; i < getMaxDistanceInFrontOfPlayer(player, 18, true); i++) {
                    Location location = getLocationInFrontOfEntity(player, i + 1);
                    Particle.DUST.builder().color(Color.RED).location(location.add(0, 1.5, 0)).count(0).allPlayers().spawn();
                }

                Score cooldownScore = scoreType(player, "RedEnergyCooldown");
                cooldownScore.setScore(200);

                if(player.isSneaking()) {
                    usingActiveSongScore.setScore(0);
                }
            }

            if(usingActiveSong == 0) {
                player.getScoreboardTags().remove("UsedAggrobeam");
            }
        }

        //AGGROQUAKE
        if(player.getScoreboardTags().contains("UsedAggroquake")) {
            Score usingAggroquakeScore = scoreType(player, "UsingAggroquake");
            int usingAggroquake = usingAggroquakeScore.getScore();
            if(usingAggroquake > 0) usingAggroquakeScore.setScore(usingAggroquake - 1);

            if(usingAggroquake == 201) {
                player.getWorld().createExplosion(player.getLocation(), 2, false, false);
            }

            if(usingAggroquake == 0) {
                player.getScoreboardTags().remove("UsedAggroquake");
            }
        }

        //AGGROBLAST
        if(player.getScoreboardTags().contains("UsedAggroblast")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggroblast");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong == 201) {
                for(Entity entity : getNearbyEntities(player.getLocation(), 4)) {
                    if(isInLightOfSight(player, entity) && entity instanceof LivingEntity living && living != player) {
                        living.setVelocity(distanceVector(player, entity).multiply(2));
                        living.damage(25 - player.getLocation().distance(living.getLocation()) * 5);
                    }
                }

                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
            }
        }

        //AGGROVORTEX
        if(player.getScoreboardTags().contains("UsedAggrovortex")) {
            Score usingPassiveSongScore = scoreType(player, "UsingAggrovortex");
            int usingPassiveSong = usingPassiveSongScore.getScore();
            if(usingPassiveSong > 0) usingPassiveSongScore.setScore(usingPassiveSong - 1);

            if(usingPassiveSong == 201) {
                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("Aggrovortex" + player.getName());
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 0) {
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
                                living.damage(80, entity);
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(9999, living);
                                }
                            }

                            player.getWorld().createExplosion(centerLocation, 2, false, false);

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

            if(usingPassiveSong == 0) {
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
                                living.damage(40, entity);
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(9999, living);
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

            if(usingPassiveSong == 201) {
                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("Aggrodetonate" + player.getName());
                playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
            }

            if(usingPassiveSong <= 200 && usingPassiveSong > 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("Aggrodetonate" + player.getName())) {
                        entity.teleport(getLocationInFrontOfEntity(entity, 0.8f));
                        Location centerLocation = entity.getLocation().add(0, 1, 0);

                        Entity collidedEntity = null;
                        for(Entity entity2 : player.getWorld().getEntities()) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 != entity && entity2 != player) {
                                collidedEntity = entity2;
                            }
                        }

                        if((collidedEntity != null) || centerLocation.getBlock().isSolid()) {
                            if(collidedEntity instanceof LivingEntity living) {
                                living.damage(30, entity);
                                if(collidedEntity instanceof Player player2 && player2.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player2.getActiveItem();
                                    stack.damage(9999, living);
                                }
                            }

                            player.getWorld().createExplosion(centerLocation, 5, false, false);

                            for(Player player2 : Bukkit.getOnlinePlayers()) {
                                if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                    Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                    supporokenisisPlayerActiveScore.setScore(0);
                                }
                            }

                            usingPassiveSongScore.setScore(0);
                        }

                        for(int i = 0; i < 10; i++) {
                            Location location = getLocationInFrontOfLoc(centerLocation, (float) -i / 2);
                            Particle.ENTITY_EFFECT.builder().location(location.clone()).count(2).allPlayers().color(Color.RED).spawn();
                        }
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
    }
}
