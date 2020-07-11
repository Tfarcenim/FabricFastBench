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
import tfar.fastbench.interfaces.CraftingDuck;

public class MixinHooks {
	public static void updateResult(CraftingScreenHandler fastBenchContainer, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		World world = player.world;
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			CraftingDuck duck = (CraftingDuck)fastBenchContainer;
			
			if (duck.checkMatrixChanges() && (duck.getLastRecipe() == null || !duck.getLastRecipe().matches(inv, world)))
				duck.setLastRecipe(findRecipe(inv, world));

			if (duck.getLastRecipe() != null) {
				itemstack = duck.getLastRecipe().craft(inv);
			}

			result.setStack(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (duck.getLastLastRecipe() != duck.getLastRecipe()) serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(fastBenchContainer.syncId, 0, itemstack));
			else if (duck.getLastLastRecipe() != null && duck.getLastLastRecipe() == duck.getLastRecipe() && !ItemStack.areItemsEqual(duck.getLastLastRecipe().craft(inv), duck.getLastRecipe().craft(inv)))
				serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(fastBenchContainer.syncId, 0, itemstack));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(duck.getLastRecipe() != null ? duck.getLastRecipe().getId().toString() : "null");
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,FastBench.recipe_sync, buf);
			duck.setLastLastRecipe(duck.getLastRecipe());
		}
	}

	public static void updateResultP(PlayerScreenHandler playerContainer, World world, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result) {
		if (!world.isClient) {

			ItemStack itemstack = ItemStack.EMPTY;

			CraftingDuck duck = (CraftingDuck)playerContainer;

			if (duck.checkMatrixChanges() && (duck.getLastRecipe() == null ||
							!duck.getLastRecipe().matches(inv, world)))
				duck.setLastRecipe(findRecipe(inv, world));

			if (duck.getLastRecipe() != null) {
				itemstack = duck.getLastRecipe().craft(inv);
			}

			result.setStack(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (duck.getLastLastRecipe() != duck.getLastRecipe())
				serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(playerContainer.syncId, 0, itemstack));
			else if (duck.getLastLastRecipe() != null && duck.getLastLastRecipe() == duck.getLastRecipe()
							&& !ItemStack.areItemsEqual(duck.getLastLastRecipe().craft(inv), duck.getLastRecipe().craft(inv)))
				serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(playerContainer.syncId, 0, itemstack));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(duck.getLastRecipe() != null ? duck.getLastRecipe().getId().toString() : "null");
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FastBench.recipe_sync, buf);
			duck.setLastLastRecipe(duck.getLastRecipe());
		}
	}

	public static Recipe<CraftingInventory> findRecipe(CraftingInventory inv, World world) {
		return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inv, world).orElse(null);
	}

}
