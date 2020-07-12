package tfar.fastbench.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.MixinHooks;

import javax.annotation.Nullable;

@Mixin(PlayerScreenHandler.class)
abstract class PlayerContainerMixin extends ScreenHandler {

	@Shadow @Final private PlayerEntity owner;
	@Shadow @Final private CraftingInventory craftingInput;
	@Shadow @Final private CraftingResultInventory craftingResult;

	@Inject(method = "onContentChanged", at = @At("HEAD"), cancellable = true)
	private void updateResult(Inventory inventory, CallbackInfo ci) {
		MixinHooks.slotChangedCraftingGrid(owner.world,owner,craftingInput,craftingResult);
	}

	@Inject(method = "transferSlot",at = @At("HEAD"),cancellable = true)
	private void handleShiftCraft(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
		if (index != 0) return;
		cir.setReturnValue(MixinHooks.handleShiftCraft(player, this, this.slots.get(index), this.craftingInput, this.craftingResult, 9, 45));
	}

	protected PlayerContainerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}
}
