package tfar.fastbench.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingContainer.class)
public interface CraftingInventoryAccessor {
	@Accessor
	NonNullList<ItemStack> getItems();
}
