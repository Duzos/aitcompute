package mc.duzo.aitcompute.common.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import loqor.ait.core.data.AbsoluteBlockPos;
import loqor.ait.core.data.SerialDimension;
import loqor.ait.tardis.TardisManager;
import loqor.ait.tardis.link.v2.TardisRef;
import loqor.ait.tardis.util.FlightUtil;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;
import mc.duzo.aitcompute.Computed;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

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

	/*
	@LuaFunction
	public final void summonTardis(IArguments args) throws LuaException {
		String uuid = args.getString(0);

		BlockPos pos = turtle.getPosition();
		if (args.optInt(1).isPresent()) {
			// Coords must be passed in then
			pos = new BlockPos(args.getInt(1), args.getInt(2), args.getInt(3));
		}

		int rotation = args.optInt(4).orElse(turtle.getDirection().getHorizontal());

		AbsoluteBlockPos.Directed target = new AbsoluteBlockPos.Directed(
				pos,
				new SerialDimension(turtle.getLevel()),
				rotation
		);

		ServerTardisManager.getInstance().getTardis(Computed.getServer().orElseThrow(), UUID.fromString(uuid), tardis -> {
			FlightUtil.travelTo(tardis, target);
		});
	}
	 */

	@LuaFunction
	public final void dematerialise(IArguments args) throws LuaException {
		boolean autopilot = args.optBoolean(1).orElse(false);
		Computed.executeOnTardis(args.getString(0), tardis -> tardis.travel().dematerialise(autopilot));
	}

	@LuaFunction
	public final void materialise(IArguments args) throws LuaException {
		Computed.executeOnTardis(args.getString(0), tardis -> tardis.travel().materialise());
	}

	@LuaFunction
	public final void setDestination(IArguments args) throws LuaException {
		String turtleDim = turtle.getLevel().getRegistryKey().getValue().toString();

		AbsoluteBlockPos.Directed destination = new AbsoluteBlockPos.Directed(
				new BlockPos(args.getInt(1), args.getInt(2), args.getInt(3)),
				new SerialDimension(args.optString(5).orElse(turtleDim)),
				args.getInt(4)
		);

		Computed.executeOnTardis(args.getString(0), tardis -> tardis.travel().setDestination(destination));
	}

	/**
	 * @return an array of the form {X, Y, Z, Rotation, Dimension ID}
	 */
	private static Object[] convertDirectedToLua(AbsoluteBlockPos.Directed pos) {
		// X, Y, Z, Rotation, Dimension ID
		return new Object[]{pos.getX(), pos.getY(), pos.getZ(), pos.getRotation(), pos.getDimension().getValue()};
	}

	@LuaFunction
	public final Object[] getPosition(String id) {
		TardisRef ref = new TardisRef(UUID.fromString(id), uuid -> ServerTardisManager.getInstance().demandTardis(Computed.getServer().orElseThrow(), uuid));

		if (ref.isEmpty()) {
			return new Object[]{0, 0, 0, 0, ""};
		}

		AbsoluteBlockPos.Directed pos = ref.get().position();
		return convertDirectedToLua(pos);
	}

	@LuaFunction
	public final Object[] getDestination(String id) {
		TardisRef ref = new TardisRef(UUID.fromString(id), uuid -> ServerTardisManager.getInstance().demandTardis(Computed.getServer().orElseThrow(), uuid));

		if (ref.isEmpty()) {
			return new Object[]{0, 0, 0, 0, ""};
		}

		AbsoluteBlockPos.Directed pos = ref.get().destination();
		return convertDirectedToLua(pos); //
	}
}
