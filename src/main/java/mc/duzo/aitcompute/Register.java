package mc.duzo.aitcompute;

import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import loqor.ait.AITMod;
import loqor.ait.core.AITItems;
import mc.duzo.aitcompute.common.upgrade.TurtleVortex;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Register {
	public static void initialize() {
		Items.initialize();
		TurtleUpgrades.initialize();
	}

	public static <V, T extends V> T register(Registry<V> registry, String name, T entry) {
		return Registry.register(registry, new Identifier(Computed.MOD_ID, name), entry);
	}

	public static <T extends Block> T registerBlockAndItem(String name, T entry) {
		T output = Register.register(Registries.BLOCK, name, entry);
		Registry.register(Registries.ITEM, new Identifier(Computed.MOD_ID, name), new BlockItem(output, new FabricItemSettings()));
		return output;
	}

	public static class Items implements ItemRegistryContainer {
		public static final Item VORTEX_UPGRADE = register("vortex_upgrade", new Item(new OwoItemSettings().group(AITMod.AIT_ITEM_GROUP).maxCount(1)));

		public static <T extends Item> T register(String name, T entry) {
			return Register.register(Registries.ITEM, name, entry);
		}

		public static void initialize() {

		}
	}

	public static class TurtleUpgrades {
		public static final Registry<TurtleUpgradeSerialiser<?>> SERIALIZERS = (Registry<TurtleUpgradeSerialiser<?>>) Registries.REGISTRIES.get(TurtleUpgradeSerialiser.registryId().getValue());

		public static final TurtleUpgradeSerialiser<TurtleVortex> VORTEX =
				registerSerializer("vortex", TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleVortex::new));

		public static <T extends TurtleUpgradeSerialiser<?>> T registerSerializer(String name, T entry) {
			return register(SERIALIZERS, name, entry);
		}

		public static void initialize() { //

		}
	}
}
