package mc.duzo.aitcompute.datagen.provider;

import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import loqor.ait.core.AITItems;
import mc.duzo.aitcompute.Computed;
import mc.duzo.aitcompute.Register;
import net.minecraft.data.DataOutput;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ComputedTurtleUpgradeProvider extends TurtleUpgradeDataProvider {
	public ComputedTurtleUpgradeProvider(DataOutput output) {
		super(output);
	}

	@Override
	protected void addUpgrades(Consumer<Upgrade<TurtleUpgradeSerialiser<?>>> consumer) {
		simpleWithCustomItem(id("vortex"), Register.TurtleUpgrades.VORTEX, AITItems.HAZANDRA).add(consumer);
	}

	private static Identifier id(String id) {
		return new Identifier(Computed.MOD_ID, id);
	}
}
