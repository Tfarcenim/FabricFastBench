package tfar.fastbench.interfaces;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;

public interface CraftingDuck {

	Recipe<CraftingInventory> getLastRecipe();
	Recipe<CraftingInventory> getLastLastRecipe();
	boolean checkMatrixChanges();
	void setLastRecipe(Recipe<CraftingInventory> recipe);
	void setLastLastRecipe(Recipe<CraftingInventory> recipe);
	void updateLastRecipe(Recipe<CraftingInventory> rec);
	}
