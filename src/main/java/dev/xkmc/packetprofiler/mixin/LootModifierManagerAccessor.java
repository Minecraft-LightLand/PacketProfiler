package dev.xkmc.packetprofiler.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LootModifierManager.class)
public interface LootModifierManagerAccessor {

	@Accessor(remap = false)
	Map<ResourceLocation, IGlobalLootModifier> getRegisteredLootModifiers();

}
