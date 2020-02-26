package tfar.fastbench.interfaces;

import net.minecraft.inventory.CraftingInventory;

public interface CraftingResultSlotInterface {
	int getAmount();
	void setAmount(int amount);
	CraftingInventory craftInventory();
}
