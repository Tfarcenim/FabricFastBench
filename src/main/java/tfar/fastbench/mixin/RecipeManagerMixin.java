package tfar.fastbench.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.MixinHooks;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

	//note: C is actually CraftingInventory, treat it accordingly
	@Inject(method = "getRemainingStacks",at = @At("HEAD"),cancellable = true)
	private <C extends Inventory, T extends Recipe<C>> void techRebornWorkAround(RecipeType<T> recipeType, C craftInput, World world, CallbackInfoReturnable<DefaultedList<ItemStack>> cir) {
		if (MixinHooks.hascachedrecipe) {
			if (MixinHooks.lastRecipe != null) cir.setReturnValue(MixinHooks.lastRecipe.getRemainingStacks((CraftingInventory) craftInput));
			else cir.setReturnValue(((CraftingInventoryAccessor) craftInput).getStacks());
			MixinHooks.hascachedrecipe = false;
		}
	}
}
