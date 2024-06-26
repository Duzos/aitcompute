package mc.duzo.aitcompute.common.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import loqor.ait.core.data.AbsoluteBlockPos;
import loqor.ait.core.data.SerialDimension;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.TardisManager;
import loqor.ait.tardis.link.v2.TardisRef;
import loqor.ait.tardis.util.FlightUtil;
import loqor.ait.tardis.util.TardisUtil;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;
import mc.duzo.aitcompute.Computed;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
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

	@LuaFunction
	public final void dematerialise(IArguments args) throws LuaException {
		boolean autopilot = args.optBoolean(1).orElse(false);
		Computed.executeOnTardis(args.getString(0), tardis -> tardis.travel().dematerialise(autopilot)); // isnt a happy chappy
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

		Computed.executeOnTardis(args.getString(0), tardis -> tardis.travel().setDestination(destination)); // TODO - doesnt appear to be setting
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
		TardisRef ref = Computed.createRef(id);

		if (ref.isEmpty()) {
			return new Object[]{0, 0, 0, 0, ""};
		}

		AbsoluteBlockPos.Directed pos = ref.get().position();
		return convertDirectedToLua(pos);
	}

	@LuaFunction
	public final Object[] getDestination(String id) {
		TardisRef ref = Computed.createRef(id);
				
		if (ref.isEmpty()) {
			return new Object[]{0, 0, 0, 0, ""};
		}

		AbsoluteBlockPos.Directed pos = ref.get().destination();
		return convertDirectedToLua(pos);
	}

	@LuaFunction
	public final int getSpeed(String id) {
		TardisRef ref = Computed.createRef(id);

		if (ref.isEmpty()) {
			return -1;
		}

		return ref.get().flight().speed().get();
	}
	@LuaFunction
	public final void setSpeed(String id, int speed) {
		Computed.executeOnTardis(id, tardis -> tardis.flight().speed().set(MathHelper.clamp(speed, 0, tardis.flight().maxSpeed().get())));
	}

	@LuaFunction
	public final int getFlightProgress(String id) {
		TardisRef ref = Computed.createRef(id);

		if (ref.isEmpty()) {
			return -1;
		}

		return ref.get().flight().getDurationAsPercentage();
	}

	private static boolean isTardisDim(World world) {
		return TardisUtil.getTardisDimension().getRegistryKey().equals(world.getRegistryKey());
	}
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
