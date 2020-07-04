package tfar.fastbench.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.fastbench.interfaces.CraftingScreenHandlerDuck;

import java.util.List;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin extends Slot {

	@Shadow @Final private PlayerEntity player;
	@Shadow @Final private CraftingInventory input;

	public CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Redirect(method = "onTakeItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/recipe/RecipeManager;getRemainingStacks(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Lnet/minecraft/util/collection/DefaultedList;"))
	private DefaultedList<ItemStack> cache(RecipeManager recipeManager, RecipeType<CraftingRecipe> recipeType, Inventory inventory, World world){
		Recipe<CraftingInventory> lastRecipe = ((CraftingScreenHandlerDuck) player.currentScreenHandler).lastRecipe();
		if (lastRecipe != null &&
						lastRecipe.matches(input, player.world))
			return lastRecipe.getRemainingStacks(input);
		else return ((CraftingInventoryAccessor) input).getStacks();
	}
}