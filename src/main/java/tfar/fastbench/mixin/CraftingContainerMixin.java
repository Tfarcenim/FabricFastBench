package tfar.fastbench.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.fastbench.interfaces.CraftingInventoryDuck;

@Mixin(CraftingContainer.class)
public class CraftingContainerMixin implements CraftingInventoryDuck {

	@Shadow
	@Final
	private AbstractContainerMenu menu;
	public boolean checkMatrixChanges = true;


	@Override
	public void setCheckMatrixChanges(boolean checkMatrixChanges) {
		this.checkMatrixChanges = checkMatrixChanges;
	}

	@Override
	public boolean getCheckMatrixChanges() {
		return this.checkMatrixChanges;
	}

	@Redirect(method = {"removeItem",
			"setItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;slotsChanged(Lnet/minecraft/world/Container;)V"))
	private void checkForChanges(AbstractContainerMenu screenHandler, Container inventory) {
		if (checkMatrixChanges) menu.slotsChanged((Container) this);
	}
}
