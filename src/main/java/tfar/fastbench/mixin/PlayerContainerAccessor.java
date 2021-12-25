package tfar.fastbench.mixin;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryMenu.class)
public interface PlayerContainerAccessor {
	@Accessor
	CraftingContainer getCraftSlots();
	@Accessor
	ResultContainer getResultSlots();
}
