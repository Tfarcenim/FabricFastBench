package tfar.fastbench.mixin;// Created 2022-05-12T13:17:33

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.fastbench.interfaces.CraftingInventoryDuck;

/**
 * @author Ampflower
 * @since ${version}
 **/
@Mixin(ServerPlaceRecipe.class)
public class ServerPlaceRecipeMixin<C extends Container> {
    @Shadow
    protected RecipeBookMenu<C> menu;

    /**
     * Inhibits the matrix check to avoid doing expensive checks for recipes while the inventory is being cleared.
     */
    @Inject(method = "clearGrid", at = @At("HEAD"))
    private void fastbench$setCheckMatrixFalse(CallbackInfo ci) {
        // Important: Do not try to call this if the menu doesn't implement the
        // duck interface.
        if (menu instanceof CraftingInventoryDuck duck) {
            duck.setCheckMatrixChanges(false);
        }
    }
}
