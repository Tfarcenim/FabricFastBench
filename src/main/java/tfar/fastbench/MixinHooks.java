package tfar.fastbench;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import tfar.fastbench.interfaces.CraftingScreenHandlerDuck;
import tfar.fastbench.interfaces.PlayerContainerInterface;

public class MixinHooks {
	public static void updateResult(CraftingScreenHandler fastBenchContainer, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		World world = player.world;
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			CraftingScreenHandlerDuck duck = (CraftingScreenHandlerDuck)fastBenchContainer;
			
			if (duck.checkMatrixChanges() && (duck.lastRecipe() == null || !duck.lastRecipe().matches(inv, world)))
				duck.setLastRecipe(findRecipe(inv, world));

			if (duck.lastRecipe() != null) {
				itemstack = duck.lastRecipe().craft(inv);
			}

			result.setStack(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (duck.lastLastRecipe() != duck.lastRecipe()) serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(fastBenchContainer.syncId, 0, itemstack));
			else if (duck.lastLastRecipe() != null && duck.lastLastRecipe() == duck.lastRecipe() && !ItemStack.areItemsEqual(duck.lastLastRecipe().craft(inv), duck.lastRecipe().craft(inv)))
				serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(fastBenchContainer.syncId, 0, itemstack));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(duck.lastRecipe() != null ? duck.lastRecipe().getId().toString() : "null");
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,FastBench.recipe_sync, buf);
			duck.setLastLastRecipe(duck.lastRecipe());
		}
	}

	public static void updateResultP(PlayerScreenHandler playerContainer, World world, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			if (checkMatrixChanges(playerContainer) && (getLastRecipe(playerContainer) == null ||
							!getLastRecipe(playerContainer).matches(inv, world)))
				setLastRecipe(playerContainer,findRecipe(inv, world));

			if (getLastRecipe(playerContainer) != null) {
				itemstack = getLastRecipe(playerContainer).craft(inv);
			}

			result.setStack(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (getLastLastRecipe(playerContainer) != getLastRecipe(playerContainer))
				serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(playerContainer.syncId, 0, itemstack));
			else if (getLastLastRecipe(playerContainer) != null && getLastLastRecipe(playerContainer) == getLastRecipe(playerContainer)
							&& !ItemStack.areItemsEqual(getLastLastRecipe(playerContainer).craft(inv), getLastRecipe(playerContainer).craft(inv)))
				serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(playerContainer.syncId, 0, itemstack));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(getLastRecipe(playerContainer) != null ? getLastRecipe(playerContainer).getId().toString() : "null");
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FastBench.recipe_sync, buf);
			setLastLastRecipe(playerContainer,getLastRecipe(playerContainer));
		}
	}

		public static Recipe<CraftingInventory> getLastRecipe(PlayerScreenHandler playerContainer){
		return ((PlayerContainerInterface)playerContainer).getLastRecipe();
		}

	public static void setLastRecipe(PlayerScreenHandler playerContainer,Recipe<CraftingInventory> recipe){
		((PlayerContainerInterface)playerContainer).setLastRecipe(recipe);
	}

	public static boolean checkMatrixChanges(PlayerScreenHandler playerContainer){
		return ((PlayerContainerInterface)playerContainer).checkMatrixChanges();
	}

	public static Recipe<CraftingInventory> getLastLastRecipe(PlayerScreenHandler playerContainer){
		return ((PlayerContainerInterface)playerContainer).getLastLastRecipe();
	}

	public static void setLastLastRecipe(PlayerScreenHandler playerContainer,Recipe<CraftingInventory> recipe){
		((PlayerContainerInterface)playerContainer).setLastLastRecipe(recipe);
	}

	public static Recipe<CraftingInventory> findRecipe(CraftingInventory inv, World world) {
		return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inv, world).orElse(null);
	}

}
