package steve6472.radiant;

import com.mojang.datafixers.util.Pair;
import net.hollowcube.luau.LuaFunc;
import net.hollowcube.luau.LuaState;
import steve6472.core.log.Log;
import steve6472.radiant.func.OverloadFuncArgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/17/2025
 * Project: MoonDust <br>
 */
public class LuauMetaTable
{
    private static final Logger LOGGER = Log.getLogger(LuauMetaTable.class);

    final Map<String, LuaFunc> functions = new HashMap<>();
    final Map<String, LuaFunc> globalFunctions = new HashMap<>();
    final Map<String, LuaFunc> metamethods = new HashMap<>();
    final Map<String, List<Pair<OverloadFuncArgs, LuaFunc>>> overloadedFunctions = new HashMap<>();
    final Map<String, List<Pair<OverloadFuncArgs, LuaFunc>>> overloadedGlobalFunctions = new HashMap<>();

    final String typeName;
    boolean enableInheritance;
    boolean preventModification;

    public LuauMetaTable(String typeName)
    {
        this.typeName = typeName;
    }

    public LuauMetaTable addFunction(String name, LuaFunc func)
    {
        LuaFunc old = functions.put(name, func);
        if (old != null)
            LOGGER.warning("Replaced method '%s' in metatable '%s'".formatted(name, typeName));
        return this;
    }

    public LuauMetaTable addGlobalFunction(String name, LuaFunc func)
    {
        LuaFunc old = globalFunctions.put(name, func);
        if (old != null)
            LOGGER.warning("Replaced method '%s' in metatable '%s'".formatted(name, typeName));
        return this;
    }

    /// [Meta Methods](https://gist.github.com/oatmealine/655c9e64599d0f0dd47687c1186de99f)
    public LuauMetaTable addMetaMethod(String name, LuaFunc func)
    {
        LuaFunc old = metamethods.put(name, func);
        if (old != null)
            LOGGER.warning("Replaced metamethod '%s' in metatable '%s'".formatted(name, typeName));
        return this;
    }

    /// any() type should always be the last added, no automatic sorting is done (steve was lazy)
    public LuauMetaTable addOverloadedFunc(String name, OverloadFuncArgs args, LuaFunc func)
    {
        overloadedFunctions.computeIfAbsent(name, _ -> new ArrayList<>()).add(Pair.of(args, func));
        return this;
    }

    /// any() type should always be the last added, no automatic sorting is done (steve was lazy)
    public LuauMetaTable addOverloadedGlobalFunc(String name, OverloadFuncArgs args, LuaFunc func)
    {
        overloadedGlobalFunctions.computeIfAbsent(name, _ -> new ArrayList<>()).add(Pair.of(args, func));
        return this;
    }

    public LuauMetaTable processOverloadedFunctions()
    {
        overloadedFunctions.forEach((key, list) -> {
            functions.put(key, state -> {
                LuaFunc funcToRun = choose(state, list);
                if (funcToRun == null)
                {
                    throw new RuntimeException("Incorrect arguments passed to 'overloaded function' " + key + ", passed: " + printArgumentTypes(state));
                }
                return funcToRun.call(state);
            });
        });

        overloadedGlobalFunctions.forEach((key, list) -> {
            globalFunctions.put(key, state -> {
                LuaFunc funcToRun = choose(state, list);
                if (funcToRun == null)
                {
                    throw new RuntimeException("Incorrect arguments passed to 'global overloaded function' " + key + ", passed: " + printArgumentTypes(state));
                }
                return funcToRun.call(state);
            });
        });
        overloadedFunctions.clear();
        overloadedGlobalFunctions.clear();
        return this;
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

    private String printArgumentTypes(LuaState state)
    {
        int argCount = state.getTop();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < argCount; i++)
        {
            s.append(state.typeName(i + 1));
            s.append(", ");
        }
        s.setLength(s.length() - 2);
        return s.toString();
    }

    public LuauMetaTable setInheritance(boolean enableInheritance)
    {
        this.enableInheritance = enableInheritance;
        return this;
    }

    public LuauMetaTable preventModification(boolean preventModification)
    {
        this.preventModification = preventModification;
        return this;
    }

    public void assignMetaTable(LuaState state)
    {
        state.getMetaTable(typeName);
        state.setMetaTable(-2);
    }
}
