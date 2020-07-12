package tfar.fastbench.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.MixinHooks;

import javax.annotation.Nullable;

@Mixin(CraftingScreenHandler.class)
abstract class CraftingContainerMixin<C extends Inventory> extends AbstractRecipeScreenHandler<C> {

	@Shadow @Final private CraftingInventory input;
	@Shadow @Final private CraftingResultInventory result;
	@Shadow @Final private PlayerEntity player;

	protected CraftingContainerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}


	@Overwrite
	public void onContentChanged(Inventory inventory) {
		MixinHooks.slotChangedCraftingGrid(this.player.world, player, input, result);
	}

	@Inject(method = "transferSlot",at = @At("HEAD"),cancellable = true)
	private void handleShiftCraft(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
		if (index != 0) return;
		cir.setReturnValue(MixinHooks.handleShiftCraft(player, this, this.slots.get(index), this.input, this.result, 10, 46));
	}
}
