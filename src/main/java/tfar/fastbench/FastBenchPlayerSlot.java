package tfar.fastbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.collection.DefaultedList;
import tfar.fastbench.mixin.CraftingInventoryAccessor;
import tfar.fastbench.mixin.CraftingResultSlotAccessor;
import tfar.fastbench.interfaces.PlayerContainerInterface;

public class FastBenchPlayerSlot extends CraftingResultSlot {

	protected final PlayerScreenHandler container;
	protected final PlayerEntity player;

	public FastBenchPlayerSlot(PlayerScreenHandler container, PlayerEntity player, CraftingInventory inv, Inventory holder, int slotIndex, int xPosition, int yPosition) {
		super(player, inv, holder, slotIndex, xPosition, yPosition);
		this.container = container;
		this.player = player;
	}


	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			int add = Math.min(amount, this.getStack().getCount());
			_setAmount(_getAmount() + add);
		}
		return super.takeStack(amount);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
	 */
	@Override
	protected void onCrafted(ItemStack stack) {
		if (this._getAmount() > 0) {
			stack.onCraft(this.player.world, this.player, this._getAmount());
		}

		_setAmount(0);
	}

	@Override
	public void onTake(int amount) {
		super.onTake(amount);
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		DefaultedList<ItemStack> list;
		if (_getLastRecipe() != null &&
						_getLastRecipe().matches(craftingInventory(), player.world))
			list = _getLastRecipe().getRemainingStacks(craftingInventory());
		else list = ((CraftingInventoryAccessor)(Object)craftingInventory()).getStacks();

		for (int i = 0; i < list.size(); ++i) {
			ItemStack itemstack = this.craftingInventory().getStack(i);
			ItemStack itemstack1 = list.get(i);

			if (!itemstack.isEmpty()) {
				this.craftingInventory().removeStack(i, 1);
				itemstack = this.craftingInventory().getStack(i);
			}

			if (!itemstack1.isEmpty()) {
				if (itemstack.isEmpty()) {
					this.craftingInventory().setStack(i, itemstack1);
				} else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemsEqual(itemstack, itemstack1)) {
					itemstack1.increment(itemstack.getCount());
					this.craftingInventory().setStack(i, itemstack1);
				} else if (!this.player.inventory.insertStack(itemstack1)) {
					this.player.dropItem(itemstack1, false);
				}
			}
		}

		return stack;
	}

	public Recipe<CraftingInventory> _getLastRecipe(){
		return ((PlayerContainerInterface)container).getLastRecipe();
	}

	public void _setAmount(int amount){
		((CraftingResultSlotAccessor)this).setAmount(amount);
	}

	public int _getAmount(){
		return ((CraftingResultSlotAccessor)this).getAmount();
	}

	public CraftingInventory craftingInventory(){
		return ((CraftingResultSlotAccessor)this).getInput();
	}
}