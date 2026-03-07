package net.mrbeelo.songPlug;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.mrbeelo.songPlug.SongPlug.plugin;
import static net.mrbeelo.songPlug.SongPlugClass.resetClassStats;
import static net.mrbeelo.songPlug.SongPlugHelper.*;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class SongPlugListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        resetClassStats(player);
    }

    @EventHandler
    public void onPlayerScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Score score = scoreType(player, "FCycle");
        score.setScore(0);
        Bukkit.getScheduler().runTaskLater(plugin(), () -> {
            resetClassStats(player);
        }, 1L);
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        Material material = stack.getType();

        if(isFish(material) && getSowClass(player) == 1 && getLevel(player) >= 50) {
            player.heal(4);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 60, 2, true, false, false));
            if(material.equals(Material.PUFFERFISH)) {
                Bukkit.getScheduler().runTask(getPlugin(SongPlug.class), () -> {
                    player.removePotionEffect(PotionEffectType.NAUSEA);
                    player.removePotionEffect(PotionEffectType.HUNGER);
                    player.removePotionEffect(PotionEffectType.POISON);
                });
            }
        }
    }

    @EventHandler
    public void onPlayerWearArmor(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        if(getSowClass(player) == 0 || getSowClass(player) == 1 || getSowClass(player) == 5) return;

        if(event.getNewItem().getType() != Material.AIR) {
            Bukkit.getScheduler().runTask(getPlugin(SongPlug.class), () -> {
                PlayerInventory inv = player.getInventory();
                inv.setItem(event.getSlot(), event.getOldItem());
                player.give(event.getNewItem());
                player.updateInventory();
            });
        }

        Bukkit.getScheduler().runTaskLater(plugin(), () -> {
            resetClassStats(player);
        }, 1L);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(block.getType().equals(Material.ANCIENT_DEBRIS) && getSowClass(player) != 5) {
            Location location = block.getLocation();
            Material material;

            int[] chancePool = {53, 30, 12, 4, 1};

            if(getSowClass(player) == 0 && getLevel(player) >= 40) chancePool = new int[]{46, 29, 16, 7, 2};

            for(ItemStack stack : usedStacks(player)) {
                if(getCustomItemDataInt(stack, "luck") != 0) {
                    int luck = getCustomItemDataInt(stack, "armor_rating");
                    switch(luck) {
                        case 1 -> {
                            chancePool[2] += 1;
                        }
                        case 2 -> {
                            chancePool[2] += 2;
                            chancePool[3] += 1;
                        }
                        case 3 -> {
                            chancePool[2] += 3;
                            chancePool[3] += 2;
                        }
                        case 4 -> {
                            chancePool[2] += 4;
                            chancePool[3] += 2;
                            chancePool[4] += 1;
                        }
                        case 5 -> {
                            chancePool[2] += 5;
                            chancePool[3] += 3;
                            chancePool[4] += 1;
                        }
                    }
                }
            }

            int chanceSum = Arrays.stream(chancePool).sum();

            ThreadLocalRandom random = ThreadLocalRandom.current();
            int randomInt = random.nextInt(chanceSum);

            if(randomInt >= chanceSum - chancePool[4]) material = Material.DIAMOND_ORE;
            else if(randomInt >= chanceSum - chancePool[4] - chancePool[3]) material = Material.GOLD_ORE;
            else if(randomInt >= chanceSum - chancePool[4] - chancePool[3] - chancePool[2]) material = Material.IRON_ORE;
            else if(randomInt >= chanceSum - chancePool[4] - chancePool[3] - chancePool[2] - chancePool[1]) material = Material.COAL_ORE;
            else material = Material.COBBLESTONE;

            event.setCancelled(true);
            location.getBlock().setType(material);
        }
    }

    @EventHandler
    public void onPlayerPressedF(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if(!player.getScoreboardTags().contains("ArdoniClass")) return;
        if(getLevel(player) < 10) return;

        Score score = scoreType(player, "FCycle");
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
    public void onPlayerSelectItem(InventoryClickEvent event) {
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

            Score energyScore = scoreType(player, "SongEnergy");
            Score energyRegenScore = scoreType(player, "SongEnergyRegen");

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

            Score songEnergyCooldownScore = scoreType(player, songColor + "EnergyCooldown");
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
                        if(!stack.getItemMeta().getDisplayName().substring(2).equals("Necromancer")) {
                            player.getScoreboardTags().add(name + "Class");
                            event.setCancelled(true);
                            view.close();
                        } else {
                            player.sendMessage("Nick is too lazy to add Necromacer!");
                            event.setCancelled(true);
                        }
                    }
                }

                if(stack.getItemMeta().getDisplayName().equals("None")) {
                    event.setCancelled(true);
                    view.close();
                }

                resetClassStats(player);

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
        } else if(view.title().equals(Component.text("Brewing Stand"))) {
            HumanEntity human = event.getWhoClicked();
            ItemStack stack = event.getCurrentItem();
            if(stack != null && human instanceof Player player) {
                String name = stack.getItemMeta().getDisplayName();

                Material material = switch(name) {
                    case "Potion of Invisibility" -> Material.OPEN_EYEBLOSSOM;
                    case "Potion of Speed" -> Material.PITCHER_PLANT;
                    case "Potion of Fire Resistance" -> Material.TORCHFLOWER;
                    case "Potion of Haste" -> Material.TORCHFLOWER_SEEDS;
                    case "Splash Potion of Weakness" -> Material.WITHER_ROSE;
                    default -> Material.BARRIER;
                };

                Inventory inv = player.getInventory();
                if(inv.contains(material, 1) && inv.contains(Material.GOLD_INGOT, 1) && inv.contains(Material.GUNPOWDER, 5)) {
                    removeItems(inv, material, 1);
                    removeItems(inv, Material.GOLD_INGOT, 1);
                    removeItems(inv, Material.GUNPOWDER, 5);
                    player.give(noLoreStack(stack));
                    player.sendMessage("Crafted " + name + " Successfully!");
                } else {
                    player.sendMessage("Missing items for " + name + "!");
                }

                event.setCancelled(true);
            }
        } else if(view.title().equals(Component.text("Enchantment Table"))) {
            HumanEntity human = event.getWhoClicked();
            ItemStack stack = event.getCurrentItem();
            if(stack != null && human instanceof Player player) {
                String name = stack.getItemMeta().getDisplayName();

                if(name.equals("First Page")) {
                    view.close();
                    event.setCancelled(true);
                    openEnchantingMenu(player);
                } else if(name.equals("Next Page")) {
                    view.close();
                    event.setCancelled(true);
                    openSecondaryEnchantingMenu(player);
                } else {
                    String nameByItself = name.substring(0, name.length() - 2);
                    int num = Integer.parseInt(name.substring(name.length() - 1));
                    String key = nameByItself.toLowerCase().replace(" ", "_");

                    Inventory inv = player.getInventory();
                    ItemStack heldStack = player.getInventory().getItemInMainHand();
                    int enchantsNumber = getCustomItemDataInt(heldStack, "enchants");

                    int maxEnchants = -1;
                    if(isCopper(heldStack)) maxEnchants = 1;
                    if(isIron(heldStack)) maxEnchants = 2;
                    if(isGold(heldStack)) maxEnchants = 3;
                    if(isDiamond(heldStack)) maxEnchants = 4;

                    boolean shouldEnchant = true;

                    if(maxEnchants == -1 && !heldStack.isEmpty()) {
                        player.sendMessage("Cannot enchant!");
                        shouldEnchant = false;
                    }
                    if(heldStack.isEmpty()) {
                        player.sendMessage("You aren't holding any items!");
                        shouldEnchant = false;
                    }

                    if(enchantsNumber >= maxEnchants && maxEnchants != -1 && getCustomItemDataInt(heldStack, key) < num) {
                        player.sendMessage("Too many enchantments!");
                        shouldEnchant = false;
                    }
                    if(getCustomItemDataInt(heldStack, key) >= num) {
                        player.sendMessage("You already have a similar enchantment!");
                        shouldEnchant = false;
                    }
                    if(key.equals("all_attributes")) {
                        if((!inv.contains(Material.LAPIS_LAZULI, 30) || !inv.contains(Material.DIAMOND, 10)) && maxEnchants != -1) {
                            player.sendMessage("Missing items for " + name + "!");
                            shouldEnchant = false;
                        }
                    } else {
                        if(!inv.contains(Material.LAPIS_LAZULI, 3 * num) && maxEnchants != -1) {
                            player.sendMessage("Missing items for " + name + "!");
                            shouldEnchant = false;
                        }
                    }
                    if(shouldEnchant) {
                        if(key.equals("all_attributes")) {
                            removeItems(inv, Material.LAPIS_LAZULI, 30);
                            removeItems(inv, Material.DIAMOND, 10);
                        } else {
                            removeItems(inv, Material.LAPIS_LAZULI, 3 * num);
                        }

                        if(getCustomItemDataInt(heldStack, key) == 0) setCustomItemDataInt(heldStack, "enchants", enchantsNumber + 1);
                        setCustomItemDataInt(heldStack, key, num);
                        ItemMeta meta = heldStack.getItemMeta();
                        meta.setEnchantmentGlintOverride(true);
                        List<Component> loreArray = new ArrayList<>();
                        loreArray.add(Component.text(name).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                        meta.lore(loreArray);
                        heldStack.setItemMeta(meta);
                        player.sendMessage("Applied " + name + " to item successfully!");
                    }
                }

                event.setCancelled(true);
            }
        } else if(view.title().equals(Component.text("Shop"))) {
            HumanEntity human = event.getWhoClicked();
            ItemStack stack = event.getCurrentItem();
            if(stack != null && human instanceof Player player) {
                String name = stack.getItemMeta().getItemName();
                String rarity = name.substring(0, name.indexOf(" "));
                int requiredPoints = switch(rarity) {
                    case "Common" -> 250;
                    case "Uncommon" -> 500;
                    case "Rare" -> 750;
                    case "Legendary" -> 1000;
                    default -> 0;
                };

                Score pointScore = scoreType(player, "Points");
                if(pointScore.getScore() >= requiredPoints) {
                    pointScore.setScore(pointScore.getScore() - requiredPoints);
                    giveCrate(player, rarity);
                } else {
                    player.sendMessage("Not enough points!");
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
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
                    clearSongs(player, true);
                    return;
                }

                String name = meta.getItemName();
                if(infuseSong(player, name, true, false)) {
                    playSoundToNearby(player.getLocation(), 10, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.MASTER, 1.0f, 1.0f);
                    if(getLevel(player) < 20) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 140, 0, true, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 140, 2, true, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 140, 0, true, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 140, 255, true, false, false));

                        Score score = scoreType(player, "InfuseDebuff");
                        score.setScore(140);

                        AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
                        assert health != null;
                        health.setBaseValue(6);
                    }
                };
            }
        }

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            assert block != null;
            int sowClass = getSowClass(player);
            int level = getLevel(player);

            if(block.getType().equals(Material.BREWING_STAND)) {
                if(sowClass == 0 && level >= 20) {
                    event.setCancelled(true);
                    openBrewingMenu(player);
                } else if(sowClass != 5) {
                    event.setCancelled(true);
                }
            } else if(block.getType().equals(Material.ENCHANTING_TABLE)) {
                if(sowClass == 0 && level >= 30) {
                    event.setCancelled(true);
                    openEnchantingMenu(player);
                } else if(sowClass != 5) {
                    event.setCancelled(true);
                }
            }
        }

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack stack = player.getInventory().getItemInMainHand();
            if(!stack.isEmpty() && stack.getItemMeta().getItemName().endsWith(" Song Crate")) {
                String name = stack.getItemMeta().getItemName();
                name = name.substring(0, name.length() - " Song Crate".length());
                if(isIn(name, rarities)) {
                    String[] songPool = switch(name) {
                        case "Common" -> commonSongs;
                        case "Uncommon" -> uncommonSongs;
                        case "Rare" -> rareSongs;
                        case "Legendary" -> legendarySongs;
                        default -> throw new IllegalStateException("Unexpected value: " + name);
                    };

                    stack.setAmount(stack.getAmount() - 1);
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    int index = random.nextInt(0, songPool.length);
                    giveSong(player, songPool[index]);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
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

        TextComponent component = getSowClassComponent(player, false, true);

        event.renderer((source, sourceDisplayName, message, viewer) ->
                Component.text("[" + component.content() + "] ", component.color())
                        .append(sourceDisplayName.color(NamedTextColor.WHITE))
                        .append(Component.text(": ", NamedTextColor.WHITE))
                        .append(message.color(NamedTextColor.WHITE))
        );
    }

    @EventHandler
    public void onPlayerDamagedByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if(entity instanceof Player player && player.getScoreboardTags().contains("UsedProtearmor")) {
            Score activeScore = scoreType(player, "UsingActiveSong");
            if(activeScore.getScore() > 0 && activeScore.getScore() <= 200) {
                Vector vec = damager.getLocation().getDirection();
                damager.setVelocity(new Vector(vec.getX() * -1, 0, vec.getZ() * -1));
                activeScore.setScore(0);

                if(damager instanceof LivingEntity living) {
                    living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 2));
                    living.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
                    living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));
                }

                Score damagerActiveScore = scoreType(damager, "UsingActiveSong");
                damagerActiveScore.setScore(0);

                event.setCancelled(true);
            }
        } else if(damager.getScoreboardTags().contains("UsedProtesphere") && damager instanceof Player player) {
            Score activeScore = scoreType(player, "UsingActiveSong");
            if(activeScore.getScore() > 0 && activeScore.getScore() <= 200) event.setCancelled(true);
        } else if(entity.getScoreboardTags().contains("UsedProtepoint") && entity instanceof Player player) {
            Score activeScore = scoreType(player, "UsingActiveSong");
            if(activeScore.getScore() > 0 && activeScore.getScore() <= 200) {
                for(Entity interaction : getEntities(player)) {
                    if(interaction.getScoreboardTags().contains("ProtepointInteraction" + player.getName())) {
                        if(interaction.getBoundingBox().overlaps(damager.getBoundingBox())) event.setCancelled(true);
                    }
                }
            }
        } else {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(entity.getScoreboardTags().contains("Proteclone" + player.getName())) {
                    BoundingBox box = entity.getBoundingBox();
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    for(int i = 0; i < 30; i++) {
                        Location loc = new Location(player.getWorld(), random.nextDouble(box.getMinX(), box.getMaxX()),
                                random.nextDouble(box.getMinY(), box.getMaxY()), random.nextDouble(box.getMinZ(), box.getMaxZ()));
                        Particle.DUST.builder().location(loc).count(0).allPlayers().color(Color.BLUE).spawn();
                    }
                    entity.remove();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        if(entity instanceof Player player) {
            if(player.getScoreboardTags().contains("UsedAggroquake")) {
                Score passiveScore = scoreType(player, "UsingAggroquake");
                if(passiveScore.getScore() >= 195 && passiveScore.getScore() <= 205) {
                    Vector velocity = player.getVelocity();
                    player.setVelocity(new Vector(velocity.getX(), 0, velocity.getZ()));
                    event.setCancelled(true);
                }
            } else if(player.getScoreboardTags().contains("UsedMobiliburst")) {
                Score passiveScore = scoreType(player, "UsingMobiliburst");
                if(passiveScore.getScore() >= 195 && passiveScore.getScore() <= 205) {
                    player.setVelocity(new Vector(0, 0, 0));
                    event.setCancelled(true);
                }
            } else if(player.getScoreboardTags().contains("UsedProtesphere")) {
                Score activeScore = scoreType(player, "UsingActiveSong");
                if(activeScore.getScore() > 0 && activeScore.getScore() <= 200) event.setCancelled(true);
            } else if(player.getScoreboardTags().contains("UsedProtearmor")) {
                Score activeScore = scoreType(player, "UsingActiveSong");
                if(activeScore.getScore() > 0 && activeScore.getScore() <= 200) {
                    if(!cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) event.setCancelled(true);
                }
            }

            if(cause.equals(EntityDamageEvent.DamageCause.PROJECTILE) && getSowClass(player) == 3 &&
                    getLevel(player) >= 50) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if(damager instanceof Player player && isUndead(entity)) {
            if(getSowClass(player) == 1 && getLevel(player) >= 10) {
                double damage = event.getDamage();
                event.setDamage(damage * 1.2f);
            }
        }

        if(damager instanceof Player player && player.getInventory().getItemInMainHand().isEmpty() &&
        scoreValue(player, "DisarmCooldown") == 0 && getSowClass(player) == 3 &&
        getLevel(player) >= 20) {
            player.playSound(player, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1f, 0.6f);
            if(entity instanceof Player player2) player2.playSound(player2, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1f, 0.6f);

            Score disarmStunScore = scoreType(entity, "DisarmStun");
            disarmStunScore.setScore(2 * 20);

            Score disarmCooldownScore = scoreType(player, "DisarmCooldown");
            disarmCooldownScore.setScore(20 * 20);
        }

        if(damager instanceof Player player && scoreValue(player, "DisarmStun") > 0) event.setCancelled(true);

        if(damager instanceof Player player && getSowClass(player) == 1 && getLevel(player) >= 30) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            String weaponType = getCustomItemDataString(stack, "weapon_type");

            if(entity instanceof Player attackedPlayer) {
                Vector attackedPlayerVector = attackedPlayer.getLocation().getDirection().normalize().setY(attackedPlayer.getLocation().getDirection().normalize().getY() / 3);
                Vector combinedVector = entityDistanceVector(player, attackedPlayer).normalize();
                double angle = Math.toDegrees(attackedPlayerVector.angle(combinedVector));

                if(weaponType != null && weaponType.equals("Dagger") && angle < 20f &&
                        scoreValue(player, "StealthCooldown") == 0) {
                    Score stealthTimeScore = scoreType(attackedPlayer, "StealthTime");
                    stealthTimeScore.setScore(2 * 20);
                    Score stealthCooldownScore = scoreType(player, "StealthCooldown");
                    stealthCooldownScore.setScore(20 * 20);
                    stealthHelper(attackedPlayer, 2);

                    Bukkit.getScheduler().runTaskLater(plugin(), () -> {
                        resetClassStats(attackedPlayer);
                    }, 1L);
                }
            }
        }

        if(damager instanceof Player player && getSowClass(player) == 1 && getLevel(player) >= 40) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            if(stack.isEmpty() && scoreValue(player, "FelineFuryCooldown") == 0) {
                Score bleedScore = scoreType(entity, "Bleed");
                bleedScore.setScore(5 * 20);
                Score felineFuryCooldownScore = scoreType(player, "FelineFuryCooldown");
                felineFuryCooldownScore.setScore(20 * 20);
            }
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        for(Entity entity : getEntities(player)) {
            if(entity.getScoreboardTags().contains("Mobilibounce" + player.getName()) &&
                    player.getBoundingBox().overlaps(entity.getBoundingBox())) {
                if(player.getPitch() > 0) {
                    event.setCancelled(true);
                    Score mobilibounceLaunchDelayScore = scoreType(player, "MobilibounceLaunchDelay");
                    mobilibounceLaunchDelayScore.setScore(1);
                } else {
                    player.setVelocity(player.getLocation().getDirection());
                }

                entity.remove();
                if(entity.getLocation().getBlock().getType().equals(Material.BARRIER)) entity.getLocation().getBlock().setType(Material.AIR);

                Score mobilibounceDelayScore = scoreType(player, "MobilibouncePlatformDelay");
                mobilibounceDelayScore.setScore(11);
            }

            if(entity.getScoreboardTags().contains("MobilibounceDisplay" + player.getName())) entity.remove();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        DamageSource source = event.getDamageSource();
        Entity killer = source.getCausingEntity();

        if(entity instanceof Player || isUndead(entity)) {
            if(killer instanceof Player player && getSowClass(player) == 3 && getLevel(player) >= 40) {
                Score skullScore = scoreType(player, "Skull");
                int skull = skullScore.getScore();
                if(skull < 5) skullScore.setScore(skull + 1);
                Score skullAliveTimeScore = scoreType(player, "SkullAliveTime");
                skullAliveTimeScore.setScore(35 * 20);
                resetClassStats(player);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;
        if(getSowClass(player) != 5 && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) player.removePotionEffect(PotionEffectType.INVISIBILITY);

    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        Entity entity = event.getEntity();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;
        if(scoreValue(entity, "DisarmStun") > 0) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(stack.getItemMeta().getItemName().endsWith(" Song Crate")) event.setCancelled(true);

        String name = stack.getItemMeta().getItemName();
        if(isIn(name, redSongs) || isIn(name, blueSongs) || isIn(name, yellowSongs) || isIn(name, greenSongs)) event.setCancelled(true);
    }
}