package tfar.fastbench;

				import net.minecraft.container.CraftingResultSlot;
				import net.minecraft.entity.player.PlayerEntity;
				import net.minecraft.inventory.CraftingInventory;
				import net.minecraft.inventory.Inventory;
				import net.minecraft.item.ItemStack;
				import net.minecraft.util.DefaultedList;
				import tfar.fastbench.mixin.CraftingInventoryAccessor;
				import tfar.fastbench.mixin.CraftingResultSlotAccessor;

public class FastBenchSlot extends CraftingResultSlot {

	protected final FastBenchContainer container;
	protected final PlayerEntity player;

	public FastBenchSlot(FastBenchContainer container, PlayerEntity player, CraftingInventory inv, Inventory holder, int slotIndex, int xPosition, int yPosition) {
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
		if (container.lastRecipe != null &&
						container.lastRecipe.matches(craftingInventory(), container.world))
			list = container.lastRecipe.getRemainingStacks(craftingInventory());
		else list = ((CraftingInventoryAccessor)(Object)craftingInventory()).getStacks();

		for (int i = 0; i < list.size(); ++i) {
			ItemStack itemstack = this.craftingInventory().getInvStack(i);
			ItemStack itemstack1 = list.get(i);

			if (!itemstack.isEmpty()) {
				this.craftingInventory().takeInvStack(i, 1);
				itemstack = this.craftingInventory().getInvStack(i);
			}

			if (!itemstack1.isEmpty()) {
				if (itemstack.isEmpty()) {
					this.craftingInventory().setInvStack(i, itemstack1);
				} else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemsEqual(itemstack, itemstack1)) {
					itemstack1.increment(itemstack.getCount());
					this.craftingInventory().setInvStack(i, itemstack1);
				} else if (!this.player.inventory.insertStack(itemstack1)) {
					this.player.dropItem(itemstack1, false);
				}
			}
		}

		return stack;
	}

	public void _setAmount(int amount){
		((CraftingResultSlotAccessor)this).setAmount(amount);
	}

	public int _getAmount(){
		return ((CraftingResultSlotAccessor)this).getAmount();
	}

	public CraftingInventory craftingInventory(){
		return ((CraftingResultSlotAccessor)this).getCraftingInv();
	}
}