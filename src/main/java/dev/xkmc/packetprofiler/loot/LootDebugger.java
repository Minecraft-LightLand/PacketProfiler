package dev.xkmc.packetprofiler.loot;

import com.mojang.brigadier.context.CommandContext;
import dev.xkmc.packetprofiler.init.PacketProfiler;
import dev.xkmc.packetprofiler.mixin.ForgeInternalHandlerAccessor;
import dev.xkmc.packetprofiler.mixin.LootModifierManagerAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.loot.LootModifierManager;

import java.util.ArrayList;
import java.util.List;

public class LootDebugger {

	private static int time = 0;
	private static CommandSource source;


	public static void onServerTick() {
		if (time > 0) {
			time--;
			if (time == 0 && source != null) {
				source.sendSystemMessage(Component.literal("Loot debug stopped"));
			}
		}
	}

	public static int onStart(CommandContext<CommandSourceStack> ctx) {
		int time = ctx.getArgument("time", Integer.class);
		LootDebugger.time = time;
		LootDebugger.source = ctx.getSource().source;
		int sec = time / 20;
		int min = sec / 60;
		int hrs = min / 60;
		String str = String.format("%02d:%02d:%02d", hrs % 24, min % 60, sec % 60);
		ctx.getSource().sendSuccess(() -> Component.literal("Start profiling loot with time " + str), true);
		return 1;
	}

	public static boolean debugLoot(ResourceLocation id) {
		return time > 0;
	}

	public static void lootStart(ResourceLocation id, ObjectArrayList<ItemStack> list, LootContext context) {
		context.setQueriedLootTableId(id);
		LootLogger logger = new LootLogger(id, list, context);
		LootModifierManager man = ForgeInternalHandlerAccessor.callGetLootModifierManager();
		for (var ent : ((LootModifierManagerAccessor) man).getRegisteredLootModifiers().entrySet()) {
			list = ent.getValue().apply(list, context);
			logger.step(ent.getKey(), list);
		}
		logger.print();
	}

	private static class LootLogger {

		private final List<String> text = new ArrayList<>();
		private String snapshot;

		public LootLogger(ResourceLocation id, ObjectArrayList<ItemStack> list, LootContext context) {
			text.add("------ LOOT DEBUG START ------");
			text.add("Table ID: " + id);
			snapshot = list.toString();
			text.add("Initial Data: " + snapshot);
		}

		public void step(ResourceLocation id, ObjectArrayList<ItemStack> list) {
			String data = list.toString();
			if (data.equals(snapshot)) {
				return;
			}
			snapshot = data;
			text.add("Modifier: " + id + ", Result: " + data);
		}

		public void print() {
			text.add("------ LOOT DEBUG STOP ------");
			for (var e : text) {
				PacketProfiler.LOGGER.info(e);
			}
		}

	}
}
