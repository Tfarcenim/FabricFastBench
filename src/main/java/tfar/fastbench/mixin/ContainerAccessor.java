package tfar.fastbench.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface ContainerAccessor {
	@Invoker("moveItemStackTo")
	boolean insert(ItemStack stack, int startIndex, int endIndex, boolean fromLast);

}
