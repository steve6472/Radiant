package steve6472.radiant.func;

import net.hollowcube.luau.LuaState;

import steve6472.radiant.func.checks.CheckAny;
import steve6472.radiant.func.checks.CheckType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 3/19/2025
 * Project: MoonDust <br>
 */
public class OverloadFuncArgs
{
    private List<ArgCheck> argumentChecks = new ArrayList<>();

    public int argCount()
    {
        return argumentChecks.size();
    }

    public boolean check(LuaState state, int index)
    {
        // +1 because lua starts at 1 reee
        return argumentChecks.get(index).test(index + 1, state);
    }

    /*
     * Builder
     */

    public OverloadFuncArgs any()
    {
        argumentChecks.add(CheckAny.INSTANCE);
        return this;
    }

    public OverloadFuncArgs custom(steve6472.radiant.func.ArgCheck check)
    {
        argumentChecks.add(check);
        return this;
    }

    private OverloadFuncArgs type(CheckType type)
    {
        argumentChecks.add(type);
        return this;
    }

    public OverloadFuncArgs number()
    {
        return type(CheckType.NUMBER);
    }

    public OverloadFuncArgs function()
    {
        return type(CheckType.FUNCTION);
    }

    public OverloadFuncArgs string()
    {
        return type(CheckType.STRING);
    }

    public OverloadFuncArgs table()
    {
        return type(CheckType.TABLE);
    }

    public OverloadFuncArgs bool()
    {
        return type(CheckType.BOOLEAN);
    }

    public OverloadFuncArgs nil()
    {
        return type(CheckType.NIL);
    }

    @Override
    public String toString()
    {
        return "OverloadFuncArgs{" + "argumentChecks=" + argumentChecks + '}';
    }
}
