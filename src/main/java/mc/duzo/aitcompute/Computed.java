package mc.duzo.aitcompute;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class Computed implements ModInitializer {
	public static final String MOD_ID = "aitcompute";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static MinecraftServer SERVER;

	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {
		Register.initialize();

		registerEvents();
	}

	private void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> SERVER = null);
		ServerWorldEvents.UNLOAD.register((server, world) -> {
			if (world.getRegistryKey() == World.OVERWORLD) {
				SERVER = null;
			}
		});

		ServerWorldEvents.LOAD.register((server, world) -> {
			if (world.getRegistryKey() == World.OVERWORLD) {
				SERVER = server;
			}
		});
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(SERVER);
	}
}
