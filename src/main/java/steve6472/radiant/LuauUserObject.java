package steve6472.radiant;

import net.hollowcube.luau.LuaState;

/**
 * Created by steve6472
 * Date: 3/26/2025
 * Project: Radiant <br>
 */
public class LuauUserObject
{
    private Object userObject;
    private LuauMetaTable metaTable;

    public LuauUserObject(Object userObject)
    {
        this.userObject = userObject;
    }

    public LuauUserObject(Object userObject, LuauMetaTable metaTable)
    {
        this.userObject = userObject;
        this.metaTable = metaTable;
    }

    public <T> T getUserObject(Class<T> type)
    {
        return type.cast(userObject);
    }

    public void setUserObject(Object object)
    {
        this.userObject = object;
    }

    public LuauMetaTable getMetaTable()
    {
        return metaTable;
    }

    public void setMetaTable(LuauMetaTable metaTable)
    {
        this.metaTable = metaTable;
    }

    public void pushUserObject(LuaState state)
    {
        state.newUserData(userObject);

        if (metaTable != null)
        {
            metaTable.assignMetaTable(state);
        }
    }
}
