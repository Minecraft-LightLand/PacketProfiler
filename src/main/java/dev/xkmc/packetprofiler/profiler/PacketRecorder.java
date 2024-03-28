package dev.xkmc.packetprofiler.profiler;

import dev.xkmc.packetprofiler.init.PacketProfiler;
import dev.xkmc.packetprofiler.statmap.StatMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;

public class PacketRecorder {

	public static SidedRecorder server, client;

	public static void initAll() {
		server = new SidedRecorder(Integer.MAX_VALUE, PacketProfiler.LOGGER::info);
		client = new SidedRecorder(Integer.MAX_VALUE, PacketProfiler.LOGGER::info);
	}

	public static Optional<StatMap> recordSent(NetworkDirection dire) {
		return recordSent(dire.getOriginationSide().isServer() ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND);
	}

	public static Optional<StatMap> recordReceived(NetworkDirection dire) {
		return recordSent(dire.getReceptionSide().isServer() ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND);
	}

	public static Optional<StatMap> recordSent(PacketFlow receiving) {
		if (receiving == PacketFlow.SERVERBOUND) {
			if (server != null) {
				return Optional.of(server.send);
			}
		} else {
			if (client != null) {
				return Optional.of(client.send);
			}
		}
		return Optional.empty();
	}

	public static Optional<StatMap> recordReceived(PacketFlow receiving) {
		if (receiving == PacketFlow.SERVERBOUND) {
			if (server != null) {
				return Optional.of(server.read);
			}
		} else {
			if (client != null) {
				return Optional.of(client.read);
			}
		}
		return Optional.empty();
	}

	public static void startProfiling(PacketFlow side, int time, CommandSourceStack source) {
		if (side == PacketFlow.SERVERBOUND) {
			if (server != null) {
				String str = ReportGenerator.generate(server, PacketFlow.SERVERBOUND, true);
				PacketProfiler.LOGGER.error("Server profiler already exists. Terminating. Saved at " + str);
			}
			server = new SidedRecorder(time, e -> source.sendSuccess(() -> Component.literal(e), true));
		} else {
			if (client != null) {
				String str = ReportGenerator.generate(server, PacketFlow.CLIENTBOUND, true);
				PacketProfiler.LOGGER.error("Client profiler already exists. Terminating. Saved at " + str);
			}
			client = new SidedRecorder(time, e -> source.sendSuccess(() -> Component.literal(e), true));
		}
	}

	public static void onServerTick() {
		if (server != null && server.time > 0) {
			server.time--;
			if (server.time == 0) {
				String str = ReportGenerator.generate(server, PacketFlow.SERVERBOUND, true);
				server.callback.accept("Profiling Complete, saved at" + str);
				server = null;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		if (client != null && client.time > 0) {
			client.time--;
			if (client.time == 0) {
				String str = ReportGenerator.generate(client, PacketFlow.CLIENTBOUND, true);
				client.callback.accept("Profiling Complete, saved at " + str);
				client = null;
			}
		}
	}

}
