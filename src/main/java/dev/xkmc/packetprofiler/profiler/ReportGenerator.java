package dev.xkmc.packetprofiler.profiler;

import dev.xkmc.packetprofiler.init.PacketProfiler;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class ReportGenerator {

	public static String generate(SidedRecorder rec, PacketFlow flow, boolean calcSize) {
		String side = flow == PacketFlow.SERVERBOUND ? "server" : "client";
		String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String log = "logs/profiler/" + side + "-" + time;
		Path path = FMLPaths.GAMEDIR.get().resolve(log);
		writeCount(rec.send, path.resolve("send_count.log"));
		writeCount(rec.read, path.resolve("read_count.log"));
		if (calcSize) {
			writeSummary(rec.send, path.resolve("send_summary.log"));
			writeSummary(rec.read, path.resolve("read_summary.log"));
			writeSize(rec.send, path.resolve("send_size.log"));
			writeSize(rec.read, path.resolve("read_size.log"));
		}
		return log;
	}

	private static void writeSummary(StatMap stat, Path path) {
		var list = sort(stat, e -> e.totalSize + e.count * 1024);
		write(path, (out) -> {
			out.println("Total time: " + stat.totalTime);
			out.println("Total packet count: " + stat.totalCount);
			out.println("Total packet size:  " + stat.totalSize);
			out.println("Total packet kind:  " + list.size());
			out.println("Total Count \tTotal Size  \tClass");
			list.forEach(ent -> out.printf("%,12d\t%,12d\t%s\n", ent.getValue().count, ent.getValue().totalSize, ent.getKey()));
		});
	}

	private static void writeCount(StatMap stat, Path path) {
		var list = sort(stat, e -> e.count);
		write(path, (out) -> {
			out.println("Total time in ticks: " + stat.totalTime);
			out.println("Total packet count: " + stat.totalCount);
			out.println("Total packet kind: " + list.size());
			out.println("Total Count \tClass");
			list.forEach(ent -> out.printf("%,12d\t%s\n", ent.getValue().count, ent.getKey()));
		});
	}

	private static void writeSize(StatMap stat, Path path) {
		var list = sort(stat, e -> e.totalSize);
		write(path, (out) -> {
			out.println("Total time in ticks: " + stat.totalTime);
			out.println("Total packet size: " + stat.totalSize);
			out.println("Total packet kind: " + list.size());
			out.println("Total Size  \tClass");
			list.forEach(ent -> out.printf("%,12d\t%s\n", ent.getValue().totalSize, ent.getKey()));
		});
	}

	private static List<Map.Entry<String, Stat>> sort(StatMap stat, ToIntFunction<Stat> func) {
		return stat.map.entrySet().stream()
				.sorted((a, b) -> IntComparators.OPPOSITE_COMPARATOR
						.compare(func.applyAsInt(a.getValue()), func.applyAsInt(b.getValue()))).toList();
	}

	private static void write(Path path, Consumer<PrintStream> cons) {
		PrintStream stream = null;
		try {
			stream = getStream(path);
			cons.accept(stream);
		} catch (Exception e) {
			PacketProfiler.LOGGER.throwing(Level.ERROR, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					PacketProfiler.LOGGER.throwing(Level.FATAL, e);
				}
			}
		}
	}

	private static PrintStream getStream(Path path) throws IOException {
		File file = path.toFile();
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					throw new IOException("failed to create directory " + file.getParentFile());
				}
			}
			if (!file.createNewFile()) {
				throw new IOException("failed to create file " + file);
			}
		}
		return new PrintStream(file);
	}

}
