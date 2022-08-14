package dev.xkmc.packetprofiler.profiler;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class Stat {

	private final boolean calcSize;

	public int count = 0;
	public int totalSize = 0;
	public int maxSize = 0;

	public Stat(boolean calcSize) {
		this.calcSize = calcSize;
	}

	public int update(Packet<?> packet) {
		count++;
		if (!calcSize) return 0;
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		packet.write(buf);
		int size = buf.writerIndex();
		totalSize += size;
		maxSize = Math.max(maxSize, size);
		return size;
	}

}
