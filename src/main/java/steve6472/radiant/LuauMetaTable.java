package steve6472.radiant;

import net.hollowcube.luau.LuaFunc;
import net.hollowcube.luau.LuaState;
import steve6472.core.log.Log;

import java.util.HashMap;
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

    final String typeName;
    boolean enableInheritance;

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

    public LuauMetaTable addMetaMethod(String name, LuaFunc func)
    {
        LuaFunc old = metamethods.put(name, func);
        if (old != null)
            LOGGER.warning("Replaced metamethod '%s' in metatable '%s'".formatted(name, typeName));
        return this;
    }

    public LuauMetaTable setInheritance(boolean enableInheritance)
    {
        this.enableInheritance = enableInheritance;
        return this;
    }

    public void assignMetaTable(LuaState state)
    {
        state.getMetaTable(typeName);
        state.setMetaTable(-2);
    }
}
