package net.mrbeelo.songPlug;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugClass {
    public static String[] weaponTypes = {"Sword", "Longsword", "Battleaxe", "Spear", "Staff", "Dagger"};

    public static void setMovementSpeed(Player player, float amount) {
        AttributeInstance instance = player.getAttribute(Attribute.MOVEMENT_SPEED);
        assert instance != null;
        float value = amount / 100f;
        instance.setBaseValue(value);
    }

    public static void setJumpStrength(Player player, float amount) {
        AttributeInstance instance = player.getAttribute(Attribute.JUMP_STRENGTH);
        assert instance != null;
        float value = amount / 10f * 0.42f;
        instance.setBaseValue(value);
    }

    public static void setAttackDamage(Player player, float amount) {
        AttributeInstance instance = player.getAttribute(Attribute.ATTACK_DAMAGE);
        assert instance != null;
        float value = amount / 6f;
        instance.setBaseValue(value);
    }

    public static void setAttackSpeed(Player player, float amount) {
        AttributeInstance instance = player.getAttribute(Attribute.ATTACK_SPEED);
        assert instance != null;
        float value = amount / 10f * 4f;
        instance.setBaseValue(value);
    }

    public static void setMaxHealth(Player player, float amount) {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        assert instance != null;
        float value = amount * 2f;
        instance.setBaseValue(value);
    }

    public static void setArmor(Player player, float amount) {
        AttributeInstance instance = player.getAttribute(Attribute.ARMOR);
        assert instance != null;
        float value = amount * 2f;
        instance.setBaseValue(value);
    }

    public static float getMovementSpeed(int index) {
        return switch(index) {
            case 1 -> 11.5f;
            case 3 -> 9;
            default -> 10;
        };
    }

    public static float getJumpStrength(int index) {
        return switch(index) {
            case 1 -> 11.7f;
            case 3 -> 9;
            default -> 10;
        };
    }

    public static float getAttackDamage(int index) {
        return switch(index) {
            case 1 -> 7;
            case 2 -> 9;
            case 3 -> 12;
            default -> 10;
        };
    }

    public static float getAttackSpeed(int index) {
        return switch(index) {
            case 3,4 -> 8.5f;
            default -> 10;
        };
    }

    public static float getMaxHealth(int index) {
        return switch(index) {
            case 1 -> 9;
            case 2 -> 15;
            case 3 -> 20;
            default -> 10;
        };
    }

    public static float getArmor(int index) {
        return switch(index) {
            case 2 -> 2;
            case 3 -> 5;
            default -> 0;
        };
    }

    public static void resetClassStats(Player player) {
        int index = getSowClass(player);

        float baseMovementSpeed = getMovementSpeed(index);
        float baseJumpStrength = getJumpStrength(index);
        float baseAttackDamage = getAttackDamage(index);
        float baseAttackSpeed = getAttackSpeed(index);
        float baseMaxHealth = getMaxHealth(index);
        float baseArmor = getArmor(index);

        if(getSowClass(player) == 3 && getLevel(player) >= 40) {
            int skull = scoreValue(player, "Skull");
            baseMaxHealth += skull / 2f;
            baseArmor += skull;
            baseAttackDamage += skull;
        }

        if(getSowClass(player) == 1 && getLevel(player) >= 20) {
            if(scoreValue(player, "ScaredyCat") > 0) {
                baseMovementSpeed += baseMovementSpeed * 0.20f;
                baseAttackDamage -= baseAttackDamage * 0.15f;
            }
        }

        if(getSowClass(player) == 3 && getLevel(player) >= 30) {
            if(scoreValue(player, "EnragedTime") > 0) {
                baseMaxHealth += baseMaxHealth * 0.20f;
                baseMovementSpeed += baseMovementSpeed * 0.20f;
                baseAttackDamage += baseAttackDamage * 0.20f;
                baseArmor -= 10;
                if(baseArmor < 0) baseArmor = 0;
            }
        }

        for(ItemStack stack : usedStacks(player)) {
            if(getCustomItemDataInt(stack, "armor_rating") != 0) {
                baseArmor += getCustomItemDataInt(stack, "armor_rating") / 2f;
            }

            if(getCustomItemDataInt(stack, "weapon_damage") != 0) {
                baseAttackDamage += getCustomItemDataInt(stack, "weapon_damage") / 2f;
            }

            if(getCustomItemDataInt(stack, "max_health") != 0) {
                baseMaxHealth += getCustomItemDataInt(stack, "max_health") / 2f;
            }

            if(getCustomItemDataInt(stack, "movement_speed") != 0) {
                baseMovementSpeed += getCustomItemDataInt(stack, "movement_speed") / 2f;
            }

            if(getCustomItemDataInt(stack, "all_attributes") != 0) {
                int all_attributes = getCustomItemDataInt(stack, "all_attributes");
                baseMovementSpeed += all_attributes;
                baseJumpStrength += all_attributes;
                baseAttackDamage += all_attributes;
                baseAttackSpeed += all_attributes;
                baseMaxHealth += all_attributes;
                baseArmor += all_attributes;
            }
        }

        if(scoreValue(player, "StealthTime") > 0) baseArmor = 0;

        ItemStack stack = player.getInventory().getItemInMainHand();
        String weaponType = getCustomItemDataString(stack, "weapon_type");

        if(getCustomItemDataInt(stack, "two_hander") != 0) {
            int twoHander = getCustomItemDataInt(stack, "two_hander");
            baseAttackDamage += (float) twoHander * 1.5f;
            baseAttackSpeed -= (float) twoHander / 2;
            if(!player.getInventory().getItemInOffHand().isEmpty()) {
                baseAttackSpeed -= 1;
                if(twoHander >= 2) baseAttackDamage -= 1f;
            }
        }

        // ! CHANGE THE BOTTOM LINE TO THE NEW VERSION
        if(getSowClass(player) == 2 && getLevel(player) >= 40 && weaponType != null && weaponType.equals("Staff")) baseAttackDamage += 0.5f;
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) baseAttackDamage -= 0.8f;

        if(weaponType != null) {
            float speedDecreasePercent = switch (weaponType) {
                case "Sword" -> 20f;
                case "Longsword" -> 25f;
                case "Battleaxe" -> 35f;
                case "Spear", "Staff" -> 30f;
                case "Dagger" -> 10f;
                default -> 0f;
            };

            baseMovementSpeed -= baseMovementSpeed * speedDecreasePercent / 100f;
        }

        setMovementSpeed(player, baseMovementSpeed);
        setJumpStrength(player, baseJumpStrength);
        setAttackDamage(player, baseAttackDamage);
        setAttackSpeed(player, baseAttackSpeed);
        setMaxHealth(player, baseMaxHealth);
        setArmor(player, baseArmor);
    }
}
