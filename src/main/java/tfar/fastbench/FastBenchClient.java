package tfar.fastbench;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import tfar.fastbench.mixin.CraftingContainerAccessor;
import tfar.fastbench.mixin.PlayerContainerAccessor;

import static tfar.fastbench.FastBench.recipe_sync;

public class FastBenchClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ClientPlayNetworking.registerGlobalReceiver(recipe_sync,
				(client, handler, attachedData, packetContext) -> {
					ResourceLocation location = attachedData.readResourceLocation();
					client.execute(() -> {
						AbstractContainerMenu container = client.player.containerMenu;
						if (container instanceof InventoryMenu || container instanceof CraftingMenu) {
							Recipe<?> r = Minecraft.getInstance().level.getRecipeManager().byKey(location).orElse(null);
							updateLastRecipe(client.player.containerMenu, (Recipe<CraftingContainer>) r);
						}
					});
				});
	}

	public static void updateLastRecipe(AbstractContainerMenu container, Recipe<CraftingContainer> rec) {

		CraftingContainer craftInput = null;
		ResultContainer craftResult = null;

		if (container instanceof InventoryMenu) {
			craftInput = ((PlayerContainerAccessor)container).getCraftSlots();
			craftResult = ((PlayerContainerAccessor)container).getResultSlots();
		}

		else if (container instanceof CraftingMenu) {
			craftInput = ((CraftingContainerAccessor)container).getCraftSlots();
			craftResult = ((CraftingContainerAccessor)container).getResultSlots();
		}

		if (craftInput == null) {
			System.out.println("why are these null?");
		} else {
		craftResult.setRecipeUsed(rec);
		if (rec != null) craftResult.setItem(0, rec.assemble(craftInput));
		else craftResult.setItem(0, ItemStack.EMPTY);
		}
	}
}
