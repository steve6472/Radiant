package steve6472.radiant.func.checks;

import net.hollowcube.luau.LuaState;
import steve6472.radiant.func.ArgCheck;

/**
 * Created by steve6472
 * Date: 3/26/2025
 * Project: Radiant <br>
 */
public class CheckUser implements ArgCheck
{
    private final String type;

    public CheckUser(String type)
    {
        this.type = type;
    }

    @Override
    public boolean test(int index, LuaState state)
    {
        if (!state.isUserData(index))
            return false;

        state.getMetaTable(index);
        state.getField(-1, "__metaname");
        if (state.isString(-1))
        {
            String string = state.toString(-1);
            return string.equals(type);
        }

        return false;
    }
}
