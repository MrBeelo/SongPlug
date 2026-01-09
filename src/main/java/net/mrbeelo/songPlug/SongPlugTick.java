package net.mrbeelo.songPlug;

import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugTick {

    public static void updateRegen(Player player, Score energyScore) {
        if(player.getScoreboardTags().contains("ArdoniClass")) {
            Score energyRegenScore = scoreType(player, "songEnergyRegen");
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
            Score redCooldownScore = scoreType(player, "redEnergyCooldown");
            Score blueCooldownScore = scoreType(player, "blueEnergyCooldown");
            Score yellowCooldownScore = scoreType(player, "yellowEnergyCooldown");
            Score greenCooldownScore = scoreType(player, "greenEnergyCooldown");
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

                        Score cooldownScore = scoreType(player, "greenEnergyCooldown");
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

                        Score cooldownScore = scoreType(player, "greenEnergyCooldown");
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

        //AGGROSPHERE
        if(player.getScoreboardTags().contains("UsedAggrosphere")) {
            Score usingAggrosphereScore = scoreType(player, "UsingAggrosphere");
            int usingAggrosphere = usingAggrosphereScore.getScore();
            if(usingAggrosphere > 0) usingAggrosphereScore.setScore(usingAggrosphere - 1);

            if(usingAggrosphere == 201) {
                Location location = player.getLocation();
                Entity entity = player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setVisibleByDefault(false);
                entity.getScoreboardTags().add("Aggrosphere" + player.getName());

                for(Player player2 : Bukkit.getOnlinePlayers()) {
                    if(locationDistance(player, player2) <= 10) {
                        player2.playSound(player2.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
                    }
                }
            }

            if(usingAggrosphere <= 200 && usingAggrosphere > 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) {
                        entity.teleport(getLocationInFrontOfEntity(entity, 0.6f));
                        Location location = entity.getLocation().add(0, 1, 0);

                        for(Entity entity2 : player.getWorld().getEntities()) {
                            if(entity.getBoundingBox().overlaps(entity2.getBoundingBox()) && entity2 instanceof LivingEntity living &&
                                    entity2 != entity && entity2 != player) {
                                living.damage(80, entity);
                                player.getWorld().createExplosion(location, 2, false, false);
                                if(entity2 instanceof Player player3 && player3.getActiveItem().getType() == Material.SHIELD) {
                                    ItemStack stack = player3.getActiveItem();
                                    stack.damage(9999, living);
                                }
                                for(Player player2 : Bukkit.getOnlinePlayers()) {
                                    if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                        Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                        supporokenisisPlayerActiveScore.setScore(0);
                                    }

                                    if(locationDistance(entity, player2) <= 30) {
                                        player2.playSound(player2.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
                                    }
                                }
                                usingAggrosphereScore.setScore(0);
                            }
                        }

                        double radius = 0.2;

                        for (int i = 0; i < 30; i++) {
                            double theta = Math.random() * Math.PI * 2;
                            double phi = Math.acos(2 * Math.random() - 1);

                            double x = radius * Math.sin(phi) * Math.cos(theta);
                            double y = radius * Math.sin(phi) * Math.sin(theta);
                            double z = radius * Math.cos(phi);

                            Particle.DUST_COLOR_TRANSITION.builder().location(location.clone().add(x, y, z)).count(0).allPlayers().colorTransition(Color.RED, Color.fromARGB(0, 255, 0, 0)).spawn();
                        }

                        if(location.getBlock().isSolid()) {
                            usingAggrosphereScore.setScore(0);

                            for(Player player2 : Bukkit.getOnlinePlayers()) {
                                if(entity.getScoreboardTags().contains("GotSupporokenisiedBy" + player2.getName())) {
                                    Score supporokenisisPlayerActiveScore = scoreType(player2, "UsingActiveSong");
                                    supporokenisisPlayerActiveScore.setScore(0);
                                }

                                if(locationDistance(entity, player2) <= 10) {
                                    player2.playSound(player2.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 100f, 0.35f);
                                }
                            }
                        }
                    }
                }
            }

            if(usingAggrosphere == 0) {
                for(Entity entity : player.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("Aggrosphere" + player.getName())) entity.remove();
                    player.getScoreboardTags().remove("UsedAggrosphere");
                }
            }
        }

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
        }
    }
}
