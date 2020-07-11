package tfar.fastbench.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfar.fastbench.MixinHooks;
import tfar.fastbench.interfaces.CraftingDuck;

import javax.annotation.Nullable;

@Mixin(CraftingScreenHandler.class)
abstract class CraftingContainerMixin extends ScreenHandler implements CraftingDuck {

	@Shadow @Final private CraftingInventory input;
	@Shadow @Final private CraftingResultInventory result;
	@Shadow @Final private PlayerEntity player;

	public Recipe<CraftingInventory> lastRecipe;
	protected Recipe<CraftingInventory> lastLastRecipe;
	protected boolean checkMatrixChanges = true;

	protected CraftingContainerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Override
	public void onContentChanged(Inventory inventoryIn) {
		MixinHooks.updateResult((CraftingScreenHandler) (Object) this, player, input, result);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		World world = player.world;
		if (index != 0) {
			return super.transferSlot(player, index);
		}

		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasStack()) {
			checkMatrixChanges = false;
			while (lastRecipe != null && lastRecipe.matches(this.input, this.player.world)) {
				ItemStack itemstack1 = slot.getStack();
				itemstack = itemstack1.copy();

				itemstack1.getItem().onCraft(itemstack1, world, player);

				if (!world.isClient && !this.insertItem(itemstack1, 10, 46, true)) {
					checkMatrixChanges = true;
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemstack1, itemstack);
				slot.markDirty();

				if (!world.isClient && itemstack1.getCount() == itemstack.getCount()) {
					checkMatrixChanges = true;
					return ItemStack.EMPTY;
				}

				ItemStack itemstack2 = slot.onTakeItem(player, itemstack1);
				player.dropItem(itemstack2, false);
			}
			checkMatrixChanges = true;
			MixinHooks.updateResult((CraftingScreenHandler)(Object)this, player, input, result);
		}
		return lastRecipe == null ? ItemStack.EMPTY : itemstack;
	}

	public void updateLastRecipe(Recipe<CraftingInventory> rec) {
		this.lastLastRecipe = lastRecipe;
		this.lastRecipe = rec;
		if (rec != null) this.result.setStack(0, rec.craft(input));
		else this.result.setStack(0, ItemStack.EMPTY);
	}

	@Override
	public Recipe<CraftingInventory> getLastRecipe() {
		return lastRecipe;
	}

	@Override
	public Recipe<CraftingInventory> getLastLastRecipe() {
		return lastLastRecipe;
	}

	@Override
	public boolean checkMatrixChanges() {
		return checkMatrixChanges;
	}

	@Override
	public void setLastRecipe(Recipe<CraftingInventory> recipe) {
		lastRecipe = recipe;
	}

	@Override
	public void setLastLastRecipe(Recipe<CraftingInventory> recipe) {
		lastLastRecipe = recipe;
	}
}
