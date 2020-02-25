package tfar.fastbench;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;

public interface CraftingTableContainerInterface {
	CraftingInventory craftingInventory();
	CraftingResultInventory craftingResultInventory();
}
