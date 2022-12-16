package dev.xkmc.packetprofiler.statmap;

public class Stat {

	public int count = 0;
	public int totalSize = 0;
	public int maxSize = 0;

	public void update(int size) {
		count++;
		totalSize += size;
		maxSize = Math.max(maxSize, size);
	}

}
