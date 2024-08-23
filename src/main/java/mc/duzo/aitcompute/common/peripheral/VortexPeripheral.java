package mc.duzo.aitcompute.common.peripheral;

import com.google.gson.JsonElement;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import loqor.ait.core.AITItems;
import loqor.ait.core.item.KeyItem;
import loqor.ait.registry.impl.TardisComponentRegistry;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.base.KeyedTardisComponent;
import loqor.ait.tardis.base.TardisComponent;
import loqor.ait.tardis.data.properties.Value;
import loqor.ait.tardis.util.TardisUtil;
import loqor.ait.tardis.wrapper.server.ServerTardis;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;
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

	private static boolean isTardisDim(World world) {
		return TardisUtil.getTardisDimension().getRegistryKey().equals(world.getRegistryKey());
	}
	private boolean hasKey(int slot, UUID tardis) {
		ItemStack stack = this.turtle.getInventory().getStack(slot);
		if (!(stack.getItem() instanceof KeyItem)) return false;

		if (stack.isOf(AITItems.SKELETON_KEY)) return true;

		Tardis found = KeyItem.getTardis(this.turtle.getLevel(), tardis);
		return tardis.equals(found.getUuid());
	}
	private Tardis getTardis(UUID tardis) {
		return ServerTardisManager.getInstance().demandTardis(this.turtle.getLevel().getServer(), tardis);
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

	/**
	 * Sets a property on a tardis
	 * @param args tardis: str, component: str, value: str, data: str
	 * @return success
	 */
	@LuaFunction
	public final <T> boolean set(IArguments args) throws LuaException {
		UUID tardisId = UUID.fromString(args.getString(0));
		int slot = this.turtle.getSelectedSlot();
		if (!hasKey(slot, tardisId)) return false;

		Tardis tardis = getTardis(tardisId); // poo
		TardisComponent.IdLike id = TardisComponentRegistry.getInstance().get(args.getString(1).toUpperCase());

		if (!(tardis.handler(id) instanceof KeyedTardisComponent keyed)) return false;

		String valueName = args.getString(2);
		Value<T> value = keyed.getPropertyData().getExact(valueName);
		Class<?> classOfT = value.getProperty().getType().getClazz();

		T obj = (T) ServerTardisManager.getInstance().getFileGson().fromJson(args.getString(3), classOfT);

		value.set(obj);

		return true;
	}

	/**
	 * Gets a property of a tardis
	 * @param args tardis: str, component: str, value: str
	 * @return the found property or empty string
	 */
	@LuaFunction
	public final <T> String get(IArguments args) throws LuaException {
		UUID tardisId = UUID.fromString(args.getString(0));
		int slot = this.turtle.getSelectedSlot();
		if (!hasKey(slot, tardisId)) return "";

		Tardis tardis = getTardis(tardisId); // poo
		TardisComponent.IdLike id = TardisComponentRegistry.getInstance().get(args.getString(1).toUpperCase());

		if (!(tardis.handler(id) instanceof KeyedTardisComponent keyed)) return "";

		String valueName = args.getString(2);
		Value<T> value = keyed.getPropertyData().getExact(valueName);
		T obj = value.get();

		String json = ServerTardisManager.getInstance().getFileGson().toJson(obj);

		return json;
	}

	/**
	 * Updates the tardis' flight state
	 * @param args tardis: str
	 */
	@LuaFunction
	public final void tryFly(IArguments args) throws LuaException {
		UUID tardisId = UUID.fromString(args.getString(0));
		int slot = this.turtle.getSelectedSlot();
		if (!hasKey(slot, tardisId)) return;

		Tardis tardis = getTardis(tardisId);
		tardis.travel().speed(tardis.travel().speed());
	}
}
