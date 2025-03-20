package dev.xkmc.packetprofiler.profiler;

import com.mojang.datafixers.util.Pair;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McFunctionFinder {

	public static List<String> findAll() {
		var server = ServerLifecycleHooks.getCurrentServer();
		if (server == null) return List.of();
		Map<String, Integer> map = new HashMap<>();
		for (var e : server.getFunctions().getFunctionNames()) {
			map.compute(e.getNamespace(), (k, v) -> (v == null ? 0 : v) + 1);
		}
		return map.entrySet().stream()
				.map(e -> Pair.of(e.getKey(), e.getValue()))
				.sorted(Comparator.comparingInt(e -> -e.getSecond()))
				.map(e -> e.getFirst() + " - " + e.getSecond())
				.toList();
	}

}
