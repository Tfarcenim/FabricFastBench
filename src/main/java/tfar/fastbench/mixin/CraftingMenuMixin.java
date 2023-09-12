package tfar.fastbench.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.MixinHooks;
import tfar.fastbench.interfaces.CraftingInventoryDuck;

import javax.annotation.Nullable;

@Mixin(CraftingMenu.class)
abstract class CraftingMenuMixin<C extends Container> extends RecipeBookMenu<C> implements CraftingInventoryDuck {

	@Shadow
	@Final
	private CraftingContainer craftSlots;
	@Shadow
	@Final
	private ResultContainer resultSlots;
	@Shadow
	@Final
	private Player player;

	@Shadow
	@Final
	private ContainerLevelAccess access;

	protected CraftingMenuMixin(@Nullable MenuType<?> type, int syncId) {
		super(type, syncId);
	}

	@Overwrite
	public void slotsChanged(Container inventory) {
		access.execute((lvl, pos) -> MixinHooks.slotChangedCraftingGrid(this.player.level, craftSlots, resultSlots));
	}

	@Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
	private void handleShiftCraft(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
		if (index != 0) return;
		cir.setReturnValue(MixinHooks.handleShiftCraft(player, this, this.slots.get(index), this.craftSlots, this.resultSlots, 10, 46));
	}

	/**
	 * Resets the checkMatrixChanges flag on return.
	 */
	@Inject(method = "clearCraftingContent", at = @At("RETURN"))
	private void fastbench$setCheckMatrixTrue(CallbackInfo ci) {
		setCheckMatrixChanges(true);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Proxies the setCheckMatrixChanges call to the {@link #craftSlots}.
	 */
	@Override
	public void setCheckMatrixChanges(final boolean checkMatrixChanges) {
		((CraftingInventoryDuck) craftSlots).setCheckMatrixChanges(checkMatrixChanges);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Proxies the getCheckMatrixChanges call to the {@link #craftSlots}.
	 */
	@Override
	public boolean getCheckMatrixChanges() {
		return ((CraftingInventoryDuck) craftSlots).getCheckMatrixChanges();
	}
}
