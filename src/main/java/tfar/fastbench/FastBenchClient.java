package tfar.fastbench;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import tfar.fastbench.interfaces.CraftingDuck;

import static tfar.fastbench.FastBench.recipe_sync;

public class FastBenchClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ClientSidePacketRegistry.INSTANCE.register(recipe_sync,
						(packetContext, attachedData) -> {
							Identifier location = attachedData.readIdentifier();
							packetContext.getTaskQueue().execute(() -> {
								if (packetContext.getPlayer().currentScreenHandler instanceof CraftingDuck) {
									Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
									((CraftingDuck) ((HandledScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler())
													.updateLastRecipe((Recipe<CraftingInventory>) r);
								}
							});
						});
	}
}
