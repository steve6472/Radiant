package steve6472.radiant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 3/20/2025
 * Project: Radiant <br>
 */
public class LuaTableOps implements DynamicOps<Object>
{
    public static final LuaTableOps INSTANCE = new LuaTableOps();

    private LuaTableOps() {}

    @Override
    public <U> U convertTo(DynamicOps<U> arg0, Object arg1)
    {
        throw new UnsupportedOperationException("Unimplemented method 'convertTo'");
    }

    @Override
    public Object createList(Stream<Object> input)
    {
        LuauTable table = new LuauTable();
        int[] index = {0};
        input.forEachOrdered(o -> table.table().put(index[0]++, o));
        return table;
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map)
    {
        LuauTable table = new LuauTable();
        map.forEach(pair -> table.add(pair.getFirst(), pair.getSecond()));
        return table;
    }

    @Override
    public Object createNumeric(Number input)
    {
        return input;
    }

    @Override
    public Object createString(String arg0)
    {
        return arg0;
    }

    @Override
    public Object empty()
    {
        return null;
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input)
    {
        if (input instanceof LuauTable table)
        {
            return DataResult.success(table.table().entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())));
        }
        return DataResult.error(() -> "Not a LuaTable: " + input);
    }

    @Override
    public DataResult<Number> getNumberValue(Object input)
    {
        if (input instanceof Number value)
        {
            return DataResult.success(value);
        }
        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public DataResult<Stream<Object>> getStream(Object input)
    {
        if (input instanceof LuauTable table)
        {
            return DataResult.success(table.table().values().stream());
        }
        return DataResult.error(() -> "Not a LuaTable: " + input);
    }

    @Override
    public DataResult<String> getStringValue(Object input)
    {
        if (!(input instanceof String str))
            return DataResult.error(() -> "Not a string: " + input);
        return DataResult.success(str);
    }

    @Override
    public DataResult<Object> mergeToList(Object input, Object value)
    {
        if (input == empty())
        {
            LuauTable table = new LuauTable();
            table.table().put(table.table().size() + 1, value);
            return DataResult.success(table);
        }
        if (input instanceof LuauTable table)
        {
            LuauTable copy = new LuauTable();
            copy.table().putAll(table.table());
            copy.table().put(table.table().size() + 1, value);
            return DataResult.success(copy);
        }

        throw new UnsupportedOperationException("Unimplemented rest of method 'mergeToList' args: input=%s, value=%s".formatted(input, value));
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Object key, Object value)
    {
        if (map == empty())
        {
            Map<Object,Object> newMap = Map.of(key, value);
            LuauTable table = new LuauTable();
            table.table().putAll(newMap);
            return DataResult.success(table);
        } else if (map instanceof LuauTable table)
        {
            table.table().put(key, value);
            return DataResult.success(table);
        }

        throw new UnsupportedOperationException("Unimplemented rest of method 'mergeToMap' " + "args: map=%s, key=%s, value=%s".formatted(map, key, value));
    }

    @Override
    public Object remove(Object arg0, String arg1)
    {
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }
}
