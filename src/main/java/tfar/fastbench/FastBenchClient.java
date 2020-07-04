package tfar.fastbench;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import tfar.fastbench.interfaces.CraftingScreenHandlerDuck;
import tfar.fastbench.interfaces.PlayerContainerInterface;

import static tfar.fastbench.FastBench.recipe_sync;

public class FastBenchClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ClientSidePacketRegistry.INSTANCE.register(recipe_sync,
						(packetContext, attachedData) -> {
							Identifier location = attachedData.readIdentifier();
							packetContext.getTaskQueue().execute(() -> {
								if (packetContext.getPlayer().currentScreenHandler instanceof CraftingScreenHandler || packetContext.getPlayer().currentScreenHandler instanceof PlayerScreenHandler) {
									if (MinecraftClient.getInstance().currentScreen instanceof CraftingScreen) {
										Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
										((CraftingScreenHandlerDuck) ((CraftingScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler())
														.updateLastRecipe((Recipe<CraftingInventory>) r);
									} else if (MinecraftClient.getInstance().currentScreen instanceof InventoryScreen) {
										Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
										((PlayerContainerInterface) ((InventoryScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler())
														.updateLastRecipe((Recipe<CraftingInventory>) r);
									}
								}
							});
						});
	}
}
