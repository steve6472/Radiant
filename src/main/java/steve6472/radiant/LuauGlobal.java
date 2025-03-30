package steve6472.radiant;

import net.hollowcube.luau.BuilinLibrary;
import net.hollowcube.luau.LuaFunc;
import net.hollowcube.luau.LuaState;
import net.hollowcube.luau.internal.vm.lua_h;

import java.util.Map;

/**
 * Created by steve6472
 * Date: 3/17/2025
 * Project: MoonDust <br>
 */
public class LuauGlobal
{
    private final LuaState state;
    private boolean sandboxed;

    private LuauGlobal()
    {
        state = LuaState.newState();
    }

    public static LuauGlobal newGlobal()
    {
        return new LuauGlobal();
    }

    public LuauScript createScript(String filename, byte[] bytecode)
    {
        if (!sandboxed)
        {
            state.sandbox();
            sandboxed = true;
        }

        LuaState state = this.state.newThread();
        state.sandboxThread();
        state.load(filename, bytecode);

        return new LuauScript(state);
    }

    public void registerLib(LuauLib library)
    {
        library.init();
        checkSandboxed();
        if (!library.functions.isEmpty())
            state.registerLib(library.name(), library.functions);
    }

    public void openLibs(BuilinLibrary... libraries)
    {
        state.openLibs(libraries);
    }

    public void registerFunction(String name, LuaFunc func)
    {
        state.pushCFunction(func, name);
        state.setGlobal(name);
    }

    public void addMetaTable(LuauMetaTable metaTable)
    {
        state.newMetaTable(metaTable.typeName);

        // For debug, push '__metaname' with typeName
        state.pushString("__metaname");
        state.pushString(metaTable.typeName);
        state.setTable(-3);

        //        if (metaTable.preventModification)
//        {
//            state.pushCFunction(_ -> {
//                state.error("Attempt to modify a read-only global variable!");
//                return 0;
//            }, "prevent_modification");
//            state.setField(-2, "__newindex");
//        }

        if (metaTable.enableInheritance)
        {
            state.pushString("__index");
            state.pushValue(-2);
        }
        state.setTable(-3);
        if (!metaTable.functions.isEmpty())
            state.registerLib(null, metaTable.functions);
        if (!metaTable.metamethods.isEmpty())
            state.registerLib(null, metaTable.metamethods);

        // Creates a global table with the typeName, gives it "global functions"
        LuauTable table = new LuauTable();
        table.setMetaTable(metaTable);
        table.pushTable(state);
        if (!metaTable.globalFunctions.isEmpty())
        {
            metaTable.globalFunctions.forEach((name, function) -> {
                state.registerLib(null, Map.of(name, function));
            });
        }
        state.setGlobal(metaTable.typeName);
    }

    public LuaState state()
    {
        return state;
    }

    public void close()
    {
        state.close();
    }

    private void checkSandboxed()
    {
        if (sandboxed)
            throw new RuntimeException("Can not complete operation, state is sandboxed!");
    }
}
