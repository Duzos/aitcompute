package mc.duzo.aitcompute.common.upgrade;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import mc.duzo.aitcompute.Computed;
import mc.duzo.aitcompute.Register;
import mc.duzo.aitcompute.common.peripheral.VortexPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * This is a peripheral that allows you to communicate with a TARDIS
 */
public class TurtleVortex extends AbstractTurtleUpgrade {
	public TurtleVortex(Identifier id, ItemStack stack) {
		super(id, TurtleUpgradeType.PERIPHERAL, stack);
	}

	@Nullable
	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new VortexPeripheral(turtle);
	}
}
