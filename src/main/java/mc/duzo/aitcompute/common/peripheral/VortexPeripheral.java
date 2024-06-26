package mc.duzo.aitcompute.common.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import mc.duzo.aitcompute.Computed;
import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.util.AbsoluteBlockPos;
import mdteam.ait.tardis.util.SerialDimension;
import mdteam.ait.tardis.util.TardisUtil;
import mdteam.ait.tardis.wrapper.server.ServerTardis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VortexPeripheral implements IPeripheral {
	private final ITurtleAccess turtle;

	public VortexPeripheral(ITurtleAccess turtle) {
		this.turtle = turtle;
	}
	@Override
	public String getType() {
		return "vortex";
	}

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return this == other || (other instanceof VortexPeripheral peripheral && turtle == peripheral.turtle);
	}

	@Nullable
	@Override
	public Object getTarget() {
		return this.turtle;
	}

	/**
	 * Dematerialises a tardis
	 * @param args string uuid, optional boolean to set autopilot
	 */
	@LuaFunction
	public final void dematerialise(IArguments args) throws LuaException {
		boolean autopilot = args.optBoolean(1).orElse(false);
		Computed.executeOnTardis(args.getString(0), tardis -> tardis.getTravel().dematerialise(autopilot)); // isnt a happy chappy
	}

	/**
	 * Materialises a tardis
	 * @param uuid string uuid
	 */
	@LuaFunction
	public final void materialise(String uuid) throws LuaException {
		Computed.executeOnTardis(uuid, tardis -> tardis.getTravel().materialise());
	}

	private static Direction directionFromId(int id) {
		Direction found = Direction.byId(id);

		if (found == Direction.UP || found == Direction.DOWN) return Direction.NORTH;

		return found;
	}

	/**
	 * Sets the destination
	 * @param args id, x, y, z, dir, dim
	 */
	@LuaFunction
	public final void setDestination(IArguments args) throws LuaException {
		String turtleDim = turtle.getLevel().getRegistryKey().getValue().toString();

		AbsoluteBlockPos.Directed destination = new AbsoluteBlockPos.Directed(
				new BlockPos(args.getInt(1), args.getInt(2), args.getInt(3)),
				new SerialDimension(args.optString(5).orElse(turtleDim)),
				directionFromId(args.getInt(4))
		);

		Computed.executeOnTardis(args.getString(0), tardis -> tardis.getTravel().setDestination(destination));
	}

	/**
	 * @return an array of the form {X, Y, Z, Rotation, Dimension ID}
	 */
	private static Object[] convertDirectedToLua(AbsoluteBlockPos.Directed pos) {
		// X, Y, Z, Rotation, Dimension ID
		return new Object[]{pos.getX(), pos.getY(), pos.getZ(), pos.getDirection().getId(), pos.getDimension().getValue()};
	}

	/**
	 * Gets the position of a tardis
	 * @param id string uuid
	 * @return an array of the form {X, Y, Z, Rotation, Dimension ID}
	 */
	@LuaFunction
	public final Object[] getPosition(String id) {
		Optional<ServerTardis> ref = Computed.findTardis(id);

		if (ref.isEmpty()) {
			return new Object[]{0, 0, 0, 0, ""};
		}

		AbsoluteBlockPos.Directed pos = ref.get().position();
		return convertDirectedToLua(pos);
	}

	/**
	 * Gets the destination of a tardis
	 * @param id string uuid
	 * @return an array of the form {X, Y, Z, Rotation, Dimension ID}
	 */
	@LuaFunction
	public final Object[] getDestination(String id) {
		Optional<ServerTardis> ref = Computed.findTardis(id);
				
		if (ref.isEmpty()) {
			return new Object[]{0, 0, 0, 0, ""};
		}

		AbsoluteBlockPos.Directed pos = ref.get().destination();
		return convertDirectedToLua(pos);
	}

	/**
	 * Gets the flight speed of a tardis
	 * @param id string uuid
	 * @return the speed
	 */
	@LuaFunction
	public final int getSpeed(String id) {
		Optional<ServerTardis> ref = Computed.findTardis(id);

		if (ref.isEmpty()) {
			return -1;
		}

		return ref.get().getTravel().getSpeed();
	}

	/**
	 * Sets the speed of a tardis
	 * @param id string uuid
	 * @param speed integer speed
	 */
	@LuaFunction
	public final void setSpeed(String id, int speed) {
		Computed.executeOnTardis(id, tardis -> tardis.getTravel().setSpeed(MathHelper.clamp(speed, 0, tardis.getTravel().getMaxSpeed())));
	}

	/**
	 * Progress of a flight
	 * @param id string uuid
	 * @return 0-100 percentage
	 */
	@LuaFunction
	public final int getFlightProgress(String id) {
		Optional<ServerTardis> ref = Computed.findTardis(id);

		if (ref.isEmpty()) {
			return -1;
		}

		return ref.get().getHandlers().getFlight().getDurationAsPercentage();
	}

	private static boolean isTardisDim(World world) {
		return TardisUtil.getTardisDimension().getRegistryKey().equals(world.getRegistryKey());
	}

	/**
	 * Finds tardis UUID from the current position of a turtle
	 * turtle must be placed inside the interior of a tardis to return a valid result
	 * @return string uuid, or empty if invalid
	 */
	@LuaFunction
	public final String findTardisId() {
		if (!isTardisDim(turtle.getLevel())) {
			return "";
		}

		Tardis found = TardisUtil.findTardisByInterior(turtle.getPosition(), true);
		if (found == null) return "";

		return found.getUuid().toString();
	}
}
