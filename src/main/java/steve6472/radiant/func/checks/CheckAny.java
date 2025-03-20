package steve6472.radiant.func.checks;

import net.hollowcube.luau.LuaState;
import steve6472.radiant.func.ArgCheck;

/**
 * Created by steve6472
 * Date: 3/19/2025
 * Project: MoonDust <br>
 */
public class CheckAny implements ArgCheck
{
    public static final CheckAny INSTANCE = new CheckAny();

    private CheckAny() {}

    @Override
    public boolean test(int index, LuaState luaState)
    {
        return true;
    }
}
