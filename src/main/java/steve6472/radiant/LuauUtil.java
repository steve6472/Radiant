package steve6472.radiant;

import net.hollowcube.luau.LuaState;
import steve6472.core.log.Log;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/17/2025
 * Project: MoonDust <br>
 */
public class LuauUtil
{
    private static final Logger LOGGER = Log.getLogger(LuauUtil.class);

    public static void push(LuaState state, Object obj)
    {
        switch (obj)
        {
            case String str -> state.pushString(str);
            case Integer num -> state.pushInteger(num);
            case Boolean bool -> state.pushBoolean(bool);
            case Double num -> state.pushNumber(num);
            case LuauTable table -> table.pushTable(state);
            default -> throw new IllegalStateException("Unexpected value: " + obj);
        }
    }

    public static void debugStack(LuaState state)
    {
        int top = state.getTop();
        for (int i = 1; i <= top; i++)
        {
            var type = state.type(i);
            var str = state.toString(i);
            LOGGER.fine("Type at %s: %s (%s)".formatted(i, type, str));
        }
        LOGGER.fine("------------");
    }

    public static String toString(LuaState state, int index)
    {
        String toString = state.toString(index);
        if (!toString.isEmpty() || state.isString(index))
        {
            return toString;
        } else if (state.isBoolean(index))
        {
            return state.toBoolean(index) ? "true" : "false";
        } else if (state.isNil(index))
        {
            return "nil";
        } else if (state.isFunction(index))
        {
            // TODO: toPointer is not yet implemented
            return "<FUNCTION>" /* + " " + state.toPointer(index)*/;
        } else
        {
            return state.typeName(index);
        }
    }
}
