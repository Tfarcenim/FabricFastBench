# FastBench for Fabric

## About

**Based on FastBench by Shadows, ported to Fabric.**

This mod does a couple things relating to the vanilla workbench so that it runs faster and causes less network traffic.

- Specifically, it caches the last recipe used, and checks this recipe first before re-scanning the entire registry.  This makes shift click crafting operate much faster than before.  It also lowers network traffic by sending a packet only when needed instead of whenever a slot in changed in the crafting grid.

- However, these changes only apply in a vanilla workbench.  Mod crafting stations should be caching recipes for performance reasons on their own, and this will not fix issues in the 2x2 player crafting inventory.  

*Note: this only replaces the container that is opened when right clicking a crafting table, mods should implement their optimized containers.*

## License

This mod is available under the [CC0 license}(https://github.com/Tfarcenim/FabricFastBench/blob/1.16.x/LICENSE).
