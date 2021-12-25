package tfar.fastbench;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tfar.fastbench.interfaces.CraftingInventoryDuck;
import tfar.fastbench.mixin.ContainerAccessor;

public class MixinHooks {

	public static boolean hascachedrecipe = false;

	public static Recipe<CraftingContainer> lastRecipe;

	public static void slotChangedCraftingGrid(Level level, Player player, CraftingContainer inv, ResultContainer result) {
		if (!level.isClientSide) {

			ItemStack itemstack = ItemStack.EMPTY;

			Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>) result.getRecipeUsed();
			if (recipe == null || !recipe.matches(inv, level)) recipe = findRecipe(inv, level);

			if (recipe != null) {
				itemstack = recipe.assemble(inv);
			}

			result.setItem(0, itemstack);
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeResourceLocation(recipe != null ? recipe.getId(): new ResourceLocation("null","null"));
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FastBench.recipe_sync, buf);

			result.setRecipeUsed(recipe);
		}
	}

	public static ItemStack handleShiftCraft(Player player, AbstractContainerMenu container, Slot resultSlot, CraftingContainer input, ResultContainer craftResult, int outStart, int outEnd) {
		ItemStack outputCopy = ItemStack.EMPTY;
		CraftingInventoryDuck duck = (CraftingInventoryDuck)input;
		duck.setCheckMatrixChanges(false);
		if (resultSlot != null && resultSlot.hasItem()) {

			Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>) craftResult.getRecipeUsed();

			while (recipe != null && recipe.matches(input, player.level)) {
				ItemStack recipeOutput = resultSlot.getItem().copy();
				outputCopy = recipeOutput.copy();

				recipeOutput.getItem().onCraftedBy(recipeOutput, player.level, player);

				if (!player.level.isClientSide && !((ContainerAccessor)container).insert(recipeOutput, outStart, outEnd,true)) {
					duck.setCheckMatrixChanges(true);
					return ItemStack.EMPTY;
				}

				resultSlot.onQuickCraft(recipeOutput, outputCopy);
				resultSlot.setChanged();

				if (!player.level.isClientSide && recipeOutput.getCount() == outputCopy.getCount()) {
					duck.setCheckMatrixChanges(true);
					return ItemStack.EMPTY;
				}

				resultSlot.onTake(player, recipeOutput);

				player.drop(resultSlot.getItem(), false);
			}
			duck.setCheckMatrixChanges(true);
			slotChangedCraftingGrid(player.level, player, input, craftResult);
		}
		duck.setCheckMatrixChanges(true);
		return craftResult.getRecipeUsed() == null ? ItemStack.EMPTY : outputCopy;
	}

	public static Recipe<CraftingContainer> findRecipe(CraftingContainer inv, Level level) {
		return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, inv, level).orElse(null);
	}
}
