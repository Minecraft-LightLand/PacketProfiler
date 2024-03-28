package dev.xkmc.packetprofiler.mixin;

import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.loot.LootModifierManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ForgeInternalHandler.class)
public interface ForgeInternalHandlerAccessor {

	@Invoker(remap = false)
	static LootModifierManager callGetLootModifierManager() {
		return null;
	}

}
