package tfar.fastbench.mixin;

import net.minecraft.container.CraftingResultSlot;
import net.minecraft.inventory.CraftingInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingResultSlot.class)
public interface CraftingResultSlotAccessor {
	@Accessor int getAmount();
	@Accessor void setAmount(int amount);
	@Accessor CraftingInventory getCraftingInv();
}
