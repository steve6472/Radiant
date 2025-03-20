package steve6472.radiant;

import com.mojang.datafixers.util.Pair;
import net.hollowcube.luau.LuaFunc;
import net.hollowcube.luau.LuaState;
import steve6472.radiant.func.OverloadFuncArgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 1/15/2025
 * Project: MoonDust <br>
 */
public abstract class LuauLib
{
    Map<String, LuaFunc> functions = new HashMap<>();
    Map<String, List<Pair<OverloadFuncArgs, LuaFunc>>> overloadedFunctions = new HashMap<>();
    private boolean canInit = true;

    void init()
    {
        if (!canInit)
            return;

        canInit = false;
        createFunctions();
        processOverloadedFunctions();
    }

    public abstract void createFunctions();
    public abstract String name();

    private void processOverloadedFunctions()
    {
        overloadedFunctions.forEach((key, list) -> {
            functions.put(key, state -> {
                LuaFunc funcToRun = choose(state, list);
                if (funcToRun == null)
                {
                    throw new RuntimeException("Incorrect arguments passed to 'overloaded function' " + key);
                }
                return funcToRun.call(state);
            });
        });
    }

    private LuaFunc choose(LuaState state, List<Pair<OverloadFuncArgs, LuaFunc>> list)
    {
        int argCount = state.getTop();
        m: for (Pair<OverloadFuncArgs, LuaFunc> pair : list)
        {
            OverloadFuncArgs args = pair.getFirst();
            if (args.argCount() != argCount) continue;
            for (int i = 0; i < argCount; i++)
            {
                if (!args.check(state, i)) continue m;
            }
            return pair.getSecond();
        }
        return null;
    }

    protected void addFunction(String name, LuaFunc func)
    {
        functions.put(name, func);
    }

    /// any() type should always be the last added, no automatic sorting is done (steve was lazy)
    protected void addOverloadedFunc(String name, OverloadFuncArgs args, LuaFunc func)
    {
        overloadedFunctions.computeIfAbsent(name, _ -> new ArrayList<>()).add(Pair.of(args, func));
    }

    protected final OverloadFuncArgs args()
    {
        return new OverloadFuncArgs();
    }
}
