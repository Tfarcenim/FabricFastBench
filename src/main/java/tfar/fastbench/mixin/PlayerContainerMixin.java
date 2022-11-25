package tfar.fastbench.mixin;


import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.MixinHooks;

import javax.annotation.Nullable;

@Mixin(InventoryMenu.class)
abstract class PlayerContainerMixin extends AbstractContainerMenu {

	@Shadow @Final private Player owner;
	@Shadow @Final private CraftingContainer craftSlots;
	@Shadow @Final private ResultContainer resultSlots;

	@Inject(method = "slotsChanged", at = @At("HEAD"), cancellable = true)
	private void updateResult(Container inventory, CallbackInfo ci) {
		MixinHooks.slotChangedCraftingGrid(owner.level, owner, this, craftSlots, resultSlots);
	}

	@Inject(method = "quickMoveStack",at = @At("HEAD"),cancellable = true)
	private void handleShiftCraft(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
		if (index != 0) return;
		cir.setReturnValue(MixinHooks.handleShiftCraft(player, this, this.slots.get(index), this.craftSlots, this.resultSlots, 9, 45));
	}

	protected PlayerContainerMixin(@Nullable MenuType<?> type, int syncId) {
		super(type, syncId);
	}
}
