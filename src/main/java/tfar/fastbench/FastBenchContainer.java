package tfar.fastbench;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
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
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		this.slotChangedCraftingGrid(world, player, getCraftingInventory(), getCraftingResultInventory());
	}

	protected void slotChangedCraftingGrid(World world, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			if (checkMatrixChanges && (lastRecipe == null || !lastRecipe.matches(inv, world)))
				lastRecipe = findRecipe(inv, world);

			if (lastRecipe != null) {
				itemstack = lastRecipe.craft(inv);
			}

			result.setInvStack(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (lastLastRecipe != lastRecipe) serverPlayerEntity.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(syncId, 0, itemstack));
			else if (lastLastRecipe != null && lastLastRecipe == lastRecipe && !ItemStack.areItemsEqual(lastLastRecipe.craft(inv), lastRecipe.craft(inv)))
				serverPlayerEntity.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(syncId, 0, itemstack));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(lastRecipe != null ? lastRecipe.getId().toString() : "null");
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,FastBenchClient.recipe_sync, buf);
			lastLastRecipe = lastRecipe;
		}
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
			this.slotChangedCraftingGrid(world, player, getCraftingInventory(), getCraftingResultInventory());
		}
		return lastRecipe == null ? ItemStack.EMPTY : itemstack;
	}

	public void updateLastRecipe(Recipe<CraftingInventory> rec) {
		this.lastLastRecipe = lastRecipe;
		this.lastRecipe = rec;
		if (rec != null) this.getCraftingResultInventory().setInvStack(0, rec.craft(getCraftingInventory()));
		else this.getCraftingResultInventory().setInvStack(0, ItemStack.EMPTY);
	}

	public static Recipe<CraftingInventory> findRecipe(CraftingInventory inv, World world) {
		return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inv, world).orElse(null);
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
