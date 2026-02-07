package net.mrbeelo.songPlug;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import static net.mrbeelo.songPlug.SongPlugHelper.getSowClass;

public class SongPlugClass {
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
        float value = amount / 10f;
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
            case 1 -> 13;
            case 3 -> 8;
            default -> 10;
        };
    }

    public static float getJumpStrength(int index) {
        return switch(index) {
            case 1 -> 11;
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
            case 3 -> 10;
            default -> 0;
        };
    }

    public static void resetClassStats(Player player) {
        int index = getSowClass(player);
        setMovementSpeed(player, getMovementSpeed(index));
        setJumpStrength(player, getJumpStrength(index));
        setAttackDamage(player, getAttackDamage(index));
        setAttackSpeed(player, getAttackSpeed(index));
        setMaxHealth(player, getMaxHealth(index));
        setArmor(player, getArmor(index));
    }
}
