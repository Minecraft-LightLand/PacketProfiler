package dev.xkmc.packetprofiler.statmap;

import dev.xkmc.packetprofiler.init.PacketProfiler;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
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

	public void handle(@Nullable PacketFlow receiving, Packet<?> packet) {
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
		if (PacketProfiler.testPacket() && receiving != null) {
			test(receiving, packet, channelName, packetName);
		}
		totalCount++;
		int size = getSize(packet);
		totalSize += size;
		Stat packetStat = packetMap.computeIfAbsent(packetName, e -> new Stat());
		Stat channelStat = channelMap.computeIfAbsent(channelName, e -> new Stat());
		packetStat.update(size);
		channelStat.update(size);
	}

	public void testRecipe(Recipe<?> r) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		try {
			ClientboundUpdateRecipesPacket.toNetwork(buf, r);
			int oldWrite = buf.writerIndex();
			if (buf.readerIndex() != 0) {
				PacketProfiler.LOGGER.fatal("Recipe " + r.getId() + " read during write");
				return;
			}
			ClientboundUpdateRecipesPacket.fromNetwork(buf);
			if (buf.writerIndex() != oldWrite) {
				PacketProfiler.LOGGER.fatal("Recipe " + r.getId() + " write during read");
				return;
			}
			if (buf.readerIndex() != buf.writerIndex()) {
				PacketProfiler.LOGGER.fatal("Recipe " + r.getId() + " does not have equal size for read and write");
				return;
			}
		} catch (Exception e) {
			var type = BuiltInRegistries.RECIPE_SERIALIZER.getKey(r.getSerializer());
			PacketProfiler.LOGGER.fatal("Recipe " + r.getId() + " of type" + type + " is malformed");
			PacketProfiler.LOGGER.throwing(e);
		}

	}

	private void test(PacketFlow flow, Packet<?> packet, String name, String ch) {
		if (packet instanceof BundlePacket<?> bundle) {
			for (var e : bundle.subPackets()) {
				test(flow, e, name + "_" + e.getClass().getName(), ch);
			}
			return;
		}
		if (packet instanceof ClientboundUpdateRecipesPacket recipes) {
			var list = recipes.getRecipes();
			for (var r : list) {
				testRecipe(r);
			}
			return;
		}
		try {
			flow = flow.getOpposite();
			var prot = ConnectionProtocol.getProtocolForPacket(packet);
			if (prot == null)
				throw new IllegalStateException("packet " + packet.getClass() + " does not have protocol assigned");
			int id = prot.getPacketId(flow, packet);
			if (id < 0)
				throw new IllegalStateException("packet " + packet.getClass() + " does not have an ID, using " + prot.name() + " with " + flow.name());

			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			packet.write(buf);
			int oldWrite = buf.writerIndex();
			if (buf.readerIndex() != 0)
				throw new IllegalStateException("packet " + packet.getClass() + " read during write");
			prot.createPacket(flow, id, buf);
			if (buf.writerIndex() != oldWrite)
				throw new IllegalStateException("packet " + packet.getClass() + " write during read");
			if (buf.readerIndex() != buf.writerIndex())
				throw new IllegalStateException("packet " + packet.getClass() + " does not have equal size for read and write");
		} catch (Exception e) {
			PacketProfiler.LOGGER.throwing(new IllegalStateException("packet " + name + " in channel " + ch + " is malformed", e));
		}
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
