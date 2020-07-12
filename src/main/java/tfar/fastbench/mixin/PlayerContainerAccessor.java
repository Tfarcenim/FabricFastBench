package tfar.fastbench.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerScreenHandler.class)
public interface PlayerContainerAccessor {
	@Accessor
	CraftingInventory getCraftingInput();
	@Accessor
	CraftingResultInventory getCraftingResult();
}
