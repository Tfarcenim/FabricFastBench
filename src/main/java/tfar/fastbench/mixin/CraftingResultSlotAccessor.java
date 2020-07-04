package tfar.fastbench.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingResultSlot.class)
public interface CraftingResultSlotAccessor {
	@Accessor int getAmount();
	@Accessor void setAmount(int amount);
	@Accessor CraftingInventory getInput();
}
