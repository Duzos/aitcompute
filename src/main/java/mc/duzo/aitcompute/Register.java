package mc.duzo.aitcompute;

import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import mc.duzo.aitcompute.common.upgrade.TurtleVortex;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Register {
	public static final Item VORTEX_UPGRADE = register(Registries.ITEM, "vortex_upgrade", new Item(new Item.Settings().maxCount(1)));
	public static void initialize() {
		UpgradeRegistry.initialize();
	}

	public static <V, T extends V> T register(Registry<V> registry, String name, T entry) {
		return Registry.register(registry, new Identifier(Computed.MOD_ID, name), entry);
	}

	public static <T extends Block> T registerBlockAndItem(String name, T entry) {
		T output = Register.register(Registries.BLOCK, name, entry);
		Registry.register(Registries.ITEM, new Identifier(Computed.MOD_ID, name), new BlockItem(output, new FabricItemSettings()));
		return output;
	}

	public static class UpgradeRegistry {
		public static final Registry<TurtleUpgradeSerialiser<?>> SERIALIZERS = (Registry<TurtleUpgradeSerialiser<?>>) Registries.REGISTRIES.get(TurtleUpgradeSerialiser.registryId().getValue());

		public static final TurtleUpgradeSerialiser<TurtleVortex> VORTEX =
				register(SERIALIZERS, "vortex", TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleVortex::new));

		public static void initialize() {

		}
	}
}
