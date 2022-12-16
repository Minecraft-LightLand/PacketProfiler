package dev.xkmc.packetprofiler.mixin;

import dev.xkmc.packetprofiler.profiler.PacketRecorder;
import dev.xkmc.packetprofiler.statmap.StatMap;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleChannel.class)
public abstract class SimpleChannelMixin {

	@Shadow
	@Final
	private NetworkInstance instance;

	@Inject(at = @At("HEAD"), method = "send", remap = false)
	public <MSG> void packetProfiler_send_HEAD(PacketDistributor.PacketTarget target, MSG message, CallbackInfo ci) {
		PacketRecorder.recordSent(target.getDirection()).ifPresent(e -> e.moddedStart(instance.getChannelName(), message));
	}

	@Inject(at = @At("RETURN"), method = "send", remap = false)
	public <MSG> void packetProfiler_send_RETURN(PacketDistributor.PacketTarget target, MSG message, CallbackInfo ci) {
		PacketRecorder.recordSent(target.getDirection()).ifPresent(StatMap::moddedEnd);
	}

	@Inject(at = @At("HEAD"), method = "sendTo", remap = false)
	public <MSG> void packetProfiler_sendTo_HEAD(MSG message, Connection manager, NetworkDirection direction, CallbackInfo ci) {
		PacketRecorder.recordSent(direction).ifPresent(e -> e.moddedStart(instance.getChannelName(), message));
	}

	@Inject(at = @At("RETURN"), method = "sendTo", remap = false)
	public <MSG> void packetProfiler_sendTo_RETURN(MSG message, Connection manager, NetworkDirection direction, CallbackInfo ci) {
		PacketRecorder.recordSent(direction).ifPresent(StatMap::moddedEnd);
	}

	@Inject(at = @At("HEAD"), method = "toVanillaPacket", remap = false)
	public <MSG> void packetProfiler_toVanillaPacket_HEAD(MSG message, NetworkDirection direction, CallbackInfoReturnable<Packet<?>> cir) {

	}

}
