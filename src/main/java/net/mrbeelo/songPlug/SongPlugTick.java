package net.mrbeelo.songPlug;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
                Score cooldownScore = scoreType(player, "blueEnergyCooldown");
                cooldownScore.setScore(200);

                Location center = player.getLocation().add(0, 1, 0); // chest height
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

        //--RED SONGS--//

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

                Score cooldownScore = scoreType(player, "redEnergyCooldown");
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
    }
}
