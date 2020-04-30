package tfar.fastbench;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import tfar.fastbench.interfaces.PlayerContainerInterface;

import static tfar.fastbench.FastBench.recipe_sync;

public class FastBenchClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ScreenProviderRegistry.INSTANCE.registerFactory(FastBench.FASTBENCH,
						(syncId, identifier, player, buf) -> new CraftingTableScreen(
										new FastBenchContainer(syncId, player,player.world, buf.readBlockPos()), player.inventory,
										new TranslatableText("container.crafting")));


		ClientSidePacketRegistry.INSTANCE.register(recipe_sync,
						(packetContext, attachedData) -> {
							Identifier location = attachedData.readIdentifier();
							packetContext.getTaskQueue().execute(() -> {
								if(packetContext.getPlayer().container instanceof CraftingTableContainer || packetContext.getPlayer().container instanceof PlayerContainer){
									if (MinecraftClient.getInstance().currentScreen instanceof CraftingTableScreen) {
										Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
										((FastBenchContainer)((CraftingTableScreen) MinecraftClient.getInstance().currentScreen).getContainer())
														.updateLastRecipe((Recipe<CraftingInventory>) r);
									}
									else if (MinecraftClient.getInstance().currentScreen instanceof InventoryScreen) {
										Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
										((PlayerContainerInterface)((InventoryScreen) MinecraftClient.getInstance().currentScreen).getContainer())
														.updateLastRecipe((Recipe<CraftingInventory>) r);
									}
								}
							});
						});
	}
}
