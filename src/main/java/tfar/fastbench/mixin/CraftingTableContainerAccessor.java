package tfar.fastbench.mixin;

import net.minecraft.container.CraftingTableContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfar.fastbench.CraftingTableContainerInterface;

@Mixin(CraftingTableContainer.class)
class CraftingTableContainerAccessor implements CraftingTableContainerInterface {
	@Shadow @Final private CraftingInventory craftingInv;

	@Shadow @Final private CraftingResultInventory resultInv;

	@Override
	public CraftingInventory craftingInventory() {
		return this.craftingInv;
	}

	@Override
	public CraftingResultInventory craftingResultInventory() {
		return resultInv;
	}
}
