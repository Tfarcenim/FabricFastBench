package tfar.fastbench;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.util.Identifier;

public class FastBench implements ModInitializer {
	public static final String MODID = "fastbench";

	public static final Identifier FASTBENCH = new Identifier(MODID,MODID);

	@Override
	public void onInitialize() {
		ContainerProviderRegistry.INSTANCE.registerFactory(FASTBENCH,(
						(i, identifier, playerEntity, packetByteBuf) -> new FastBenchContainer(i, playerEntity, playerEntity.world,packetByteBuf.readBlockPos())));
	}
}
