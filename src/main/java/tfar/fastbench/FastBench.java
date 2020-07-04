package tfar.fastbench;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class FastBench implements ModInitializer {
	public static final String MODID = "fastbench";

	public static final Identifier recipe_sync = new Identifier(FastBench.MODID,"sync_recipe");

	@Override
	public void onInitialize() {
	}
}
