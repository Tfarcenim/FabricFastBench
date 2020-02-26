package tfar.fastbench.mixin;

import net.minecraft.container.CraftingResultSlot;
import net.minecraft.inventory.CraftingInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfar.fastbench.interfaces.CraftingResultSlotInterface;

@Mixin(CraftingResultSlot.class)
class CraftingResultSlotAccessor implements CraftingResultSlotInterface {

	@Shadow private int amount;

	@Shadow @Final private CraftingInventory craftingInv;

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public CraftingInventory craftInventory() {
		return craftingInv;
	}
}
