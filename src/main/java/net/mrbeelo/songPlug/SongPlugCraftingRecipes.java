package net.mrbeelo.songPlug;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mrbeelo.songPlug.SongPlugHelper.*;

public class SongPlugCraftingRecipes {
    static List<Map<Integer, ItemStack>> recipes = new ArrayList<>();
    static int recipeAmount = 27;

    public static int coordsToSlot(int[] coords) {
        int slot = 0;
        slot += 2 + coords[0];
        slot += 9 * coords[1];
        return slot;
    }

    public static void append(Map<Integer, ItemStack> recipe, int[] coords, ItemStack item) {
        recipe.put(coordsToSlot(coords), item);
    }

    public static void append(Map<Integer, ItemStack> recipe, int[] coords, Material material) {
        recipe.put(coordsToSlot(coords), new ItemStack(material));
    }

    public static void append(Map<Integer, ItemStack> recipe, int[] coords, Material material, int amount) {
        recipe.put(coordsToSlot(coords), new ItemStack(material, amount));
    }

    public static ItemStack getRecipeResult(int index) {
        switch(index) {
            case 0: return weaponStack("Deathsinger");
            case 1: return weaponStack("Diamond_Bardice");
            case 2: return weaponStack("Diamond_Chronos_Sword");
            case 3: return weaponStack("Diamond_Halberd");
            case 4: return weaponStack("Iron_Halberd_Double");
            case 5: return weaponStack("Diamond_Staff_Guard");
            case 6: return weaponStack("Heavy_Diamond_Spear");
            case 7: return weaponStack("Iron_Bardice");
            case 8: return weaponStack("Iron_Blazer_Claws");
            case 9: return weaponStack("Iron_Chronos_Sword");
            case 10: return weaponStack("Iron_Dagger");
            case 11: return weaponStack("Iron_Greatsword");
            case 12: return weaponStack("Iron_Halberd");
            case 13: return weaponStack("Iron_Scythe");
            case 14: return weaponStack("Diamond_Light_Spear");
            case 15: return weaponStack("Iron_Light_Spear");
            case 16: return weaponStack("Iron_Mace");
            case 17: return weaponStack("Ria");
            case 18: return weaponStack("Iron_Splitsword");
            case 19: return weaponStack("Iron_Staffpoint");
            case 20: return weaponStack("Iron_Staffstrike");
            case 21: return weaponStack("Iron_Stubby_Axe");
            case 22: return weaponStack("Zweihander");
            case 23: return weaponStack("Thalleous");
            case 24: return weaponStack("Tygren");
            case 25: return weaponStack("Warmaul");
            case 26: return weaponStack("Iron_Katana");
        }

        return ItemStack.empty();
    }

