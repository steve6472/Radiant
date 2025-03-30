package steve6472.radiant;

import net.hollowcube.luau.LuaType;
import net.hollowcube.luau.compiler.LuauCompiler;
import steve6472.core.log.Log;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 1/13/2025
 * Project: MoonDust <br>
 */
public class LuauLearning
{
    public static final Logger LUA_LOGGER = Log.getLogger("Lua");

    public static LuauMetaTable vec2MetaTable;
    public static LuauMetaTable myarray;

    public static void main(String[] args) throws Exception
    {
        vec2MetaTable = new LuauMetaTable("vec2");
        vec2MetaTable.setInheritance(true);
        vec2MetaTable.addMetaMethod("__add", state -> {
            state.checkType(1, LuaType.TABLE);
            state.getField(1, "x");
            state.getField(1, "y");
            double x1 = state.toNumber(-2);
            double y1 = state.toNumber(-1);
            state.pop(2);

            state.checkType(2, LuaType.TABLE);
            state.getField(2, "x");
            state.getField(2, "y");
            double x2 = state.toNumber(-2);
            double y2 = state.toNumber(-1);
            state.pop(2);

            LuauTable table = new LuauTable();
            table.setMetaTable(LuauLearning.vec2MetaTable);
            table.add("x", x1 + x2);
            table.add("y", y1 + y2);
            table.pushTable(state);

            return 1;
        });
        vec2MetaTable.addGlobalFunction("new", state -> {
            double x = state.checkNumberArg(1);
            double y = state.checkNumberArg(2);

            LuauTable table = new LuauTable();
            table.setMetaTable(LuauLearning.vec2MetaTable);
            table.add("x", x);
            table.add("y", y);
            table.pushTable(state);

            return 1;
        });

        myarray = new LuauMetaTable("myarray");
        myarray.setInheritance(true);
        myarray.addFunction("push", state ->
        {
            //noinspection unchecked
            List<Integer> arr = (List<Integer>) state.checkUserDataArg(1, "myarray");
            int value = state.checkIntegerArg(2);
            arr.add(value);
            return 0;
        });

        var source = """
            print('hello from lua')
            
            type Point = { x: number, y: number }
            
            local arr = m2.newarray()
            arr:push(1)
            arr:push(2)
            m2.show(arr)
            
            local p: Point = { x = 0x0f, y = 2 }
            
            function onEvent(event)
                print("onEvent: "..event.fname)
                print("onEvent: "..event.ftype)
            end
            
            function testEvent()
                print("Test Event")
            end
            
            local num = 42
            
            print(m2.add(1, 2))
            print(m2.sub(1, 2))
            abc()
            print("x:"..p.x.." y:"..p.y)
            p.x += 1
            print("x:"..p.x.." y:"..p.y)
            
            print("Metatable vec2 test:")
            local point1 = vec2.new(3, 1)
            print("point1 -> "..core.dump(point1))
            print("vec2   -> "..core.dump(getmetatable(point1)))
            print("vec2!!!-> "..core.dump(vec2))
            
            local point2 = m2.newvec2(2, 8)
            print("point2 -> "..core.dump(point2))
            
            local added = point1 + point2
            print("added  -> "..core.dump(added))
            
            local otherTestTable = {theWorld = "is shit"}
            local testTable = {a = 'string', b = 'other text', c = 420.69, fact = otherTestTable}
            --local testTable = {4, 5, 6}
            --local testTable = {}
            --core.dumpWIP(testTable)
            print(core.dump(testTable))
            core.toJavaTable(testTable)
            
            print("passed arg count test: ")
            print(core.debug_countPassedArguments())
            print(core.debug_countPassedArguments("1"))
            print(core.debug_countPassedArguments("HEllo", 5))
            print(core.debug_countPassedArguments(951, 9857, 63542, testTable))
            
            print("overload test: ")
            print(core.overloadTest(0))
            print(core.overloadTest(false))
            print(core.overloadTest("hi"))
            print(core.overloadTest(0, 5))
            print("hello", "world")
            """;
//        source += SET;
        byte[] bytecode = LuauCompiler.DEFAULT.compile(source);

        LuauGlobal global = LuauGlobal.newGlobal();

        try
        {
            global.openLibs();
            global.addMetaTable(myarray);
            global.addMetaTable(vec2MetaTable);

            global.registerLib(M2Lib.INSTANCE);
            global.registerLib(CoreLib.INSTANCE);
            global.registerFunction("abc", _ -> {
                LUA_LOGGER.fine("Hello from Java!");
                return 0;
            });

            // Override default print function
            global.registerFunction("print", state -> {
                StringBuilder bob = new StringBuilder();
                for (int i = 1; i <= state.getTop(); i++)
                {
                    bob.append(LuauUtil.toString(state, i));
                    if (i != state.getTop())
                        bob.append("    ");
                }
                LUA_LOGGER.fine(bob.toString());
                return 0;
            });

            LuauScript script = global.createScript("main.luau", bytecode);
            script.run();

//            LuauTable table = new LuauTable();
//            table.add("fname", "Margie");
//            table.add("ftype", "name");
//
//            script.callFunction("onEvent", 0, table);

            global.state().pop(1); // the thread was added to the stack, remove it so that it can be garbage collected.

        } finally
        {
            global.close();
        }
    }

    public static String SET = """
            print("Set:")
        
            Set = {}
            Set.mt = {}    -- metatable for sets
        
            function Set.union (a,b)
                local res = Set.new{}
                for k in pairs(a) do res[k] = true end
                for k in pairs(b) do res[k] = true end
                return res
            end
        
            function Set.intersection (a,b)
                local res = Set.new{}
                for k in pairs(a) do
                    res[k] = b[k]
                end
                return res
            end
        
            function Set.new (t)   -- 2nd version
                local set = {}
                setmetatable(set, Set.mt)
                for _, l in ipairs(t) do set[l] = true end
                return set
            end
        
            function Set.union (a,b)
                local res = Set.new{}
                for k in pairs(a) do res[k] = true end
                for k in pairs(b) do res[k] = true end
                return res
            end
        
            Set.mt.__add = Set.union
        
            function Set.intersection (a,b)
                local res = Set.new{}
                for k in pairs(a) do
                    res[k] = b[k]
                end
                return res
            end
            Set.mt.__mul = Set.intersection
        
            function Set.tostring (set)
                local s = "{"
                local sep = ""
                for e in pairs(set) do
                    s = s .. sep .. e
                    sep = ", "
                end
                return s .. "}"
            end
        
            function Set.print (s)
                print(Set.tostring(s))
            end
        
            s1 = Set.new{10, 20, 30, 50}
            s2 = Set.new{30, 1}
            print(getmetatable(s1))          --> table: 00672B60
            print(getmetatable(s2))          --> table: 00672B60
            print("dump s1 -> "..core.dump(s1))
            print("dump s2 -> "..core.dump(s2))
            print("dump s1 metatable -> "..core.dump(getmetatable(s1)))
        
            s3 = s1 + s2
            print("dump s3 -> "..core.dump(s3))
            print("print(s3) -> ");
            Set.print(s3)  --> {1, 10, 20, 30, 50}
            print("dump Set -> "..core.dump(Set)) -- only this one contains mt
        """;
}
