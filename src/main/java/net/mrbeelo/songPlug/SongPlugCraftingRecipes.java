package net.mrbeelo.songPlug;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongPlugCraftingRecipes {
    static List<Map<Integer, Material>> recipes = new ArrayList<>();
    static int recipeAmount = 1;

    public static int coordsToSlot(int[] coords) {
        int slot = 0;
        slot += 2 + coords[0];
        slot += 9 * coords[1];
        return slot;
    }

    public static void append(Map<Integer, Material> recipe, int[] coords, Material material) {
        recipe.put(coordsToSlot(coords), material);
    }

    public static ItemStack getRecipeResult(int index) {
        switch(index) {
            case 0: return new ItemStack(Material.DIAMOND_SWORD);
        }

        return ItemStack.empty();
    }

    public static void initRecipes() {
        for(int i = 0; i < recipeAmount; i++) recipes.add(new HashMap<>());
        append(recipes.getFirst(), new int[]{0, 0}, Material.DIAMOND);
        append(recipes.getFirst(), new int[]{1, 0}, Material.IRON_INGOT);
    }
}
