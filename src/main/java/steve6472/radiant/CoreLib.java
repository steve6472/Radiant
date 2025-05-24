package steve6472.radiant;

import net.hollowcube.luau.LuaState;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 3/19/2025
 * Project: MoonDust <br>
 */
public class CoreLib extends LuauLib
{
    public static final CoreLib INSTANCE = new CoreLib();

    private CoreLib() {}

    @Override
    public void createFunctions()
    {
        addFunction("nanoTime", state -> {
            state.pushNumber((double) System.nanoTime());
            return 1;
        });

        addFunction("dump", state ->
        {
            String result = dumpRecursive(state, 1);
            state.pushString(result);
            return 1;
        });

        addFunction("debug_toJavaTable", state ->
        {
            int index = state.absIndex(1);
            if (state.isTable(index))
            {
                LuauTable table = new LuauTable();
                table.readTable(state, 1);
                System.out.println(table);
            }
            return 0;
        });

        addFunction("debug_toJava", state ->
        {
            int index = state.absIndex(1);
            Object java = LuauUtil.toJava(state, index);
            state.pushString(Objects.toString(java));
            return 1;
        });

        addFunction("debug_countPassedArguments", state ->
        {
            int argc = state.getTop();
            state.pushInteger(argc);
            return 1;
        });
    }

    private String dumpRecursive(LuaState state, int startIndex)
    {
        int index = state.absIndex(startIndex);
        if (state.isTable(index))
        {
            StringBuilder bob = new StringBuilder();
            bob.append("{ ");
            state.pushNil();
            while (state.next(index))
            {
                state.pushValue(-2);

                boolean number = state.isNumber(-1);
                String key = state.toString(-1);
                if (number)
                    bob.append("[%s] = ".formatted(key));
                else
                    bob.append("[\"%s\"] = ".formatted(key));

                if (state.rawEqual(index, -2))
                {
                    // Handle metatable with inheritance
                    bob.append("__index, ");
                } else
                {
                    bob.append(dumpRecursive(state, -2));
                    bob.append(", ");
                }

                state.pop(2);
            }
            // Remove the last ", "
            if (bob.charAt(bob.length() - 2) != '{')
                bob.setLength(bob.length() - 2);
            bob.append(" } ");

            int metaTable = state.getMetaTable(index);
            if (metaTable != 0)
            {
                bob.append(" getmetatable = ");
                bob.append(dumpRecursive(state, -1));
                state.pop(1);
            }

            return bob.toString();
        }
        // tostring needs some more processing for some types
        else
        {
            return LuauUtil.toString(state, index);
        }
    }

    @Override
    public String name()
    {
        return "core";
    }
}
