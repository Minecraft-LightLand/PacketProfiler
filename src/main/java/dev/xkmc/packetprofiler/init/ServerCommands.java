package dev.xkmc.packetprofiler.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.xkmc.packetprofiler.profiler.PacketRecorder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerCommands {

	@SubscribeEvent
	public static void onCommandRegister(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("profileserver");
		base.requires(e -> e.hasPermission(2))
				.then(Commands.argument("time", IntegerArgumentType.integer(1, 20 * 60 * 60 * 24))
						.executes(ctx -> onStart(ctx, PacketFlow.SERVERBOUND)));
		event.getDispatcher().register(base);
	}

	protected static int onStart(CommandContext<CommandSourceStack> ctx, PacketFlow flow) {
		int time = ctx.getArgument("time", Integer.class);
		PacketRecorder.startProfiling(flow, time, ctx.getSource());
		int sec = time / 20;
		int min = sec / 60;
		int hrs = min / 60;
		String str = String.format("%02d:%02d:%02d", hrs % 24, min % 60, sec % 60);
		String side = flow == PacketFlow.SERVERBOUND ? "server" : "client";
		ctx.getSource().sendSuccess(() -> Component.literal("Start profiling " + side + " with time " + str), true);
		return 1;
	}

}
