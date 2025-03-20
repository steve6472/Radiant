package steve6472.radiant.func.checks;

import net.hollowcube.luau.LuaState;
import net.hollowcube.luau.LuaType;
import steve6472.radiant.func.ArgCheck;

/**
 * Created by steve6472
 * Date: 3/19/2025
 * Project: MoonDust <br>
 */
public class CheckType implements ArgCheck
{
    public static final CheckType NUMBER = new CheckType(LuaType.NUMBER);
    public static final CheckType FUNCTION = new CheckType(LuaType.FUNCTION);
    public static final CheckType STRING = new CheckType(LuaType.STRING);
    public static final CheckType TABLE = new CheckType(LuaType.TABLE);
    public static final CheckType BOOLEAN = new CheckType(LuaType.BOOLEAN);

    public static final CheckType NIL = new CheckType(LuaType.NIL);

    private final LuaType type;

    private CheckType(LuaType type)
    {
        this.type = type;
    }

    @Override
    public boolean test(int index, LuaState state)
    {
        return state.type(index) == type;
    }

    @Override
    public String toString()
    {
        return "CheckType{" + "type=" + type + '}';
    }
}
