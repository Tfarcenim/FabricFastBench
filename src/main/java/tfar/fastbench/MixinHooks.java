package tfar.fastbench;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import tfar.fastbench.interfaces.PlayerContainerInterface;

public class MixinHooks {
	public static void updateResult(FastBenchContainer fastBenchContainer, World world, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			if (fastBenchContainer.checkMatrixChanges && (fastBenchContainer.lastRecipe == null || !fastBenchContainer.lastRecipe.matches(inv, world)))
				fastBenchContainer.lastRecipe = findRecipe(inv, world);

			if (fastBenchContainer.lastRecipe != null) {
				itemstack = fastBenchContainer.lastRecipe.craft(inv);
			}

			result.setInvStack(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (fastBenchContainer.lastLastRecipe != fastBenchContainer.lastRecipe) serverPlayerEntity.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(fastBenchContainer.syncId, 0, itemstack));
			else if (fastBenchContainer.lastLastRecipe != null && fastBenchContainer.lastLastRecipe == fastBenchContainer.lastRecipe && !ItemStack.areItemsEqual(fastBenchContainer.lastLastRecipe.craft(inv), fastBenchContainer.lastRecipe.craft(inv)))
				serverPlayerEntity.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(fastBenchContainer.syncId, 0, itemstack));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(fastBenchContainer.lastRecipe != null ? fastBenchContainer.lastRecipe.getId().toString() : "null");
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,FastBenchClient.recipe_sync, buf);
			fastBenchContainer.lastLastRecipe = fastBenchContainer.lastRecipe;
		}
	}

	public static void updateResultP(PlayerContainer playerContainer, World world, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			if (checkMatrixChanges(playerContainer) && (getLastRecipe(playerContainer) == null ||
							!getLastRecipe(playerContainer).matches(inv, world)))
				setLastRecipe(playerContainer,findRecipe(inv, world));

			if (getLastRecipe(playerContainer) != null) {
				itemstack = getLastRecipe(playerContainer).craft(inv);
			}

			result.setInvStack(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (getLastLastRecipe(playerContainer) != getLastRecipe(playerContainer))
				serverPlayerEntity.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(playerContainer.syncId, 0, itemstack));
			else if (getLastLastRecipe(playerContainer) != null && getLastLastRecipe(playerContainer) == getLastRecipe(playerContainer)
							&& !ItemStack.areItemsEqual(getLastLastRecipe(playerContainer).craft(inv), getLastRecipe(playerContainer).craft(inv)))
				serverPlayerEntity.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(playerContainer.syncId, 0, itemstack));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(getLastRecipe(playerContainer) != null ? getLastRecipe(playerContainer).getId().toString() : "null");
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FastBenchClient.recipe_sync, buf);
			setLastLastRecipe(playerContainer,getLastRecipe(playerContainer));
		}
	}

		public static Recipe<CraftingInventory> getLastRecipe(PlayerContainer playerContainer){
		return ((PlayerContainerInterface)playerContainer).getLastRecipe();
		}

	public static void setLastRecipe(PlayerContainer playerContainer,Recipe<CraftingInventory> recipe){
		((PlayerContainerInterface)playerContainer).setLastRecipe(recipe);
	}

	public static boolean checkMatrixChanges(PlayerContainer playerContainer){
		return ((PlayerContainerInterface)playerContainer).checkMatrixChanges();
	}

	public static Recipe<CraftingInventory> getLastLastRecipe(PlayerContainer playerContainer){
		return ((PlayerContainerInterface)playerContainer).getLastLastRecipe();
	}

	public static void setLastLastRecipe(PlayerContainer playerContainer,Recipe<CraftingInventory> recipe){
		((PlayerContainerInterface)playerContainer).setLastLastRecipe(recipe);
	}

	public static Recipe<CraftingInventory> findRecipe(CraftingInventory inv, World world) {
		return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inv, world).orElse(null);
	}

}
