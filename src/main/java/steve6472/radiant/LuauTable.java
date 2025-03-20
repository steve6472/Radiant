package steve6472.radiant;

import net.hollowcube.luau.LuaState;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 3/17/2025
 * Project: MoonDust <br>
 */
public class LuauTable
{
    private final Map<Object, Object> table = new LinkedHashMap<>();
    private LuauMetaTable metaTable;

    public LuauTable add(Object key, Object value)
    {
        table.put(key, value);
        return this;
    }

    public Object get(Object key)
    {
        return table.get(key);
    }

    public void setMetaTable(LuauMetaTable metaTable)
    {
        this.metaTable = metaTable;
    }

    public void pushTable(LuaState state)
    {
        state.newTable();
        table.forEach((name, value) -> pushKeyValuePair(state, name, value));

        if (metaTable != null)
            metaTable.assignMetaTable(state);
    }

    private void pushKeyValuePair(LuaState state, Object key, Object value)
    {
        // Push key = value
        LuauUtil.push(state, key);
        LuauUtil.push(state, value);

        // Store key and value into the table
        state.setTable(-3);
    }

    public Map<Object, Object> table()
    {
        return table;
    }

    @Override
    public String toString()
    {
        return "LuauTable" + table;
    }
}
