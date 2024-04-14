package dev.xkmc.packetprofiler.mixin;

import dev.xkmc.packetprofiler.init.PacketProfiler;
import dev.xkmc.packetprofiler.intf.EntitySectionStreamable;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySection.class)
public class EntitySectionMixin<T extends EntityAccess> implements EntitySectionStreamable {

	@Unique
	public boolean packetprofiler$streaming = false;

	@Inject(method = "remove", at = @At("HEAD"))
	public void packetprofiler$remove$detectIllegalRemoval(T entity, CallbackInfoReturnable<Boolean> cir) {
		if (packetprofiler$streaming) {
			PacketProfiler.LOGGER.throwing(new IllegalStateException("Entity " + entity + " is removed illegally"));
		}
	}

	@Inject(method = "add", at = @At("HEAD"))
	public void packetprofiler$add$detectIllegalRemoval(T entity, CallbackInfo ci) {
		if (packetprofiler$streaming) {
			PacketProfiler.LOGGER.throwing(new IllegalStateException("Entity " + entity + " is added illegally"));
		}
	}

	@Override
	public void packetprofiler$startDebug() {
		packetprofiler$streaming = true;
	}

	@Override
	public void packetprofiler$stopDebug() {
		packetprofiler$streaming = false;
	}

}
