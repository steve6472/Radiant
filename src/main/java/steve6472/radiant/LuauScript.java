package steve6472.radiant;

import net.hollowcube.luau.LuaState;
import steve6472.core.log.Log;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 1/14/2025
 * Project: MoonDust <br>
 */
public class LuauScript
{
    private static final Logger LOGGER = Log.getLogger(LuauScript.class);
    private final LuaState state;

    LuauScript(LuaState state)
    {
        this.state = state;
    }

    public void run()
    {
        state.pcall(0, 0);
    }

    public void callFunction(String functionName, int resultCount, Object... args)
    {
//        LOGGER.fine("Call function '%s' args: %s".formatted(functionName, Arrays.toString(args)));

        state.getGlobal(functionName);
        for (Object arg : args)
        {
            LuauUtil.push(state, arg);
        }
        state.pcall(args.length, resultCount);
    }

    public LuaState state()
    {
        return state;
    }
}
