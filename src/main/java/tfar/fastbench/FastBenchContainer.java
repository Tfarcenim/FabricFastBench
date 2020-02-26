package tfar.fastbench;

import net.minecraft.container.BlockContext;
import net.minecraft.container.ContainerType;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tfar.fastbench.interfaces.CraftingTableContainerInterface;

public class FastBenchContainer  extends CraftingTableContainer {

	protected final World world;
	public Recipe<CraftingInventory> lastRecipe;
	protected Recipe<CraftingInventory> lastLastRecipe;
	protected final BlockPos pos;
	protected boolean checkMatrixChanges = true;
	protected boolean useNormalTransfer = false;
	protected PlayerEntity player;

	public FastBenchContainer(int syncId, PlayerEntity player,World world, BlockPos pos) {
		super(syncId,player.inventory, BlockContext.create(world,pos));
		Slot slot = new FastBenchSlot(this, player, ((CraftingTableContainerInterface)this).craftingInventory(),
										((CraftingTableContainerInterface)this).craftingResultInventory(), 0, 124, 35);
		slot.id = 0;
		slot.setStack(ItemStack.EMPTY);
		slotList.set(0,slot);
		this.world = player.world;
		this.pos = pos;
		this.player = player;
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onContentChanged(Inventory inventoryIn) {
		MixinHooks.updateResult(this,world, player, getCraftingInventory(), getCraftingResultInventory());
	}



	@Override
	public void close(PlayerEntity player) {
		if (pos != BlockPos.ZERO) super.close(player);
		else {
			PlayerInventory inv = player.inventory;
			if (!inv.getCursorStack().isEmpty()) {
				player.dropItem(inv.getCursorStack(), false);
				inv.setCursorStack(ItemStack.EMPTY);
			}
			if (!this.world.isClient) this.dropInventory(player, this.world, this.getCraftingInventory());
		}
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		if (useNormalTransfer || index != 0) return super.transferSlot(player, index);

		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slotList.get(index);

		if (slot != null && slot.hasStack()) {
			checkMatrixChanges = false;
			while (lastRecipe != null && lastRecipe.matches(this.getCraftingInventory(), this.world)) {
				ItemStack itemstack1 = slot.getStack();
				itemstack = itemstack1.copy();

				itemstack1.getItem().onCraft(itemstack1, this.world, player);

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
			MixinHooks.updateResult(this,world, player, getCraftingInventory(), getCraftingResultInventory());
		}
		return lastRecipe == null ? ItemStack.EMPTY : itemstack;
	}

	public void updateLastRecipe(Recipe<CraftingInventory> rec) {
		this.lastLastRecipe = lastRecipe;
		this.lastRecipe = rec;
		if (rec != null) this.getCraftingResultInventory().setInvStack(0, rec.craft(getCraftingInventory()));
		else this.getCraftingResultInventory().setInvStack(0, ItemStack.EMPTY);
	}

	@Override
	public ContainerType<?> getType() {
		return null;
	}


	public CraftingInventory getCraftingInventory(){
		return ((CraftingTableContainerInterface)this).craftingInventory();
	}

	public CraftingResultInventory getCraftingResultInventory(){
		return ((CraftingTableContainerInterface)this).craftingResultInventory();
	}

}
