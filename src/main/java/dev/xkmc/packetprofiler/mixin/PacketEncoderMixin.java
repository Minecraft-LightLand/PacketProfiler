package dev.xkmc.packetprofiler.mixin;

import dev.xkmc.packetprofiler.init.PacketProfiler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketEncoder.class)
public class PacketEncoderMixin<T extends net.minecraft.network.PacketListener> {

	@Shadow
	@Final
	private ProtocolInfo<T> protocolInfo;

	@Inject(method = "encode", at = @At("HEAD"))
	private void packetprofiler$verifyRoundTrip(ChannelHandlerContext ctx, Packet<T> packet, ByteBuf out, CallbackInfo ci) {
		ByteBuf dummy = Unpooled.buffer();
		try {
			// write packet to dummy buffer using the same codec that will be used for the real wire encoding
			// ProtocolInfo.codec() internally decorates ByteBuf -> RegistryFriendlyByteBuf via mapStream,
			// so plain ByteBuf is correct here; indices propagate to the underlying buffer.
			this.protocolInfo.codec().encode(dummy, packet);
			int writerIndex = dummy.writerIndex();
			int readableBefore = dummy.readableBytes();

			try {
				// read back from the same buffer via same codec
				this.protocolInfo.codec().decode(dummy);
			} catch (Exception e) {
				PacketProfiler.LOGGER.error(
						"[PacketProfiler] round-trip decode failed for {} (protocol={}, written {} bytes, readerIndex {} writerIndex {}): {}",
						packet.type(), this.protocolInfo.id().id(), readableBefore, dummy.readerIndex(), writerIndex, e.toString(), e);
				return;
			}

			int readerIndex = dummy.readerIndex();
			int readableAfter = dummy.readableBytes();

			// check if read and write index match (and no leftover bytes)
			if (readerIndex != writerIndex || readableAfter != 0) {
				PacketProfiler.LOGGER.error(
						"[PacketProfiler] read/write index mismatch for {} (protocol={}): writerIndex={}, readerIndex={}, readableBefore={}, readableAfter={}, packetClass={}",
						packet.type(), this.protocolInfo.id().id(), writerIndex, readerIndex, readableBefore, readableAfter, packet.getClass().getName());
			}
		} catch (Exception e) {
			PacketProfiler.LOGGER.error("[PacketProfiler] round-trip encode failed for {} (protocol={}): {}",
					packet.type(), this.protocolInfo.id().id(), e.toString(), e);
		} finally {
			dummy.release();
		}
	}
}
