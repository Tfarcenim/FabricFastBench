package tfar.fastbench;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import tfar.fastbench.interfaces.CraftingInventoryDuck;
import tfar.fastbench.mixin.ContainerAccessor;

public class MixinHooks {

	public static void slotChangedCraftingGrid(World world, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			Recipe<CraftingInventory> recipe = (Recipe<CraftingInventory>) result.getLastRecipe();
			if (recipe == null || !recipe.matches(inv, world)) recipe = findRecipe(inv, world);

			if (recipe != null) {
				itemstack = recipe.craft(inv);
			}

			result.setStack(0, itemstack);
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeIdentifier(recipe != null ? recipe.getId(): new Identifier("null","null"));
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FastBench.recipe_sync, buf);

			result.setLastRecipe(recipe);
		}
	}

	public static ItemStack handleShiftCraft(PlayerEntity player, ScreenHandler container, Slot resultSlot, CraftingInventory input, CraftingResultInventory craftResult, int outStart, int outEnd) {
		ItemStack outputCopy = ItemStack.EMPTY;
		CraftingInventoryDuck duck = (CraftingInventoryDuck)input;
		duck.setCheckMatrixChanges(false);
		if (resultSlot != null && resultSlot.hasStack()) {

			Recipe<CraftingInventory> recipe = (Recipe<CraftingInventory>) craftResult.getLastRecipe();
			while (recipe != null && recipe.matches(input, player.world)) {
				ItemStack recipeOutput = resultSlot.getStack().copy();
				outputCopy = recipeOutput.copy();

				recipeOutput.getItem().onCraft(recipeOutput, player.world, player);

				if (!player.world.isClient && !((ContainerAccessor)container).insert(recipeOutput, outStart, outEnd,true)) {
					duck.setCheckMatrixChanges(true);
					return ItemStack.EMPTY;
				}

				resultSlot.onStackChanged(recipeOutput, outputCopy);
				resultSlot.markDirty();

				if (!player.world.isClient && recipeOutput.getCount() == outputCopy.getCount()) {
					duck.setCheckMatrixChanges(true);
					return ItemStack.EMPTY;
				}

				ItemStack itemstack2 = resultSlot.onTakeItem(player, recipeOutput);
				player.dropItem(itemstack2, false);
			}
			duck.setCheckMatrixChanges(true);
			slotChangedCraftingGrid(player.world, player, input, craftResult);
		}
		duck.setCheckMatrixChanges(true);
		return craftResult.getLastRecipe() == null ? ItemStack.EMPTY : outputCopy;
	}

	public static Recipe<CraftingInventory> findRecipe(CraftingInventory inv, World world) {
		return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inv, world).orElse(null);
	}
}
