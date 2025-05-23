package dk.gtz.graphedit.plugins.lua;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.yalibs.yafunc.IFunction1;
import dk.yalibs.yafunc.IRunnable1;
import dk.yalibs.yafunc.IRunnable2;
import dk.yalibs.yafunc.IRunnable3;
import dk.yalibs.yafunc.IRunnable4;
import party.iroiro.luajava.JFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.Lua.LuaType;
import party.iroiro.luajava.value.LuaValue;

public class LuaUtils {
	private static final Logger logger = LoggerFactory.getLogger(LuaUtils.class);

	public static JFunction wrap(Runnable r) {
		return w(l -> r.run());
	}

	@SuppressWarnings("unchecked")
	public static <T> JFunction wrap(IRunnable1<T> r) {
		return w(l -> r.run((T) l.toObject(-1)));
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2> JFunction wrap(IRunnable2<T1, T2> r) {
		return w(l -> r.run((T1) l.toObject(-2), (T2) l.toObject(-1)));
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2, T3> JFunction wrap(IRunnable3<T1, T2, T3> r) {
		return w(l -> r.run((T1) l.toObject(-3), (T2) l.toObject(-2), (T3) l.toObject(-1)));
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2, T3, T4> JFunction wrap(IRunnable4<T1, T2, T3, T4> r) {
		return w(l -> r.run((T1) l.toObject(-4), (T2) l.toObject(-3), (T3) l.toObject(-2),
				(T4) l.toObject(-1)));
	}

	private static JFunction w(IRunnable1<Lua> r) {
		return (Lua l) -> {
			try {
				r.run(l);
				return 0;
			} catch (Exception e) {
				return error(e);
			}
		};
	}

	private static int error(Exception e) {
		logger.error("jfunction call failed: {}", e.getMessage(), e);
		return 1;
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, LuaType type, IFunction1<T, LuaValue> converter)
			throws IllegalArgumentException {
		if (value instanceof LuaValue v) {
			if (v.type() != type)
				throw new IllegalArgumentException("expected type '%s', but got '%s'"
						.formatted(type.name(), v.type().name()));
			return converter.run(v);
		}
		if (value instanceof String s) {
			if (type != LuaType.STRING)
				throw new IllegalArgumentException(
						"expected type '%s', but got String".formatted(type.name()));
			return (T) s;
		}
		throw new IllegalArgumentException("unexpected value type '%s'".formatted(value.getClass().getName()));
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, LuaType type) throws IllegalArgumentException {
		return convert(value, type, v -> (T) v);
	}
}
