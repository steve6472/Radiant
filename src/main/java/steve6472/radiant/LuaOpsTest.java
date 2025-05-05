package steve6472.radiant;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * Created by steve6472
 * Date: 3/19/2025
 * Project: MoonDust <br>
 */
public class LuaOpsTest
{
    private record Test(String value, String otherValue, double someNumber, List<String> tags, boolean flag)
    {
        public static final Codec<Test> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("the_key").forGetter(Test::value),
            Codec.STRING.fieldOf("the_other_key").forGetter(Test::otherValue),
            Codec.DOUBLE.fieldOf("a_number").forGetter(Test::someNumber),
            Codec.STRING.listOf().fieldOf("tags").forGetter(Test::tags),
            Codec.BOOL.fieldOf("flag").forGetter(Test::flag)
        ).apply(instance, Test::new));
    }

    // I need to convert JavaObject (Test) to LuaTable (LuaTable) and back

    public static void main(String[] args)
    {
        String testJson = """
        {
            "the_key": "the_value"
        }
        """;

        Test test = new Test(
            "the_value_but_test",
            "I am STEVE",
            69,
            List.of("smart", "shart", "shark", "987", "XZY", "ZYX", "aAAAA"),
            true);

        /*
        JsonObject json = JsonParser.parseString(testJson).getAsJsonObject();

        DataResult<Pair<Test,JsonElement>> decode = Test.CODEC.decode(JsonOps.INSTANCE, json);
        Pair<Test,JsonElement> orThrow = decode.getOrThrow();
        Test first = orThrow.getFirst();
        JsonElement second = orThrow.getSecond();

        System.out.println(first);*/

        DataResult<JsonElement> encodeStartJson = Test.CODEC.encodeStart(JsonOps.INSTANCE, test);
        System.out.println("Test -> JSON : " + encodeStartJson.getOrThrow());

        DataResult<Object> encodeStart = Test.CODEC.encodeStart(LuaTableOps.INSTANCE, test);
        LuauTable table = ((LuauTable) encodeStart.getOrThrow());
        System.out.println("Test -> LuaTable : " + table);

        //var decodeLua = Test.CODEC.decode(JsonOps.INSTANCE, table);
        //System.out.println("LuaTable -> JSON : " + encodeStartJson.getOrThrow());

        DataResult<Pair<Test,Object>> decode = Test.CODEC.decode(LuaTableOps.INSTANCE, table);
        Pair<Test,Object> orThrow = decode.getOrThrow();
        Test first = orThrow.getFirst();
        Object second = orThrow.getSecond();
        System.out.println("LuaTable -> Test, first: " + first);
        System.out.println("LuaTable -> Test, second: " + second);
    }
}
