package dev.xkmc.packetprofiler.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class PPClient {

	public static void onCtorClient(IEventBus bus) {
		bus.addListener(PPClient::onClientSetup);
		MinecraftForge.EVENT_BUS.addListener(PPClient::onClientCommandRegister);
	}

	@OnlyIn(Dist.CLIENT)
	public static void onClientSetup(FMLClientSetupEvent event) {
	}


	@OnlyIn(Dist.CLIENT)
	public static void onClientCommandRegister(RegisterClientCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("profileclient");
		base.then(Commands.argument("time", IntegerArgumentType.integer(1, 20 * 60 * 60))
				.executes(ctx -> ServerCommands.onStart(ctx, PacketFlow.CLIENTBOUND)));
		event.getDispatcher().register(base);
	}

}
