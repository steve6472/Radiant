package steve6472.radiant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 3/17/2025
 * Project: MoonDust <br>
 */
public class M2Lib extends LuauLib
{
    public static final M2Lib INSTANCE = new M2Lib();

    private M2Lib() {}

    @Override
    public void createFunctions()
    {
        addFunction("add", state -> {
            int left = state.checkIntegerArg(1);
            int right = state.checkIntegerArg(2);
            state.pushInteger(left + right);
            return 1;
        });

        addFunction("sub", state -> {
            int left = state.checkIntegerArg(1);
            int right = state.checkIntegerArg(2);
            state.pushInteger(left - right);
            //                        state.error("error from java");
            return 1;
        });

        addFunction("newarray", state -> {
            List<Integer> arr = new ArrayList<>();
            state.newUserData(arr);

            LuauLearning.myarray.assignMetaTable(state);

            return 1;
        });

        addFunction("newvec2", state -> {
            double x = state.checkNumberArg(1);
            double y = state.checkNumberArg(2);

            LuauTable table = new LuauTable();
            table.setMetaTable(LuauLearning.vec2MetaTable);
            table.add("x", x);
            table.add("y", y);
            table.pushTable(state);

            return 1;
        });

        addFunction("show", state -> {
            //noinspection unchecked
            List<Integer> arr = (List<Integer>) state.checkUserDataArg(1, "myarray");
            LuauLearning.LUA_LOGGER.fine("array: " + arr);
            return 0;
        });
    }

    @Override
    public String name()
    {
        return "m2";
    }
}
