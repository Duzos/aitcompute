package mc.duzo.aitcompute;

import loqor.ait.tardis.link.v2.TardisRef;
import loqor.ait.tardis.wrapper.server.ServerTardis;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.registry.Registries;
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

	public static void executeOnTardis(UUID id, Consumer<ServerTardis> consumer) {
		ServerTardisManager.getInstance().getTardis(getServer().orElseThrow(), id, consumer);
	}
	public static void executeOnTardis(String id, Consumer<ServerTardis> consumer) {
		executeOnTardis(UUID.fromString(id), consumer);
	}

	public static TardisRef createRef(UUID id) {
		return new TardisRef(id, uuid -> ServerTardisManager.getInstance().demandTardis(Computed.getServer().orElseThrow(), uuid));
	}
	public static TardisRef createRef(String id) {
		return createRef(UUID.fromString(id));
	}
}
