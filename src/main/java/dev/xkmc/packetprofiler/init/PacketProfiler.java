package dev.xkmc.packetprofiler.init;

import dev.xkmc.packetprofiler.profiler.PacketRecorder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PacketProfiler.MODID)
public class PacketProfiler {


	public static final String MODID = "packetprofiler";
	public static final Logger LOGGER = LogManager.getLogger();

	public PacketProfiler() {
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		MinecraftForge.EVENT_BUS.register(ServerCommands.class);
		MinecraftForge.EVENT_BUS.register(PacketRecorder.class);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PPClient.onCtorClient(bus));
	}

}
