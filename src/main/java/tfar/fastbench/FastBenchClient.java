package tfar.fastbench;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import tfar.fastbench.mixin.CraftingContainerAccessor;
import tfar.fastbench.mixin.PlayerContainerAccessor;

import static tfar.fastbench.FastBench.recipe_sync;

public class FastBenchClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ClientSidePacketRegistry.INSTANCE.register(recipe_sync,
						(packetContext, attachedData) -> {
							Identifier location = attachedData.readIdentifier();
							packetContext.getTaskQueue().execute(() -> {
								ScreenHandler container = packetContext.getPlayer().currentScreenHandler;
								if (container instanceof PlayerScreenHandler || container instanceof CraftingScreenHandler) {
									Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
									updateLastRecipe(packetContext.getPlayer().currentScreenHandler, (Recipe<CraftingInventory>) r);
								}
							});
						});
	}

	public static void updateLastRecipe(ScreenHandler container, Recipe<CraftingInventory> rec) {

		CraftingInventory craftInput = null;
		CraftingResultInventory craftResult = null;

		if (container instanceof PlayerScreenHandler) {
			craftInput = ((PlayerContainerAccessor)container).getCraftingInput();
			craftResult = ((PlayerContainerAccessor)container).getCraftingResult();
		}

		else if (container instanceof CraftingScreenHandler) {
			craftInput = ((CraftingContainerAccessor)container).getInput();
			craftResult = ((CraftingContainerAccessor)container).getResult();
		}

		if (craftInput == null) {
			System.out.println("why are these null?");
		} else {
		craftResult.setLastRecipe(rec);
		if (rec != null) craftResult.setStack(0, rec.craft(craftInput));
		else craftResult.setStack(0, ItemStack.EMPTY);
		}
	}
}
