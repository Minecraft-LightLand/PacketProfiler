package dev.xkmc.packetprofiler.profiler;

import dev.xkmc.packetprofiler.statmap.StatMap;
import net.minecraft.commands.CommandSourceStack;

public class SidedRecorder {

	public final StatMap send, read;
	int time;
	CommandSourceStack callback;

	SidedRecorder(int time, CommandSourceStack callback) {
		this.time = time;
		send = new StatMap(time, true);
		read = new StatMap(time, true);
		this.callback = callback;
	}

}
