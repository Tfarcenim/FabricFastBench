package tfar.fastbench.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreenHandler.class)
public interface CraftingContainerAccessor {
	@Accessor
	CraftingInventory getInput();
	@Accessor
	CraftingResultInventory getResult();
}
