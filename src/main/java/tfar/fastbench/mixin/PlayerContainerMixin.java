package tfar.fastbench.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.FastBenchPlayerSlot;
import tfar.fastbench.MixinHooks;
import tfar.fastbench.interfaces.PlayerContainerInterface;

import javax.annotation.Nullable;

@Mixin(PlayerScreenHandler.class)
abstract class PlayerContainerMixin extends ScreenHandler implements PlayerContainerInterface {

	@Shadow @Final private PlayerEntity owner;
	@Shadow @Final private CraftingInventory craftingInput;
	@Shadow @Final private CraftingResultInventory craftingResult;
	@Unique public Recipe<CraftingInventory> lastRecipe;
	@Unique protected Recipe<CraftingInventory> lastLastRecipe;
	@Unique protected boolean checkMatrixChanges = true;
	@Unique protected boolean useNormalTransfer = false;

	@Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V",
					at = @At("RETURN"))
	private void replaceCraftingSlot(PlayerInventory inv, boolean local, PlayerEntity player, CallbackInfo ci) {
		Slot slot = new FastBenchPlayerSlot((PlayerScreenHandler) (Object) this, player, craftingInput, craftingResult,
						0, 154, 28);
		slot.id = 0;
		slot.setStack(ItemStack.EMPTY);
		slots.set(0, slot);
	}

	@Inject(method = "onContentChanged", at = @At("HEAD"), cancellable = true)
	private void updateResult(Inventory inventory, CallbackInfo ci) {
		PlayerScreenHandler playerContainer = (PlayerScreenHandler) (Object) this;
		MixinHooks.updateResultP(playerContainer, owner.world, this.owner, this.craftingInput, this.craftingResult);
		ci.cancel();
	}

	@Inject(method = "transferSlot", at = @At("HEAD"), cancellable = true)
	private void shiftClick(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
		if (useNormalTransfer || index != 0) return;


		ItemStack stackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasStack()) {
			checkMatrixChanges = false;
			while (lastRecipe != null && lastRecipe.matches(craftingInput, player.world)) {
				ItemStack stack = slot.getStack();
				stackCopy = stack.copy();

				stack.getItem().onCraft(stack, player.world, player);

				if (!player.world.isClient && !this.insertItem(stack, 9, 45, true)) {
					checkMatrixChanges = true;
					cir.setReturnValue(ItemStack.EMPTY);
					return;
				}

				slot.onStackChanged(stack, stackCopy);
				slot.markDirty();

				if (!player.world.isClient && stack.getCount() == stackCopy.getCount()) {
					checkMatrixChanges = true;
					cir.setReturnValue(ItemStack.EMPTY);
					return;
				}

				ItemStack itemstack2 = slot.onTakeItem(player, stack);
				player.dropItem(itemstack2, false);
			}
			checkMatrixChanges = true;
			MixinHooks.updateResultP((PlayerScreenHandler) (Object) this, player.world, player, craftingInput, craftingResult);
		}
		cir.setReturnValue(lastRecipe == null ? ItemStack.EMPTY : stackCopy);
	}

	@Override
	public Recipe<CraftingInventory> getLastRecipe() {
		return lastRecipe;
	}

	@Override
	public void setLastRecipe(Recipe<CraftingInventory> recipe) {
		lastRecipe = recipe;
	}

	@Override
	public Recipe<CraftingInventory> getLastLastRecipe() {
		return lastLastRecipe;
	}

	@Override
	public void setLastLastRecipe(Recipe<CraftingInventory> recipe) {
		this.lastLastRecipe = recipe;
	}

	public void updateLastRecipe(Recipe<CraftingInventory> rec) {
		this.lastLastRecipe = lastRecipe;
		this.lastRecipe = rec;
		if (rec != null) this.craftingResult.setStack(0, rec.craft(craftingInput));
		else this.craftingResult.setStack(0, ItemStack.EMPTY);
	}

	@Override
	public boolean checkMatrixChanges() {
		return checkMatrixChanges;
	}

	protected PlayerContainerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}
}
