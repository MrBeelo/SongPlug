package net.mrbeelo.songPlug;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class SongPlugClass {
    public static float getMovementSpeed(int index) {
        return switch(index) {
            case 1 -> 0.13f;
            case 3 -> 0.08f;
            default -> 0.1f;
        };
    }

    public static float getJumpStrength(int index) {
        return switch(index) {
            case 1 -> 0.51f;
            case 3 -> 0.34f;
            default -> 0.42f;
        };
    }

    public static float getAttackDamage(int index) {
        return switch(index) {
            case 1 -> 0.75f;
            case 2 -> 0.9f;
            case 3 -> 1.25f;
            default -> 1.0f;
        };
    }

    public static float getAttackSpeed(int index) {
        return switch(index) {
            case 3,4 -> 3.5f;
            default -> 4.0f;
        };
    }

    public static float getMaxHealth(int index) {
        return switch(index) {
            case 1 -> 18f;
            case 2 -> 30f;
            case 3 -> 40f;
            default -> 20f;
        };
    }

    public static float getArmor(int index) {
        return switch(index) {
            case 2 -> 4f;
            case 3 -> 20f;
            default -> 0f;
        };
    }

    public static void resetClassStats(Player player) {
        for(String tag : player.getScoreboardTags()) {
            if(tag.endsWith("Class")) {
                tag = tag.substring(0, tag.length() - "Class".length());

                int index = switch (tag) {
                    case "Felina" -> 1;
                    case "Ardoni" -> 2;
                    case "Magnorite" -> 3;
                    case "Necromancer" -> 4;
                    default -> 0;
                };

                AttributeInstance movementSpeedInstance = player.getAttribute(Attribute.MOVEMENT_SPEED);
                AttributeInstance jumpStrengthInstance = player.getAttribute(Attribute.JUMP_STRENGTH);
                AttributeInstance attackDamageInstance = player.getAttribute(Attribute.ATTACK_DAMAGE);
                AttributeInstance attackSpeedInstance = player.getAttribute(Attribute.ATTACK_SPEED);
                AttributeInstance maxHealthInstance = player.getAttribute(Attribute.MAX_HEALTH);
                AttributeInstance armorInstance = player.getAttribute(Attribute.ARMOR);

                assert movementSpeedInstance != null;
                assert jumpStrengthInstance != null;
                assert attackDamageInstance != null;
                assert attackSpeedInstance != null;
                assert maxHealthInstance != null;
                assert armorInstance != null;

                movementSpeedInstance.setBaseValue(getMovementSpeed(index));
                jumpStrengthInstance.setBaseValue(getJumpStrength(index));
                attackDamageInstance.setBaseValue(getAttackDamage(index));
                attackSpeedInstance.setBaseValue(getAttackSpeed(index));
                maxHealthInstance.setBaseValue(getMaxHealth(index));
                armorInstance.setBaseValue(getArmor(index));
            }
        }
    }
}
