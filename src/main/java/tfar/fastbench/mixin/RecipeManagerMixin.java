package tfar.fastbench.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.MixinHooks;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

	//note: C is actually CraftingInventory, treat it accordingly
	@Inject(method = "getRemainingItemsFor",at = @At("HEAD"),cancellable = true)
	private <C extends Container, T extends Recipe<C>> void techRebornWorkAround(RecipeType<T> recipeType, C craftInput, Level world, CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
		if (MixinHooks.hascachedrecipe) {
			if (MixinHooks.lastRecipe != null) cir.setReturnValue(MixinHooks.lastRecipe.getRemainingItems((CraftingContainer) craftInput));
			else cir.setReturnValue(((CraftingInventoryAccessor) craftInput).getItems());
			MixinHooks.hascachedrecipe = false;
		}
	}
}
