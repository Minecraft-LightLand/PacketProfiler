package dev.xkmc.packetprofiler.profiler;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PacketRecorder {

	private static SidedRecorder server, client;

	public static void recordSent(Packet<?> packet, PacketFlow receiving) {
		if (receiving == PacketFlow.SERVERBOUND) {
			if (server != null) {
				server.send.handle(packet);
			}
		} else {
			if (client != null) {
				client.send.handle(packet);
			}
		}
	}

	public static void recordReceived(Packet<?> packet, PacketFlow receiving) {
		if (receiving == PacketFlow.SERVERBOUND) {
			if (server != null) {
				server.read.handle(packet);
			}
		} else {
			if (client != null) {
				client.read.handle(packet);
			}
		}
	}

	public static void startProfiling(PacketFlow side, int time, CommandSourceStack source) {
		if (side == PacketFlow.SERVERBOUND) {
			server = new SidedRecorder(time, source);
		} else {
			client = new SidedRecorder(time, source);
		}
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		if (server != null && server.time > 0) {
			server.time--;
			if (server.time == 0) {
				String str = ReportGenerator.generate(server, PacketFlow.SERVERBOUND, true);
				server.callback.sendSuccess(new TextComponent("Profiling Complete, saved at" + str), true);
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
				client.callback.sendSuccess(new TextComponent("Profiling Complete, saved at " + str), true);
				client = null;
			}
		}
	}

}
