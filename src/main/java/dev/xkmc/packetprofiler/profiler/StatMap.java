package dev.xkmc.packetprofiler.profiler;

import net.minecraft.network.protocol.Packet;

import java.util.HashMap;

public class StatMap {

	private final boolean calcSize;

	public final HashMap<String, Stat> map = new HashMap<>();
	public int totalTime, totalCount = 0, totalSize = 0;

	StatMap(int time, boolean calcSize) {
		this.calcSize = calcSize;
		this.totalTime = time;
	}

	void handle(Packet<?> packet) {
		String name = packet.getClass().getName();
		Stat stat = map.computeIfAbsent(name, e -> new Stat(calcSize));
		totalCount++;
		totalSize += stat.update(packet);
	}
}
