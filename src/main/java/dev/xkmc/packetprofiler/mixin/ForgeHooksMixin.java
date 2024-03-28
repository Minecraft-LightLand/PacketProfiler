package dev.xkmc.packetprofiler.mixin;

import dev.xkmc.packetprofiler.loot.LootDebugger;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

	@Inject(at = @At("HEAD"), remap = false, cancellable = true,
			method = "modifyLoot(Lnet/minecraft/resources/ResourceLocation;Lit/unimi/dsi/fastutil/objects/ObjectArrayList;Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;")
	private static void packetProfiler$modifyLoot$printHead(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
		if (LootDebugger.debugLoot(lootTableId)) {
			LootDebugger.lootStart(lootTableId, generatedLoot, context);
			cir.setReturnValue(generatedLoot);
		}
	}

}
