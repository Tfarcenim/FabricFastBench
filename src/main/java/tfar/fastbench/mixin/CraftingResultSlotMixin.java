package tfar.fastbench.mixin;


import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.fastbench.MixinHooks;

@Mixin(ResultSlot.class)
public class CraftingResultSlotMixin extends Slot {

	@Shadow @Final private CraftingContainer craftSlots;

	public CraftingResultSlotMixin(Container inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Redirect(method = "takeStack",at = @At(value = "INVOKE",target = "Lnet/minecraft/screen/slot/Slot;takeStack(I)Lnet/minecraft/item/ItemStack;"))
	private ItemStack copy(Slot slot, int amount) {
		return slot.getItem().copy();
	}

	@Override
	public void setStack(ItemStack stack) {
		//do nothing
	}

	@Redirect(method = "checkTakeAchievements",
					at = @At(value = "INVOKE",target = "Lnet/minecraft/world/inventory/RecipeHolder;awardUsedRecipes(Lnet/minecraft/world/entity/player/Player;)V"))
	public void no(RecipeHolder recipeUnlocker, Player player) {
		//do nothing
	}

	//inventory is actually the crafting result inventory so it's a safe cast
	//using an inject instead of a redirect as a workaround for tech reborn's BS
	@Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRemainingItemsFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Lnet/minecraft/core/NonNullList;"))
	private void cache(Player player, ItemStack stack, CallbackInfo ci) {
		Recipe<CraftingContainer> lastRecipe = (Recipe<CraftingContainer>) ((ResultContainer)this.craftSlots).getLastRecipe();
		MixinHooks.lastRecipe = lastRecipe != null && lastRecipe.matches(craftSlots, player.level) ? lastRecipe : null;
		MixinHooks.hascachedrecipe = true;
	}
}