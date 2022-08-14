package dev.xkmc.packetprofiler.mixin;

import dev.xkmc.packetprofiler.profiler.PacketRecorder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin {

	@Shadow
	private Channel channel;

	@Shadow
	@Final
	private PacketFlow receiving;

	@Shadow
	public abstract Channel channel();

	@Inject(at = @At("HEAD"), method = "sendPacket")
	public void packetprofiler_observeSentPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, CallbackInfo ci) {
		PacketRecorder.recordSent(packet, receiving);
	}

	@Inject(at = @At("HEAD"), method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V")
	public void packetprofiler_observeReceivedPacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		PacketRecorder.recordReceived(packet, receiving);
	}

}
