package tfar.fastbench.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.fastbench.interfaces.CraftingInventoryDuck;

@Mixin(CraftingInventory.class)
public class CraftingInventoryMixin implements CraftingInventoryDuck {

	@Shadow @Final private ScreenHandler handler;
	public boolean checkMatrixChanges = true;


	@Override
	public void setCheckMatrixChanges(boolean checkMatrixChanges) {
		this.checkMatrixChanges = checkMatrixChanges;
	}

	@Redirect(method = {"removeStack(II)Lnet/minecraft/item/ItemStack;",
					"setStack"},at = @At(value = "INVOKE",target = "Lnet/minecraft/screen/ScreenHandler;onContentChanged(Lnet/minecraft/inventory/Inventory;)V"))
	private void checkForChanges(ScreenHandler screenHandler, Inventory inventory) {
		if (checkMatrixChanges)handler.onContentChanged((Inventory)this);
	}

}
