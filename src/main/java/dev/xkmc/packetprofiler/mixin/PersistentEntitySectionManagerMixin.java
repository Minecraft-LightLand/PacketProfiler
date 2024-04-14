package dev.xkmc.packetprofiler.mixin;

import dev.xkmc.packetprofiler.intf.EntitySectionStreamable;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {

	@Inject(method = {"lambda$updateChunkStatus$6", "m_157543_"}, at = @At("HEAD"))
	public void packetprofiler$updateChunkStatus$startDebug(Visibility vis, EntitySection<?> sec, CallbackInfo ci) {
		((EntitySectionStreamable) sec).packetprofiler$startDebug();
	}

	@Inject(method = {"lambda$updateChunkStatus$6", "m_157543_"}, at = @At("TAIL"))
	public void packetprofiler$updateChunkStatus$stopDebug(Visibility vis, EntitySection<?> sec, CallbackInfo ci) {
		((EntitySectionStreamable) sec).packetprofiler$stopDebug();
	}

}
