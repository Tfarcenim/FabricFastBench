package tfar.fastbench.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfar.fastbench.CraftingInventoryInterface;

@Mixin(CraftingInventory.class)
class CraftingInventoryAccessor implements CraftingInventoryInterface {
	@Shadow @Final private DefaultedList<ItemStack> stacks;

	@Override
	public DefaultedList<ItemStack> stacks() {
		return this.stacks;
	}
}