    public static void initRecipes() {
        for(int i = 0; i < recipeAmount; i++) recipes.add(new HashMap<>());

        // DEATHSINGER
        append(recipes.getFirst(), new int[]{2, 0}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{1, 1}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{2, 1}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{3, 1}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{1, 2}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{2, 2}, Material.RESIN_BRICK);
        append(recipes.getFirst(), new int[]{3, 2}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{1, 3}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{2, 3}, Material.RESIN_BRICK);
        append(recipes.getFirst(), new int[]{3, 3}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{0, 4}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{1, 4}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.getFirst(), new int[]{3, 4}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{4, 4}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{2, 5}, Material.DIAMOND);

        // DIAMOND BARDICE
        append(recipes.get(1), new int[]{0, 0}, Material.DIAMOND);
        append(recipes.get(1), new int[]{2, 0}, Material.DIAMOND);
        append(recipes.get(1), new int[]{0, 1}, Material.DIAMOND);
        append(recipes.get(1), new int[]{1, 1}, Material.DIAMOND);
        append(recipes.get(1), new int[]{2, 1}, Material.DIAMOND);
        append(recipes.get(1), new int[]{3, 1}, Material.DIAMOND);
        append(recipes.get(1), new int[]{0, 2}, Material.DIAMOND);
        append(recipes.get(1), new int[]{1, 2}, Material.DIAMOND);
        append(recipes.get(1), new int[]{2, 2}, Material.RESIN_BRICK);
        append(recipes.get(1), new int[]{0, 3}, Material.DIAMOND);
        append(recipes.get(1), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(1), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(1), new int[]{2, 5}, Material.STICK, 2);

        // DIAMOND CHRONOS SWORD
        append(recipes.get(2), new int[]{2, 1}, Material.DIAMOND);
        append(recipes.get(2), new int[]{2, 2}, Material.DIAMOND);
        append(recipes.get(2), new int[]{1, 3}, Material.DIAMOND);
        append(recipes.get(2), new int[]{2, 3}, Material.DIAMOND);
        append(recipes.get(2), new int[]{3, 3}, Material.DIAMOND);
        append(recipes.get(2), new int[]{1, 4}, Material.DIAMOND);
        append(recipes.get(2), new int[]{2, 4}, Material.DIAMOND_SWORD);
        append(recipes.get(2), new int[]{3, 4}, Material.DIAMOND);
        append(recipes.get(2), new int[]{2, 5}, Material.STICK);

        // DIAMOND HALBERD
        append(recipes.get(3), new int[]{1, 0}, Material.DIAMOND);
        append(recipes.get(3), new int[]{0, 1}, Material.DIAMOND);
        append(recipes.get(3), new int[]{1, 1}, Material.DIAMOND);
        append(recipes.get(3), new int[]{2, 1}, Material.DIAMOND);
        append(recipes.get(3), new int[]{3, 1}, Material.DIAMOND);
        append(recipes.get(3), new int[]{0, 2}, Material.DIAMOND);
        append(recipes.get(3), new int[]{1, 2}, Material.DIAMOND);
        append(recipes.get(3), new int[]{2, 2}, Material.DIAMOND_AXE);
        append(recipes.get(3), new int[]{3, 2}, Material.DIAMOND);
        append(recipes.get(3), new int[]{1, 3}, Material.DIAMOND);
        append(recipes.get(3), new int[]{2, 3}, Material.STICK);
        append(recipes.get(3), new int[]{2, 4}, Material.STICK);
        append(recipes.get(3), new int[]{2, 5}, Material.STICK, 2);

        // IRON HALBERD DOUBLE
        append(recipes.get(4), new int[]{0, 0}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{1, 0}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{3, 0}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{4, 0}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{0, 1}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{4, 1}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{0, 2}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{2, 2}, Material.RESIN_BRICK);
        append(recipes.get(4), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{4, 2}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{0, 3}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(4), new int[]{4, 3}, Material.IRON_INGOT);
        append(recipes.get(4), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(4), new int[]{2, 5}, Material.IRON_INGOT);

        // DIAMOND STAFF GUARD
        append(recipes.get(5), new int[]{0, 0}, Material.DIAMOND);
        append(recipes.get(5), new int[]{2, 0}, Material.DIAMOND);
        append(recipes.get(5), new int[]{4, 0}, Material.DIAMOND);
        append(recipes.get(5), new int[]{0, 1}, Material.DIAMOND);
        append(recipes.get(5), new int[]{1, 1}, Material.DIAMOND);
        append(recipes.get(5), new int[]{2, 1}, Material.RESIN_BRICK);
        append(recipes.get(5), new int[]{3, 1}, Material.DIAMOND);
        append(recipes.get(5), new int[]{4, 1}, Material.DIAMOND);
        append(recipes.get(5), new int[]{2, 2}, Material.STICK, 2);
        append(recipes.get(5), new int[]{1, 3}, Material.DIAMOND, 2);
        append(recipes.get(5), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(5), new int[]{3, 3}, Material.DIAMOND, 2);
        append(recipes.get(5), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(5), new int[]{2, 5}, Material.DIAMOND);

        // HEAVY DIAMOND SPEAR
        append(recipes.get(6), new int[]{2, 0}, Material.DIAMOND);
        append(recipes.get(6), new int[]{2, 1}, Material.DIAMOND, 2);
        append(recipes.get(6), new int[]{1, 2}, Material.DIAMOND);
        append(recipes.get(6), new int[]{2, 2}, Material.STICK, 2);
        append(recipes.get(6), new int[]{3, 2}, Material.DIAMOND);
        append(recipes.get(6), new int[]{1, 3}, Material.DIAMOND, 2);
        append(recipes.get(6), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(6), new int[]{3, 3}, Material.DIAMOND, 2);
        append(recipes.get(6), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(6), new int[]{2, 5}, Material.STICK, 2);

        // IRON BARDICE
        append(recipes.get(7), new int[]{0, 0}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{0, 1}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{0, 2}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{2, 2}, Material.RESIN_BRICK);
        append(recipes.get(7), new int[]{0, 3}, Material.IRON_INGOT);
        append(recipes.get(7), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(7), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(7), new int[]{2, 5}, Material.STICK, 2);

        // IRON BLAZER CLAWS
        append(recipes.get(8), new int[]{0, 1}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{4, 1}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{0, 2}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{4, 2}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{0, 3}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{2, 3}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{3, 3}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{4, 3}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{0, 4}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{1, 4}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{2, 4}, Material.RESIN_BRICK);
        append(recipes.get(8), new int[]{3, 4}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{4, 4}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{1, 5}, Material.IRON_INGOT);
        append(recipes.get(8), new int[]{2, 5}, Material.STICK);
        append(recipes.get(8), new int[]{3, 5}, Material.IRON_INGOT);

        // IRON CHRONOS SWORD
        append(recipes.get(9), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(9), new int[]{2, 2}, Material.IRON_INGOT);
        append(recipes.get(9), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(9), new int[]{2, 3}, Material.IRON_INGOT);
        append(recipes.get(9), new int[]{3, 3}, Material.IRON_INGOT);
        append(recipes.get(9), new int[]{1, 4}, Material.IRON_INGOT);
        append(recipes.get(9), new int[]{2, 4}, Material.IRON_SWORD);
        append(recipes.get(9), new int[]{3, 4}, Material.IRON_INGOT);
        append(recipes.get(9), new int[]{2, 5}, Material.STICK);

        // IRON DAGGER
        append(recipes.get(10), new int[]{1, 0}, Material.IRON_INGOT);
        append(recipes.get(10), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(10), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(10), new int[]{2, 2}, Material.IRON_INGOT);
        append(recipes.get(10), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(10), new int[]{2, 3}, Material.IRON_INGOT);
        append(recipes.get(10), new int[]{2, 4}, Material.STICK);
        append(recipes.get(10), new int[]{2, 5}, Material.IRON_INGOT);

        // IRON GREATSWORD
        append(recipes.get(11), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{2, 2}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{2, 3}, Material.RESIN_BRICK);
        append(recipes.get(11), new int[]{3, 3}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{0, 4}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{1, 4}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(11), new int[]{3, 4}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{4, 4}, Material.IRON_INGOT);
        append(recipes.get(11), new int[]{2, 5}, Material.IRON_INGOT);

        // IRON HALBERD
        append(recipes.get(12), new int[]{1, 0}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{0, 1}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{0, 2}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{2, 2}, Material.IRON_AXE);
        append(recipes.get(12), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(12), new int[]{2, 3}, Material.STICK);
        append(recipes.get(12), new int[]{2, 4}, Material.STICK);
        append(recipes.get(12), new int[]{2, 5}, Material.STICK, 2);

        // IRON SCYTHE
        append(recipes.get(13), new int[]{0, 0}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{3, 0}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{4, 0}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{0, 1}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{2, 1}, Material.RESIN_BRICK);
        append(recipes.get(13), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{4, 1}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{0, 2}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{1, 2}, Material.STICK, 2);
        append(recipes.get(13), new int[]{4, 2}, Material.IRON_INGOT);
        append(recipes.get(13), new int[]{1, 3}, Material.STICK);
        append(recipes.get(13), new int[]{2, 3}, Material.STICK);
        append(recipes.get(13), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(13), new int[]{2, 5}, Material.IRON_INGOT);

        // DIAMOND LIGHT SPEAR
        append(recipes.get(14), new int[]{2, 0}, Material.DIAMOND);
        append(recipes.get(14), new int[]{2, 1}, Material.DIAMOND_SWORD);
        append(recipes.get(14), new int[]{2, 2}, Material.STICK, 2);
        append(recipes.get(14), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(14), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(14), new int[]{2, 5}, Material.DIAMOND);

        // IRON LIGHT SPEAR
        append(recipes.get(15), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(15), new int[]{2, 1}, Material.IRON_SWORD);
        append(recipes.get(15), new int[]{2, 2}, Material.STICK, 2);
        append(recipes.get(15), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(15), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(15), new int[]{2, 5}, Material.IRON_INGOT);

        // IRON MACE
        append(recipes.get(16), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(16), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(16), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(16), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(16), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(16), new int[]{2, 2}, Material.IRON_INGOT);
        append(recipes.get(16), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(16), new int[]{2, 3}, Material.STICK);
        append(recipes.get(16), new int[]{2, 4}, Material.STICK);
        append(recipes.get(16), new int[]{2, 5}, Material.IRON_INGOT);

        // RIA
        append(recipes.get(17), new int[]{2, 0}, Material.IRON_INGOT, 3);
        append(recipes.get(17), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(17), new int[]{2, 1}, Material.RESIN_BRICK, 2);
        append(recipes.get(17), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(17), new int[]{1, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(17), new int[]{2, 2}, Material.STICK, 2);
        append(recipes.get(17), new int[]{3, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(17), new int[]{0, 3}, Material.IRON_INGOT);
        append(recipes.get(17), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(17), new int[]{4, 3}, Material.IRON_INGOT);
        append(recipes.get(17), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(17), new int[]{2, 5}, Material.IRON_INGOT, 2);

        // IRON SPLIT SWORD
        append(recipes.get(18), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{2, 3}, Material.RESIN_BRICK);
        append(recipes.get(18), new int[]{3, 3}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{0, 4}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{1, 4}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{2, 4}, Material.STICK, 3);
        append(recipes.get(18), new int[]{3, 4}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{4, 4}, Material.IRON_INGOT);
        append(recipes.get(18), new int[]{2, 5}, Material.IRON_INGOT);

        // IRON STAFF POINT
        append(recipes.get(19), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(19), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(19), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(19), new int[]{1, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(19), new int[]{2, 2}, Material.IRON_INGOT);
        append(recipes.get(19), new int[]{3, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(19), new int[]{2, 3}, Material.STICK, 3);
        append(recipes.get(19), new int[]{2, 4}, Material.STICK, 3);
        append(recipes.get(19), new int[]{2, 5}, Material.IRON_INGOT);

        // IRON STAFF STRIKE
        append(recipes.get(20), new int[]{1, 0}, Material.IRON_INGOT);
        append(recipes.get(20), new int[]{3, 0}, Material.IRON_INGOT);
        append(recipes.get(20), new int[]{1, 1}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(20), new int[]{3, 1}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{0, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{2, 2}, Material.RESIN_BRICK);
        append(recipes.get(20), new int[]{4, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{0, 3}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{1, 3}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{2, 3}, Material.STICK, 3);
        append(recipes.get(20), new int[]{3, 3}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{4, 3}, Material.IRON_INGOT, 2);
        append(recipes.get(20), new int[]{2, 4}, Material.STICK, 3);
        append(recipes.get(20), new int[]{2, 5}, Material.IRON_INGOT);

        // IRON STUBBY AXE
        append(recipes.get(21), new int[]{0, 1}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{1, 1}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{4, 1}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{0, 2}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{2, 2}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{4, 2}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{2, 3}, Material.STICK);
        append(recipes.get(21), new int[]{3, 3}, Material.IRON_INGOT);
        append(recipes.get(21), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(21), new int[]{2, 5}, Material.IRON_INGOT);

        // ZWEIHANDER
        append(recipes.get(22), new int[]{2, 0}, Material.IRON_INGOT, 2);
        append(recipes.get(22), new int[]{2, 1}, Material.IRON_INGOT, 2);
        append(recipes.get(22), new int[]{2, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(22), new int[]{1, 3}, Material.IRON_INGOT, 2);
        append(recipes.get(22), new int[]{2, 3}, Material.RESIN_BRICK, 2);
        append(recipes.get(22), new int[]{3, 3}, Material.IRON_INGOT, 2);
        append(recipes.get(22), new int[]{1, 4}, Material.IRON_INGOT);
        append(recipes.get(22), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(22), new int[]{3, 4}, Material.IRON_INGOT);
        append(recipes.get(22), new int[]{2, 5}, Material.IRON_INGOT);

        // THALLEOUS
        append(recipes.get(23), new int[]{2, 0}, Material.DIAMOND, 2);
        append(recipes.get(23), new int[]{2, 1}, Material.DIAMOND);
        append(recipes.get(23), new int[]{1, 2}, Material.DIAMOND);
        append(recipes.get(23), new int[]{2, 2}, Material.RESIN_BRICK, 2);
        append(recipes.get(23), new int[]{3, 2}, Material.DIAMOND);
        append(recipes.get(23), new int[]{1, 3}, Material.DIAMOND, 2);
        append(recipes.get(23), new int[]{2, 3}, Material.STICK);
        append(recipes.get(23), new int[]{3, 3}, Material.DIAMOND, 2);
        append(recipes.get(23), new int[]{0, 4}, Material.DIAMOND);
        append(recipes.get(23), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(23), new int[]{4, 4}, Material.DIAMOND);
        append(recipes.get(23), new int[]{2, 5}, Material.DIAMOND);

        // TYGREN
        append(recipes.get(24), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(24), new int[]{2, 1}, Material.IRON_INGOT);
        append(recipes.get(24), new int[]{2, 2}, Material.IRON_INGOT);
        append(recipes.get(24), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(24), new int[]{2, 3}, Material.RESIN_BRICK);
        append(recipes.get(24), new int[]{3, 3}, Material.IRON_INGOT);
        append(recipes.get(24), new int[]{1, 4}, Material.IRON_INGOT);
        append(recipes.get(24), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(24), new int[]{3, 4}, Material.IRON_INGOT);
        append(recipes.get(24), new int[]{2, 5}, Material.IRON_INGOT);

        // WARMAUL
        append(recipes.get(25), new int[]{0, 0}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{1, 0}, Material.IRON_INGOT, 2);
        append(recipes.get(25), new int[]{2, 0}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{3, 0}, Material.IRON_INGOT, 2);
        append(recipes.get(25), new int[]{4, 0}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{0, 1}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{1, 1}, Material.IRON_INGOT, 2);
        append(recipes.get(25), new int[]{2, 1}, Material.RESIN_BRICK);
        append(recipes.get(25), new int[]{3, 1}, Material.IRON_INGOT, 2);
        append(recipes.get(25), new int[]{4, 1}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{0, 2}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{1, 2}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{2, 2}, Material.RESIN_BRICK);
        append(recipes.get(25), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{4, 2}, Material.IRON_INGOT);
        append(recipes.get(25), new int[]{2, 3}, Material.STICK, 2);
        append(recipes.get(25), new int[]{2, 4}, Material.STICK, 2);
        append(recipes.get(25), new int[]{2, 5}, Material.STICK, 2);

        // IRON KATANA
        append(recipes.get(26), new int[]{2, 0}, Material.IRON_INGOT, 2);
        append(recipes.get(26), new int[]{2, 1}, Material.IRON_INGOT, 2);
        append(recipes.get(26), new int[]{3, 1}, Material.IRON_INGOT);
        append(recipes.get(26), new int[]{2, 2}, Material.IRON_INGOT, 2);
        append(recipes.get(26), new int[]{3, 2}, Material.IRON_INGOT);
        append(recipes.get(26), new int[]{1, 3}, Material.IRON_INGOT);
        append(recipes.get(26), new int[]{2, 3}, Material.IRON_INGOT, 2);
        append(recipes.get(26), new int[]{3, 3}, Material.IRON_INGOT);
        append(recipes.get(26), new int[]{1, 4}, Material.IRON_INGOT);
        append(recipes.get(26), new int[]{2, 4}, Material.STICK);
        append(recipes.get(26), new int[]{3, 4}, Material.IRON_INGOT);
        append(recipes.get(26), new int[]{2, 5}, Material.STICK, 2);
    }
}
