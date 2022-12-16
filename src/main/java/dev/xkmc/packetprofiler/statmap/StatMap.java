package dev.xkmc.packetprofiler.statmap;

import dev.xkmc.packetprofiler.init.PacketProfiler;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class StatMap {

	private final boolean calcSize;

	public final HashMap<String, Stat> packetMap = new HashMap<>();
	public final HashMap<String, Stat> channelMap = new HashMap<>();
	public int totalTime, totalCount = 0, totalSize = 0;

	private boolean isModded = false;
	private String moddedChannelName = null;
	private String moddedPacketName = null;

	public StatMap(int time, boolean calcSize) {
		this.calcSize = calcSize;
		this.totalTime = time;
	}

	public void handle(Packet<?> packet) {
		String packetName;
		String channelName = "minecraft";
		if (isModded) {
			channelName = moddedChannelName;
		}
		if (isModded && moddedPacketName != null) {
			packetName = moddedPacketName;
		} else {
			packetName = packet.getClass().getName();
		}
		totalCount++;
		int size = getSize(packet);
		totalSize += size;
		Stat packetStat = packetMap.computeIfAbsent(packetName, e -> new Stat());
		Stat channelStat = channelMap.computeIfAbsent(channelName, e -> new Stat());
		packetStat.update(size);
		channelStat.update(size);
	}

	private int getSize(Packet<?> packet) {
		if (!calcSize) return 0;
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		packet.write(buf);
		return buf.writerIndex();
	}

	public void moddedStart(ResourceLocation channel, Object msg) {
		if (isModded) {
			PacketProfiler.LOGGER.fatal("trigger did not disable");
			throw new RuntimeException("Trigger did not disable before re-enable");
		}
		isModded = true;
		moddedChannelName = channel.toString();
		moddedPacketName = msg.getClass().getName();
	}

	public void recordName(String name) {
		if (!isModded) {
			return;
		}
		moddedPacketName = name;
	}

	public void moddedEnd() {
		if (!isModded) {
			PacketProfiler.LOGGER.fatal("trigger did not enable");
			throw new RuntimeException("Trigger did not enable before disable");
		}
		isModded = false;
		moddedPacketName = null;
	}

}
