package tfar.fastbench.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin extends Slot {

	@Shadow
	@Final
	private PlayerEntity player;
	@Shadow
	@Final
	private CraftingInventory input;

	@Shadow
	private int amount;

	public CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Redirect(method = "takeStack",at = @At(value = "INVOKE",target = "Lnet/minecraft/screen/slot/Slot;takeStack(I)Lnet/minecraft/item/ItemStack;"))
	private ItemStack copy(Slot slot, int amount) {
		return slot.getStack().copy();
	}

	@Override
	public void setStack(ItemStack stack) {
		//do nothing
	}

	@Redirect(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V",
					at = @At(value = "INVOKE",target = "Lnet/minecraft/recipe/RecipeUnlocker;unlockLastRecipe(Lnet/minecraft/entity/player/PlayerEntity;)V"))
	public void no(RecipeUnlocker recipeUnlocker, PlayerEntity player) {
		//do nothing
	}

	//inventory is actually the crafting result inventory so it's a safe cast
	@Redirect(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getRemainingStacks(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Lnet/minecraft/util/collection/DefaultedList;"))
	private DefaultedList<ItemStack> cache(RecipeManager recipeManager, RecipeType<CraftingRecipe> recipeType, Inventory craftInput, World world) {
		Recipe<CraftingInventory> lastRecipe = (Recipe<CraftingInventory>) ((CraftingResultInventory)this.inventory).getLastRecipe();
		if (lastRecipe != null &&
						lastRecipe.matches(input, player.world))
			return lastRecipe.getRemainingStacks(input);
		else return ((CraftingInventoryAccessor) input).getStacks();
	}
}