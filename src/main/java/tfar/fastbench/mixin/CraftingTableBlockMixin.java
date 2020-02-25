package tfar.fastbench.mixin;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbench.FastBench;

@Mixin(CraftingTableBlock.class)
class CraftingTableBlockMixin {
	@Inject(method = "onUse",
					at = @At(value = "INVOKE",
									target = "net/minecraft/block/BlockState.createContainerProvider(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/container/NameableContainerProvider;"),
					cancellable = true)
	private void createFastContainer(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
		if ((Object) this == Blocks.CRAFTING_TABLE) {
			ContainerProviderRegistry.INSTANCE.openContainer(FastBench.FASTBENCH,player,packetByteBuf -> packetByteBuf.writeBlockPos(pos));
			player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			info.setReturnValue(ActionResult.SUCCESS);
		}
	}
}
