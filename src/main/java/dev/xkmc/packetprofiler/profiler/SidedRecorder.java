package dev.xkmc.packetprofiler.profiler;

import dev.xkmc.packetprofiler.statmap.StatMap;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Consumer;

public class SidedRecorder {

	public final StatMap send, read;
	int time;
	Consumer<String> callback;

	SidedRecorder(int time, Consumer<String> callback) {
		this.time = time;
		send = new StatMap(time, true);
		read = new StatMap(time, true);
		this.callback = callback;
	}

}
