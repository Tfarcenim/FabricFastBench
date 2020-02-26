package tfar.fastbench.interfaces;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;

public interface PlayerContainerInterface {
	Recipe<CraftingInventory> getLastRecipe();
	void setLastRecipe(Recipe<CraftingInventory> recipe);
	Recipe<CraftingInventory> getLastLastRecipe();
	void setLastLastRecipe(Recipe<CraftingInventory> recipe);
	boolean checkMatrixChanges();
	void updateLastRecipe(Recipe<CraftingInventory> recipe);
}
