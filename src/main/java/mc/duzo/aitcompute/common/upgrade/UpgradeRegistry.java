package mc.duzo.aitcompute.common.upgrade;

import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import mc.duzo.aitcompute.Register;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class UpgradeRegistry {
	public static final Registry<TurtleUpgradeSerialiser<?>> SERIALIZERS = (Registry<TurtleUpgradeSerialiser<?>>) Registries.REGISTRIES.get(TurtleUpgradeSerialiser.registryId().getValue());

	public static final TurtleUpgradeSerialiser<TurtleVortex> VORTEX =
			Register.register(SERIALIZERS, "vortex", TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleVortex::new));

	public static void initialize() {

	}
}
